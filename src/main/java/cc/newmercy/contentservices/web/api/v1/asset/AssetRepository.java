package cc.newmercy.contentservices.web.api.v1.asset;

public interface AssetRepository {
    TemporaryAsset save(TransientAsset asset);
}
