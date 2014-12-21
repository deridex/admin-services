package cc.newmercy.contentservices.web.api.v1.sermon;

import java.util.List;

import cc.newmercy.contentservices.validation.collection.NotEmptyString;
import cc.newmercy.contentservices.validation.collection.SupplementalCollectionConstraints;
import org.hibernate.validator.constraints.NotEmpty;

public abstract class SermonCommonFields {
    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @SupplementalCollectionConstraints(NotEmptyString.class)
    @NotEmpty
    private List<String> passages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPassages() {
        return passages;
    }

    public void setPassages(List<String> passages) {
        this.passages = passages;
    }
}
