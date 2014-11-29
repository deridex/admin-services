package cc.newmercy.contentservices.web.id;

import cc.newmercy.contentservices.neo4j.json.*;
import cc.newmercy.contentservices.repository.RepositoryException;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class Neo4jIdService implements IdService, AutoCloseable {

	private static final int NEXT_IDX = 0;

	private static final int MAX_IDX = 1;

	private static final String START_INCL_PROPERTY = "startIncl";

	private static final String END_EXCL_PROPERTY = "endExcl";

	private static final String FETCH_ID_BLOCK_QUERY = "match (n:Sequence) where n.name = { name } set n.next = n.next + n.increment return { startIncl: n.next - n.increment, endExcl: n.next }";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final AtomicReference<ExecutorService> threadPool = new AtomicReference<>();

	private final Map<String, long[]> nameToRange = new HashMap<>();

	private final WebTarget neo4jCommit;

	public Neo4jIdService(WebTarget neo4j) {
		this.neo4jCommit = neo4j.path("commit");
	}

	@PostConstruct
	public void start() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		if (threadPool.compareAndSet(null, executorService)) {
			logger.info("starting");
		} else {
			executorService.shutdown();

			logger.warn("already started");
		}
	}

	@PreDestroy
	@Override
	public void close() {
		ExecutorService executorService = threadPool.get();

		if (executorService != null) {
			if (threadPool.compareAndSet(executorService, null)) {
				executorService.shutdownNow();
			}
		}
	}

	@Override
	public String next(String name) {
		Future<String> future = threadPool.get().submit(new GetNextId(name));

		String id;

		try {
			id = future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		}

		return id;
	}

	private class GetNextId implements Callable<String> {

		private final String name;

		private GetNextId(String name) {
			this.name = name;
		}

		@Override
		public String call() {
			long[] range = nameToRange.get(name);

			if (range == null) {
				range = reset();
			}

			long id = range[NEXT_IDX]++;

			if (id == range[MAX_IDX]) {
				range = reset();

				id = range[NEXT_IDX]++;
			}

			return Base62.INSTANCE.encode(id);
		}

		private long[] reset() {
			Map<String, Object> parameters = Collections.singletonMap("name", name);

			Statement statement = new Statement();
			statement.setStatement(FETCH_ID_BLOCK_QUERY);
			statement.setParameters(parameters);

			TransactionRequest request = new TransactionRequest();
			request.setStatements(Arrays.asList(statement));

			logger.debug("executing query '{}' with parameters {}", FETCH_ID_BLOCK_QUERY, parameters);

			Response response = neo4jCommit.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(request));

			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				Response.StatusType statusInfo = response.getStatusInfo();

				throw new RepositoryException("could not get id block for sequence '" + name + "': "
						+ statusInfo.getStatusCode() + " " + statusInfo.getReasonPhrase());
			}

			TransactionResponse txnResponse = response.readEntity(TransactionResponse.class);

			if (!txnResponse.getErrors().isEmpty()) {
				throw new RepositoryException("could not get id block for sequence '" + name + "': "
						+ txnResponse.getErrors().toString());
			}

			List<Result> results = txnResponse.getResults();

			if (results.isEmpty()) {
				throw new IllegalArgumentException("no such sequence '" + name + "'");
			}

			/*
			 * One query, one result.
			 */
			List<Datum> data = results.get(0).getData();

			if (data.isEmpty()) {
				throw new IllegalArgumentException("no such series '" + name + "'");
			}

			/*
			 * One row with one element.
			 */
			@SuppressWarnings("unchecked")
			Map<String, Number> map = (Map<String, Number>) data.get(0).getRow().get(0);

			long[] range = new long[]{
					map.get(START_INCL_PROPERTY).longValue(),
					map.get(END_EXCL_PROPERTY).longValue()
			};

			nameToRange.put(name, range);

			return range;
		}
	}
}
