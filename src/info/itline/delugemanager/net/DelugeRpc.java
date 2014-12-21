package info.itline.delugemanager.net;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class DelugeRpc extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return new SimpleBinder();
	}
	
	public class SimpleBinder extends Binder {
		public DelugeRpc getService() {
			return DelugeRpc.this;
		}
	}
	
	private class NetworkStateReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (hasNetworkConnection()) {
				Log.i(LOG_TAG, "Has network connection, trying to reconnect");
				tryReconnect();
			}
			else {
				Log.i(LOG_TAG, "No network connection");
				shutdownConnection(true);
			}
		}
	}
	
	private void registerNetworkStateReceiver() {
		mNetworkStateReceiver = new NetworkStateReceiver();
		registerReceiver(mNetworkStateReceiver, 
				new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	private void unregisterNetworkStateReceiver() {
		unregisterReceiver(mNetworkStateReceiver);
	}
	
	private boolean hasNetworkConnection() {
		ConnectivityManager cm =
		        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnected();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		registerNetworkStateReceiver();
		mBroadcastManager = LocalBroadcastManager.getInstance(this);
		Log.i(LOG_TAG, "Service created");
	}
	
	@Override
	public void onDestroy() {
		unregisterNetworkStateReceiver();
		mScheduler.shutdown();
		shutdownConnection(false);
		Log.i(LOG_TAG, "Service destroyed");
		super.onDestroy();
	}
	
	private synchronized void establishConnection() {
		mConnectionManager = new ConnectionManager(
				getApplicationContext(), mDaemon);
	}
	
	private synchronized void shutdownConnection(final boolean notifyAboutShutdown) {
		if (mConnectionManager != null) {
			if (mReconnectorFuture != null) {
				mReconnectorFuture.cancel(true);
			}
			final ConnectionManager cm = mConnectionManager;
			mConnectionManager = null;
			new Thread() {
				
				@Override
				public void run() {
					cm.shutdown(notifyAboutShutdown);
				}
			}.start();
		}
	}
	
	public synchronized void connect(DelugeDaemonInfo daemon) {
		Log.i(LOG_TAG, "Connecting");
		mDaemon = daemon;
		if (!hasNetworkConnection()) {
			BroadcastHelper.sendConnectionFailedBroadcast(
					mBroadcastManager, Actions.IO_ERROR);
			return;
		}
		if (isConnected()) {
			Log.i(LOG_TAG, "Shutting down previous connection");
			shutdownConnection(true);
		}
		establishConnection();
	}
	
	public synchronized boolean isConnected() {
		return mConnectionManager != null && 
				mConnectionManager.isRunning();
	}
	
	public synchronized void disconnect() {
		Log.i(LOG_TAG, "Disconnecting");
		if (mConnectionManager != null) {
			Log.i(LOG_TAG, "Shutting down connection");
			shutdownConnection(true);
			mDaemon = null;
		}
	}
	
	public synchronized void keepConnection() {
		if (mDaemon != null) {
			Log.i(LOG_TAG, "Keeping connection");
			if (mReconnectorFuture == null) {
				mReconnectorFuture = mScheduler.scheduleAtFixedRate(mReconnector, 
							RECONNECTION_INTERVAL, RECONNECTION_INTERVAL, 
							TimeUnit.MILLISECONDS);
			}
		}
	}
	
	private synchronized void reconnectIfConnectionIsLost() {
		Log.w(LOG_TAG, "isConnected = " + isConnected() + ", mDaemon = " + mDaemon);
		if (!isConnected() && mDaemon != null) {
			Log.i(LOG_TAG, "Reconnecting");
			connect(mDaemon);
		}
	}
	
	private synchronized void tryReconnect() {
		if (mDaemon != null) {
			connect(mDaemon);
		}
	}
	
	public synchronized void call(String name, List<Object> args, 
			Map<Object, Object> opts, ResponseListener listener) {
//		Log.i(LOG_TAG, "Calling " + name);
		if (isConnected()) {
			mConnectionManager.send(new Request(mNextPacketId++, name, args, opts, listener));
		}
		else {
			listener.notifyNoResponse();
		}
	}
	
	private class Reconnector implements Runnable {
		
		@Override
		public void run() {
			reconnectIfConnectionIsLost();
		}
	}
	
	private volatile ConnectionManager mConnectionManager;
	private volatile DelugeDaemonInfo mDaemon;
	private int mNextPacketId;
	private NetworkStateReceiver mNetworkStateReceiver;
	private LocalBroadcastManager mBroadcastManager;
	private ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);
	private Runnable mReconnector = new Reconnector();
	private ScheduledFuture<?> mReconnectorFuture;
	
	private static final int RECONNECTION_INTERVAL = 25 * 1000;
	
	private static final String LOG_TAG = DelugeRpc.class.getSimpleName();
}
