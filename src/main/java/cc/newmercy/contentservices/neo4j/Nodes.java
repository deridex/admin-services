package cc.newmercy.contentservices.neo4j;

import java.util.Formatter;

public final class Nodes {

	public static final String ID_PROPERTY = "id";

	public static final String VERSION_PROPERTY = "v";

	public static String createNodeQuery(String label, boolean hasId, String... properties) {
		StringBuilder query = new StringBuilder();

		try (Formatter formatter = new Formatter(query)) {
			formatter.format("create (n:%s { %s: { %2$s }, %s: 1", label, ID_PROPERTY, VERSION_PROPERTY);

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

	public static String updateNodeQuery(String label, String property, String... otherProperties) {
		StringBuilder query = new StringBuilder();

		try (Formatter formatter = new Formatter(query)) {
			formatter.format("match (n:%s) where n.%s = { %2$s } and n.%s = { %3$s } set n.%3$s = n.%3$s + 1, n.%s = { %4$s }",
					label,
					ID_PROPERTY,
					VERSION_PROPERTY,
					property);

			for (String otherProperty : otherProperties) {
				formatter.format(", n.%s = { %1$s }", otherProperty);
			}
		}

		query.append(" return n");

		return query.toString();
	}

	private Nodes() { }
}
