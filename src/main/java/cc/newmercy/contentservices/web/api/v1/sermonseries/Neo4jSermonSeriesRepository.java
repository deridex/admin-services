package cc.newmercy.contentservices.web.api.v1.sermonseries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.client.WebTarget;

import jersey.repackaged.com.google.common.base.Preconditions;
import cc.newmercy.contentservices.neo4j.Neo4jRepository;
import cc.newmercy.contentservices.neo4j.Neo4jTransaction;
import cc.newmercy.contentservices.neo4j.Nodes;
import cc.newmercy.contentservices.neo4j.json.Datum;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;

public class Neo4jSermonSeriesRepository extends Neo4jRepository implements SermonSeriesRepository {

	private static final String IMAGE_URL_PROPERTY = "imageUrl";

	private static final String DESCRIPTION_PROPERTY = "description";

	private static final String NAME_PROPERTY = "name";

	private static final String ID_PROPERTY = "id";

	private static Function<Datum, PersistentSermonSeries> MAP_SERMON_SERIES = (datum) -> {
		@SuppressWarnings("unchecked")
		Map<String, Object> propertMap = (Map<String, Object>) datum.getRow().get(0);

		PersistentSermonSeries sermonSeries = new PersistentSermonSeries();
		sermonSeries.setId((String) propertMap.get(ID_PROPERTY));
		sermonSeries.setName((String) propertMap.get(NAME_PROPERTY));
		sermonSeries.setDescription((String) propertMap.get(DESCRIPTION_PROPERTY));
		sermonSeries.setImageUrl((String) propertMap.get(IMAGE_URL_PROPERTY));

		return sermonSeries;
	};

	private static final String SERMON_SERIES_LABEL = "SermonSeries";

	private static final String SKIP_PARAM = "skip";

	private static final String PAGE_SIZE_PARAM = "pageSize";

	private static final String LIST_QUERY = String.format("match (n:%s) return n order by n.created_at skip { %s } limit { %s }",
			SERMON_SERIES_LABEL,
			SKIP_PARAM,
			PAGE_SIZE_PARAM);

	private static final String SAVE_QUERY = Nodes.createNodeQuery(
			SERMON_SERIES_LABEL,
			ID_PROPERTY,
			NAME_PROPERTY,
			DESCRIPTION_PROPERTY,
			IMAGE_URL_PROPERTY);

	public Neo4jSermonSeriesRepository(WebTarget neo4j, Neo4jTransaction neo4jTransaction) {
		super(neo4j, neo4jTransaction);
	}

	@Override
	public List<PersistentSermonSeries> list(int page, int pageSize) {
		Preconditions.checkArgument(page > 0, "page must be positive");
		Preconditions.checkArgument(pageSize > 0, "page size must be positive");

		Map<String, Object> parameters = new HashMap<>(2, 1);
		parameters.put(SKIP_PARAM, (page - 1) * pageSize);
		parameters.put(PAGE_SIZE_PARAM, pageSize);

		TransactionResponse response = post(LIST_QUERY, parameters);

		List<PersistentSermonSeries> sermonSeriesList = response.getResults().get(0).getData().stream()
				.map(MAP_SERMON_SERIES)
				.collect(Collectors.toList());

		return sermonSeriesList;
	}

	@Override
	public PersistentSermonSeries save(TransientSermonSeries transientSeries) {
		Map<String, Object> parameters = new HashMap<>(4, 1);
		parameters.put(ID_PROPERTY, "test");
		parameters.put(NAME_PROPERTY, transientSeries.getName());
		parameters.put(DESCRIPTION_PROPERTY, transientSeries.getDescription());
		parameters.put(IMAGE_URL_PROPERTY, transientSeries.getImageUrl());

		return post(SAVE_QUERY, parameters, MAP_SERMON_SERIES);
	}
}
