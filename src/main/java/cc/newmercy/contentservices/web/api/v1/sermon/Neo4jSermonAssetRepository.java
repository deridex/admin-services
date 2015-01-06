package cc.newmercy.contentservices.web.api.v1.sermon;

import java.util.List;
import java.util.Map;

import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Nodes;
import cc.newmercy.contentservices.neo4j.Relationships;
import cc.newmercy.contentservices.neo4j.RequestExecutor;
import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.neo4j.json.Result;
import cc.newmercy.contentservices.web.exceptions.BadRequestException;
import cc.newmercy.contentservices.web.id.IdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4jSermonAssetRepository extends Neo4jRepository implements SermonAssetRepository {

    private static final String SERMON_ASSET_LABEL = "SermonAsset";

    private static final String CONTENT_TYPE_PROPERTY = "contentType";

    private static final String S3_KEY_PROPERTY = "s3Key";

    private static final String LENGTH_PROPERTY = "length";

    private static final String SAVE_QUERY = Nodes.createNodeQuery(false, SERMON_ASSET_LABEL, S3_KEY_PROPERTY, LENGTH_PROPERTY, CONTENT_TYPE_PROPERTY);

    private static final String INDEX_PROPERTY = "index";

    private static final String HAS_ASSET_LABEL = "HAS_ASSET";

    private static final String LINK_QUERY = String.format("match (s:%s { id: { %s }, v: { %s } }) optional match (s)-[r:%s]-(:%s) with s, count(r) as numAssets match (e:%s { id: { %s } }) create (s)-[r:%s { %s: numAssets }]->(e) set s.%s = s.%s + 1 return r",
            Neo4jSermonRepository.SERMON_LABEL,
            Relationships.START_ID_PARAMETER,
            Relationships.START_VERSION_PARAMETER,
            HAS_ASSET_LABEL,
            SERMON_ASSET_LABEL,
            SERMON_ASSET_LABEL,
            Relationships.END_ID_PARAMETER,
            HAS_ASSET_LABEL,
            INDEX_PROPERTY,
            Nodes.VERSION_PROPERTY,
            Nodes.VERSION_PROPERTY);

    private static final String GET_SERMON_VERSION_QUERY = Nodes.getVersionQuery(Neo4jSermonRepository.SERMON_LABEL);

    private static final String LIST_ASSETS_QUERY = Relationships.listRelationsQuery(
            Neo4jSermonRepository.SERMON_LABEL,
            HAS_ASSET_LABEL,
            SERMON_ASSET_LABEL);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Neo4jSermonAssetRepository(
            RequestExecutor requestExecutor,
            IdService idService,
            ObjectMapper jsonMapper,
            EntityReader entityReader) {
        super(requestExecutor, idService, jsonMapper, entityReader);
    }

    @Override
    public List<SermonAsset> list(String sermonId, int sermonVersion) {
        List<Result> results = post(
                query(GET_SERMON_VERSION_QUERY, Integer.class)
                        .set(Nodes.ID_PARAMETER, sermonId),
                query(LIST_ASSETS_QUERY, SermonAsset.class)
                        .set(Relationships.START_ID_PARAMETER, sermonId));

        Nodes.ensureVersion(results.get(0), Neo4jSermonRepository.SERMON_LABEL, sermonId, sermonVersion);

        return Lists.transform(results.get(1).getData(), row -> row.getColumns().get(0));
    }

    @Override
    public SermonAsset save(String sermonId, int sermonVersion, TransientAsset transientAsset) {
        List<Result> results = post(
                query(GET_SERMON_VERSION_QUERY, Integer.class)
                        .set(Nodes.ID_PARAMETER, sermonId),
                query(SAVE_QUERY, SermonAsset.class)
                        .set(Nodes.ID_PROPERTY, transientAsset.getId())
                        .set(CONTENT_TYPE_PROPERTY, transientAsset.getContentType())
                        .set(S3_KEY_PROPERTY, transientAsset.getKey())
                        .set(LENGTH_PROPERTY, transientAsset.getLength()),
                query(LINK_QUERY, Map.class)
                        .set(Relationships.START_ID_PARAMETER, sermonId)
                        .set(Relationships.START_VERSION_PARAMETER, sermonVersion)
                        .set(Relationships.END_ID_PARAMETER, transientAsset.getId()));

        Nodes.ensureVersion(results.get(0), Neo4jSermonRepository.SERMON_LABEL, sermonId, sermonVersion);

        SermonAsset asset = results.get(1).getData().get(0).getColumns().get(0);

        Result linkResult = results.get(2);

        if (linkResult.getData().isEmpty()) {
            throw new BadRequestException("could not add asset to sermon '" + sermonId + "' version " + sermonVersion);
        }

        assert linkResult.getData().size() == 1;

        logger.debug("sermon '{}' linked by {} to asset '{}'",
                sermonId,
                linkResult.getData().get(0).getColumns().get(0),
                transientAsset.getId());

        return asset;
    }
}
