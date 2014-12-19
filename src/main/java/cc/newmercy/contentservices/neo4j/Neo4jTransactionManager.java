package cc.newmercy.contentservices.neo4j;

import java.util.Objects;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import com.google.common.base.Preconditions;
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

	private final Neo4jTransactionClass neo4jTransaction = new Neo4jTransactionClass();

	private Client client;

	private ThreadLocal<String> url = new ThreadLocal<>();

	public Neo4jTransactionManager(Client client) {
		this.client = Objects.requireNonNull(client, "client");;
	}

	@Override
	protected Object doGetTransaction() throws TransactionException {
		return neo4jTransaction;
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
		if (url.get() != null) {
			logger.warn("stale transaction '{}' detected", url.get());
		}

		logger.debug("beginning transaction");

		url.set(null);
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		if (url.get() == null) {
			logger.warn("cannot commit without transaction");
		} else {
			logger.debug("committing {}", url.get());

			try {
				TransactionResponse response = client.target(url.get()).path("commit").request()
						.accept(MediaType.APPLICATION_JSON)
						.post(null, TransactionResponse.class);

				if (!response.getErrors().isEmpty()) {
					throw new TransactionSystemException(response.getErrors().toString());
				}
			} finally {
				url.set(null);
			}
		}
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
		if (url.get() == null) {
			logger.warn("cannot rollback without transaction");
		} else {
			logger.debug("rolling back {}", url.get());

			try {
				TransactionResponse response = client.target(url.get()).request()
						.accept(MediaType.APPLICATION_JSON)
						.delete(TransactionResponse.class);

				if (!response.getErrors().isEmpty()) {
					throw new TransactionSystemException(response.getErrors().toString());
				}
			} finally {
				url.set(null);
			}
		}
	}

	public Neo4jTransaction getTransaction() {
		return neo4jTransaction;
	}

	private class Neo4jTransactionClass implements Neo4jTransaction {
		@Override
		public void setTransactionUrl(String url) {
			String currentUrl = Neo4jTransactionManager.this.url.get();

			if (currentUrl != null) {
				Preconditions.checkArgument(currentUrl.equals(url), "transaction '%s' in progress but got '%s'",
						Neo4jTransactionManager.this.url.get(), url);
			} else {
				logger.debug("setting transaction '{}'", url);

				Neo4jTransactionManager.this.url.set(url);
			}
		}

		@Override
		public String toString() {
			return "Neo4jTransactionClass [url=" + Neo4jTransactionManager.this.url + "]";
		}
	}
}
