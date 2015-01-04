package cc.newmercy.contentservices.aws;

import java.io.InputStream;

public interface AssetStorage {
    void save(String key, long length, InputStream data);
}
