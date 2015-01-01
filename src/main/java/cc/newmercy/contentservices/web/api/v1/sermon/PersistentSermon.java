package cc.newmercy.contentservices.web.api.v1.sermon;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersistentSermon extends SermonCommonFields {

    private String id;

    @JsonProperty("v")
    private int version;

    private Instant createdAt;

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PersistentSermon{" +
                "id='" + id + '\'' +
                ", version=" + version +
                ", name='" + getName() + '\'' +
                ", date=" + getDate() +
                ", by='" + getBy() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", passages=" + getPassages() +
                ", createdAt=" + createdAt +
                '}';
    }
}
