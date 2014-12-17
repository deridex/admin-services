package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.client.WebTarget;

import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.neo4j.Nodes;
import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.neo4j.json.Row;
import cc.newmercy.contentservices.web.id.IdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

public class Neo4jSermonSeriesRepository extends Neo4jRepository implements SermonSeriesRepository {

    private static final String ID_SERIES_NAME = "sermon-series";

    private static final String IMAGE_URL_PROPERTY = "imageUrl";

    private static final String DESCRIPTION_PROPERTY = "description";

    private static final String NAME_PROPERTY = "name";

    private static final String CREATED_AT_PROPERTY = "createdAt";

    public static final String SERMON_SERIES_LABEL = "SermonSeries";

    private static final String SKIP_PARAM = "skip";

    private static final String PAGE_SIZE_PARAM = "pageSize";

    private static final String LIST_QUERY = String.format("match (n:%s) return n order by n.createdAt desc skip { %s } limit { %s }",
            SERMON_SERIES_LABEL,
            SKIP_PARAM,
            PAGE_SIZE_PARAM);

    private static final String SAVE_QUERY = Nodes.createNodeQuery(
            SERMON_SERIES_LABEL,
            true,
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
    public List<PersistentSermonSeries> list(int page, int pageSize) {
        Preconditions.checkArgument(page > 0, "page must be positive");
        Preconditions.checkArgument(pageSize > 0, "page size must be positive");

        List<Row> rows = post(query(LIST_QUERY, PersistentSermonSeries.class)
                .set(SKIP_PARAM, (page - 1) * pageSize)
                .set(PAGE_SIZE_PARAM, pageSize))
                .get(0)
                .getData();

        return rows.stream()
                .map(datum -> datum.getColumns().<PersistentSermonSeries> get(0))
                .collect(Collectors.toList());
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
    public PersistentSermonSeries update(String id, EditedSermonSeries editedSeries) {
        return postForOne(query(UPDATE_QUERY, PersistentSermonSeries.class)
                .set(Nodes.ID_PROPERTY, id)
                .set(NAME_PROPERTY, editedSeries.getName())
                .set(DESCRIPTION_PROPERTY, editedSeries.getDescription())
                .set(IMAGE_URL_PROPERTY, editedSeries.getImageUrl()));
    }
}

