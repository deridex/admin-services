package cc.newmercy.contentservices.web.api.v1.sermon;

public class TransientSermon extends SermonCommonFields {
    @Override
    public String toString() {
        return "TransientSermon{" +
                "name='" + getName() + '\'' +
                ", date=" + getDate() +
                ", by='" + getBy() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", passages=" + getPassages() +
                '}';
    }
}
