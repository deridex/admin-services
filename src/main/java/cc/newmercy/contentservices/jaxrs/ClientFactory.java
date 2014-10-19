package cc.newmercy.contentservices.jaxrs;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class ClientFactory {

	private ClientFactory() { }

	public static Client newClient(ObjectMapper jsonMapper) {
		return ClientBuilder.newBuilder()
				.register(new JacksonContextResolver(jsonMapper))
				.build();
	}
}
