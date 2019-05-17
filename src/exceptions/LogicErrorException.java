package exceptions;

@SuppressWarnings("unused")
public class LogicErrorException extends RuntimeException{
	public LogicErrorException() {
		super();
	}
	
	public LogicErrorException(String message) {
		super(message);
	}
	
	public LogicErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LogicErrorException(Throwable cause) {
		super(cause);
	}
}
