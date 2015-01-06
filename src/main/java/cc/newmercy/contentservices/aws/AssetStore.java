package cc.newmercy.contentservices.aws;

import java.io.InputStream;

import cc.newmercy.contentservices.web.api.v1.sermon.TransientAsset;

public interface AssetStore {
    void save(TransientAsset transientAsset, InputStream data);
}
