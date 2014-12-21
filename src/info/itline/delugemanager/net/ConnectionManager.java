package info.itline.delugemanager.net;

import static info.itline.jrencode.Constructor.list;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

class ConnectionManager {
	
	ConnectionManager(Context application, 
			DelugeDaemonInfo daemon) {
		mContext = application;
		mBroadcastManager = LocalBroadcastManager.getInstance(application);
		mDaemon = daemon;
		new Initializer().start();
	}
	
	private WakeLock aquirePartialWakeLock() {
		PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		WakeLock result = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
		result.acquire();
		return result;
	}
	
	public synchronized boolean send(Request r) {
		if (mIsRunning) {
			mCaller.send(r);
			return true;
		}
		else {
			return false;
		}
	}
	
	private void sendEventBroadcast(String type, Object args) {
		Intent i = new Intent(Actions.EVENT);
		i.putExtra(Actions.EXTRA_EVENT_TYPE, type);
		if (args instanceof List) {
			i.putExtra(Actions.EXTRA_EVENT_ARGS, new ArrayList<Object>((List<Object>) args));
		}
		else {
			ArrayList<Object> w = new ArrayList<Object>();
			w.add(args);
			i.putExtra(Actions.EXTRA_EVENT_ARGS, w);
		}
		
		mBroadcastManager.sendBroadcast(i);
	}
	
	private synchronized boolean registerSentRequest(Request r) {
		if (!mIsRunning) {
			return false;
		}
		mReceiver.registerSentRequest(r);
		return true;
	}
	
	
	synchronized void shutdown(boolean notifyAboutShutdown) {
		if (!mIsRunning) {
			return;
		}
		IoHelper.closeQuetly(mSocket);
		mCaller.interrupt();
		mReceiver.interrupt();
		if (notifyAboutShutdown) {
			BroadcastHelper.sendDisconnectedBroadcast(mBroadcastManager);
		}
		mIsRunning = false;
		Log.i(LOG_TAG, "Connection terminated");
	}
	
	boolean isRunning() {
		return mIsRunning;
	}
	
	private static class LoginException extends Exception {
		
		LoginException(int reason) {
			this.reason = reason;
		}
		
		int reason;
	}
	
	private class Initializer extends Thread {
		
		@Override
		public void run() {
			synchronized (ConnectionManager.this) {
				WakeLock wl = aquirePartialWakeLock();
				try {
					mSocket = IoHelper.createSocket(mDaemon.getHost(), mDaemon.getPort());
					login(mDaemon.getLogin(), mDaemon.getPassword(), mSocket);
					OutputStream o = mSocket.getOutputStream();
					InputStream i = mSocket.getInputStream();
					mCaller = new Caller(o);
					mReceiver = new Receiver(i);
					mCaller.start();
					mReceiver.start();
					BroadcastHelper.sendConnectedBroadcast(mBroadcastManager);
					mIsRunning = true;
					Log.i(LOG_TAG, "Connected to " + mSocket.getRemoteSocketAddress() + 
							", listening on " + mSocket.getLocalSocketAddress());
				}
				catch (IOException e) {
					Log.i(LOG_TAG, mDaemon.getHost() + ": Connection failed", e);
					BroadcastHelper.sendConnectionFailedBroadcast(mBroadcastManager, Actions.IO_ERROR);
					IoHelper.closeQuetly(mSocket);
				}
				catch (LoginException e) {
					Log.i(LOG_TAG, mDaemon.getHost() + ": Cannot login: " + e.reason);
					BroadcastHelper.sendConnectionFailedBroadcast(mBroadcastManager, e.reason);
					IoHelper.closeQuetly(mSocket);
				}
				wl.release();
			}
		}
		
		private void login(String login, String password, Socket socket) throws IOException, LoginException {
			Object o = null;
			try {
				Request request = new Request(LOGIN_REQUEST_ID, Methods.LOGIN, list(login, password), null, null);
				IoHelper.serializeRequest(request, socket.getOutputStream());
				o = IoHelper.deserializeResponse(socket.getInputStream());
				RpcResponse response = IoHelper.parseResponse(o);
				if (response instanceof RpcResult && 
						((RpcResult) response).getRequestId() == LOGIN_REQUEST_ID) {
					Log.i(LOG_TAG, "Logged in");
				}
				else if (response instanceof RpcError && 
						((RpcError) response).getRequestId() == LOGIN_REQUEST_ID) {
					Log.i(LOG_TAG, "Wrong login/pass");
					throw new LoginException(Actions.WRONG_LOGIN_PASS);
				}
				else {
					throw new LoginException(Actions.GARBAGE_IN_RESPONSE);
				}
				
			}
			catch (IllegalArgumentException e) {
				Log.i(LOG_TAG, "Failed to parse login response: " + o);
				throw new LoginException(Actions.GARBAGE_IN_RESPONSE);
			}
		}
		
		private static final int LOGIN_REQUEST_ID = -1;
	}
	
	private class Caller extends Thread {
		
		Caller(OutputStream o) {
			mOutputStream = o;
		}
		
		@Override
		public void run() {
			outer: while (true) {
				WakeLock wl = null;
				try {
					synchronized (this) {
						wait();
					}
					wl = aquirePartialWakeLock();
					Request r;
					while ((r = peekRequest()) != null) {
						IoHelper.serializeRequest(r, mOutputStream);
						synchronized (ConnectionManager.this) {
							if (mIsRunning) {
								r.setExpirationTimestamp(System.currentTimeMillis() + TIMEOUT);
								registerSentRequest(r);
								removeHead();
							}
							else {
								notifyAllQueueNoResponse();
								break outer;
							}
						}
					}
				}
				catch (InterruptedException e) {
					Log.i(LOG_TAG, "Caller interrupted");
					notifyAllQueueNoResponse();
					break;
				}
				catch (IOException e) {
					Log.i(LOG_TAG, "IO exception in caller thread", e);
					synchronized (ConnectionManager.this) {
						notifyAllQueueNoResponse();
						shutdown(true);
					}
				}
				finally {
					if (wl != null) {
						wl.release();
					}
				}
			}
			Log.i(LOG_TAG, "Caller thread terminated");
		}
		
		private synchronized Request peekRequest() {
			if (!mRequestQueue.isEmpty()) {
				return mRequestQueue.get(0);
			}
			else {
				return null;
			}
		}
		
		private synchronized void removeHead() {
			mRequestQueue.remove(0);
		}
		
		private synchronized void notifyAllQueueNoResponse() {
			for (Request r: mRequestQueue) {
				r.getResponseListener().notifyNoResponse();
			}
		}
		
		synchronized void send(Request r) {
			mRequestQueue.add(r);
			notify();
		}
		
		private OutputStream mOutputStream;
		private final List<Request> mRequestQueue = new LinkedList<Request>();
	}
	
	private class Receiver extends Thread {
		
		Receiver(InputStream i) {
			mInputStream = i;
		}
		
		@Override
		public void run() {
			while (true) {
				RpcResponse response = null;
				try {
					Object o = IoHelper.deserializeResponse(mInputStream);
					response = IoHelper.parseResponse(o);
				}
				catch (InterruptedIOException e) {
					
				}
				catch (IOException e) {
					Log.i(LOG_TAG, "IO exception in receiver thread", e);
					synchronized (ConnectionManager.this) {
						notifyAllNoResponse();
						shutdown(true);
						break;
					}
				}
				catch (IllegalArgumentException e) {
					Log.i(LOG_TAG, "Garbage in response packet", e);
				}
				
				synchronized (this) {
					if (response instanceof RpcResult) {
						RpcResult result = (RpcResult) response;
						Request request = mSentRequests.get(result.getRequestId());
						if (request != null) {
							request.getResponseListener().notifyMethodCompleted(result.getResult());
							mSentRequests.remove(request.getRequestId());
						}
						else {
							Log.i(LOG_TAG, "No request with given id " + result.getRequestId());
						}
					}
					else if (response instanceof RpcError) {
						RpcError error = (RpcError) response;
						Request request = mSentRequests.get(error.getRequestId());
						if (request != null) {
							request.getResponseListener().notifyMethodFailed(
									error.getType(), error.getMessage());
							mSentRequests.remove(request.getRequestId());
						}
						else {
							Log.i(LOG_TAG, "No request with given id " + error.getRequestId());
						}
					}
					else if (response instanceof RpcEvent) {
						RpcEvent event = (RpcEvent) response;
						sendEventBroadcast(event.getType(), event.getData());
					}
					
					Iterator<Entry<Integer, Request>> iterator = 
							mSentRequests.entrySet().iterator();
					for (; iterator.hasNext(); ) {
						Entry<Integer, Request> e = iterator.next();
						Request r = e.getValue();
						if (e.getValue().getExpirationTimestamp() < System.currentTimeMillis()) {
							r.getResponseListener().notifyNoResponse();
							iterator.remove();
						}
					}
				}
				
				synchronized (ConnectionManager.this) {
					if (!mIsRunning) {
						notifyAllNoResponse();
						break;
					}
				}
			}
			Log.i(LOG_TAG, "Receiver thread terminated");
		}
		
		synchronized void registerSentRequest(Request r) {
			mSentRequests.put(r.getRequestId(), r);
		}
		
		private synchronized void notifyAllNoResponse() {
			for (Request r: mSentRequests.values()) {
				r.getResponseListener().notifyNoResponse();
			}
		}
		
		private InputStream mInputStream;
		private final Map<Integer, Request> mSentRequests = new HashMap<Integer, Request>();
	}
	
	private volatile Socket mSocket;
	private volatile LocalBroadcastManager mBroadcastManager;
	private volatile Context mContext;
	
	private volatile boolean mIsRunning;
	
	private volatile DelugeDaemonInfo mDaemon;
	
	private volatile Caller mCaller;
	private volatile Receiver mReceiver;
	
	private static final int TIMEOUT = 10000;
	
	private static final String LOG_TAG = ConnectionManager.class.getSimpleName();
}