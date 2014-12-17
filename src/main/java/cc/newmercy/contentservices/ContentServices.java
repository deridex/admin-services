package cc.newmercy.contentservices;

import java.lang.management.ManagementFactory;

import cc.newmercy.contentservices.config.ContentServicesConfiguration;
import cc.newmercy.contentservices.web.admin.config.AdminConfiguration;
import cc.newmercy.contentservices.web.api.v1.config.ApiConfiguration;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ContentServices {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentServices.class);

	public static void main(String[] args) throws Exception {
		// http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-jvm-ttl.html
		java.security.Security.setProperty("networkaddress.cache.ttl" , "60");

		int port = 8080;

		try (AnnotationConfigWebApplicationContext rootInjector = new AnnotationConfigWebApplicationContext()) {
			rootInjector.register(ContentServicesConfiguration.class);
			rootInjector.refresh();

			Server server = newServer(rootInjector, port);

			try {
				LOGGER.info("server starting up");

				server.start();
				server.join();
			} finally {
				LOGGER.info("shutting server down");

				try {
					server.stop();
				} catch (Exception e) {
					LOGGER.warn("trapped exception stopping server", e);
				}
			}
		}
	}

	private static Server newServer(AnnotationConfigWebApplicationContext rootInjector, int port) {
		Server server = new Server(port);

		ServerStopper serverStopper = rootInjector.getBean(ServerStopper.class);
		serverStopper.setServer(server);

		server.addBean(new MBeanContainer(ManagementFactory.getPlatformMBeanServer()));

		ContextHandler apiServletHandler = newServletHandler(rootInjector, ApiConfiguration.class);
		apiServletHandler.setContextPath("/api");

		ContextHandler adminServletHandler = newServletHandler(rootInjector, AdminConfiguration.class);
		adminServletHandler.setContextPath("/admin");

		ContextHandler jsResourceHandler = newClasspathResourceHandler("/js");
		jsResourceHandler.setContextPath("/js");

		ContextHandler appResourceHandler = newClasspathResourceHandler("/app");
		appResourceHandler.setContextPath("/app");

		ContextHandlerCollection handlerCollection = new ContextHandlerCollection();
		handlerCollection.setHandlers(new Handler[] { jsResourceHandler, appResourceHandler, apiServletHandler, adminServletHandler });

		server.setHandler(handlerCollection);

		return server;
	}

	private static ContextHandler newServletHandler(AnnotationConfigWebApplicationContext rootInjector, Class<?> config) {
		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY);

		AnnotationConfigWebApplicationContext injector = new AnnotationConfigWebApplicationContext();
		injector.register(config);
		injector.setParent(rootInjector);

		servletHandler.addServlet(new ServletHolder(new DispatcherServlet(injector)), "/");

		return servletHandler;
	}

	private static ContextHandler newClasspathResourceHandler(String topLevelDir) {
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setBaseResource(Resource.newClassPathResource(topLevelDir));
		resourceHandler.setEtags(true);

		ContextHandler contextHandler = new ContextHandler();
		contextHandler.setHandler(resourceHandler);

		return contextHandler;
	}
}
