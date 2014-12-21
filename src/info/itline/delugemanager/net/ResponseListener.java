package info.itline.delugemanager.net;

import java.util.List;

import android.os.Handler;

public abstract class ResponseListener implements Runnable {
	
	public ResponseListener() {
	}
	
	@Override
	public final void run() {
		onReceive();
	}
	
	protected abstract void onReceive();
	
	protected List<Object> getResult() throws DelugeRpcException {
		if (mException != null) {
			throw mException;
		}
		return mResult;
	}
	
	public void setHandler(Handler h) {
		mHandler = h;
	}
	
	public void notifyMethodCompleted(List<Object> result) {
		mResult = result;
		mException = null;
		mHandler.post(this);
	}
	
	public void notifyMethodFailed(String errorType, String message) {
		mException = new MethodFailedException(errorType, message);
		mResult = null;
		mHandler.post(this);
	}
	
	public void notifyNoResponse() {
		mException = new NoResponseException();
		mResult = null;
		mHandler.post(this);
	}
	
	private DelugeRpcException mException;
	private List<Object> mResult;
	private Handler mHandler;
}
