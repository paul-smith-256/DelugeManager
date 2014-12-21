package info.itline.delugemanager.net;

public class DelugeRpcException extends Exception {

	public DelugeRpcException() {
		super();
	}

	public DelugeRpcException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public DelugeRpcException(String detailMessage) {
		super(detailMessage);
	}

	public DelugeRpcException(Throwable throwable) {
		super(throwable);
	}
	
	private static final long serialVersionUID = 1;
}
