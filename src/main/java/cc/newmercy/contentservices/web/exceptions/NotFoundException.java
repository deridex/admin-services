package cc.newmercy.contentservices.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends IllegalArgumentException {

	private static final long serialVersionUID = 6146121017783001204L;

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(String s) {
		super(s);
	}

	public NotFoundException() {
		super();
	}
}
