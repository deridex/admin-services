package cc.newmercy.contentservices.web.api.v1.sermon;

import com.google.common.base.Preconditions;

import javax.ws.rs.client.WebTarget;

public class Neo4jSermonRepository implements SermonRepository {

    private WebTarget neo4j;

    public Neo4jSermonRepository(WebTarget neo4j) {
        this.neo4j = Preconditions.checkNotNull(neo4j, "neo4j");
    }

    @Override
    public PersistentSermon get(String id) {
        return null;
    }

    @Override
    public PersistentSermon save(TransientSermon transientSermon) {
        return null;
    }
}
