package cc.newmercy.contentservices.neo4j.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    private List<String> columns;

    private List<Row> data;

    /**
     * @return Column names.
     */
    public List<String> getColumns() {
        return columns;
    }

    /**
     * @return Data mapped to the names returned by {@link #getColumns()}.
     */
    public List<Row> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Results [columns=" + columns + ", data=" + data + "]";
    }
}
