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
import cc.newmercy.contentservices.web.exceptions.BadRequestException;
import cc.newmercy.contentservices.web.id.IdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class Neo4jSermonRepository extends Neo4jRepository implements SermonRepository {

    private static final String SEQUENCE_NAME = "sermon";

    public static final String SERMON_LABEL = "Sermon";

    private static final String NAME_PROPERTY= "name";

    private static final String DATE_PROPERTY = "date";

    private static final String BY_PROPERTY = "by";

    private static final String DESCRIPTION_PROPERTY = "description";

    private static final String PASSAGES_PROPERTY = "passages";

    private static final String CREATED_AT_PROPERTY = "createdAt";

    private static final String CREATE_QUERY = Nodes.createNodeQuery(
            true, SERMON_LABEL,
            NAME_PROPERTY,
            BY_PROPERTY,
            DATE_PROPERTY,
            DESCRIPTION_PROPERTY,
            PASSAGES_PROPERTY,
            CREATED_AT_PROPERTY);

    private static final String UPDATE_QUERY = Nodes.updateNodeQuery(
            SERMON_LABEL,
            NAME_PROPERTY,
            BY_PROPERTY,
            DATE_PROPERTY,
            DESCRIPTION_PROPERTY,
            PASSAGES_PROPERTY);

    public static final String HAS_SERMON_LABEL = "HAS_SERMON";

    private static final String LINK_QUERY = Relationships.createRelationshipQuery(
            Neo4jSermonSeriesRepository.SERMON_SERIES_LABEL,
            SERMON_LABEL,
            HAS_SERMON_LABEL);

    private static final String LIST_SERMONS_QUERY = Relationships.listRelationsQuery(
            Neo4jSermonSeriesRepository.SERMON_SERIES_LABEL,
            HAS_SERMON_LABEL,
            SERMON_LABEL);

    private static final String GET_SERMON_QUERY = Relationships.fetchRelatedQuery(
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
    public PersistentSermon get(String sermonSeriesId, String sermonId) {
        return postForOne(query(GET_SERMON_QUERY, PersistentSermon.class)
                .set(Relationships.START_ID_PARAMETER, sermonSeriesId)
                .set(Relationships.END_ID_PARAMETER, sermonId));
    }

    @Override
    public PersistentSermon save(String seriesId, int version, TransientSermon transientSermon, Instant now) {
        String id = nextId(SEQUENCE_NAME);

        List<Result> results = post(
                query(CREATE_QUERY, PersistentSermon.class)
                        .set(Nodes.ID_PROPERTY, id)
                        .set(NAME_PROPERTY, transientSermon.getName())
                        .set(BY_PROPERTY, transientSermon.getBy())
                        .set(DATE_PROPERTY, transientSermon.getDate())
                        .set(DESCRIPTION_PROPERTY, transientSermon.getDescription())
                        .setStrings(PASSAGES_PROPERTY, transientSermon.getPassages())
                        .set(CREATED_AT_PROPERTY, now),
                query(LINK_QUERY, Map.class)
                        .set(Relationships.START_ID_PARAMETER, seriesId)
                        .set(Relationships.START_VERSION_PARAMETER, version)
                        .set(Relationships.END_ID_PARAMETER, id)
                        .set(Relationships.END_VERSION_PARAMETER, 1));

        return results.get(0).getData().get(0).getColumns().get(0);
    }

    @Override
    public PersistentSermon update(String sermonId, int sermonVersion, PersistentSermon editedSermon) {
        PersistentSermon persistentSermon = postForOne(query(UPDATE_QUERY, PersistentSermon.class)
                .set(Nodes.ID_PROPERTY, sermonId)
                .set(Nodes.VERSION_PROPERTY, sermonVersion)
                .set(NAME_PROPERTY, editedSermon.getName())
                .set(BY_PROPERTY, editedSermon.getBy())
                .set(DATE_PROPERTY, editedSermon.getDate())
                .set(DESCRIPTION_PROPERTY, editedSermon.getDescription())
                .setStrings(PASSAGES_PROPERTY, editedSermon.getPassages()));

        if (persistentSermon == null) {
            throw new BadRequestException("cannot update sermon " + sermonId + " version " + sermonVersion);
        }

        return persistentSermon;
    }
}
