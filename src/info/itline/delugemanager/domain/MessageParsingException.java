package info.itline.delugemanager.domain;

public class MessageParsingException extends Exception {

	public MessageParsingException() {
		super();
	}

	public MessageParsingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MessageParsingException(String arg0) {
		super(arg0);
	}

	public MessageParsingException(Throwable arg0) {
		super(arg0);
	}
	
	private static final long serialVersionUID = 1;
}