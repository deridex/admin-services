package cc.newmercy.contentservices.neo4j;

import java.util.Formatter;

public final class Nodes {

	private Nodes() { }

	public static String createNodeQuery(String label, String... properties) {
		StringBuilder query = new StringBuilder();

		try (Formatter formatter = new Formatter(query)) {
			formatter.format("create (n:%s { %s: { %2$s }", label, properties[0]);

			for (int i = 1; i < properties.length; i++) {
				formatter.format(", %s: { %1$s }", properties[i]);
			}
		}

		query.append(" }) return n");

		return query.toString();
	}
}
