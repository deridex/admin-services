package cc.newmercy.contentservices.neo4j;

import java.util.Formatter;

public final class Nodes {

	public static final String ID_PROPERTY = "id";

	public static String createNodeQuery(String label, boolean hasId, String... properties) {
		StringBuilder query = new StringBuilder();

		try (Formatter formatter = new Formatter(query)) {
			formatter.format("create (n:%s { %s: { %2$s }", label, ID_PROPERTY);

			for (String property : properties) {
				formatter.format(", %s: { %1$s }", property);
			}
		}

		query.append(" }) return n");

		return query.toString();
	}

	public static String getNodeQuery(String label) {
		StringBuilder query = new StringBuilder();

		try (Formatter formatter = new Formatter(query)) {
			formatter.format("match (n:%s) where n.%s = { %2$s } return n", label, ID_PROPERTY);
		}

		return query.toString();
	}

	public static String updateNodeQuery(String label, String... properties) {
		StringBuilder query = new StringBuilder();

		try (Formatter formatter = new Formatter(query)) {
			formatter.format("match (n:%s) where n.%s = { %2$s } set n.%s = { %3$s }", label, ID_PROPERTY, properties[0]);

			for (int i = 1; i < properties.length; i++) {
				formatter.format(", n.%s = { %1$s }", properties[i]);
			}
		}

		query.append(" return n");

		return query.toString();
	}

	private Nodes() { }
}
