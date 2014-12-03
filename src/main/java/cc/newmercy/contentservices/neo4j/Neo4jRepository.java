package cc.newmercy.contentservices.neo4j;

import cc.newmercy.contentservices.neo4j.json.*;
import cc.newmercy.contentservices.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Neo4jRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private WebTarget neo4j;

    private Neo4jTransaction neo4jTransaction;

    protected Neo4jRepository(WebTarget neo4j, Neo4jTransaction neo4jTransaction) {
        this.neo4j = Objects.requireNonNull(neo4j, "neo4j web target");
        this.neo4jTransaction = Objects.requireNonNull(neo4jTransaction, "neo4j transaction");
    }

    protected final <COLUMNS> TransactionResponse<COLUMNS> post(
            String cypherQuery,
            Map<String, Object> parameters,
            GenericType<TransactionResponse<COLUMNS>> type) {
        Statement statement = new Statement();
        statement.setStatement(cypherQuery);
        statement.setParameters(parameters);

        TransactionRequest request = new TransactionRequest();
        request.setStatements(Arrays.asList(statement));

        logger.debug("executing query '{}' with parameters {}", cypherQuery, parameters);

        Response response = neo4j.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(request));

        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            StatusType statusInfo = response.getStatusInfo();

            throw new RepositoryException(statusInfo.getStatusCode() + " " + statusInfo.getReasonPhrase());
        }

        neo4jTransaction.setTransactionUrl(response.getHeaderString("Location"));

        TransactionResponse<COLUMNS> txnResponse = response.readEntity(type);

        if (!txnResponse.getErrors().isEmpty()) {
            throw new RepositoryException(txnResponse.getErrors().toString());
        }

        return txnResponse;
    }

    protected final <COLUMNS> COLUMNS postForOne(
            String cypherQuery,
            Map<String, Object> parameters,
            GenericType<TransactionResponse<COLUMNS>> type) {
        TransactionResponse<COLUMNS> response = post(cypherQuery, parameters, type);

        List<Result<COLUMNS>> results = response.getResults();

        if (results.size() != 1) {
            throw new IllegalArgumentException("expected 1 result but got " + results.size());
        }

        List<Row<COLUMNS>> rows = results.get(0).getData();

        if (rows.size() != 1) {
            throw new IllegalArgumentException("expected 1 row but got " + rows.size());
        }

        COLUMNS columns = rows.get(0).getRow();

        return columns;
    }
}
