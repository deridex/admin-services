package cc.newmercy.contentservices;

import org.eclipse.jetty.server.Server;

public class ServerStopper {

	private Server server;

	public void setServer(Server server) {
		this.server = server;
	}

	public void stopServer() throws Exception {
		Thread stopper = new Thread() {
			@Override
			public void run() {
				try {
					server.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		stopper.setName("server-stopper");
		stopper.start();
	}
}
