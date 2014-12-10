package cc.newmercy.contentservices.web.api.v1.sermon;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class Passage {
    @NotEmpty
    private String book;

    @NotNull
    @Valid
    private Verse start;

    @NotNull
    @Valid
    private Verse end;

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public Verse getStart() {
        return start;
    }

    public void setStart(Verse start) {
        this.start = start;
    }

    public Verse getEnd() {
        return end;
    }

    public void setEnd(Verse end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Passage{" +
                "book='" + book + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
