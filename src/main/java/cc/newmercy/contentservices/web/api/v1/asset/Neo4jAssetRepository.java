package cc.newmercy.contentservices.web.api.v1.asset;

import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.neo4j.Nodes;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import cc.newmercy.contentservices.web.id.IdService;
import com.google.common.collect.ImmutableMap;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class Neo4jAssetRepository extends Neo4jRepository implements AssetRepository {

    private static final String LABEL = "Asset";

    private static final String ID_SERIES_NAME = "asset";

    private static final String ID_PROPERTY = "id";

    private static final String CONTENT_TYPE_PROPERTY = "contentType";

    private static final String URL_PROPERTY = "url";

    private static final String LENGTH_PROPERTY = "length";

    private static final String SAVE_QUERY = Nodes.createNodeQuery(LABEL, ID_PROPERTY, URL_PROPERTY, LENGTH_PROPERTY, CONTENT_TYPE_PROPERTY);

    private final IdService idService;

    public Neo4jAssetRepository(
            IdService idService,
            WebTarget neo4j,
            Neo4jTransaction neo4jTransaction) {
        super(neo4j, neo4jTransaction);

        this.idService = checkNotNull(idService, "id service");
    }

    @Override
    public TemporaryAsset save(TransientAsset transientAsset) {
        String id = idService.next(ID_SERIES_NAME);

        Map<String, Object> params = ImmutableMap.<String, Object>builder()
                .put(ID_PROPERTY, id)
                .put(CONTENT_TYPE_PROPERTY, transientAsset.getContentType())
                .put(URL_PROPERTY, "s3:/tmp/" + id)
                .put(LENGTH_PROPERTY, transientAsset.getLength())
                .build();

        return postForOne(SAVE_QUERY, params, new GenericType<TransactionResponse<TemporaryAssetColumns>>() { }).get(0);
    }
}
