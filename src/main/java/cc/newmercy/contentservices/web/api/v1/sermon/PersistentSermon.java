package cc.newmercy.contentservices.web.api.v1.sermon;

import java.util.List;

public class PersistentSermon extends SermonCommonFields{

    private String id;

    private List<Asset> assets;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
