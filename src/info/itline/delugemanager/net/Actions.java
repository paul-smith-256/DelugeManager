package info.itline.delugemanager.net;


public final class Actions {
	
	private Actions() {
	}
	
	private static String sActionPrefix = Actions.class.getName() + ".";
	
	public static final String 
		
		CONNECTION_STATE_CHANGED 	= sActionPrefix + "CONNECTED_STATE_CHANGED",
		EVENT						= sActionPrefix + "EVENT";
	
	public static final String 
	
		EXTRA_CONNECTION_STATE				= "connectionState",
		EXTRA_CONNECTION_FAILURE_REASON 	= "connectionFailureReason",
		EXTRA_EVENT_TYPE 					= "eventType",
		EXTRA_EVENT_ARGS 					= "eventArgs";
	
	public static final int
		
		CONNECTED				= 0,
		CONNECTION_FAILED		= 1,
		DISCONNECTED			= 2;
	
	public static final int
	
		IO_ERROR 				= 0,
		GARBAGE_IN_RESPONSE 	= 1,
		WRONG_LOGIN_PASS		= 2;
}
