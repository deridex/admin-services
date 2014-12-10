package cc.newmercy.contentservices.neo4j.json;

public class Row {

    private Columns row;

    public Columns getRow() {
        return row;
    }

    public void setRow(Columns row) {
        this.row = row;
    }

    @Override
    public String toString() {
        return "Row{" +
                "row=" + row +
                '}';
    }
}
