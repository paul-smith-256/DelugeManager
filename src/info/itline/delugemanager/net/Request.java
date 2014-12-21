package info.itline.delugemanager.net;

import static info.itline.jrencode.Constructor.list;
import static info.itline.jrencode.Constructor.map;

import java.util.List;
import java.util.Map;

class Request {

	Request(int id, String name, List<Object> args, 
			Map<Object, Object> opts, ResponseListener mListener) {
		mRequestId = id;
		mMethodName = name;
		mArgs = args != null ? args : sEmptyList;
		mOpts = opts != null ? opts : sEmptyMap;
		mResponseListener = mListener;
	}
	
	Integer getRequestId() {
		return mRequestId;
	}
	
	String getMethodName() {
		return mMethodName;
	}
	
	List<Object> getArgs() {
		return mArgs;
	}
	
	Map<Object, Object> getOpts() {
		return mOpts;
	}

	ResponseListener getResponseListener() {
		return mResponseListener;
	}
	
	long getExpirationTimestamp() {
		return mExpirationTimestamp;
	}

	void setExpirationTimestamp(long expirationTimestamp) {
		mExpirationTimestamp = expirationTimestamp;
	}

	private int mRequestId;
	private String mMethodName;
	private List<Object> mArgs;
	private Map<Object, Object> mOpts;
	private ResponseListener mResponseListener;
	private long mExpirationTimestamp;
	
	private static final List<Object> sEmptyList = list();
	private static final Map<Object, Object> sEmptyMap = map();
}
