package cc.newmercy.contentservices.web.admin;

import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.client.WebTarget;

import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.neo4j.json.Row;
import cc.newmercy.contentservices.web.api.v1.sermon.Neo4jSermonRepository;
import cc.newmercy.contentservices.web.api.v1.sermonseries.Neo4jSermonSeriesRepository;
import cc.newmercy.contentservices.web.id.IdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

public class Neo4jSermonSeriesInfoRepository extends Neo4jRepository implements SermonSeriesInfoRepository {

    private static final String SKIP_PARAM = "skip";

    private static final String PAGE_SIZE_PARAM = "pageSize";

    private static final String LIST_QUERY = String.format("match (ss:%s) optional match (ss)-[]->(s:%s) return { id: ss.id, name: ss.name, createdAt: ss.createdAt, sermonCount: count(s) } order by ss.createdAt desc skip { %s } limit { %s }",
            Neo4jSermonSeriesRepository.SERMON_SERIES_LABEL,
            Neo4jSermonRepository.SERMON_LABEL,
            SKIP_PARAM,
            PAGE_SIZE_PARAM);

    public Neo4jSermonSeriesInfoRepository(WebTarget neo4j, Neo4jTransaction neo4jTransaction, IdService idService, ObjectMapper jsonMapper, EntityReader entityReader) {
        super(neo4j, neo4jTransaction, idService, jsonMapper, entityReader);
    }

    @Override
    public List<SermonSeriesInfo> list(int page, int pageSize) {
        Preconditions.checkArgument(page > 0, "page must be positive");
        Preconditions.checkArgument(pageSize > 0, "page size must be positive");

        List<Row> rows = post(query(LIST_QUERY, SermonSeriesInfo.class)
                .set(SKIP_PARAM, (page - 1) * pageSize)
                .set(PAGE_SIZE_PARAM, pageSize))
                .get(0)
                .getData();

        return rows.stream()
                .map(datum -> datum.getColumns().<SermonSeriesInfo> get(0))
                .collect(Collectors.toList());
    }
}
