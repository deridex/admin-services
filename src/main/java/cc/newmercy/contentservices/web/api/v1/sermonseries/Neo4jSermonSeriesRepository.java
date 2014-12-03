package cc.newmercy.contentservices.web.api.v1.sermonseries;

import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.neo4j.Nodes;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import cc.newmercy.contentservices.web.id.IdService;
import com.google.common.base.Preconditions;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Neo4jSermonSeriesRepository extends Neo4jRepository implements SermonSeriesRepository {

    private static final GenericType<TransactionResponse<PersistentSermonSeriesColumns>> COLUMNS = new GenericType<TransactionResponse<PersistentSermonSeriesColumns>>() { };

    private static final String ID_SERIES_NAME = "sermon-series";

    private static final String IMAGE_URL_PROPERTY = "imageUrl";

    private static final String DESCRIPTION_PROPERTY = "description";

    private static final String NAME_PROPERTY = "name";

    private static final String ID_PROPERTY = "id";

    private static final String SERMON_SERIES_LABEL = "SermonSeries";

    private static final String SKIP_PARAM = "skip";

    private static final String PAGE_SIZE_PARAM = "pageSize";

    private static final String LIST_QUERY = String.format("match (n:%s) return n order by n.created_at skip { %s } limit { %s }",
            SERMON_SERIES_LABEL,
            SKIP_PARAM,
            PAGE_SIZE_PARAM);

    private static final String SAVE_QUERY = Nodes.createNodeQuery(
            SERMON_SERIES_LABEL,
            ID_PROPERTY,
            NAME_PROPERTY,
            DESCRIPTION_PROPERTY,
            IMAGE_URL_PROPERTY);

    private static final String GET_QUERY = Nodes.getNodeQuery(SERMON_SERIES_LABEL, ID_PROPERTY);

    private static final String UPDATE_QUERY = Nodes.updateNodeQuery(
            SERMON_SERIES_LABEL,
            ID_PROPERTY,
            NAME_PROPERTY,
            DESCRIPTION_PROPERTY,
            IMAGE_URL_PROPERTY);

    private final IdService idService;

    public Neo4jSermonSeriesRepository(IdService idService, WebTarget neo4j, Neo4jTransaction neo4jTransaction) {
        super(neo4j, neo4jTransaction);

        this.idService = Preconditions.checkNotNull(idService, "id service");
    }

    @Override
    public List<PersistentSermonSeries> list(int page, int pageSize) {
        Preconditions.checkArgument(page > 0, "page must be positive");
        Preconditions.checkArgument(pageSize > 0, "page size must be positive");

        Map<String, Object> parameters = new HashMap<>(2, 1);
        parameters.put(SKIP_PARAM, (page - 1) * pageSize);
        parameters.put(PAGE_SIZE_PARAM, pageSize);

        TransactionResponse<PersistentSermonSeriesColumns> response = post(LIST_QUERY, parameters, COLUMNS);

        List<PersistentSermonSeries> sermonSeriesList = response.getResults().get(0).getData().stream()
                .map(columns -> columns.<PersistentSermonSeries> get(0))
                .collect(Collectors.toList());

        return sermonSeriesList;
    }

    @Override
    public PersistentSermonSeries save(TransientSermonSeries transientSeries) {
        Map<String, Object> parameters = new HashMap<>(4, 1);
        parameters.put(ID_PROPERTY, idService.next(ID_SERIES_NAME));
        parameters.put(NAME_PROPERTY, transientSeries.getName());
        parameters.put(DESCRIPTION_PROPERTY, transientSeries.getDescription());
        parameters.put(IMAGE_URL_PROPERTY, transientSeries.getImageUrl());

        return postForOne(SAVE_QUERY, parameters, COLUMNS).get(0);
    }

    @Override
    public PersistentSermonSeries get(String id) {
        Map<String, Object> parameters = Collections.singletonMap(ID_PROPERTY, id);

        return postForOne(GET_QUERY, parameters, COLUMNS).get(0);
    }

    @Override
    public PersistentSermonSeries update(String id, EditedSermonSeries editedSeries) {
        Map<String, Object> parameters = new HashMap<>(4, 1);
        parameters.put(ID_PROPERTY, id);
        parameters.put(NAME_PROPERTY, editedSeries.getName());
        parameters.put(DESCRIPTION_PROPERTY, editedSeries.getDescription());
        parameters.put(IMAGE_URL_PROPERTY, editedSeries.getImageUrl());

        return postForOne(UPDATE_QUERY, parameters, COLUMNS).get(0);
    }
}
