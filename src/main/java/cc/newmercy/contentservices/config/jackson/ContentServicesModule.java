package cc.newmercy.contentservices.config.jackson;

import cc.newmercy.contentservices.neo4j.jackson.ColumnsDeserializer;
import cc.newmercy.contentservices.web.api.v1.asset.TemporaryAssetColumns;
import cc.newmercy.contentservices.web.api.v1.sermonseries.PersistentSermonSeriesColumns;
import cc.newmercy.contentservices.web.id.IdBlockColumns;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ContentServicesModule extends SimpleModule {{
        addDeserializer(PersistentSermonSeriesColumns.class, new ColumnsDeserializer<>(PersistentSermonSeriesColumns.class));
        addDeserializer(IdBlockColumns.class, new ColumnsDeserializer<>(IdBlockColumns.class));
        addDeserializer(TemporaryAssetColumns.class, new ColumnsDeserializer<>(TemporaryAssetColumns.class));
}}
