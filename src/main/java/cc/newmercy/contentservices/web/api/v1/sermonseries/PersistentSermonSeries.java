package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.util.Objects;

public class PersistentSermonSeries extends SermonSeriesCommonFields {

    private String id;

    private int version;

    public PersistentSermonSeries() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PersistentSermonSeries)) {
            return false;
        }

        if (this == o) {
            return true;
        }

        PersistentSermonSeries other = (PersistentSermonSeries) o;

        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "PersistentSermonSeries [id=" + id + ", version=" + version + ", name=" + getName() + ", description="
                + getDescription() + ", imageUrl=" + getImageUrl() + "]";
    }
}
