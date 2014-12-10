package cc.newmercy.contentservices.neo4j.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Row {

    private Columns row;

    public Columns getColumns() {
        return row;
    }

    public void setColumns(Columns row) {
        this.row = row;
    }

    @Override
    public String toString() {
        return "Row{" +
                "row=" + row +
                '}';
    }
}
