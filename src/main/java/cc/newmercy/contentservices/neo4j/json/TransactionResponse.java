package cc.newmercy.contentservices.neo4j.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponse {

	private String commit;

	private List<Result> results;

	private Transaction transaction;

	private List<Error> errors;

	/**
	 * Returns the url to commit or rollback the transaction.
	 */
	public String getCommit() {
		return commit;
	}

	/**
	 * Returns query results. Each passed statement has a corresponding result.
	 */
	public List<Result> getResults() {
		return results;
	}

	/**
	 * Returns transaction details.
	 */
	public Transaction getTransaction() {
		return transaction;
	}

	/**
	 * Returns errors.
	 */
	public List<Error> getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return "TransactionResponse [commit=" + commit + ", results=" + results + ", transaction=" + transaction
				+ ", errors=" + errors + "]";
	}
}
