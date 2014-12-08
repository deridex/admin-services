package cc.newmercy.contentservices.web.id;

import cc.newmercy.contentservices.neo4j.json.Columns;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@Columns.Types(IdBlockColumns.IdBlockType.class)
public class IdBlockColumns extends Columns {

    public IdBlockColumns(List<Object> list) {
        super(list);
    }

    public static class IdBlockType extends TypeReference<IdBlock> { }
}
