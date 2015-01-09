package cc.newmercy.contentservices.web.api.v1.sermon;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cc.newmercy.contentservices.aws.AssetStore;
import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Nodes;
import cc.newmercy.contentservices.neo4j.Relationships;
import cc.newmercy.contentservices.neo4j.RequestExecutor;
import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.neo4j.json.Result;
import cc.newmercy.contentservices.web.exceptions.NotFoundException;
import cc.newmercy.contentservices.web.id.IdService;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4jSermonAssetRepository extends Neo4jRepository implements SermonAssetRepository {

    private static final String SEQUENCE_NAME = "asset";

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

    private static final String GET_ASSET_QUERY = Relationships.fetchRelatedQuery(
            Neo4jSermonRepository.SERMON_LABEL,
            HAS_ASSET_LABEL,
            SERMON_ASSET_LABEL);

    private static final String DELETE_SERMON_ASSET_QUERY = Nodes.deleteNodeQuery(SERMON_ASSET_LABEL, false);

    private static final String DELETE_HAS_SERMON_ASSET_QUERY = Relationships.deleteInboundRelationshipsQuery(
            Neo4jSermonRepository.SERMON_LABEL,
            HAS_ASSET_LABEL,
            SERMON_ASSET_LABEL,
            false);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String assetS3KeyPrefix;

    private final AssetStore assetStore;

    public Neo4jSermonAssetRepository(
            RequestExecutor requestExecutor,
            IdService idService,
            ObjectMapper jsonMapper,
            EntityReader entityReader,
            String assetS3KeyPrefix,
            AssetStore assetStore) {
        super(requestExecutor, idService, jsonMapper, entityReader);

        Objects.requireNonNull(assetS3KeyPrefix, "s3 key prefix");
        Preconditions.checkArgument(!assetS3KeyPrefix.isEmpty(), "s3 key prefix");
        this.assetS3KeyPrefix = Objects.requireNonNull(assetS3KeyPrefix, "s3 key prefix");

        this.assetStore = Objects.requireNonNull(assetStore, "asset store");
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
    public SermonAsset save(String sermonId, int sermonVersion, TransientAsset transientAsset, InputStream data) {
        String id = nextId(SEQUENCE_NAME);

        String key = String.format("%s/%tY/%2$tY%2$tm%2$td-%s-%s",
                assetS3KeyPrefix,
                transientAsset.getDate(),
                id,
                transientAsset.getName());

        List<Result> results = post(
                query(GET_SERMON_VERSION_QUERY, Integer.class)
                        .set(Nodes.ID_PARAMETER, sermonId),
                query(SAVE_QUERY, SermonAsset.class)
                        .set(Nodes.ID_PARAMETER, id)
                        .set(CONTENT_TYPE_PROPERTY, transientAsset.getContentType())
                        .set(S3_KEY_PROPERTY, key)
                        .set(LENGTH_PROPERTY, transientAsset.getLength()),
                query(LINK_QUERY, Map.class)
                        .set(Relationships.START_ID_PARAMETER, sermonId)
                        .set(Relationships.START_VERSION_PARAMETER, sermonVersion)
                        .set(Relationships.END_ID_PARAMETER, id));

        Nodes.ensureVersion(results.get(0), Neo4jSermonRepository.SERMON_LABEL, sermonId, sermonVersion);

        SermonAsset asset = results.get(1).getData().get(0).getColumns().get(0);

        Result linkResult = results.get(2);

        if (linkResult.getData().isEmpty()) {
            throw new IllegalArgumentException("could not add asset to sermon '" + sermonId + "' version " + sermonVersion);
        }

        assert linkResult.getData().size() == 1;

        logger.debug("sermon '{}' linked by {} to asset '{}'", sermonId, linkResult.getData().get(0).getColumns().get(0), id);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(transientAsset.getLength());
        metadata.setContentType(transientAsset.getContentType());

        assetStore.save(key, metadata, data);

        return asset;
    }

    @Override
    public void delete(String sermonId, String assetId) {
        List<Result> results = post(
                query(GET_ASSET_QUERY, SermonAsset.class)
                        .set(Relationships.START_ID_PARAMETER, sermonId)
                        .set(Relationships.END_ID_PARAMETER, assetId),
                query(DELETE_HAS_SERMON_ASSET_QUERY, Integer.class)
                        .set(Relationships.END_ID_PARAMETER, assetId),
                query(DELETE_SERMON_ASSET_QUERY, Integer.class)
                        .set(Nodes.ID_PARAMETER, assetId));

        if (results.get(2).getData().get(0).getColumns().<Integer> get(0) != 1) {
            throw new NotFoundException("sermon '" + sermonId + "' has no such asset '" + assetId + "'");
        }

        SermonAsset sermonAsset = results.get(0).getData().get(0).getColumns().get(0);

        logger.debug("deleting s3 object '{}'", sermonAsset.getS3Key());

        assetStore.delete(sermonAsset.getS3Key());

        Integer relationshipCount = results.get(1).getData().get(0).getColumns().get(0);

        logger.debug("deleted {} relationships", relationshipCount);
    }
}
