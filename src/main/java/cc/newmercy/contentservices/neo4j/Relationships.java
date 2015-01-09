package cc.newmercy.contentservices.neo4j;

import java.util.Formatter;

public final class Relationships {

    public static final String START_ID_PARAMETER = "startId";

    public static final String START_VERSION_PARAMETER = "startVersion";

    public static final String END_ID_PARAMETER = "endId";

    public static final String END_VERSION_PARAMETER = "endVersion";

    public static String listRelationsQuery(
            String startLabel,
            String relationshipLabel,
            String endLabel) {
        return String.format("match (:%s { %s: { %s } })-[:%s]->(n:%s) return n",
                startLabel,
                Nodes.ID_PROPERTY,
                START_ID_PARAMETER,
                relationshipLabel,
                endLabel);
    }

    public static String createRelationshipQuery(
            String startLabel,
            String endLabel,
            String relationshipLabel,
            String... properties) {
        StringBuilder query = new StringBuilder();

        try (Formatter formatter = new Formatter(query)) {
            formatter.format("match (a:%s { %s: { %s }, %s: { %s } }), (b:%s { %s: { %s }, %s: { %s }}) create (a)-[r:%s",
                    startLabel,
                    Nodes.ID_PROPERTY,
                    START_ID_PARAMETER,
                    Nodes.VERSION_PROPERTY,
                    START_VERSION_PARAMETER,
                    endLabel,
                    Nodes.ID_PROPERTY,
                    END_ID_PARAMETER,
                    Nodes.VERSION_PROPERTY,
                    END_VERSION_PARAMETER,
                    relationshipLabel);

            if (properties.length > 0) {
                formatter.format(" { %s: { %1$s }", properties[0]);

                for (int i = 1; i < properties.length; i++) {
                    formatter.format(", %s: { %1$s }", properties[i]);
                }

                formatter.format("%s", " }");
            }

            formatter.format("%s", "]->(b)");
        }

        query.append(" return r");

        return query.toString();
    }

    public static String fetchRelatedQuery(String startLabel, String relationshipLabel, String endLabel) {
        return String.format("match (:%s { %s: { %s } })-[:%s]->(n:%s { %s: { %s } }) return n",
                startLabel,
                Nodes.ID_PROPERTY,
                START_ID_PARAMETER,
                relationshipLabel,
                endLabel,
                Nodes.ID_PROPERTY,
                END_ID_PARAMETER);
    }

    public static String updateRelationshipQuery(
            String startLabel,
            String relationshipLabel,
            String endLabel,
            String property,
            String... otherProperties) {
        StringBuilder sb = new StringBuilder();

        try (Formatter formatter = new Formatter(sb)) {
            formatter.format("match (s:%s { %s: { %s }, %s: { %s }})-[r:%s]->(e:%s { %s: { %s }, %s: { %s }}) set s.%s = s.%s + 1, e.%s = e.%s + 1, r = { %s: { %s }",
                    startLabel,
                    Nodes.ID_PROPERTY,
                    START_ID_PARAMETER,
                    Nodes.VERSION_PROPERTY,
                    START_VERSION_PARAMETER,
                    relationshipLabel,
                    endLabel,
                    Nodes.ID_PROPERTY,
                    END_ID_PARAMETER,
                    Nodes.VERSION_PROPERTY,
                    END_VERSION_PARAMETER,
                    Nodes.ID_PROPERTY,
                    Nodes.ID_PROPERTY,
                    Nodes.ID_PROPERTY,
                    Nodes.ID_PROPERTY,
                    property,
                    property);

            for (String otherProperty : otherProperties) {
                formatter.format(", %s: { %1$s }", otherProperty);
            }

            formatter.format("}) return r");
        }

        return sb.toString();
    }

    public static String deleteInboundRelationshipsQuery(
            String startLabel,
            String relationshipLabel,
            String endLabel,
            boolean endHasVersion) {
        StringBuilder sb = new StringBuilder();

        try (Formatter formatter = new Formatter(sb)) {
            formatter.format("match (e:%s { %s: { %s }", endLabel, Nodes.ID_PROPERTY, END_ID_PARAMETER);

            if (endHasVersion) {
                formatter.format(", %s { %s }", Nodes.VERSION_PROPERTY, END_VERSION_PARAMETER);
            }

            formatter.format(" }) optional match (:%s)-[r:%s]->(e) delete r return count(r) as numDeleted", startLabel, relationshipLabel);
        }

        return sb.toString();
    }

    private Relationships() { }
}
