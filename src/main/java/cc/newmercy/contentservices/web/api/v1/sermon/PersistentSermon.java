package cc.newmercy.contentservices.web.api.v1.sermon;

import java.time.Instant;
import java.util.List;

public class PersistentSermon extends SermonCommonFields {

    private String id;

    private List<Asset> assets;

    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
