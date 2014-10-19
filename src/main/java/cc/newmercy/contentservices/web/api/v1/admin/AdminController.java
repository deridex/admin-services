package cc.newmercy.contentservices.web.api.v1.admin;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cc.newmercy.contentservices.ServerStopper;

@RequestMapping(value = "/v1/admin")
public class AdminController {

	private final ServerStopper serverStopper;

	public AdminController(ServerStopper serverStopper) {
		this.serverStopper = Objects.requireNonNull(serverStopper, "server stopper");
	}

	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	public ResponseEntity<?> stop() throws Exception {
		serverStopper.stopServer();

		return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
	}
}
