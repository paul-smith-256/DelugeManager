package info.itline.delugemanager.ui;

import info.itline.delugemanager.net.ServiceWrapper;
import info.itline.delugemanager.net.ServiceWrapper.ServiceStateListener;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class PeriodicalUpdater 
		extends Fragment 
		implements ServiceStateListener {

	public PeriodicalUpdater(String logTag) {
		mLogTag = logTag;
	}
	
	public ServiceWrapper getService() {
		return mWrapper;
	}
	
	public void setStateListener(StateListener lis) {
		mStateListener = lis;
		if (mUpdaterFuture != null) {
			lis.onUpdatingStarted();
		}
		else {
			lis.onUpdatingStopped();
		}
	}
	
	public void removeStateListener() {
		mStateListener = null;
	}
	
	public static interface StateListener {
		void onUpdatingStarted();
		void onUpdatingStopped();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Log.i(mLogTag, "Wrapper created");
		mPreferenceManager = new PreferenceManager(getActivity());
		mWrapper = new ServiceWrapper(getActivity(), this);
		mWrapper.bind();
	}
	
	@Override
	public void onDestroy() {
		mScheduler.shutdown();
		mWrapper.unbind();
		super.onDestroy();
	}
	
	private void beginUpdating() {
		if (!mInBackground) {
			mUpdaterFuture = mScheduler.scheduleAtFixedRate(mUpdater,
					0, mPreferenceManager.getInfomationUpdatingInterval(), 
					TimeUnit.MILLISECONDS);
		}
	}
	
	private void stopUpdating() {
		if (mUpdaterFuture != null) {
			mUpdaterFuture.cancel(false);
			mUpdaterFuture = null;
		}
	}
	
	private void beginUpdatingAndNotifyListener() {
		if (mStateListener != null) {
			mStateListener.onUpdatingStarted();
		}
		beginUpdating();
	}
	
	private void stopUpdatingAndNotifyListener() {
		if (mStateListener != null) {
			mStateListener.onUpdatingStopped();
		}
		stopUpdating();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mInBackground = false;
		if (mWrapper.isConnected()) {
			beginUpdating();
		}
	}
	
	@Override
	public void onPause() {
		mInBackground = true;
		stopUpdating();
		super.onPause();
	}
	
	protected abstract void onPerformUpdate(ServiceWrapper wrapper);
	
	@Override
	public void onBound() {
		Log.i(mLogTag, "Bound");
		if (mWrapper.isConnected()) {
			beginUpdatingAndNotifyListener();
		}
	}
	
	@Override
	public void onConnected() {
		Log.i(mLogTag, "Connected");
		beginUpdatingAndNotifyListener();
	}

	@Override
	public void onEvent(String type, List<Object> data) {
		
	}

	@Override
	public void onConnectionFailed(int reason, String description) {
		Log.i(mLogTag, "Connection failed: " + reason + ": " + description);
		stopUpdatingAndNotifyListener();
	}

	@Override
	public void onDisconnected() {
		Log.i(mLogTag, "Disconnected");
		stopUpdatingAndNotifyListener();
	}

	@Override
	public void onTerminated() {
		Log.i(mLogTag, "Terminated");
		stopUpdatingAndNotifyListener();
	}
	
	private class Updater implements Runnable {
		
		@Override
		public void run() {
			mHandler.post(mUpdateTask);
		}
		
		private class UpdateTask implements Runnable {
			
			@Override
			public void run() {
				if (mWrapper != null) {
					onPerformUpdate(mWrapper);
				}
			}
		}
		
		private Handler mHandler = new Handler();
		private UpdateTask mUpdateTask = new UpdateTask();
	}
	
	private ServiceWrapper mWrapper;
	private String mLogTag;
	private ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> mUpdaterFuture;
	private Runnable mUpdater = new Updater();
	private PreferenceManager mPreferenceManager;
	private StateListener mStateListener;
	private boolean mInBackground = true;
}
