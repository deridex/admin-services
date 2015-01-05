package cc.newmercy.contentservices.web.api.v1.sermon;

import java.util.List;

public interface SermonAssetRepository {
    SermonAsset save(String sermonId, int sermonVersion, TransientAsset asset);

    List<SermonAsset> list(String sermonId, int sermonVersion);
}
