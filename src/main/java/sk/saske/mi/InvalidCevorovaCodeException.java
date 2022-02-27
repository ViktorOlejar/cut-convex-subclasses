package sk.saske.mi;

@SuppressWarnings("serial")
public class InvalidCevorovaCodeException extends RuntimeException {

	public InvalidCevorovaCodeException() {
		super();
	}

	public InvalidCevorovaCodeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidCevorovaCodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCevorovaCodeException(String message) {
		super(message);
	}

	public InvalidCevorovaCodeException(Throwable cause) {
		super(cause);
	}

	
}
