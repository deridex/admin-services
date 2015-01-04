package cc.newmercy.contentservices.web.api.v1.asset;

import javax.ws.rs.client.WebTarget;

import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.neo4j.Nodes;
import cc.newmercy.contentservices.web.id.IdService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Neo4jAssetRepository extends Neo4jRepository implements AssetRepository {

    private static final String LABEL = "Asset";

    private static final String ID_SERIES_NAME = "asset";

    private static final String CONTENT_TYPE_PROPERTY = "contentType";

    private static final String URL_PROPERTY = "url";

    private static final String LENGTH_PROPERTY = "length";

    private static final String SAVE_QUERY = Nodes.createNodeQuery(false, LABEL, URL_PROPERTY, LENGTH_PROPERTY, CONTENT_TYPE_PROPERTY);

    public Neo4jAssetRepository(
            IdService idService,
            WebTarget neo4j,
            Neo4jTransaction neo4jTransaction,
            ObjectMapper jsonMapper,
            EntityReader entityReader) {
        super(neo4j, neo4jTransaction, idService, jsonMapper, entityReader);
    }

    @Override
    public TemporaryAsset save(TransientAsset transientAsset) {
        String id = nextId(ID_SERIES_NAME);

        return postForOne(query(SAVE_QUERY, TemporaryAsset.class)
                .set(Nodes.ID_PROPERTY, id)
                .set(CONTENT_TYPE_PROPERTY, transientAsset.getContentType())
                .set(URL_PROPERTY, "s3:/tmp/" + id)
                .set(LENGTH_PROPERTY, transientAsset.getLength()));
    }
}
