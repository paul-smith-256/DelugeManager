package info.itline.delugemanager.ui;

import info.itline.delugemanager.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ConfirmTorrentRemovingDialog extends DialogFragment {
	
	public static ConfirmTorrentRemovingDialog newInstance(int ownerIdentity, String torrentId) {
		Bundle b = new Bundle();
		b.putString(ARG_TORRENT_ID, torrentId);
		b.putInt(ARG_OWNER_IDENTITY, ownerIdentity);
		ConfirmTorrentRemovingDialog result = new ConfirmTorrentRemovingDialog();
		result.setArguments(b);
		return result;
	}
	
	public int getOwnerIdentity() {
		return getArguments().getInt(ARG_OWNER_IDENTITY);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder result = new AlertDialog.Builder(getActivity());
		result.setPositiveButton(R.string.yes, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mUserInputListener != null) {
					mUserInputListener.onUserInput(getTorrentId(), true);
					dismiss();
				}
			}
		});
		result.setNegativeButton(R.string.no, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mUserInputListener != null) {
					mUserInputListener.onUserInput(getTorrentId(), false);
					dismiss();
				}
			}
		});
		result.setNeutralButton(R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});
		// result.setTitle(R.string.removeTorrent);
		result.setMessage(R.string.removeContent);
		
		return result.create();
	}
	
	public static interface UserInputListener {
		void onUserInput(String torrentId, boolean approved);
	}
	
	public ConfirmTorrentRemovingDialog setUserInputListener(UserInputListener listener) {
		mUserInputListener = listener;
		return this;
	}
	
	public UserInputListener getUserInputListener() {
		return mUserInputListener;
	}
	
	private String getTorrentId() {
		return getArguments().getString(ARG_TORRENT_ID);
	}
	
	private UserInputListener mUserInputListener;
	
	private static final String ARG_TORRENT_ID = "torrentId";
	private static final String ARG_OWNER_IDENTITY = "ownerIdentity";
}
