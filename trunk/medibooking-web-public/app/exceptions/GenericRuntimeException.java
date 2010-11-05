package exceptions;

import play.Logger;

public class GenericRuntimeException extends RuntimeException {

	public GenericRuntimeException(String message) {
		super(message);
	}

}
