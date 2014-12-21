package info.itline.delugemanager.ui;

import info.itline.delugemanager.R;
import info.itline.delugemanager.domain.FileInTorrent;
import info.itline.delugemanager.domain.Torrent;
import info.itline.delugemanager.ui.TorrentCardActivity.TorrentStatusChangedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TorrentFilesList 
		extends SortableListFragment<FileInTorrent>
		implements TorrentStatusChangedListener {
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Torrent t = getTorrentCardActivity().getTorrent();
		if (t != null) {
			setItems(t.getFiles());
		}
		getTorrentCardActivity().addTorrentStatusChangedListener(this);
		registerForContextMenu(getListView());
	}
	
	@Override
	public void onDestroyView() {
		getTorrentCardActivity().removeTorrentStatusChangedListener(this);
		super.onDestroyView();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.context_torrent_files_fragment, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (sMenuItemToPriorityMapping.containsKey(item.getItemId())) {
			int pri = sMenuItemToPriorityMapping.get(item.getItemId());
			Torrent t = getTorrentCardActivity().getTorrent();
			List<Integer> pris = new ArrayList<Integer>(t.getFilePriorities());
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			FileInTorrent file = (FileInTorrent) info.targetView.getTag();
			pris.set(file.getIndex(), pri);
			getTorrentCardActivity().getService().setFilePriorities(t.getHash(), pris, 
					new SimpleResponseListener(getActivity()));
			
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onTorrentStatusChanged(Torrent t) {
		setItems(t.getFiles());
	}

	@Override
	protected void prepareView(View v, FileInTorrent item) {
		TextView name = (TextView) v.findViewById(R.id.fileName);
		TextView info = (TextView) v.findViewById(R.id.fileInfo);
		ProgressBar progress = (ProgressBar) v.findViewById(R.id.fileProgress);
		
		name.setText(item.getPath());
		
		Resources r = getResources();
		info.setText(String.format(r.getString(R.string.fileInfoFormat),
				UnitHelper.getSizeWithUnits(item.getSize()),
				r.getString(item.getPriorityDescription())));
		progress.setProgress((int) (item.getProgress() * 100)); 
		
		v.setTag(item);
	}

	@Override
	protected List<ItemComparator<FileInTorrent>> getComparators() {
		Resources r = getResources();
		
		ItemComparator<FileInTorrent> sizeComp = 
				new ItemComparator<FileInTorrent>(
						r.getString(R.string.bySize)) {
			@Override
			protected int compareImpl(FileInTorrent a, FileInTorrent b) {
				long diff = a.getSize() - b.getSize();
				return diff > 0 ? 1 : (diff < 0 ? -1 : 0);
			}
		};
		
		ItemComparator<FileInTorrent> progressComp = 
				new ItemComparator<FileInTorrent>(
						r.getString(R.string.byProgress)) {
			@Override
			protected int compareImpl(FileInTorrent a, FileInTorrent b) {
				return (int) (a.getProgress() - b.getProgress());
			}
		};
		
		ItemComparator<FileInTorrent> nameComp = 
				new ItemComparator<FileInTorrent>(
						r.getString(R.string.byProgress)) {
			@Override
			protected int compareImpl(FileInTorrent a, FileInTorrent b) {
				return a.getPath().compareTo(b.getPath());
			}
		};
		
		List<ItemComparator<FileInTorrent>> result = 
				new LinkedList<ItemComparator<FileInTorrent>>();
		result.add(nameComp);
		result.add(sizeComp);
		result.add(progressComp);
		
		return result;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.row_file_in_torrent;
	}

	private TorrentCardActivity getTorrentCardActivity() {
		return (TorrentCardActivity) getActivity();
	}
	
	private static final Map<Integer, Integer> sMenuItemToPriorityMapping = 
			new HashMap<Integer, Integer>();
	static {
		sMenuItemToPriorityMapping.put(R.id.priorityDontDownload, 	FileInTorrent.PRIORITY_DONT_DOWNLOAD);
		sMenuItemToPriorityMapping.put(R.id.priorityNormal, 		FileInTorrent.PRIORITY_NORMAL);
		sMenuItemToPriorityMapping.put(R.id.priorityHigh,			FileInTorrent.PRIORITY_HIGH);
		sMenuItemToPriorityMapping.put(R.id.priorityMaximum,		FileInTorrent.PRIORITY_MAX);
	}
	
	private static final String LOG_TAG = TorrentFilesList.class.getSimpleName();
}
