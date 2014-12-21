package info.itline.delugemanager.ui;

import info.itline.delugemanager.R;
import info.itline.delugemanager.domain.MessageParsingException;
import info.itline.delugemanager.domain.Peer;
import info.itline.delugemanager.domain.Torrent;
import info.itline.delugemanager.net.DelugeRpcException;
import info.itline.delugemanager.net.ResponseListener;
import info.itline.delugemanager.net.ServiceWrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

public class TorrentCardActivity extends ActionBarActivity {
	
	static void start(Context c, String torrentId) {
		Intent i = new Intent(c, TorrentCardActivity.class);
		i.putExtra(ARG_TORRENT_ID, torrentId);
		c.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_torrent_card);
		
		FragmentManager fm = getSupportFragmentManager();
		mStorage = (Storage) fm.findFragmentByTag(TAG_STORAGE);
		if (mStorage == null) {
			mStorage = Storage.newInstance(getIntent().getStringExtra(ARG_TORRENT_ID));
			fm.beginTransaction().add(mStorage, TAG_STORAGE).commit();
		}
		
		ActionBarNavigationHelper.setupNavigation(this, R.id.viewPager, 
				sPageTitles, new Adapter(fm));
	}
	
	public ServiceWrapper getService() {
		return mStorage.getService();
	}
	
	private static class Adapter extends FragmentPagerAdapter {
		
		public Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			switch (pos) {
			case 0:
				return new TorrentFilesList();
			case 1:
				return new TorrentPeersFragment();
			default:
				throw new IndexOutOfBoundsException();
			}
		}

		@Override
		public int getCount() {
			return sPageTitles.length;
		}
	}
	
	static interface TorrentStatusChangedListener {
		void onTorrentStatusChanged(Torrent t);
	}
	
	void addTorrentStatusChangedListener(TorrentStatusChangedListener lis) {
		mStorage.addTorrentStatusChangedListener(lis);
	}
	
	void removeTorrentStatusChangedListener(TorrentStatusChangedListener lis) {
		mStorage.removeTorrentStatusChangedListener(lis);
	}
	
	Torrent getTorrent() {
		return mStorage.getTorrent();
	}
	
	private static class Storage extends PeriodicalUpdater {
		
		static Storage newInstance(String torrentId) {
			Bundle b = new Bundle();
			b.putString(ARG_TORRENT_ID, torrentId);
			Storage result = new Storage();
			result.setArguments(b);
			return result;
		}
		
		Storage() {
			super(LOG_TAG);
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mTorrentId = getArguments().getString(ARG_TORRENT_ID);
		}
		
		@Override
		protected void onPerformUpdate(ServiceWrapper wrapper) {
			getService().getTorrentStatus(mTorrentId, mTorrentStatusReceiver);
		}
		
		private class TorrentStatusReceiver extends ResponseListener {
			
			@Override
			@SuppressWarnings("unchecked")
			protected void onReceive() {
				try {
					mTorrent = new Torrent((Map<Object, Object>) getResult().get(0));
					fireTorrentStatusChangedListener();
				}
				catch (DelugeRpcException e) {
					Log.i(LOG_TAG, "RPC exception", e);
					Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();;
				}
				catch (MessageParsingException e) {
					Log.i(LOG_TAG, "", e);
				}
				catch (ClassCastException e) {
					Log.i(LOG_TAG, "", e);
				}
				catch (IndexOutOfBoundsException e) {
					Log.i(LOG_TAG, "", e);
				}
			}
		}
		
		Torrent getTorrent() {
			return mTorrent;
		}
		
		void addTorrentStatusChangedListener(TorrentStatusChangedListener lis) {
			mTorrentStatusChangedListeners.add(lis);
		}
		
		void removeTorrentStatusChangedListener(TorrentStatusChangedListener lis) {
			mTorrentStatusChangedListeners.remove(lis);
		}
		
		void fireTorrentStatusChangedListener() {
			for (TorrentStatusChangedListener lis: mTorrentStatusChangedListeners) {
				lis.onTorrentStatusChanged(mTorrent);
			}
		}
		
		private TorrentStatusReceiver mTorrentStatusReceiver = new TorrentStatusReceiver();
		private List<TorrentStatusChangedListener> mTorrentStatusChangedListeners = 
				new LinkedList<TorrentStatusChangedListener>();
		private Torrent mTorrent;
		private String mTorrentId;
	}
	
	private Storage mStorage;
	
	private static final int[] sPageTitles = new int[] {
		R.string.files,
		R.string.peers,
	};
	
	public static final String ARG_TORRENT_ID = "torrentId";
	
	private static final String LOG_TAG = TorrentCardActivity.class.getSimpleName();
	private static final String TAG_STORAGE = "storage";
}
