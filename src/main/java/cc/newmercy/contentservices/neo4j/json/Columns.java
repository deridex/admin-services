package cc.newmercy.contentservices.neo4j.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

public abstract class Columns {

    private final List columns;

    public Columns(List<Object> list) {
        this.columns = Preconditions.checkNotNull(list);
    }

    public <T> T get(int index) {
        return (T) columns.get(index);
    }

    public int size() {
        return columns.size();
    }

    @Override
    public String toString() {
        return "Columns{" +
                "columns=" + columns +
                '}';
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Types {
        Class<? extends TypeReference<?>>[] value();
    }
}
