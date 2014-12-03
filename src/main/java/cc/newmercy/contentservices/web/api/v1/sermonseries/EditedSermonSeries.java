package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.util.Objects;

public class EditedSermonSeries extends SermonSeriesCommonFields {

    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getImageUrl());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EditedSermonSeries)) {
            return false;
        }

        EditedSermonSeries other = (EditedSermonSeries) o;

        return Objects.equals(getName(), other.getName())
                && Objects.equals(getDescription(), other.getDescription())
                && Objects.equals(getImageUrl(), other.getImageUrl())
                && Objects.equals(version, other.version);
    }

    @Override
    public String toString() {
        return "TransientSermonSeries [version=" + version + ", name=" + getName() + ", description=" + getDescription() +
                ", imageUrl=" + getImageUrl() + "]";
    }
}
