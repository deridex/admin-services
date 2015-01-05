package cc.newmercy.contentservices.web.api.v1.sermon;

public class SermonAsset {

    private String id;

    private long length;

    private String contentType;

    private String s3Key;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    @Override
    public String toString() {
        return "SermonAsset{" +
                "id='" + id + '\'' +
                ", length=" + length +
                ", contentType='" + contentType + '\'' +
                ", s3Key='" + s3Key + '\'' +
                '}';
    }
}
