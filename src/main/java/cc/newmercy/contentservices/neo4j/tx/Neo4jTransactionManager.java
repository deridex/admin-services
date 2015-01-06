package cc.newmercy.contentservices.neo4j.tx;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cc.newmercy.contentservices.neo4j.RequestExecutor;
import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import cc.newmercy.contentservices.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class Neo4jTransactionManager extends AbstractPlatformTransactionManager {

	private static final long serialVersionUID = 3791930530325180706L;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Neo4jTransactionRequestExecutor requestExecutor = new Neo4jTransactionRequestExecutor();

	private final Client client;

	private final WebTarget neo4j;

	private ThreadLocal<LinkedList<WebTarget>> txns = new ThreadLocal<LinkedList<WebTarget>>() {
		@Override
		protected LinkedList<WebTarget> initialValue() {
			return new LinkedList<>();
		}
	};

	public Neo4jTransactionManager(Client client, String neo4jTxnRoot) {
		this.client = Objects.requireNonNull(client, "client");
		this.neo4j = client.target(neo4jTxnRoot);
	}

	@Override
	protected Object doGetTransaction() throws TransactionException {
		return requestExecutor;
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
		if (!txns.get().isEmpty()) {
			logger.warn("stale transactions detected: {}", txns.get());
		}

		logger.debug("beginning transaction");

		txns.get().clear();
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		if (txns.get().isEmpty()) {
			logger.debug("nothing to commit");
		} else {
			logger.debug("committing {}", txns.get().getFirst().getUri());

			WebTarget txn = txns.get().getFirst();

			try {
				TransactionResponse response = txn.path("commit").request()
						.accept(MediaType.APPLICATION_JSON)
						.post(null, TransactionResponse.class);

				if (!response.getErrors().isEmpty()) {
					throw new TransactionSystemException(response.getErrors().toString());
				}
			} finally {
				txns.get().clear();
			}
		}
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
		if (txns.get().isEmpty()) {
			logger.warn("cannot rollback without transaction");
		} else {
			for (Iterator<WebTarget> iterator = txns.get().iterator(); iterator.hasNext(); ) {
				WebTarget txn = iterator.next();

				logger.debug("rolling back {}", txn.getUri());

				try {
					TransactionResponse response = txn.request()
							.accept(MediaType.APPLICATION_JSON)
							.delete(TransactionResponse.class);

					if (!response.getErrors().isEmpty()) {
						logger.warn("detected errors rolling back transaction: {}", response.getErrors().toString());
					}
				} catch (RuntimeException e) {
					logger.warn("trapped exception rolling back transaction", e);
				} finally {
					iterator.remove();
				}
			}
		}
	}

	public RequestExecutor getRequestExecutor() {
		return requestExecutor;
	}

	private class Neo4jTransactionRequestExecutor implements RequestExecutor {
		@Override
		public Response post(MediaType mediaType, Entity<?> entity) {
			WebTarget service;

			if (txns.get().isEmpty()) {
				service = neo4j;
			} else {
				service = txns.get().getFirst();
			}

			Response response = service.request(mediaType).post(entity);

			Response.Status expectedStatus;

			if (txns.get().isEmpty()) {
				expectedStatus = Response.Status.CREATED;
			} else {
				expectedStatus = Response.Status.OK;
			}

			String txnUrl = response.getHeaderString("Location");

			if (txnUrl != null && !txnUrl.isEmpty()) {
				setTransactionUrl(txnUrl);
			}

			if (response.getStatus() != expectedStatus.getStatusCode()) {
				Response.StatusType statusInfo = response.getStatusInfo();

				throw new RepositoryException(statusInfo.getStatusCode() + " " + statusInfo.getReasonPhrase());
			}

			return response;
		}

		private void setTransactionUrl(String url) {
			URI uri;

			try {
				uri = new URI(url);
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException(url, e);
			}

			LinkedList<WebTarget> currentUrls = txns.get();

			if (currentUrls.size() == 1) {
				WebTarget expectedUrl = currentUrls.getFirst();

				if (!expectedUrl.getUri().equals(uri)) {
					currentUrls.add(client.target(uri));

					throw new IllegalStateException(String.format("transaction '%s' in progress but got '%s'", expectedUrl, url));
				}

				logger.trace("already added transaction '{}'", expectedUrl);
			} else {
				logger.debug("setting transaction '{}'", url);

				currentUrls.add(client.target(uri));
			}
		}

		@Override
		public String toString() {
			return "Neo4jTransactionRequestExecutor [txns=" + txns.get() + "]";
		}
	}
}
