package cc.newmercy.contentservices.web.api.v1.sermon;

import cc.newmercy.contentservices.validation.collection.NotEmptyString;
import cc.newmercy.contentservices.validation.collection.SupplementalCollectionConstraints;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class TransientSermon extends SermonCommonFields {
    @NotEmpty
    @SupplementalCollectionConstraints(NotEmptyString.class)
    private List<String> assetIds;

    public List<String> getAssetIds() {
        return assetIds;
    }

    public void setAssetIds(List<String> assetIds) {
        this.assetIds = assetIds;
    }
}
