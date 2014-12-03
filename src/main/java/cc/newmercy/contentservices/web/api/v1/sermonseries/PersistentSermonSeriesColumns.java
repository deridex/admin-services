package cc.newmercy.contentservices.web.api.v1.sermonseries;

import cc.newmercy.contentservices.neo4j.json.Columns;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@Columns.Types(PersistentSermonSeriesColumns.PersistentSermonSeriesType.class)
public class PersistentSermonSeriesColumns extends Columns {

    public static class PersistentSermonSeriesType extends TypeReference<PersistentSermonSeries> { }

    public PersistentSermonSeriesColumns(List<Object> list) {
        super(list);
    }
}
