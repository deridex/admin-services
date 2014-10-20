package cc.newmercy.contentservices.neo4j.json;

public class Transaction {

	private String expires;

	public String getExpires() {
		return expires;
	}

	@Override
	public String toString() {
		return "Transaction [expires=" + expires + "]";
	}
}
