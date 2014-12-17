package cc.newmercy.contentservices.web.admin;

import java.time.Instant;

public class SermonSeriesInfo {

    private String id;

    private String name;

    private Instant createdAt;

    private int sermonCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public int getSermonCount() {
        return sermonCount;
    }

    public void setSermonCount(int sermonCount) {
        this.sermonCount = sermonCount;
    }

    @Override
    public String toString() {
        return "SermonSeriesInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", sermonCount=" + sermonCount +
                '}';
    }
}
