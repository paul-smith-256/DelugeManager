package info.itline.delugemanager.ui;

import info.itline.delugemanager.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class SortableListFragment<E> extends ListFragment {
	
	protected abstract void prepareView(View v, E item);	
	protected abstract List<ItemComparator<E>> getComparators();
	protected abstract int getLayoutId();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		
		mComparators = getComparators();
		
		int comparatorId;
		boolean sortDescending;
		if (savedInstanceState != null) {
			comparatorId = savedInstanceState.getInt(STATE_COMPARATOR_ID, 0);
			sortDescending = savedInstanceState.getBoolean(STATE_SORT_DESCENDING, false);
		}
		else {
			comparatorId = 0;
			sortDescending = false;
		}
		
		ItemComparator<E> c = mComparators.get(comparatorId);
		c.setSortDescending(sortDescending);
		
		mAdapter = new Adapter(c);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_COMPARATOR_ID, mComparators.indexOf(mAdapter.getItemComparator()));
		outState.putBoolean(STATE_SORT_DESCENDING, mAdapter.isSortDescending());
	}
	
	protected void setItems(List<E> items) {
		Parcelable p = getListView().onSaveInstanceState();
		mAdapter.setItems(items);
		getListView().onRestoreInstanceState(p);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		SubMenu sortItem = menu.addSubMenu(Menu.NONE, 
				COMPARATOR_START_ITEM_ID, COMPARATOR_START_ITEM_ID, 
				R.string.sorting);
		sortItem.setIcon(R.drawable.ic_sort);
		MenuItemCompat.setShowAsAction(sortItem.getItem(), 
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		
		int i = 1;
		for (ItemComparator<E> c: mComparators) {
			MenuItem mi = sortItem.add(COMPARATOR_GROUP_ID, 
					COMPARATOR_START_ITEM_ID + i, COMPARATOR_START_ITEM_ID + i, 
					mComparators.get(i - 1).getName());
			i += 1;
			mi.setCheckable(true);
			if (c == mAdapter.getItemComparator()) {
				mi.setChecked(true);
			}
		}
		sortItem.setGroupCheckable(COMPARATOR_GROUP_ID, true, true);
		
		MenuItem desc = sortItem.add(COMPARATOR_GROUP_ID + 1, 
				COMPARATOR_START_ITEM_ID + i, COMPARATOR_START_ITEM_ID + i,
				R.string.sortDescending);
		desc.setCheckable(true);
		desc.setChecked(mAdapter.isSortDescending());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id > COMPARATOR_START_ITEM_ID && 
				id <= COMPARATOR_START_ITEM_ID + mComparators.size()) {
			mAdapter.setItemComparator(mComparators.get(id - COMPARATOR_START_ITEM_ID - 1));
			item.setChecked(true);
			return true;
		}
		else if (id == COMPARATOR_START_ITEM_ID + mComparators.size() + 1) {
			mAdapter.setSortDescending(!item.isChecked());
			item.setChecked(mAdapter.isSortDescending());
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class Adapter extends BaseAdapter {
		
		Adapter(ItemComparator<E> comparator) {
			mComparator = comparator;
		}
		
		@Override
		public int getCount() {
			if (mItems != null) {
				return mItems.size();
			}
			else {
				return 0;
			}
		}

		@Override
		public E getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View result;
			if (convertView != null) {
				result = convertView;
			}
			else {
				result = getActivity().getLayoutInflater().inflate(getLayoutId(), parent, false);
			}
			prepareView(result, mItems.get(position));
			return result;
		}
		
		ItemComparator<E> getItemComparator() {
			return mComparator;
		}
		
		void setItemComparator(ItemComparator<E> comparator) {
			boolean desc = mComparator.isSortDescending();
			mComparator = comparator;
			mComparator.setSortDescending(desc);
			sortItems();
		}
		
		boolean isSortDescending() {
			return mComparator.isSortDescending();
		}
		
		void setSortDescending(boolean v) {
			mComparator.setSortDescending(v);
			sortItems();
		}

		void setItems(List<E> items) {
			ArrayList<E> t = new ArrayList<E>(items);
			Collections.sort(t, mComparator);
			mItems = t;
			notifyDataSetChanged();
		}
		
		private void sortItems() {
			if (mItems != null) {
				Collections.sort(mItems, mComparator);
				notifyDataSetChanged();
			}
		}
		
		private List<E> mItems;
		private ItemComparator<E> mComparator;
	}
	
	public static abstract class ItemComparator<T> implements Comparator<T> {
		
		public ItemComparator(String name) {
			mName = name;
		}
		
		public int compare(T a, T b) {
			if (mSortDescending) {
				return compareImpl(b, a);
			}
			else {
				return compareImpl(a, b);
			}
		}
		
		public String getName() {
			return mName;
		}

		public void setName(String name) {
			mName = name;
		}

		public boolean isSortDescending() {
			return mSortDescending;
		}
		
		public void setSortDescending(boolean v) {
			mSortDescending = v;
		}
		
		protected abstract int compareImpl(T a, T b);
		
		private String mName;
		private boolean mSortDescending;
	}
	
	private Adapter mAdapter;
	private List<ItemComparator<E>> mComparators;
	
	private static final String 
			STATE_COMPARATOR_ID = "comparatorId",
			STATE_SORT_DESCENDING = "sortDescending";
	
	private static final int
			COMPARATOR_START_ITEM_ID 	= 500,
			COMPARATOR_GROUP_ID			= 500;
}
