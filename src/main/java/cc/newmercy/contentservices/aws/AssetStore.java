package cc.newmercy.contentservices.aws;

import java.io.InputStream;

import com.amazonaws.services.s3.model.ObjectMetadata;

public interface AssetStore {
    void save(String key, ObjectMetadata metadata, InputStream data);

    void delete(String key);
}
