package cc.newmercy.contentservices.web.api.v1.asset;

public class TemporaryAsset {

    private String id;

    private String contentType;

    private long length;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "TemporaryAsset{" +
                "id='" + id + '\'' +
                ", contentType='" + contentType + '\'' +
                ", length=" + length +
                '}';
    }
}
