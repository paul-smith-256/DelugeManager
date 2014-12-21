package info.itline.delugemanager.ui;

import info.itline.delugemanager.R;
import info.itline.delugemanager.domain.Torrent;
import info.itline.delugemanager.net.ResponseListener;
import info.itline.delugemanager.ui.ConfirmTorrentRemovingDialog.UserInputListener;
import info.itline.delugemanager.ui.TorrentListActivity.SortingOrderChangedListener;
import info.itline.delugemanager.ui.TorrentListActivity.TorrentListUpdatingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TorrentListFragment extends ListFragment 
		implements TorrentListUpdatingListener, UserInputListener, SortingOrderChangedListener {
	
	public static TorrentListFragment newInstance(int fragmentIdentity, String... stateFilter) {
		Bundle b = new Bundle();
		b.putInt(TAG_FRAGMENT_IDENTITY, fragmentIdentity);
		b.putStringArray(TAG_FILTER, stateFilter);
		TorrentListFragment result = new TorrentListFragment();
		result.setArguments(b);
		return result;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTorrentStates = new HashSet<String>();
		Collections.addAll(mTorrentStates, getArguments().getStringArray(TAG_FILTER));
		mFragmentIdentity = getArguments().getInt(TAG_FRAGMENT_IDENTITY);
		mSimpleResponseListener = new SimpleResponseListener(getActivity());
		ConfirmTorrentRemovingDialog dlg = (ConfirmTorrentRemovingDialog) 
				getFragmentManager().findFragmentByTag(TAG_REMOVE_TORRENT_DIALOG);
		if (dlg != null && dlg.getOwnerIdentity() == mFragmentIdentity) {
			dlg.setUserInputListener(this);
		}
	}
	
	@Override	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		TorrentListActivity a = getTorrentListActivity();
		
		mAdapter = new Adapter();	
		mAdapter.setTorrents(filterTorentsByState(
				a.getTorrentList(), mTorrentStates));
		setListAdapter(mAdapter);
		a.addTorrentListUpdatingListener(this);
		
		mAdapter.setItemComparator(a.getCurrentSortingComparator());
		a.addSortingOrderChangedListener(this);
		
		registerForContextMenu(getListView());
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String hash = (String) view.getTag(); // mAdapter.getItem(position).getHash();
				TorrentCardActivity.start(getActivity(), hash);
			}
		});
	}
	
	@Override
	public void onDestroyView() {
		getTorrentListActivity().removeTorrentListUpdatingListener(this);
		getTorrentListActivity().removeSortingOrderChangedListener(this);
		super.onDestroyView();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == android.R.id.list) {
			mSelectedTorrent = mAdapter.getItem(((AdapterContextMenuInfo) menuInfo).position);
			getActivity().getMenuInflater().inflate(R.menu.context_torrent_list_fragment, menu);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (mSelectedTorrent == null) {
			return false;
		}
		String hash = mSelectedTorrent.getHash();
		switch (item.getItemId()) {
		case R.id.resume:
			getTorrentListActivity().getService()
					.resumeTorrent(hash, mSimpleResponseListener);
			return true;
		case R.id.pause:
			getTorrentListActivity().getService()
					.pauseTorrent(hash, mSimpleResponseListener);
			return true;
		case R.id.queueDown:
			getTorrentListActivity().getService()
					.queueTorrentDown(hash, mSimpleResponseListener);
			return true;
		case R.id.queueUp:
			getTorrentListActivity().getService()
					.queueTorrentUp(hash, mSimpleResponseListener);
			return true;
		case R.id.remove:
			ConfirmTorrentRemovingDialog.newInstance(mFragmentIdentity, hash)
					.setUserInputListener(this)
					.show(getFragmentManager(), TAG_REMOVE_TORRENT_DIALOG);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	/* private String getTorrentIdByPosition(MenuItem i) {
		int pos = ((AdapterContextMenuInfo) i.getMenuInfo()).position;
		return mAdapter.getItem(pos).getHash();
	} */
	
	@Override
	public void onUserInput(String torrentId, boolean removeContent) {
		getTorrentListActivity().getService().removeTorrent(torrentId, 
				removeContent, mSimpleResponseListener);
	}

	@Override
	public void onTorrentListUpdated(List<Torrent> torrents) {
		updateTorrentList(filterTorentsByState(torrents, mTorrentStates));
	}
	
	@Override
	public void onSortingOrderChanged(Comparator<Torrent> comparator) {
		mAdapter.setItemComparator(comparator);
	}
	
	private static List<Torrent> filterTorentsByState(List<Torrent> list, Set<String> states) {
		if (states.isEmpty()) {
			return new ArrayList<Torrent>(list);
		}
		else {
			List<Torrent> filtered = new ArrayList<Torrent>();
			for (Torrent t: list) {
				if (states.contains(t.getState())) {
					filtered.add(t);
				}
			}
			return filtered;
		}
	}
	
	private TorrentListActivity getTorrentListActivity() {
		return (TorrentListActivity) getActivity();
	}
	
	private void updateTorrentList(List<Torrent> torrents) {
		Parcelable s = getListView().onSaveInstanceState();
		mAdapter.setTorrents(torrents);
		getListView().onRestoreInstanceState(s);
	}
	
	private class Adapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return mTorrents.size();
		}

		@Override
		public Torrent getItem(int position) {
			return mTorrents.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void setItemComparator(Comparator<Torrent> c) {
			mComparator = c;
			Collections.sort(mTorrents, mComparator);
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View result;
			if (convertView != null) {
				result = convertView;
			}
			else {
				result = getActivity().getLayoutInflater()
						.inflate(R.layout.row_torrent, parent, false);
			}
			TextView name = (TextView) result.findViewById(R.id.torrentName);
			TextView sizeEta = (TextView) result.findViewById(R.id.torrentSizeEta);
			TextView upDown = (TextView) result.findViewById(R.id.torrentUpDown);
			TextView queue = (TextView) result.findViewById(R.id.torrentQueue);
			ProgressBar progress = (ProgressBar) 
					result.findViewById(R.id.torrentProgress);
			ImageView playPause = (ImageView) result.findViewById(R.id.playPause);
			
			Torrent torrent = getItem(position);
			
			Resources r = getResources();
			name.setText(torrent.getName());
			sizeEta.setText(String.format(
					r.getString(R.string.torrentSizeEtaLabel), 
					UnitHelper.getSizeWithUnits(torrent.getTotalSize()),
					UnitHelper.getTimeWithUnits(torrent.getEta())));
			upDown.setText(String.format(
					r.getString(R.string.torrentUpDownLabel),
					UnitHelper.getSpeedWithUnits(torrent.getDownloadPayloadRate()),
					UnitHelper.getSpeedWithUnits(torrent.getUploadPayloadRate())));
			queue.setText(String.format(
					r.getString(R.string.torrentQueueLabel),
					torrent.getQueue()));
			progress.setProgress((int) torrent.getProgress());
			playPause.setBackgroundResource(torrent.isPaused() ? R.drawable.ic_pause : R.drawable.ic_play);
			
			result.setTag(torrent.getHash());
			
			return result;
		}
		
		private void setTorrents(List<Torrent> list) {
			mTorrents = list;
			if (mComparator != null) {
				Collections.sort(mTorrents, mComparator);
			}
			notifyDataSetChanged();
		}

		private List<Torrent> mTorrents = new ArrayList<Torrent>();
		private Comparator<Torrent> mComparator;
	}
	
	private Set<String> mTorrentStates;
	private Adapter mAdapter;
	private ResponseListener mSimpleResponseListener;
	private Torrent mSelectedTorrent;
	private int mFragmentIdentity;
	
	private static final String LOG_TAG = TorrentListActivity.class.getSimpleName();
	private static final String 
			TAG_FILTER = "filter",
			TAG_FRAGMENT_IDENTITY = "fragmentIdentity";
	private static final String TAG_REMOVE_TORRENT_DIALOG = "removeTorrentDialog";
}
