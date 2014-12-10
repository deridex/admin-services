package cc.newmercy.contentservices.neo4j.jackson;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.newmercy.contentservices.neo4j.json.Columns;
import cc.newmercy.contentservices.neo4j.json.Result;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JacksonEntityReader implements EntityReader {

    private final ThreadLocal<List<List<Object>>> resultSetTypes = new ThreadLocal<List<List<Object>>>() {
        @Override
        protected List<List<Object>> initialValue() {
            return new ArrayList<>();
        }
    };

    private final ResultDeserializer resultDeserializer = new ResultDeserializer();

    public Parser parse(JavaType... types) {
        reset();

        ParserClass parser = new ParserClass();

        parser.then(types);

        return parser;
    }

    public Parser parse(Class<?>... types) {
        reset();

        ParserClass parser = new ParserClass();

        parser.then(types);

        return parser;
    }

    private void reset() {
        List<List<Object>> newList = new ArrayList<>();

        resultSetTypes.set(newList);
    }

    public JsonDeserializer<Result> getResultDeserializer(JsonDeserializer<Result> defaultDeserializer) {
        resultDeserializer.defaultDeserializer = defaultDeserializer;

        return resultDeserializer;
    }

    public JsonDeserializer<Columns> getColumnsDeserializer() {
        return new ColumnsDeserializer();
    }

    private class ParserClass implements Parser {
        public ParserClass then(JavaType... types) {
            resultSetTypes.get().add(Arrays.asList((Object[]) types));

            return this;
        }

        public ParserClass then(Class<?>... types) {
            resultSetTypes.get().add(Arrays.asList((Object[]) types));

            return this;
        }

        public TransactionResponse from(Response response) {
            return response.readEntity(TransactionResponse.class);
        }
    }

    private class ResultDeserializer extends StdDeserializer<Result> implements ResolvableDeserializer {

        private volatile JsonDeserializer<Result> defaultDeserializer;

        private ResultDeserializer() {
            super(Result.class);
        }

        @Override
        public Result deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            Result result = defaultDeserializer.deserialize(jp, ctxt);

            List<List<Object>> actual = resultSetTypes.get();

            if (actual.size() > 1) {
                resultSetTypes.set(actual.subList(1, actual.size()));
            }

            return result;
        }

        @Override
        public void resolve(DeserializationContext ctx) throws JsonMappingException {
            ((ResolvableDeserializer) defaultDeserializer).resolve(ctx);
        }
    }

    private class ColumnsDeserializer extends StdDeserializer<Columns> {

        private ColumnsDeserializer() {
            super(Columns.class);
        }

        @Override
        public Columns deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
            if (!parser.isExpectedStartArrayToken()) {
                return null;
            }

            List<Object> types = resultSetTypes.get().get(0);

            List<Object> values = new ArrayList<>(types.size());

            JsonToken t;

            for (int i = 0; i < types.size() && (t = parser.nextToken()) != JsonToken.END_ARRAY; i++) {
                JavaType javaType;

                if (types.get(i) instanceof Class) {
                    javaType = ctx.getTypeFactory().constructType((Class<?>) types.get(i));
                } else {
                    javaType = (JavaType) types.get(i);
                }

                Object value;

                if (t == JsonToken.VALUE_NULL) {
                    value = null;
                } else {
                    JsonDeserializer<Object> deserializer = ctx.findRootValueDeserializer(javaType);

                    value = deserializer.deserialize(parser, ctx);
                }

                values.add(value);
            }

            if (parser.nextToken() != JsonToken.END_ARRAY) {
                throw new IllegalArgumentException("parsed " + values.size() + " columns but more remain");
            }

            if (values.size() != types.size()) {
                throw new IllegalArgumentException("expected " + types.size() + " elements but got " + values);
            }

            return new Columns(values);
        }
    }
}
