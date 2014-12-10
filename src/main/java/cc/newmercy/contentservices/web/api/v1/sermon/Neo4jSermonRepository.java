package cc.newmercy.contentservices.web.api.v1.sermon;

import javax.ws.rs.client.WebTarget;

import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.neo4j.Nodes;
import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.web.id.IdService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Neo4jSermonRepository extends Neo4jRepository implements SermonRepository {

    private static final String SEQUENCE_NAME = "sermon";

    private static final String LABEL = "Sermon";

    private static final String NAME_PROPERTY= "name";

    private static final String DESCRIPTION_PROPERTY = "description";

    private static final String PASSAGES_PROPERTY = "passages";

    private static final String CREATE_QUERY = Nodes.createNodeQuery(
            LABEL,
            true,
            NAME_PROPERTY,
            DESCRIPTION_PROPERTY,
            PASSAGES_PROPERTY);

    public Neo4jSermonRepository(
            WebTarget neo4j,
            Neo4jTransaction neo4jTransaction,
            IdService idService,
            ObjectMapper jsonMapper,
            EntityReader entityReader) {
        super(neo4j, neo4jTransaction, idService, jsonMapper, entityReader);
    }

    @Override
    public PersistentSermon get(String id) {
        return null;
    }

    @Override
    public PersistentSermon save(String seriesId, TransientSermon transientSermon) {
        String id = nextId(SEQUENCE_NAME);

        return postForOne(query(CREATE_QUERY, PersistentSermon.class)
                .set(Nodes.ID_PROPERTY, id)
                .set(NAME_PROPERTY, transientSermon.getName())
                .set(DESCRIPTION_PROPERTY, transientSermon.getDescription())
                .set(PASSAGES_PROPERTY, transientSermon.getPassages()));
    }
}
