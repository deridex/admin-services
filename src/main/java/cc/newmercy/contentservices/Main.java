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
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class Main {
	public static void main(String[] args) {
		int port = 8080;

		Server server = new Server(port);

		try {
			server.addBean(new MBeanContainer(ManagementFactory.getPlatformMBeanServer()));

      ContextHandler servletHandler = newServletHandler();
      servletHandler.setContextPath("/");

      ContextHandler jsResourceHandler = newClasspathResourceHandler("/js");
      jsResourceHandler.setContextPath("/js");

      ContextHandler appResourceHandler = newClasspathResourceHandler("/app");
      appResourceHandler.setContextPath("/app");

      ContextHandlerCollection handlerCollection = new ContextHandlerCollection();
      handlerCollection.setHandlers(new Handler[] { servletHandler, jsResourceHandler, appResourceHandler });

			server.setHandler(handlerCollection);
			server.start();
			server.join();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			try {
				server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static ContextHandler newServletHandler() {
    ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

    ServletHolder servletHolder = new ServletHolder(new DispatcherServlet());
    servletHolder.getInitParameters().put("contextClass", AnnotationConfigWebApplicationContext.class.getName());
    servletHolder.getInitParameters().put("contextConfigLocation", ContentServicesConfiguration.class.getName());

    servletHandler.addServlet(servletHolder, "/api/*");

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
