package cc.newmercy.contentservices.config.jackson;

import cc.newmercy.contentservices.neo4j.jackson.JacksonEntityReader;
import cc.newmercy.contentservices.neo4j.json.Columns;
import cc.newmercy.contentservices.neo4j.json.Result;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ContentServicesModule extends SimpleModule {
    public ContentServicesModule(JacksonEntityReader entityReader) {
        addDeserializer(Columns.class, entityReader.getColumnsDeserializer());

        // http://stackoverflow.com/questions/18313323/how-do-i-call-the-default-deserializer-from-a-custom-deserializer-in-jackson
        setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                if (beanDesc.getBeanClass() == Result.class) {
                    return entityReader.getResultDeserializer((JsonDeserializer<Result>) deserializer);
                }

                return super.modifyDeserializer(config, beanDesc, deserializer);
            }
        });
    }
}
