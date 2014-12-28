package cc.newmercy.contentservices.web.id;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cc.newmercy.contentservices.neo4j.jackson.EntityReader;
import cc.newmercy.contentservices.neo4j.json.Row;
import cc.newmercy.contentservices.neo4j.json.Statement;
import cc.newmercy.contentservices.neo4j.json.TransactionRequest;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import cc.newmercy.contentservices.repository.RepositoryException;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4jIdService implements IdService, AutoCloseable {

	private static final int NEXT_IDX = 0;

	private static final int MAX_IDX = 1;

	private static final String FETCH_ID_BLOCK_QUERY = "match (n:Sequence) where n.name = { name } set n.next = n.next + n.increment return { startIncl: n.next - n.increment, endExcl: n.next } as ids";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final AtomicReference<ExecutorService> threadPool = new AtomicReference<>();

	private final Map<String, long[]> nameToRange = new HashMap<>();

	private final WebTarget neo4jCommit;

	private final EntityReader entityReader;

	public Neo4jIdService(WebTarget neo4j, EntityReader entityReader) {
		this.neo4jCommit = neo4j.path("commit");
		this.entityReader = Preconditions.checkNotNull(entityReader, "entity reader");
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

			return Long.toString(id, 36);
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

			TransactionResponse txnResponse = entityReader.parse(IdBlock.class).from(response);

			if (!txnResponse.getErrors().isEmpty()) {
				throw new RepositoryException("could not get id block for sequence '" + name + "': "
						+ txnResponse.getErrors().toString());
			}

			/*
			 * One query, one result.
			 */
			List<Row> data = txnResponse.getResults().get(0).getData();

			if (data.isEmpty()) {
				throw new IllegalArgumentException("no such series '" + name + "'");
			}

			/*
			 * One row with one element.
			 */
			IdBlock idBlock = data.get(0).getColumns().get(0);

			long[] range = new long[] { idBlock.getStartIncl(), idBlock.getEndExcl() };

			nameToRange.put(name, range);

			return range;
		}
	}
}
