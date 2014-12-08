package cc.newmercy.contentservices.web.api.v1.asset;

import cc.newmercy.contentservices.neo4j.json.Columns;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@Columns.Types(TemporaryAssetColumns.TemporaryAssetType.class)
public class TemporaryAssetColumns extends Columns {

    public TemporaryAssetColumns(List<Object> list) {
        super(list);
    }

    public static class TemporaryAssetType extends TypeReference<TemporaryAsset> { }
}
