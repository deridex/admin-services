package cc.newmercy.contentservices.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends IllegalArgumentException {

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictException(String s) {
        super(s);
    }
}
