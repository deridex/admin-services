package cc.newmercy.contentservices.web.api.v1.sermon;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.WebTarget;

import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.neo4j.Nodes;
import cc.newmercy.contentservices.neo4j.Relationships;
import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.neo4j.json.Result;
import cc.newmercy.contentservices.neo4j.json.Row;
import cc.newmercy.contentservices.web.api.v1.sermonseries.Neo4jSermonSeriesRepository;
import cc.newmercy.contentservices.web.id.IdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class Neo4jSermonRepository extends Neo4jRepository implements SermonRepository {

    private static final String SEQUENCE_NAME = "sermon";

    public static final String SERMON_LABEL = "Sermon";

    private static final String NAME_PROPERTY= "name";

    private static final String DESCRIPTION_PROPERTY = "description";

    private static final String PASSAGES_PROPERTY = "passages";

    private static final String CREATED_AT_PROPERTY = "createdAt";

    private static final String CREATE_QUERY = Nodes.createNodeQuery(
            SERMON_LABEL,
            true,
            NAME_PROPERTY,
            DESCRIPTION_PROPERTY,
            PASSAGES_PROPERTY,
            CREATED_AT_PROPERTY);

    public static final String HAS_SERMON_LABEL = "HAS_SERMON";

    private static final String LINK_QUERY = Relationships.createRelationshipQuery(
            Neo4jSermonSeriesRepository.SERMON_SERIES_LABEL,
            SERMON_LABEL,
            HAS_SERMON_LABEL);

    private static final String LIST_SERMONS_QUERY = Relationships.listRelationsQuery(
            Neo4jSermonSeriesRepository.SERMON_SERIES_LABEL,
            HAS_SERMON_LABEL,
            SERMON_LABEL);

    public Neo4jSermonRepository(
            WebTarget neo4j,
            Neo4jTransaction neo4jTransaction,
            IdService idService,
            ObjectMapper jsonMapper,
            EntityReader entityReader) {
        super(neo4j, neo4jTransaction, idService, jsonMapper, entityReader);
    }

    @Override
    public List<PersistentSermon> list(String sermonSeriesId) {
        Result result = post(query(LIST_SERMONS_QUERY, PersistentSermon.class)
                .set(Relationships.START_ID_PARAMETER, sermonSeriesId))
                .get(0);

        List<Row> data = result.getData();

        return Lists.transform(data, row -> row.getColumns().get(0));
    }

    @Override
    public PersistentSermon save(String seriesId, TransientSermon transientSermon, Instant now) {
        String id = nextId(SEQUENCE_NAME);

        List<Result> results = post(
                query(CREATE_QUERY, PersistentSermon.class)
                        .set(Nodes.ID_PROPERTY, id)
                        .set(NAME_PROPERTY, transientSermon.getName())
                        .set(DESCRIPTION_PROPERTY, transientSermon.getDescription())
                        .setStrings(PASSAGES_PROPERTY, transientSermon.getPassages())
                        .set(CREATED_AT_PROPERTY, now),
                query(LINK_QUERY, Map.class)
                        .set(Relationships.START_ID_PARAMETER, seriesId)
                        .set(Relationships.END_ID_PARAMETER, id));

        return results.get(0).getData().get(0).getColumns().get(0);
    }
}
