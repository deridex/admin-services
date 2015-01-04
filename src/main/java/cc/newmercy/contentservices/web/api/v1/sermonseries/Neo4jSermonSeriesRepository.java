package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.time.Instant;
import javax.ws.rs.client.WebTarget;

import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.neo4j.Nodes;
import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.web.exceptions.BadRequestException;
import cc.newmercy.contentservices.web.id.IdService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Neo4jSermonSeriesRepository extends Neo4jRepository implements SermonSeriesRepository {

    private static final String ID_SERIES_NAME = "sermon-series";

    private static final String IMAGE_URL_PROPERTY = "imageUrl";

    private static final String DESCRIPTION_PROPERTY = "description";

    private static final String NAME_PROPERTY = "name";

    private static final String CREATED_AT_PROPERTY = "createdAt";

    public static final String SERMON_SERIES_LABEL = "SermonSeries";

    private static final String SAVE_QUERY = Nodes.createNodeQuery(
            true, SERMON_SERIES_LABEL,
            NAME_PROPERTY,
            DESCRIPTION_PROPERTY,
            IMAGE_URL_PROPERTY,
            CREATED_AT_PROPERTY);

    private static final String GET_QUERY = Nodes.getNodeQuery(SERMON_SERIES_LABEL);

    private static final String UPDATE_QUERY = Nodes.updateNodeQuery(
            SERMON_SERIES_LABEL,
            NAME_PROPERTY,
            DESCRIPTION_PROPERTY,
            IMAGE_URL_PROPERTY);

    public Neo4jSermonSeriesRepository(
            IdService idService,
            WebTarget neo4j,
            Neo4jTransaction neo4jTransaction,
            ObjectMapper jsonMapper,
            EntityReader entityReader) {
        super(neo4j, neo4jTransaction, idService, jsonMapper, entityReader);
    }

    @Override
    public PersistentSermonSeries save(TransientSermonSeries transientSeries, Instant now) {
        return postForOne(query(SAVE_QUERY, PersistentSermonSeries.class)
                .set(Nodes.ID_PROPERTY, nextId(ID_SERIES_NAME))
                .set(NAME_PROPERTY, transientSeries.getName())
                .set(DESCRIPTION_PROPERTY, transientSeries.getDescription())
                .set(IMAGE_URL_PROPERTY, transientSeries.getImageUrl())
                .set(CREATED_AT_PROPERTY, now));
    }

    @Override
    public PersistentSermonSeries get(String id) {
        return postForOne(query(GET_QUERY, PersistentSermonSeries.class).set(Nodes.ID_PROPERTY, id));
    }

    @Override
    public PersistentSermonSeries update(String id, Integer version, PersistentSermonSeries editedSeries) {
        PersistentSermonSeries sermonSeries = postForOne(query(UPDATE_QUERY, PersistentSermonSeries.class)
                .set(Nodes.ID_PROPERTY, id)
                .set(Nodes.VERSION_PROPERTY, version)
                .set(NAME_PROPERTY, editedSeries.getName())
                .set(DESCRIPTION_PROPERTY, editedSeries.getDescription())
                .set(IMAGE_URL_PROPERTY, editedSeries.getImageUrl()));

        if (sermonSeries == null) {
            throw new BadRequestException("cannot update sermon series " + id + " version " + version);
        }

        return sermonSeries;
    }
}
