package cc.newmercy.contentservices.neo4j;

import java.util.Formatter;

public final class Relationships {

    public static final String START_ID_PARAMETER = "startId";

    public static final String END_ID_PARAMETER = "endId";

    public static String createRelationshipQuery(
            String startLabel,
            String endLabel,
            String relationshipLabel,
            String... properties) {
        StringBuilder query = new StringBuilder();

        try (Formatter formatter = new Formatter(query)) {
            formatter.format("match (a:%s { %s: { %s }}), (b:%s { %s: { %s }}) create unique (a)-[r:%s",
                    startLabel,
                    Nodes.ID_PROPERTY,
                    START_ID_PARAMETER,
                    endLabel,
                    Nodes.ID_PROPERTY,
                    END_ID_PARAMETER,
                    relationshipLabel);

            if (properties.length > 0) {
                formatter.format(" { %s: { %1$s }", properties[0]);

                for (int i = 1; i < properties.length; i++) {
                    formatter.format(", %s: { %1$s }", properties[i]);
                }

                formatter.format("%s", "}");
            }

            formatter.format("%s", "]->(b)");
        }

        query.append(" return r");

        return query.toString();
    }

    private Relationships() { }
}
