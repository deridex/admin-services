package cc.newmercy.contentservices.web.api.v1.sermon;

import java.time.Instant;

public class TransientSermon extends SermonCommonFields {

    private Instant createdAt;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TransientSermon{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", passages=" + getPassages() +
                ", createdAt=" + createdAt +
                '}';
    }
}
