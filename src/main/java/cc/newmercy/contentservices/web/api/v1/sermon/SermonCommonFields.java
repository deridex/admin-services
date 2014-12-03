package cc.newmercy.contentservices.web.api.v1.sermon;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

public abstract class SermonCommonFields {
    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotEmpty
    @Valid
    private List<Passage> passages;

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

    public List<Passage> getPassages() {
        return passages;
    }

    public void setPassages(List<Passage> passages) {
        this.passages = passages;
    }
}
