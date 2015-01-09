package cc.newmercy.contentservices.neo4j;

import java.util.Formatter;
import java.util.List;

import cc.newmercy.contentservices.neo4j.json.Result;
import cc.newmercy.contentservices.neo4j.json.Row;
import cc.newmercy.contentservices.web.exceptions.ConflictException;
import cc.newmercy.contentservices.web.exceptions.NotFoundException;

public final class Nodes {

	public static final String ID_PROPERTY = "id";

	public static final String ID_PARAMETER = "id";

	public static final String VERSION_PROPERTY = "v";

	public static String createNodeQuery(boolean hasVersion, String label, String... properties) {
		StringBuilder query = new StringBuilder();

		try (Formatter formatter = new Formatter(query)) {
			formatter.format("create (n:%s { %s: { %2$s }", label, ID_PROPERTY);

			if (hasVersion) {
				formatter.format(", %s: 1", VERSION_PROPERTY);
			}

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

	public static void ensureVersion(Result result, String type, String id, int version) {
		List<Row> versionData = result.getData();

		if (versionData.isEmpty()) {
			throw new NotFoundException("no such " + type + " '" + id + "'");
		}

		int currentVersion = versionData.get(0).getColumns().<Integer> get(0).intValue();

		if (currentVersion != version) {
			throw new ConflictException(String.format("%s '%s' is version %d not %d", type, id, version, currentVersion));
		}
	}

	public static String getVersionQuery(String label) {
		return String.format("match (n:%s { %s : { %s } }) return n.%s", label, ID_PROPERTY, ID_PROPERTY, VERSION_PROPERTY);
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

	public static String deleteNodeQuery(String label, boolean hasVersion) {
		StringBuilder sb = new StringBuilder();

		try (Formatter formatter = new Formatter(sb)) {
			formatter.format("match (n:%s { %s: { %s }",
					label,
					Nodes.ID_PROPERTY,
					Nodes.ID_PARAMETER);

			if (hasVersion) {
				formatter.format(", %s: { %s }", Nodes.VERSION_PROPERTY, Nodes.VERSION_PROPERTY);
			}

			formatter.format(" }) delete n return count(n) as numDeleted",
					label,
					Nodes.ID_PROPERTY,
					Nodes.ID_PARAMETER);
		}

		return sb.toString();
	}

	private Nodes() { }
}
