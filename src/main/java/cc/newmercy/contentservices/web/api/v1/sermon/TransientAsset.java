package cc.newmercy.contentservices.web.api.v1.sermon;

public class TransientAsset {

    private String name;

    private String contentType;

    private long length;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "name='" + name + '\'' +
                ",contentType='" + contentType + '\'' +
                ", length=" + length +
                '}';
    }
}
