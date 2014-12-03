package cc.newmercy.contentservices.config.jackson;

import cc.newmercy.contentservices.neo4j.jackson.ColumnsDeserializer;
import cc.newmercy.contentservices.web.api.v1.sermonseries.PersistentSermonSeriesColumns;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ContentServicesModule extends SimpleModule {{
        addDeserializer(PersistentSermonSeriesColumns.class, new ColumnsDeserializer<>(PersistentSermonSeriesColumns.class));
}}
