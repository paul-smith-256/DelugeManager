package info.itline.delugemanager.net;


public class MethodFailedException extends DelugeRpcException {
	
	public MethodFailedException(String errorType, String message) {
		super(message);
		mErrorType = errorType;
	}
	
	public String getErrorType() {
		return mErrorType;
	}

	public void setErrorType(String errorType) {
		mErrorType = errorType;
	}

	private String mErrorType;
	
	private static final long serialVersionUID = 1;
}
