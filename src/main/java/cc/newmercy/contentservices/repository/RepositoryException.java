package cc.newmercy.contentservices.repository;

public class RepositoryException extends RuntimeException {

	private static final long serialVersionUID = -3427319151962195753L;

	public RepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepositoryException(String message) {
		super(message);
	}
}
