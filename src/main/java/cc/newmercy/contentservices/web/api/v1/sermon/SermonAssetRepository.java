package cc.newmercy.contentservices.web.api.v1.sermon;

import java.io.InputStream;
import java.util.List;

public interface SermonAssetRepository {
    SermonAsset save(String sermonId, int sermonVersion, TransientAsset asset, InputStream data);

    List<SermonAsset> list(String sermonId);

    void delete(String sermonId, String assetId);
}
