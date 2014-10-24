package cc.newmercy.contentservices.neo4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.newmercy.contentservices.neo4j.json.Datum;
import cc.newmercy.contentservices.neo4j.json.Statement;
import cc.newmercy.contentservices.neo4j.json.TransactionRequest;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import cc.newmercy.contentservices.repository.RepositoryException;
import cc.newmercy.contentservices.web.api.exceptions.NotFoundException;

public class Neo4jRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private WebTarget neo4j;

	private Neo4jTransaction neo4jTransaction;

	protected Neo4jRepository(WebTarget neo4j, Neo4jTransaction neo4jTransaction) {
		this.neo4j = Objects.requireNonNull(neo4j, "neo4j web target");
		this.neo4jTransaction = Objects.requireNonNull(neo4jTransaction, "neo4j transaction");
	}

	protected final TransactionResponse post(String cyperQuery, Map<String, Object> parameters) {
		Statement statement = new Statement();
		statement.setStatement(cyperQuery);
		statement.setParameters(parameters);

		TransactionRequest request = new TransactionRequest();
		request.setStatements(Arrays.asList(statement));

		logger.debug("executing query '{}' with parameters {}", cyperQuery, parameters);

		Response response = neo4j.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(request));

		if (response.getStatus() != Status.CREATED.getStatusCode()) {
			StatusType statusInfo = response.getStatusInfo();

			throw new RepositoryException(statusInfo.getStatusCode() + " " + statusInfo.getReasonPhrase());
		}

		neo4jTransaction.setTransactionUrl(response.getHeaderString("Location"));

		TransactionResponse txnResponse = response.readEntity(TransactionResponse.class);

		if (!txnResponse.getErrors().isEmpty()) {
			throw new RepositoryException(txnResponse.getErrors().toString());
		}

		return txnResponse;
	}

	protected final <T> T post(String cyperQuery, Map<String, Object> parameters, Function<Datum, T> mapper) {
		TransactionResponse response = post(cyperQuery, parameters);

		List<Datum> data = response.getResults().get(0).getData();

		if (data.isEmpty()) {
			throw new NotFoundException();
		}

		return mapper.apply(data.get(0));
	}
}
