package cc.newmercy.contentservices.web.api.v1.sermon;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.net.URI;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes(value = @JsonSubTypes.Type(value = Mp3.class, name = "mp3"))
public abstract class Asset {

    private URI url;

    private String contentType;

    private long length;

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
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
}
