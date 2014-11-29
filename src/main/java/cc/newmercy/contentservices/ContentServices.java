package cc.newmercy.contentservices;

import java.lang.management.ManagementFactory;

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

import cc.newmercy.contentservices.config.ContentServicesConfiguration;
import cc.newmercy.contentservices.web.config.WebConfiguration;

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

		ContextHandler servletHandler = newServletHandler(rootInjector);
		servletHandler.setContextPath("/api");

		ContextHandler jsResourceHandler = newClasspathResourceHandler("/js");
		jsResourceHandler.setContextPath("/js");

		ContextHandler appResourceHandler = newClasspathResourceHandler("/app");
		appResourceHandler.setContextPath("/app");

		ContextHandlerCollection handlerCollection = new ContextHandlerCollection();
		handlerCollection.setHandlers(new Handler[] { servletHandler, jsResourceHandler, appResourceHandler });

		server.setHandler(handlerCollection);

		return server;
	}

	private static ContextHandler newServletHandler(AnnotationConfigWebApplicationContext rootInjector) {
		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY);

		AnnotationConfigWebApplicationContext webInjector = new AnnotationConfigWebApplicationContext();
		webInjector.register(WebConfiguration.class);
		webInjector.setParent(rootInjector);

		ServletHolder servletHolder = new ServletHolder(new DispatcherServlet(webInjector));

		servletHandler.addServlet(servletHolder, "/");

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
