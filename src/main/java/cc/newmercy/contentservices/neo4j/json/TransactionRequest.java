package cc.newmercy.contentservices.neo4j.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class TransactionRequest {

	private List<Statement> statements;

	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statementsArg) {
		statements = statementsArg;
	}

	@Override
	public String toString() {
		return "TransactionRequest [statements=" + statements + "]";
	}
}
