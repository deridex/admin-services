package cc.newmercy.contentservices.jaxrs;

import java.util.Objects;

import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

	private final ObjectMapper jsonMapper;

	public JacksonContextResolver(ObjectMapper jsonMapper) {
		this.jsonMapper = Objects.requireNonNull(jsonMapper, "json mapper");;
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		if (type == ObjectMapper.class) {
			return jsonMapper;
		}

		return null;
	}
}
