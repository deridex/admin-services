package cc.newmercy.contentservices.neo4j.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result<COLUMNS> {

    private List<String> columns;

    private List<COLUMNS> data;

    /**
     * @return Column names.
     */
    public List<String> getColumns() {
        return columns;
    }

    /**
     * @return Data mapped to the names returned by {@link #getColumns()}.
     */
    public List<COLUMNS> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Results [columns=" + columns + ", data=" + data + "]";
    }
}
