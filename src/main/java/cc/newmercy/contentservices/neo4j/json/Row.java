package cc.newmercy.contentservices.neo4j.json;

public class Row<COLUMNS> {

    private COLUMNS row;

    public COLUMNS getRow() {
        return row;
    }

    public void setRow(COLUMNS row) {
        this.row = row;
    }

    @Override
    public String toString() {
        return "Row{" +
                "row=" + row +
                '}';
    }
}
