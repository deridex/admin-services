package cc.newmercy.contentservices.aws;

import org.springframework.web.multipart.MultipartFile;

public interface AssetStorage {
    void save(String key, MultipartFile file);
}
