package info.itline.delugemanager.net;

import static info.itline.jrencode.Constructor.list;
import static info.itline.jrencode.Constructor.map;
import info.itline.delugemanager.R;
import info.itline.delugemanager.net.DelugeRpc.SimpleBinder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ServiceWrapper {
	
	public ServiceWrapper(Context context, ServiceStateListener listener) {
		mContext = context.getApplicationContext();
		mStateListener = listener;
	}
	
	public void bind() {
		boolean connected = mContext.bindService(new Intent(mContext, DelugeRpc.class), 
				mConnection, Context.BIND_AUTO_CREATE);
		Log.i(LOG_TAG, "Is connected = " + connected);
		if (mStateListener != null) {
			LocalBroadcastManager m = LocalBroadcastManager.getInstance(mContext);
			mStateBroadcastReceiver = new ConnectionStateBroadcastReceiver();
			mRpcEventReceiver = new RpcEventReceiver();
			m.registerReceiver(mStateBroadcastReceiver, 
					new IntentFilter(Actions.CONNECTION_STATE_CHANGED));
			m.registerReceiver(mRpcEventReceiver, new IntentFilter(Actions.EVENT));
		}
		mWasBound = true;
	}
	
	public void unbind() {
		mContext.unbindService(mConnection);
		if (mWasBound && mStateListener != null) {
			LocalBroadcastManager m = LocalBroadcastManager.getInstance(mContext);
			m.unregisterReceiver(mStateBroadcastReceiver);
			m.unregisterReceiver(mRpcEventReceiver);
		}
	}
	
	public boolean isBound() {
		return mService != null;
	}
	
	private void call(String methodName, List<Object> args, 
			Map<Object, Object> opts, ResponseListener listener) {
		if (isBound()) {
			listener.setHandler(mHandler);
			mService.call(methodName, args, opts, listener);
		}
	}
	
	public void getTorrentList(ResponseListener listener) {
		call(Methods.GET_TORRENT_LIST, list(map(), map()), null, listener);
	}
	
	public void getTorrentStatus(String id, ResponseListener listener) {
		call(Methods.GET_TORRENT_STATUS, list(id, map()), null, listener);
	}
	
	public void pauseTorrent(String id, ResponseListener listener) {
		call(Methods.PAUSE_TORRENT, list(list(id)), null, listener);
	}
	
	public void resumeTorrent(String id, ResponseListener listener) {
		call(Methods.RESUME_TORRENT, list(list(id)), null, listener);
	}
	
	public void queueTorrentUp(String id, ResponseListener listener) {
		call(Methods.QUEUE_UP, list(list(id)), null, listener);
	}
	
	public void queueTorrentDown(String id, ResponseListener listener) {
		call(Methods.QUEUE_DOWN, list(list(id)), null, listener);
	}
	
	public void removeTorrent(String id, boolean removeContent, ResponseListener listener) {
		call(Methods.REMOVE_TORRENT, list(id, removeContent), null, listener);
	}
	
	public void getSessionStatus(ResponseListener listener, String... keys) {
		List<Object> keyList = new LinkedList<Object>();
		Collections.addAll(keyList, keys);
		call(Methods.GET_SESSION_STATUS, list(keyList), null, listener);
	}
	
	public void setFilePriorities(String torrentId, List<Integer> priorities, ResponseListener listener) {
		call(Methods.SET_FILE_PRIORITIES, list(torrentId, priorities), null, listener);
	}
	
	public void connect(DelugeDaemonInfo d) {
		if (isBound()) {
			mService.connect(d);
		}
	}
	
	/* public void forgetDaemon() {
		if (mService != null) {
			mService.forgetDaemon();
		}
	} */
	
	public void keepConnection() {
		if (isBound()) {
			mService.keepConnection();
		}
	}
	
	public void disconnect() {
		if (isBound()) {
			mService.disconnect();
		}
	}
	
	public boolean isConnected() {
		return isBound() && mService.isConnected();
	}
	
	public static interface ServiceStateListener {
		void onBound();
		void onConnected();
		void onEvent(String type, List<Object> data);
		void onConnectionFailed(int reason, String description);
		void onDisconnected();
		void onTerminated();
	}
	
	private class ServiceConnectionImpl implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((SimpleBinder) service).getService();
			Log.i(LOG_TAG, "Service connected");
			if (mStateListener != null) {
				mStateListener.onBound();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			if (mStateListener != null) {
				mStateListener.onTerminated();
			}
		}
	}
	
	private class ConnectionStateBroadcastReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int state = intent.getIntExtra(Actions.EXTRA_CONNECTION_STATE, -1);
			switch (state) {
			case Actions.CONNECTED:
				mStateListener.onConnected();
				break;
			case Actions.CONNECTION_FAILED:
				int reason = intent.getExtras().getInt(
						Actions.EXTRA_CONNECTION_FAILURE_REASON);
				mStateListener.onConnectionFailed(reason, 
						getFailureReasonDescription(reason));
				break;
			case Actions.DISCONNECTED:
				mStateListener.onDisconnected();
			}
		}
	}
	
	private class RpcEventReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			mStateListener.onEvent(
					intent.getStringExtra(Actions.EXTRA_EVENT_TYPE),
					(List<Object>) intent.getSerializableExtra(Actions.EXTRA_EVENT_ARGS));
		}
	}
	
	private String getFailureReasonDescription(int reason) {
		int reasonId = 0;
		switch (reason) {
		case Actions.IO_ERROR:
			reasonId = R.string.reasonCannotOpenConnection;
			break;
		case Actions.GARBAGE_IN_RESPONSE:
			reasonId = R.string.reasonWrongServerVersion;
			break;
		case Actions.WRONG_LOGIN_PASS:
			reasonId = R.string.reasonWrongLoginOrPassword;
			break;
		default:
			reasonId = R.string.reasonUnknown;
		}
		return mContext.getString(reasonId);
	}
	
	private Context mContext;
	private DelugeRpc mService;
	private ServiceConnection mConnection = new ServiceConnectionImpl();
	private ServiceStateListener mStateListener;
	private ConnectionStateBroadcastReceiver mStateBroadcastReceiver;
	private RpcEventReceiver mRpcEventReceiver;
	private Handler mHandler = new Handler();
	private boolean mWasBound;
	
	private static final String LOG_TAG = ServiceWrapper.class.getSimpleName();
}
