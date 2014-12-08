package cc.newmercy.contentservices.neo4j.jackson;

import cc.newmercy.contentservices.config.jackson.ContentServicesModule;
import cc.newmercy.contentservices.neo4j.json.Columns;
import cc.newmercy.contentservices.neo4j.json.Result;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ColumnsDeserializerTest {

    private static final String JSON;

    static {
        try {
            JSON = FileCopyUtils.copyToString(new InputStreamReader(ColumnsDeserializerTest.class.getResourceAsStream(ColumnsDeserializerTest.class.getSimpleName() + ".json")));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static final List<String> COLUMN_NAMES = Arrays.asList("map", "string", "pojo");

    private static final int STRING_TO_OBJECT_IDX = 0;

    private static final List<Map<String, Object>> EXPECTED_STRING_TO_OBJECTS = ImmutableList.<Map<String, Object>> builder()
            .add(ImmutableMap.<String, Object>builder()
                    .put("id", "map id 1")
                    .put("name", "map name 1")
                    .put("description", "map description 1")
                    .build())
            .add(ImmutableMap.<String, Object> builder()
                    .put("id", "map id 2")
                    .put("name", "map name 2")
                    .put("description", "map description 2")
                    .put("version", 1)
                    .put("imageUrl", "http://2.image.com/2.png")
                    .build())
            .add(ImmutableMap.<String, Object> builder()
                    .put("id", "map id 3")
                    .put("name", "map name 3")
                    .put("description", "map description 3")
                    .put("imageUrl", "http://3.image.com/3.png")
                    .build())
            .build();

    private static final int STRING_IDX = 1;

    private static final List<String> EXPECTED_STRINGS = Arrays.asList("string 1", "string 2", "string 3");

    private static final int POJO_IDX = 2;

    private static final List<String> EXPECTED_POJO_NAMES = Arrays.asList("pojo name 1", "pojo name 2", "pojo name 3");

    private static final List<Integer> EXPECTED_POJO_AGES = Arrays.asList(123, 456, 789);

    private final ObjectMapper jsonMapper = new ObjectMapper()
            .registerModule(new ContentServicesModule())
            .registerModule(new TestModule());

    @Test
    public void testRead() throws IOException {
        TransactionResponse<TestColumns> response = jsonMapper.readValue(JSON, new TypeReference<TransactionResponse<TestColumns>>() { });

        List<Result<TestColumns>> results = response.getResults();

        Result<TestColumns> result = results.get(0);

        assertThat(result.getColumns(), equalTo(COLUMN_NAMES));

        List<TestColumns> rows = Lists.transform(result.getData(), datum -> datum.getRow());

        assertThat(Lists.transform(rows, row -> row.<Map<String, Object>> get(STRING_TO_OBJECT_IDX)), equalTo(EXPECTED_STRING_TO_OBJECTS));
        assertThat(Lists.transform(rows, row -> row.<String> get(STRING_IDX)), equalTo(EXPECTED_STRINGS));
        assertThat(Lists.transform(rows, row -> row.<Pojo> get(POJO_IDX).getName()), equalTo(EXPECTED_POJO_NAMES));
        assertThat(Lists.transform(rows, row -> row.<Pojo> get(POJO_IDX).getAge()), equalTo(EXPECTED_POJO_AGES));

        assertThat(rows.size(), equalTo(EXPECTED_STRING_TO_OBJECTS.size()));
        assertThat(results.size(), equalTo(1));
    }

    public static class StringToObjectType extends TypeReference<Map<String, Object>> { }

    public static class StringType extends TypeReference<String> { }

    public static class Pojo {

        private String name;

        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class PojoType extends TypeReference<Pojo> { }

    @Columns.Types({ StringToObjectType.class, StringType.class, PojoType.class })
    public static class TestColumns extends Columns {
        public TestColumns(List<Object> list) {
            super(list);
        }
    }

    public static class TestModule extends SimpleModule {{
            addDeserializer(TestColumns.class, new ColumnsDeserializer<>(TestColumns.class));
    }}
}
