package cc.newmercy.contentservices.neo4j;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface RequestExecutor {
    Response post(MediaType mediaType, Entity<?> entity);
}
