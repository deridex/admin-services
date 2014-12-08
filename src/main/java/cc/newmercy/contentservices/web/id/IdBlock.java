package cc.newmercy.contentservices.web.id;

public class IdBlock {

    private long startIncl;

    private long endExcl;

    public long getStartIncl() {
        return startIncl;
    }

    public void setStartIncl(long startIncl) {
        this.startIncl = startIncl;
    }

    public long getEndExcl() {
        return endExcl;
    }

    public void setEndExcl(long endExcl) {
        this.endExcl = endExcl;
    }

    @Override
    public String toString() {
        return "IdBlock{" +
                "startIncl=" + startIncl +
                ", endExcl=" + endExcl +
                '}';
    }
}
