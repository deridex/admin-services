package cc.newmercy.contentservices.neo4j.jackson;

import static cc.newmercy.contentservices.test.http.JettyResource.isGet;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cc.newmercy.contentservices.config.jackson.ContentServicesModule;
import cc.newmercy.contentservices.jaxrs.JacksonContextResolver;
import cc.newmercy.contentservices.neo4j.json.Result;
import cc.newmercy.contentservices.neo4j.json.Row;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import cc.newmercy.contentservices.test.http.JettyResource;
import cc.newmercy.contentservices.test.util.ClasspathResources;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class JacksonEntityReaderTest {

    private static final String JSON = ClasspathResources.loadClassPathDocument(JacksonEntityReaderTest.class, ".json");

    private static final List<String> COLUMN_NAMES_1 = Arrays.asList("string", "pojo", "map");

    private static final int STRING_IDX_1= 0;

    private static final List<String> EXPECTED_STRINGS_1 = Arrays.asList("string 1", "string 2", "string 3");

    private static final int POJO_IDX_1 = 1;

    private static final List<String> EXPECTED_POJO_NAMES_1 = Arrays.asList("pojo name 1", "pojo name 2", "pojo name 3");

    private static final List<Integer> EXPECTED_POJO_AGES_1 = Arrays.asList(123, 456, 789);

    private static final int STRING_TO_OBJECT_IDX_1 = 2;

    private static final List<Map<String, Object>> EXPECTED_STRING_TO_OBJECTS_1 = ImmutableList.<Map<String, Object>> builder()
            .add(ImmutableMap.<String, Object> builder()
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

    private static final List<String> COLUMN_NAMES_2 = Arrays.asList("map", "string", "pojo");

    private static final int STRING_TO_OBJECT_IDX_2 = 0;

    private static final List<Map<String, Object>> EXPECTED_STRING_TO_OBJECTS_2 = ImmutableList.<Map<String, Object>> builder()
            .add(ImmutableMap.<String, Object> builder()
                    .put("id", "map id 4")
                    .put("name", "map name 4")
                    .put("description", "map description 4")
                    .build())
            .add(ImmutableMap.<String, Object> builder()
                    .put("id", "map id 5")
                    .put("name", "map name 5")
                    .put("description", "map description 5")
                    .put("version", 2)
                    .put("imageUrl", "http://5.image.com/5.png")
                    .build())
            .build();

    private static final int STRING_IDX_2 = 1;

    private static final List<String> EXPECTED_STRINGS_2 = Arrays.asList("string 4", "string 5");

    private static final int POJO_IDX_2 = 2;

    private static final List<String> EXPECTED_POJO_NAMES_2 = Arrays.asList("pojo name 4", "pojo name 5");

    private static final List<Integer> EXPECTED_POJO_AGES_2 = Arrays.asList(12, 34);

    @Rule
    public final JettyResource jetty = new JettyResource();

    private ObjectMapper jsonMapper;

    private Client client;

    private WebTarget webTarget;

    private JacksonEntityReader reader;

    @Before
    public void before() {
        reader = new JacksonEntityReader();

        jsonMapper = new ObjectMapper().registerModule(new ContentServicesModule(reader));

        client = ClientBuilder.newBuilder()
                .register(new JacksonContextResolver(jsonMapper))
                .build();

        webTarget = client.target("http://127.0.0.1:" + jetty.getPort());
    }

    @After
    public void after() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void test() {
        jetty.expect(isGet()).then(response -> {
            response.setContentType(MediaType.APPLICATION_JSON);
            response.getOutputStream().print(JSON);
        });

        TransactionResponse transactionResponse = reader.parse(
                jsonMapper.constructType(String.class),
                jsonMapper.constructType(Pojo.class),
                jsonMapper.getTypeFactory().constructType(new TypeReference<Map<String, Object>>() {
                }))
                .then(Map.class, String.class, Pojo.class)
                .from(webTarget.request().get());

        List<Result> results = transactionResponse.getResults();

        Result result = results.get(0);

        assertThat(result.getColumns(), equalTo(COLUMN_NAMES_1));

        List<Row> rows = result.getData();

        assertThat(Lists.transform(rows, row -> row.getColumns().<String> get(STRING_IDX_1)), equalTo(EXPECTED_STRINGS_1));
        assertThat(Lists.transform(rows, row -> row.getColumns().<Map<String, Object>>get(STRING_TO_OBJECT_IDX_1)), equalTo(EXPECTED_STRING_TO_OBJECTS_1));
        assertThat(Lists.transform(rows, row -> row.getColumns().<Pojo> get(POJO_IDX_1).getName()), equalTo(EXPECTED_POJO_NAMES_1));
        assertThat(Lists.transform(rows, row -> row.getColumns().<Pojo> get(POJO_IDX_1).getAge()), equalTo(EXPECTED_POJO_AGES_1));

        result = results.get(1);

        assertThat(result.getColumns(), equalTo(COLUMN_NAMES_2));

        rows = result.getData();

        assertThat(Lists.transform(rows, row -> row.getColumns().<Map<String, Object>>get(STRING_TO_OBJECT_IDX_2)), equalTo(EXPECTED_STRING_TO_OBJECTS_2));
        assertThat(Lists.transform(rows, row -> row.getColumns().<String> get(STRING_IDX_2)), equalTo(EXPECTED_STRINGS_2));
        assertThat(Lists.transform(rows, row -> row.getColumns().<Pojo> get(POJO_IDX_2).getName()), equalTo(EXPECTED_POJO_NAMES_2));
        assertThat(Lists.transform(rows, row -> row.getColumns().<Pojo> get(POJO_IDX_2).getAge()), equalTo(EXPECTED_POJO_AGES_2));

        assertThat(results.size(), equalTo(2));
    }

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
}
