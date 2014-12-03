package cc.newmercy.contentservices.neo4j.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponse<COLUMNS> {

    private List<Result<COLUMNS>> results;

    private List<Error> errors;

    /**
     * Returns query results. Each passed statement has a corresponding result.
     */
    public List<Result<COLUMNS>> getResults() {
        return results;
    }

    /**
     * Returns errors.
     */
    public List<Error> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "TransactionResponse [results=" + results + ", errors=" + errors + "]";
    }
}
