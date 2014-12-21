package info.itline.delugemanager.ui;

import info.itline.delugemanager.R;
import info.itline.delugemanager.domain.Peer;
import info.itline.delugemanager.domain.Torrent;
import info.itline.delugemanager.ui.TorrentCardActivity.TorrentStatusChangedListener;

import java.util.LinkedList;
import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TorrentPeersFragment 
		extends SortableListFragment<Peer>
		implements TorrentStatusChangedListener {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Torrent t = getTorrentCardActivity().getTorrent();
		if (t != null) {
			setItems(t.getPeers());
		}
		getTorrentCardActivity().addTorrentStatusChangedListener(this);
	}
	
	@Override
	public void onDestroyView() {
		getTorrentCardActivity().removeTorrentStatusChangedListener(this);
		super.onDestroyView();
	}
	
	@Override
	public void onTorrentStatusChanged(Torrent t) {
		setItems(t.getPeers());
	}
	
	@Override
	protected void prepareView(View v, Peer item) {
		TextView country = (TextView) v.findViewById(R.id.peerCountry);
		TextView ipAddress = (TextView) v.findViewById(R.id.peerIpAddress);
		TextView client = (TextView) v.findViewById(R.id.peerClient);
		TextView upDownSpeed = (TextView) v.findViewById(R.id.peerUpDownSpeed);
		ProgressBar progress = (ProgressBar) v.findViewById(R.id.peerProgress);
		
		country.setText(item.getCountry());
		ipAddress.setText(item.getIp());
		client.setText(item.getClient());
		upDownSpeed.setText(String.format(getResources().getString(R.string.peerUpDownSpeed),
				UnitHelper.getSpeedWithUnits(item.getUpSpeed()),
				UnitHelper.getSpeedWithUnits(item.getDownSpeed())));
		progress.setProgress((int) (item.getProgress() * 100));
	}

	@Override
	protected List<ItemComparator<Peer>> getComparators() {
		Resources r = getResources();
		
		ItemComparator<Peer> countryComparator = 
				new ItemComparator<Peer>(r.getString(R.string.byCountry)) {
			
			@Override
			protected int compareImpl(Peer a, Peer b) {
				return a.getCountry().compareTo(b.getCountry());
			}
		};
		
		ItemComparator<Peer> clientComparator = 
				new ItemComparator<Peer>(r.getString(R.string.byClient)) {
			
			@Override
			protected int compareImpl(Peer a, Peer b) {
				return a.getClient().compareTo(b.getClient());
			}
		};
		
		ItemComparator<Peer> progressComparator = 
				new ItemComparator<Peer>(r.getString(R.string.byProgress)) {
			
			@Override
			protected int compareImpl(Peer a, Peer b) {
				return (int) (a.getProgress() * 100 - b.getProgress() * 100);
			}
		};
		
		ItemComparator<Peer> upSpeedComparator = 
				new ItemComparator<Peer>(r.getString(R.string.byUpSpeed)) {
			
			@Override
			protected int compareImpl(Peer a, Peer b) {
				return a.getUpSpeed() - b.getUpSpeed();
			}
		};
		
		ItemComparator<Peer> downSpeedComparator =
				new ItemComparator<Peer>(r.getString(R.string.byDownSpeed)) {
				
			@Override
			protected int compareImpl(Peer a, Peer b) {
				return a.getDownSpeed() - b.getDownSpeed();
			}
		};
		
		LinkedList<ItemComparator<Peer>> result = new LinkedList<ItemComparator<Peer>>();
		result.add(countryComparator);
		result.add(clientComparator);
		result.add(progressComparator);
		result.add(upSpeedComparator);
		result.add(downSpeedComparator);
		
		return result;
	}
	
	private TorrentCardActivity getTorrentCardActivity() {
		return (TorrentCardActivity) getActivity();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.row_peer;
	}
	
}
