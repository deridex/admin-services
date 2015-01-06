package cc.newmercy.contentservices.web.api.v1.sermon;

public class TransientAsset {

    private String id;

    private String key;

    private String contentType;

    private long length;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
        return "TransientAsset{" +
                "id='" + id + '\'' +
                ",key='" + key + '\'' +
                ",contentType='" + contentType + '\'' +
                ", length=" + length +
                '}';
    }
}
