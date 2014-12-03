package cc.newmercy.contentservices.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.ext.ContextResolver;
import java.util.Objects;

public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

	private final ObjectMapper jsonMapper;

	public JacksonContextResolver(ObjectMapper jsonMapper) {
		this.jsonMapper = Objects.requireNonNull(jsonMapper, "json mapper");;
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return jsonMapper;
	}
}
