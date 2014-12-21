package info.itline.delugemanager.net;

import java.util.List;

class RpcResponse {
	
}

class RpcResult extends RpcResponse {
	
	RpcResult(int requestId, List<Object> result) {
		mRequestId = requestId;
		mResult = result;
	}
	
	int getRequestId() {
		return mRequestId;
	}
	
	List<Object> getResult() {
		return mResult;
	}

	private int mRequestId;
	private List<Object> mResult;
}

class RpcError extends RpcResponse {
	
	RpcError(int requestId, String type, String message) {
		mRequestId = requestId;
		mType = type;
		mMessage = message;
	}
	
	int getRequestId() {
		return mRequestId;
	}
	
	String getType() {
		return mType;
	}
	
	String getMessage() {
		return mMessage;
	}
	
	private int mRequestId;
	private String mType;
	private String mMessage;
}

class RpcEvent extends RpcResponse {
	
	RpcEvent(String type, Object data) {
		mType = type;
		mData = data;
	}
	
	String getType() {
		return mType;
	}
	
	Object getData() {
		return mData;
	}

	private String mType;
	private Object mData;
} 