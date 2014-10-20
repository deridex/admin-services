package cc.newmercy.contentservices.neo4j.json;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class Statement {

	private String statement;

	private Map<String, Object> parameters;

	/**
	 * Returns the cypher query.
	 */
	public String getStatement() {
		return statement;
	}

	public void setStatement(String statementArg) {
		statement = statementArg;
	}

	/**
	 * Returns the named parameters to the cypher query.
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parametersArg) {
		parameters = parametersArg;
	}

	@Override
	public String toString() {
		return "Statement [statement=" + statement + ", parameters=" + parameters + "]";
	}
}
