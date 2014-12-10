package cc.newmercy.contentservices.neo4j;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.neo4j.json.Result;
import cc.newmercy.contentservices.neo4j.json.Statement;
import cc.newmercy.contentservices.neo4j.json.TransactionRequest;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import cc.newmercy.contentservices.repository.RepositoryException;
import cc.newmercy.contentservices.util.Arguments;
import cc.newmercy.contentservices.web.id.IdService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4jRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final IdService idService;

    private WebTarget neo4j;

    private Neo4jTransaction neo4jTransaction;

    private EntityReader entityReader;

    private ObjectMapper jsonMapper;

    protected Neo4jRepository(
            WebTarget neo4j,
            Neo4jTransaction neo4jTransaction,
            IdService idService,
            ObjectMapper jsonMapper,
            EntityReader entityReader) {
        this.neo4j = Objects.requireNonNull(neo4j, "neo4j web target");
        this.neo4jTransaction = Objects.requireNonNull(neo4jTransaction, "neo4j transaction");
        this.idService = Objects.requireNonNull(idService, "id service");
        this.jsonMapper = Objects.requireNonNull(jsonMapper, "json mapper");
        this.entityReader = Objects.requireNonNull(entityReader, "entity reader");
    }

    protected final String nextId(String sequenceName) {
        return idService.next(sequenceName);
    }

    protected final List<Result> post(Query... queries) {
        Objects.requireNonNull(queries, "queries");
        Preconditions.checkArgument(queries.length > 0, "no queries");

        List<Statement> statements = Arrays.asList(queries).stream()
                .map(query -> {
                    Statement statement = new Statement();

                    statement.setStatement(query.cypher);
                    statement.setParameters(query.parameters);

                    return statement;
                })
                .collect(Collectors.toList());

        TransactionRequest request = new TransactionRequest();

        request.setStatements(statements);

        logger.debug("executing request {}", request);

        Response response = neo4j.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(request));

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
            Response.StatusType statusInfo = response.getStatusInfo();

            throw new RepositoryException(statusInfo.getStatusCode() + " " + statusInfo.getReasonPhrase());
        }

        neo4jTransaction.setTransactionUrl(response.getHeaderString("Location"));

        EntityReader.Parser parser = entityReader.parse(queries[0].types.apply(jsonMapper.getTypeFactory()));

        for (int i = 1; i < queries.length; i++) {
            parser.then(queries[i].types.apply(jsonMapper.getTypeFactory()));
        }

        TransactionResponse txnResponse = parser.from(response);

        if (!txnResponse.getErrors().isEmpty()) {
            throw new RepositoryException(txnResponse.getErrors().toString());
        }

        return txnResponse.getResults();
    }

    protected final <T> T postForOne(Query... queries) {
        return post(queries).get(0).getData().get(0).getRow().get(0);
    }

    protected final Query query(String cypher, JavaType... types) {
        Arguments.requireNotEmpty(cypher, "cypher");
        Objects.requireNonNull(types, "types");
        Preconditions.checkArgument(types.length > 0, "no types");
        Preconditions.checkArgument(!Arrays.asList(types).contains(null), "null type");

        return new Query(cypher, typesFactory -> types);
    }

    protected final Query query(String cypher, Class<?>... types) {
        Arguments.requireNotEmpty(cypher, "cypher");
        Objects.requireNonNull(types, "types");
        Preconditions.checkArgument(types.length > 0, "no types");

        return new Query(cypher, typeFactory ->
                Arrays.asList(types)
                        .stream()
                        .map(type -> typeFactory.constructType(type))
                        .collect(Collectors.toList())
                        .toArray(new JavaType[types.length]));
    }

    protected class Query {

        private final String cypher;

        private final Map<String, Object> parameters = new HashMap<>();

        private final Function<TypeFactory, JavaType[]> types;

        private Query(String cypher, Function<TypeFactory, JavaType[]> types) {
            this.cypher = cypher;
            this.types = types;
        }

        public Query set(String parameter, Object argument) {
            if (parameters.put(parameter, argument) != null) {
                throw new IllegalArgumentException("duplicate parameter '" + parameter + "'");
            }

            return this;
        }
    }
}
