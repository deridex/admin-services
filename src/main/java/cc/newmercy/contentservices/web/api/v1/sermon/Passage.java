package cc.newmercy.contentservices.web.api.v1.sermon;

import org.hibernate.validator.constraints.NotEmpty;

public class Passage {
    @NotEmpty
    private String start;

    @NotEmpty
    private String end;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Passage{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
