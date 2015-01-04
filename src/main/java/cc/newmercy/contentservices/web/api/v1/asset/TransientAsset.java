package cc.newmercy.contentservices.web.api.v1.asset;

public class TransientAsset {

    private String contentType;

    private long length;

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
                "contentType='" + contentType + '\'' +
                ", length=" + length +
                '}';
    }
}
