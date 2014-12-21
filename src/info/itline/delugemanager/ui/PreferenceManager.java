package info.itline.delugemanager.ui;

import android.content.Context;

public class PreferenceManager {
	
	public PreferenceManager(Context c) {
		mContext = c.getApplicationContext();
	}
	
	public int getInfomationUpdatingInterval() {
		return 3000;
	}
	
	public void setInformationUpdatingInterval(int interval) {
		
	}
	
	private Context mContext;
}
