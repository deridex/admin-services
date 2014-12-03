package cc.newmercy.contentservices.neo4j.jackson;

import cc.newmercy.contentservices.neo4j.json.Columns;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ColumnsDeserializer<COLUMNS extends Columns> extends StdDeserializer<COLUMNS> {

    private final TypeReference<?>[] types;

    private final Constructor<COLUMNS> ctor;

    public ColumnsDeserializer(Class<COLUMNS> columnsClass) {
        super(columnsClass);

        try {
            ctor = columnsClass.getConstructor(List.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(columnsClass.toString(), e);
        }

        Columns.Types types = columnsClass.getAnnotation(Columns.Types.class);

        if (types == null) {
            throw new NullPointerException(Columns.Types.class.toString());
        }

        this.types = new TypeReference<?>[types.value().length];

        for (int i = 0; i < types.value().length; i++) {
            try {
                this.types[i] = types.value()[i].newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException(types.value()[i].toString(), e);
            }
        }
    }

    @Override
    public COLUMNS deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        if (!parser.isExpectedStartArrayToken()) {
            return null;
        }

        List<Object> values = new ArrayList<>(types.length);

        JsonToken t;

        for (int i = 0; i < types.length && (t = parser.nextToken()) != JsonToken.END_ARRAY; i++) {
            JavaType javaType = ctx.getTypeFactory().constructType(types[i]);

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

        if (values.size() != types.length) {
            throw new IllegalArgumentException("expected " + types.length + " elements but got " + values);
        }

        COLUMNS columns;

        try {
            columns = ctor.newInstance(values);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }

        return columns;
    }
}
