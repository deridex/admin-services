package cc.newmercy.contentservices.web.api.v1.sermon;

import java.time.LocalDate;

public class TransientAsset {

    private String name;

    private LocalDate date;

    private String contentType;

    private long length;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
                ", date=" + date +
                ", contentType='" + contentType + '\'' +
                ", length=" + length +
                '}';
    }
}
