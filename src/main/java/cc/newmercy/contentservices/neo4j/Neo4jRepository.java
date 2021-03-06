package cc.newmercy.contentservices.neo4j;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.neo4j.json.Result;
import cc.newmercy.contentservices.neo4j.json.Statement;
import cc.newmercy.contentservices.neo4j.json.TransactionRequest;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import cc.newmercy.contentservices.repository.RepositoryException;
import cc.newmercy.contentservices.util.Arguments;
import cc.newmercy.contentservices.web.id.IdService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4jRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final IdService idService;

    private final RequestExecutor requestExecutor;

    private final EntityReader entityReader;

    private final ObjectMapper jsonMapper;

    protected Neo4jRepository(
            RequestExecutor requestExecutor,
            IdService idService,
            ObjectMapper jsonMapper,
            EntityReader entityReader) {
        this.requestExecutor = Objects.requireNonNull(requestExecutor, "request executor");
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

        Response response = executePost(queries);

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

    private Response executePost(Query... queries) {
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

        return requestExecutor.post(MediaType.APPLICATION_JSON_TYPE, Entity.json(request));
    }

    protected final <T> T postForOne(Query... queries) {
        return post(queries).get(0).getData().get(0).getColumns().get(0);
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

    protected final EntityReader getEntityReader() {
        return entityReader;
    }

    protected class Query {

        private final String cypher;

        private final Map<String, Object> parameters = new HashMap<>();

        private final Function<TypeFactory, JavaType[]> types;

        private Query(String cypher, Function<TypeFactory, JavaType[]> types) {
            this.cypher = cypher;
            this.types = types;
        }

        public Query set(String parameter, String argument) {
            return set(parameter, (Object) argument);
        }

        public Query set(String parameter, Integer argument) {
            return set(parameter, (Object) argument);
        }

        public Query set(String parameter, Long argument) {
            return set(parameter, (Object) argument);
        }

        public Query set(String parameter, Double argument) {
            return set(parameter, (Object) argument);
        }

        public Query set(String parameter, Instant argument) {
            return setJsonString(parameter, argument);
        }

        public Query set(String parameter, LocalDate argument) {
            return setJsonString(parameter, argument);
        }

        private Query setJsonString(String parameter, Object argument) {
            if (argument == null) {
                return set(parameter, (Object) null);
            }

            String json;

            try {
                json = jsonMapper.writeValueAsString(argument);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException(e);
            }

            /*
             * Throw away the quotes.
             */
            return set(parameter, json.substring(1, json.length() - 1));
        }

        public Query setStrings(String parameter, List<String> argument) {
            return set(parameter, argument);
        }

        private Query set(String parameter, Object argument) {
            if (parameters.containsKey(parameter)) {
                throw new IllegalArgumentException("duplicate parameter '" + parameter + "'");
            }

            parameters.put(parameter, argument);

            return this;
        }
    }
}
