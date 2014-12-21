package info.itline.delugemanager.ui;

import info.itline.delugemanager.R;
import android.content.res.Resources;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;

final class ActionBarNavigationHelper {

	private ActionBarNavigationHelper() {
	}
	
	@SuppressWarnings("deprecation")
	static void setupNavigation(ActionBarActivity a, int viewPagerId, int[] titleIds, FragmentPagerAdapter adapter) {
		final ActionBar bar = a.getSupportActionBar();
		
		final ViewPager pager = (ViewPager) a.findViewById(viewPagerId);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				bar.setSelectedNavigationItem(position);
			}
		});
		
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		Resources r = a.getResources();
		String[] titles = new String[titleIds.length];
		for (int i = 0; i < titleIds.length; i++) {
			titles[i] = r.getString(titleIds[i]);
		}
		ArrayAdapter<String> navAdapter = new ArrayAdapter<String>(a, 
				R.layout.action_bar_navigation_item, titles);
		bar.setListNavigationCallbacks(navAdapter, new OnNavigationListener() {
			
			@Override
			public boolean onNavigationItemSelected(int pos, long id) {
				pager.setCurrentItem(pos);
				return false;
			}
		});
	}
}
