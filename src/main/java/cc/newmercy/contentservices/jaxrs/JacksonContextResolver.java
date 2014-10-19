package cc.newmercy.contentservices.jaxrs;

import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

	private final ObjectMapper jsonMapper;

	public JacksonContextResolver(ObjectMapper jsonMapper) {
		this.jsonMapper = Preconditions.checkNotNull(jsonMapper, "json mapper");;
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		if (type == ObjectMapper.class) {
			return jsonMapper;
		}

		return null;
	}
}
