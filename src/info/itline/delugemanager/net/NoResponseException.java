package info.itline.delugemanager.net;


public class NoResponseException extends DelugeRpcException {

	public NoResponseException() {
		super("No response");
	}
	
	private static final long serialVersionUID = 1;
}
