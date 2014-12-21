package info.itline.delugemanager.ui;

import info.itline.delugemanager.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class LoginActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
//		FragmentManager fm = getSupportFragmentManager();
//		Storage s = (Storage) fm.findFragmentByTag("storage");
//		if (s == null) {
//			s = new Storage();
//			fm.beginTransaction().add(s, "storage").commit();
//		}
	}
	
//	private static class Storage extends PeriodicalUpdater {
//
//		public Storage() {
//			super(LOG_TAG);
//		}
//
//		@Override
//		protected void onPerformUpdate(ServiceWrapper w) {
//			w.getTorrentList(mTorrentListListener);
//		}
//		
//		private class TorrentListListener extends ResponseListener {
//			
//			@Override
//			protected void onReceive() {
//				try {
//					Log.i(LOG_TAG, "Response for torrent list request");
//					getResult();
//					Log.i(LOG_TAG, "Torrent list received");
//				}
//				catch (DelugeRpcException e) {
//					Log.i(LOG_TAG, "Cannot get torrent list: " + e.getMessage());
//				}
//			}
//		}
//		
//		private TorrentListListener mTorrentListListener = new TorrentListListener();
//	}
	
	private static final String LOG_TAG = LoginActivity.class.getSimpleName();
}
