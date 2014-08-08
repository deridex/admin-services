package cc.newmercy.contentservices;

import java.lang.management.ManagementFactory;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.servlet.DispatcherServlet;

public class Main {
	public static void main(String[] args) {
		int port = 8080;

		Server server = new Server(port);

		try {
			MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
      server.addBean(mbContainer);

      ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
      servletHandler.setContextPath("/");

      ServletHolder servletHolder = new ServletHolder(new DispatcherServlet());
      servletHolder.getInitParameters().put("contextConfigLocation", "classpath:content-services.xml");

      servletHandler.addServlet(servletHolder, "/content/*");

      server.setHandler(servletHandler);

			server.start();
			server.join();
		} catch (Exception e) {
			try {
				server.stop();
			} catch (Exception stopExc) {
				stopExc.printStackTrace();
			}

			throw new IllegalArgumentException("port " + 8080, e);
		}
	}
}
