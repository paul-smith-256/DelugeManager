package info.itline.delugemanager.ui;

import info.itline.delugemanager.net.DelugeRpcException;
import info.itline.delugemanager.net.ResponseListener;
import android.app.Activity;
import android.widget.Toast;

public class SimpleResponseListener extends ResponseListener {
	
	public SimpleResponseListener(Activity a) {
		mActivity = a;
	}
	
	@Override
	protected void onReceive() {
		try {
			getResult();
		}
		catch (DelugeRpcException e) {
			Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private Activity mActivity;
}
