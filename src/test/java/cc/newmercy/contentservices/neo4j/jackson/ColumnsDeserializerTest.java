package cc.newmercy.contentservices.neo4j.jackson;

import cc.newmercy.contentservices.neo4j.json.Columns;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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

    private static final List<Map<String, Double>> EXPECTED_STRING_TO_DOUBLES = ImmutableList.<Map<String, Double>> builder()
            .add(ImmutableMap.<String, Double>builder()
                    .put("one point two", 1.2)
                    .put("two point three", 2.3)
                    .build())
            .add(ImmutableMap.<String, Double> builder()
                    .put("three point four", 3.4)
                    .put("five point six", 5.6)
                    .build())
            .build();

    private static final String EXPECTED_OUTER_CONTAINER_NAME = "outer container name";

    private static final String EXPECTED_INNER_CONTAINER_NAME = "inner container name";

    private static final List<String> EXPECTED_STRINGS = Arrays.asList("string 1", "string 2");

    private static final List<String> EXPECTED_POJO_NAMES = Arrays.asList("pojo name 1", "pojo name 2");

    private static final List<Integer> EXPECTED_POJO_AGES = Arrays.asList(123, 456);

    private final ObjectMapper jsonMapper = new ObjectMapper().registerModule(new TestModule());

    @Test
    public void testRead() throws IOException {
        OuterContainer<TestColumns> actualOuterContainer = jsonMapper.readValue(JSON, new TypeReference<OuterContainer<TestColumns>>() { });

        assertThat(actualOuterContainer.getName(), equalTo(EXPECTED_OUTER_CONTAINER_NAME));

        InnerContainer<TestColumns> actualInnerContainer = actualOuterContainer.getInnerContainer();

        assertThat(actualInnerContainer.getName(), equalTo(EXPECTED_INNER_CONTAINER_NAME));

        List<TestColumns> actualList = actualInnerContainer.getColumnsList();

        assertThat(actualList.size(), equalTo(2));

        TestColumns actualTestColumns = actualList.get(0);

        assertThat(actualTestColumns.get(0), equalTo(EXPECTED_STRING_TO_DOUBLES.get(0)));

        assertThat(actualTestColumns.get(1), equalTo(EXPECTED_STRINGS.get(0)));

        Pojo actualPojo = actualTestColumns.get(2);

        assertThat(actualPojo.getName(), equalTo(EXPECTED_POJO_NAMES.get(0)));
        assertThat(actualPojo.getAge(), equalTo(EXPECTED_POJO_AGES.get(0)));

        actualTestColumns = actualList.get(1);

        assertThat(actualTestColumns.get(0), equalTo(EXPECTED_STRING_TO_DOUBLES.get(1)));

        assertThat(actualTestColumns.get(1), equalTo(EXPECTED_STRINGS.get(1)));

        actualPojo = actualTestColumns.get(2);

        assertThat(actualPojo.getName(), equalTo(EXPECTED_POJO_NAMES.get(1)));
        assertThat(actualPojo.getAge(), equalTo(EXPECTED_POJO_AGES.get(1)));
    }

    public static class StringToDoubleType extends TypeReference<Map<String, Double>> { }

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

    @Columns.Types({ StringToDoubleType.class, StringType.class, PojoType.class })
    public static class TestColumns extends Columns {

        public static final StringToDoubleType STRING_TO_DOUBLE_TYPE = new StringToDoubleType();

        public static final StringType STRING_TYPE = new StringType();

        public static final PojoType POJO_TYPE = new PojoType();

        public TestColumns(List<Object> list) {
            super(list);
        }
    }

    public static class InnerContainer<COLUMNS> {

        private String name;

        private List<COLUMNS> columnsList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<COLUMNS> getColumnsList() {
            return columnsList;
        }

        public void setColumnsList(List<COLUMNS> columnsList) {
            this.columnsList = columnsList;
        }
    }

    public static class OuterContainer<COLUMNS> {

        private String name;

        public InnerContainer<COLUMNS> getInnerContainer() {
            return innerContainer;
        }

        public void setInnerContainer(InnerContainer<COLUMNS> innerContainer) {
            this.innerContainer = innerContainer;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private InnerContainer<COLUMNS> innerContainer;
    }

    public static class TestModule extends SimpleModule {{
            addDeserializer(TestColumns.class, new ColumnsDeserializer<>(TestColumns.class));
    }}
}
