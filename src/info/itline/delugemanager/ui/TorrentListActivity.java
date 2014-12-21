package info.itline.delugemanager.ui;

import info.itline.delugemanager.R;
import info.itline.delugemanager.domain.MessageParsingException;
import info.itline.delugemanager.domain.Torrent;
import info.itline.delugemanager.net.DelugeRpcException;
import info.itline.delugemanager.net.Methods;
import info.itline.delugemanager.net.ResponseListener;
import info.itline.delugemanager.net.ServiceWrapper;
import info.itline.delugemanager.ui.PeriodicalUpdater.StateListener;
import info.itline.jrencode.Decoder;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class TorrentListActivity 
		extends ActionBarActivity 
		implements /* TabListener, OnNavigationListener, */ StateListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_torrent_list);
		
		FragmentManager fm = getSupportFragmentManager();
		
		mStorage = (Storage) fm.findFragmentByTag(TAG_STORAGE);
		if (mStorage == null) {
			mStorage = new Storage();
			fm.beginTransaction().add(mStorage, TAG_STORAGE).commit();
		}
		mStorage.setStateListener(this);
		
		final ActionBar bar = getSupportActionBar();
		ActionBarNavigationHelper.setupNavigation(this, R.id.viewPager, 
				sTabTitleIds, new Adapter(fm));
		
//		mPager = (ViewPager) findViewById(R.id.viewPager);
//		mPager.setAdapter(new Adapter(getSupportFragmentManager()));
//		mPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
//			
//			@Override
//			public void onPageSelected(int position) {
//				bar.setSelectedNavigationItem(position);
//			}
//		});
//		
//		/* bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//		for (int titleId: TAB_TITLES) {
//			addTab(bar, titleId);
//		} */
//		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//		
//		Resources r = getResources();
//		String[] titles = new String[sTabTitleIds.length];
//		for (int i = 0; i < sTabTitleIds.length; i++) {
//			titles[i] = r.getString(sTabTitleIds[i]);
//		}
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
//				R.layout.action_bar_navigation_item, titles);
//		bar.setListNavigationCallbacks(adapter, this);
	}
	
	@Override
	protected void onDestroy() {
		mStorage.removeStateListener();
		super.onDestroy();
	}
		
	public ServiceWrapper getService() {
		return mStorage.getService();
	}
	
	@Override
	public void onUpdatingStarted() {
		if (mHaveConnectionItem != null) {
			mHaveConnectionItem.setIcon(R.drawable.ic_green_circle);
			// mHaveConnectionItem.setTitle(R.string.connected);
		}
	}
	
	@Override
	public void onUpdatingStopped() {
		if (mHaveConnectionItem != null) {
			mHaveConnectionItem.setIcon(R.drawable.ic_red_circle);
			// mHaveConnectionItem.setTitle(R.string.disconnected);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.options_torrent_list_activity, menu);
		mHaveConnectionItem = menu.findItem(R.id.haveConnection);
		menu.findItem(R.id.sortDescending).setChecked(mStorage.mDescending);
		menu.findItem(mStorage.getCurrentComparatorId()).setChecked(true);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (mStorage.hasSortingComparator(id)) {
			mStorage.setCurrentSortingComparator(id);
			item.setChecked(true);
			return true;
		}
		
		if (id == R.id.sortDescending) {
			mStorage.setSortDescending(!mStorage.isSortDescending());
			item.setChecked(mStorage.isSortDescending());
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void addSortingOrderChangedListener(SortingOrderChangedListener lis) {
		mStorage.addSortingOrderChangedListener(lis);
	}
	
	public void removeSortingOrderChangedListener(SortingOrderChangedListener lis) {
		mStorage.removeSortingOrderChangedListener(lis);
	}
	
	public Comparator<Torrent> getCurrentSortingComparator() {
		return mStorage.getCurrentSortingComparator();
	}
	
	public static interface SortingOrderChangedListener {
		void onSortingOrderChanged(Comparator<Torrent> comparator);
	}
	
	private static class Adapter extends FragmentPagerAdapter {
		
		public Adapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public int getCount() {
			return sTabTitleIds.length;
		}
		
		@Override
		public Fragment getItem(int pos) {
			if (pos == 0) {
				return TorrentListFragment.newInstance(pos);
			}
			else {
				return TorrentListFragment.newInstance(pos, sTasStateFilters[pos]);
			}
		}
	}
	
	/* private void addTab(ActionBar bar, int titleId) {
		String title = getResources().getString(titleId);
		bar.addTab(bar.newTab().setText(title).setTabListener(this));
	}
	
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		
	} */
	
//	@Override
//	public boolean onNavigationItemSelected(int pos, long id) {
//		mPager.setCurrentItem(pos);
//		return true;
//	}

	public void addTorrentListUpdatingListener(TorrentListUpdatingListener lis) {
		mStorage.addTorrentListUpdatingListener(lis);
	}
	
	public void removeTorrentListUpdatingListener(TorrentListUpdatingListener lis) {
		mStorage.removeTorrentListUpdatingListener(lis);
	}
	
	public List<Torrent> getTorrentList() {
		return mStorage.getTorrents();
	}
	
	private static class Storage extends PeriodicalUpdater {
		
		public Storage() {
			super(LOG_TAG);
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mMenuItemToComparator.put(R.id.sortByName, new NameComparator());
			mMenuItemToComparator.put(R.id.sortBySize, new SizeComparator());
			mMenuItemToComparator.put(R.id.sortbyQueue, new QueueComparator());
			mMenuItemToComparator.put(R.id.sortByEta, new EtaComparator());
			for (Entry<Integer, TorrentComparator> e: mMenuItemToComparator.entrySet()) {
				mComparatorToMenuItem.put(e.getValue(), e.getKey());
			}
			mComparator = mMenuItemToComparator.get(R.id.sortByName);
		}
		
		public boolean hasSortingComparator(int id) {
			return mMenuItemToComparator.containsKey(id);
		}
		
		public int getCurrentComparatorId() {
			return mComparatorToMenuItem.get(mComparator);
		}
		
		public void setCurrentSortingComparator(int id) {
			mComparator = mMenuItemToComparator.get(id);
			mComparator.setDescending(mDescending);
			fireSortingOrderChanged();
		}
		
		public TorrentComparator getCurrentSortingComparator() {
			return mComparator;
		}
		
		public void setSortDescending(boolean descending) {
			mComparator.setDescending(descending);
			mDescending = descending;
			fireSortingOrderChanged();
		}
		
		public boolean isSortDescending() {
			return mDescending;
		}
		
		@Override
		protected void onPerformUpdate(ServiceWrapper wrapper) {
			wrapper.getTorrentList(mTorrentListReceiver);
			wrapper.getSessionStatus(mSessionStateReceiver, 
					Methods.PAYLOAD_DOWNLOAD_RATE,
					Methods.PAYLOAD_UPLOAD_RATE,
					Methods.TOTAL_PAYLOAD_DOWNLOAD,
					Methods.TOTAL_PAYLOAD_UPLOAD);
		}
		
		private class TorrentListReceiver extends ResponseListener {

			@Override
			protected void onReceive() {
				try {
					// Log.e(LOG_TAG, "Updating state");
					List<Object> response = getResult();
					mTorrents = Torrent.parseMessage(response);
					for (TorrentListUpdatingListener lis: mTorrentListUpdatingListeners) {
						lis.onTorrentListUpdated(mTorrents);
					}
				}
				catch (DelugeRpcException e) {
					Log.i(LOG_TAG, "Cannot get torrent list", e);
				}
				catch (MessageParsingException e) {
					Log.i(LOG_TAG, "Cannot parse message", e);
				}
			}
		}
		
		private class SessionStateReceiver extends ResponseListener {
			
			@Override
			protected void onReceive() {
				try {
					List<Object> response = getResult();
					Map<String, Object> status = 
							(Map<String, Object>) response.get(0);
					int uploadRate = (Integer) status.get(Methods.PAYLOAD_UPLOAD_RATE);
					int downloadRate = (Integer) status.get(Methods.PAYLOAD_DOWNLOAD_RATE);
					long totalUpload = Decoder.asLong(status.get(Methods.TOTAL_PAYLOAD_UPLOAD));
					long totalDownload = Decoder.asLong(status.get(Methods.TOTAL_PAYLOAD_DOWNLOAD));
					TextView statusText = (TextView) getActivity().findViewById(R.id.sessionStatus);
					statusText.setText(String.format(
							getResources().getString(R.string.sessionStatusLabel), 
							UnitHelper.getSizeWithUnits(totalUpload),
							UnitHelper.getSpeedWithUnits(uploadRate),
							UnitHelper.getSizeWithUnits(totalDownload),
							UnitHelper.getSpeedWithUnits(downloadRate)));
				}
				catch (DelugeRpcException e) {
					Log.i(LOG_TAG, e.getMessage(), e);
				}
				catch (ClassCastException e) {
					Log.i(LOG_TAG, e.getMessage(), e);
				}
				catch (IndexOutOfBoundsException e) {
					Log.i(LOG_TAG, e.getMessage(), e);
				}
			}
		}
		
		public void addTorrentListUpdatingListener(
				TorrentListUpdatingListener lis) {
			mTorrentListUpdatingListeners.add(lis);
		}
		
		public void removeTorrentListUpdatingListener(
				TorrentListUpdatingListener lis) {
			mTorrentListUpdatingListeners.remove(lis);
		}
		
		public void addSortingOrderChangedListener(SortingOrderChangedListener lis) {
			mSortingOrderChangedListeners.add(lis);
		}
		
		public void removeSortingOrderChangedListener(SortingOrderChangedListener lis) {
			mSortingOrderChangedListeners.remove(lis);
		}
		
		private void fireSortingOrderChanged() {
			for (SortingOrderChangedListener lis: mSortingOrderChangedListeners) {
				lis.onSortingOrderChanged(mComparator);
			}
		}
		
		public List<Torrent> getTorrents() {
			return Collections.unmodifiableList(mTorrents);
		}
		
		private List<Torrent> mTorrents = new LinkedList<Torrent>();
		private TorrentListReceiver mTorrentListReceiver 
				= new TorrentListReceiver();
		private SessionStateReceiver mSessionStateReceiver = 
				new SessionStateReceiver();
		private List<TorrentListUpdatingListener> mTorrentListUpdatingListeners = 
				new LinkedList<TorrentListUpdatingListener>();
		private TorrentComparator mComparator;
		private boolean mDescending;
		
		private Map<Integer, TorrentComparator> mMenuItemToComparator = 
				new HashMap<Integer, TorrentComparator>();
		private Map<TorrentComparator, Integer> mComparatorToMenuItem = 
				new HashMap<TorrentComparator, Integer>();
		private List<SortingOrderChangedListener> mSortingOrderChangedListeners =
				new LinkedList<SortingOrderChangedListener>();
	}
	
	public static interface TorrentListUpdatingListener {
		void onTorrentListUpdated(List<Torrent> torrents);
	}
	
	private Storage mStorage;
	private ViewPager mPager;
	private MenuItem mHaveConnectionItem;
	
	private static final int[] sTabTitleIds = new int[] {
		R.string.all,
		R.string.downloading, 
		R.string.seeding, 
		R.string.finished,
		R.string.queued,
	};
	
	private static final String[] sTasStateFilters = new String[] {
		"AllTorrents",
		Torrent.DOWNLOADING,
		Torrent.SEEDING,
		Torrent.FINISHED,
		Torrent.QUEUED,
	};
	
	private static abstract class TorrentComparator implements Comparator<Torrent> {
		
		@Override
		public int compare(Torrent a, Torrent b) {
			if (mDescending) {
				return innerCompare(b, a);
			}
			else {
				return innerCompare(a, b);
			}
		}
		
		protected abstract int innerCompare(Torrent a, Torrent b);
		
		public boolean isDescending() {
			return mDescending;
		}
		
		public void setDescending(boolean descending) {
			mDescending = descending;
		}
		
		private boolean mDescending;
	}
	
	private static class QueueComparator extends TorrentComparator {
		
		@Override
		protected int innerCompare(Torrent a, Torrent b) {
			return a.getQueue() - b.getQueue();
		}
	}
	
	private static class NameComparator extends TorrentComparator {
		
		@Override
		protected int innerCompare(Torrent a, Torrent b) {
			return a.getName().compareTo(b.getName());
		}
	}
	
	private static class SizeComparator extends TorrentComparator {
		
		@Override
		protected int innerCompare(Torrent a, Torrent b) {
			long diff = a.getTotalSize() - b.getTotalSize();
			return diff > 0 ? 1 : (diff < 0 ? -1 : 0);
		}
	}
	
	private static class EtaComparator extends TorrentComparator {
		
		@Override
		protected int innerCompare(Torrent a, Torrent b) {
			return a.getEta() - b.getEta();
		}
	}
	
	private static final String LOG_TAG = 
			TorrentListActivity.class.getSimpleName();
	private static final String TAG_STORAGE = "storage";
}
