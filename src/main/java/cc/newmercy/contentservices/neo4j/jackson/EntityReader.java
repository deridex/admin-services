package cc.newmercy.contentservices.neo4j.jackson;

import javax.ws.rs.core.Response;

import cc.newmercy.contentservices.neo4j.json.TransactionResponse;
import com.fasterxml.jackson.databind.JavaType;

public interface EntityReader {
    Parser parse(JavaType... types);

    Parser parse(Class<?>... types);

    interface Parser {
        Parser then(JavaType... types);

        Parser then(Class<?>... types);

        TransactionResponse from(Response response);
    }
}
