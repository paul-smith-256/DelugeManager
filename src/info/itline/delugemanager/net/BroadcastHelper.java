package info.itline.delugemanager.net;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

final class BroadcastHelper {
	
	public BroadcastHelper() {
	}
	
	static void sendConnectedBroadcast(LocalBroadcastManager m) {
		Intent i = new Intent(Actions.CONNECTION_STATE_CHANGED);
		i.putExtra(Actions.EXTRA_CONNECTION_STATE, Actions.CONNECTED);
		m.sendBroadcast(i);
	}
	
	static void sendDisconnectedBroadcast(LocalBroadcastManager m) {
		Intent i = new Intent(Actions.CONNECTION_STATE_CHANGED);
		i.putExtra(Actions.EXTRA_CONNECTION_STATE, Actions.DISCONNECTED);
		m.sendBroadcast(i);
	}
	
	static void sendConnectionFailedBroadcast(LocalBroadcastManager m, int reason) {
		Intent i = new Intent(Actions.CONNECTION_STATE_CHANGED);
		i.putExtra(Actions.EXTRA_CONNECTION_STATE, Actions.CONNECTION_FAILED);
		i.putExtra(Actions.EXTRA_CONNECTION_FAILURE_REASON, reason);
		m.sendBroadcast(i);
	}
}
