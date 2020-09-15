package com.sfmap.library.container;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import com.sfmap.map.api.BuildConfig;

import java.util.ArrayList;


/**
 * Implementation of {@link PagerAdapter} that uses a
 * {@link Fragment} to manage each page. This class also handles saving and
 * restoring of fragment's state.
 * 
 * <p>
 * This version of the pager is more useful when there are a large number of
 * pages, working more like a list view. When pages are not visible to the user,
 * their entire fragment may be destroyed, only keeping the saved state of that
 * fragment. This allows the pager to hold on to much less memory associated
 * with each visited page as compared to {@link FragmentPagerAdapter} at the
 * cost of potentially more overhead when switching between pages.
 * 
 * <p>
 * When using FragmentPagerAdapter the host ViewPager must have a valid ID set.
 * </p>
 * 
 * <p>
 * Subclasses only need to implement {@link #getItem(int)} and
 * {@link #getCount()} to have a working adapter.
 * 
 * <p>
 * Here is an example implementation of a pager containing fragments of lists:
 * 
 * {@sample
 * development/samples/Support13Demos/src/com/example/android/supportv13/app/
 * FragmentStatePagerSupport.java complete}
 * 
 * <p>
 * The <code>R.layout.fragment_pager</code> resource of the top-level fragment
 * is:
 * 
 * {@sample development/samples/Support13Demos/res/layout/fragment_pager.xml
 * complete}
 * 
 * <p>
 * The <code>R.layout.fragment_pager_list</code> resource containing each
 * individual fragment's layout is:
 * 
 * {@sample
 * development/samples/Support13Demos/res/layout/fragment_pager_list.xml
 * complete}
 */
public abstract class NodeAdapter extends PagerAdapter {
	private static final String TAG = "NodeAdapter";
	private static final boolean DEBUG = false;

	private final FragmentManager mFragmentManager;
	private FragmentTransaction mCurTransaction = null;

	// private ArrayList<Fragment.SavedState> mSavedState = new
	// ArrayList<Fragment.SavedState>();
	private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
	private Fragment mCurrentPrimaryItem = null;

	public NodeAdapter(FragmentManager fm) {
		mFragmentManager = fm;
	}

	/**
	 * Return the Fragment associated with a specified position.
	 */
	public abstract NodeFragment getItem(int position);

	public FragmentManager getFragmentManager() {
		return mFragmentManager;
	}

	@Override
	public void startUpdate(ViewGroup container) {
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// If we already have this item instantiated, there is nothing
		// to do. This can happen when we are restoring the entire pager
		// from its saved state, where the fragment manager has already
		// taken care of restoring the fragments we previously had instantiated.
		if (mFragments.size() > position) {
			Fragment f = mFragments.get(position);
			if (f != null) {
				return f;
			}
		}

		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}

		Fragment fragment = getItem(position);
		if (DEBUG)
			Log.v(TAG, "Adding item #" + position + ": f=" + fragment);
		// if (mSavedState.size() > position) {
		// Fragment.SavedState fss = mSavedState.get(position);
		// if (fss != null) {
		// fragment.setInitialSavedState(fss);
		// }
		// }
		while (mFragments.size() <= position) {
			mFragments.add(null);
		}
		if (position > 0) {
			NodeFragment lastFragment = getItem(position - 1);
			pauseLastFragment(lastFragment); // 把上一个Fragment置为Hide状态，这样当再次退栈至此Fragment时系统才会自动回调该Fragment的onResume方法
		}
		fragment.setMenuVisibility(false);
		fragment.setUserVisibleHint(false);
		mFragments.set(position, fragment);
//		mFragments.add(fragment);
		mCurTransaction.add(container.getId(), fragment);
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		Fragment fragment = (Fragment) object;

		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		if (DEBUG)
			Log.v(TAG, "Removing item #" + position + ": f=" + object + " v="
					+ ((Fragment) object).getView());
		// while (mSavedState.size() <= position) {
		// mSavedState.add(null);
		// }
		// mSavedState.set(position,
		// mFragmentManager.saveFragmentInstanceState(fragment));
		mFragments.set(position, null);

		mCurTransaction.remove(fragment);
	}

	public void resumeLastFragment(Fragment lastFragment) {
		if (lastFragment == null)
			return;
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		mCurTransaction.show(lastFragment);
	}

	public void pauseLastFragment(Fragment lastFragment) {
		if (lastFragment == null)
			return;
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		if (lastFragment instanceof NodeFragment) {
			((NodeFragment) lastFragment).onTurnPage();
		}
		mCurTransaction.hide(lastFragment);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		Fragment fragment = (Fragment) object;
		if (fragment != mCurrentPrimaryItem) {
			if (mCurrentPrimaryItem != null) {
				mCurrentPrimaryItem.setMenuVisibility(false);
				mCurrentPrimaryItem.setUserVisibleHint(false);
			}
			if (fragment != null) {
				fragment.setMenuVisibility(true);
				fragment.setUserVisibleHint(true);
			}
			mCurrentPrimaryItem = fragment;
		}
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
//			if (!mFragmentManager.isExecutingActions()) {
			if (!mFragmentManager.executePendingTransactions()) {
				mFragmentManager.executePendingTransactions();
			} else {
				if (BuildConfig.DEBUG) {
//		    ToastUtil.longTips("重复执行Fragment堆栈的添加/删除动作，请检查页面跳转逻辑是否正常！");
				}
			}
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ((Fragment) object).getView() == view;
	}

	@Override
	public Parcelable saveState() {
		Bundle state = null;
		for (int i = 0; i < mFragments.size(); i++) {
			Fragment f = mFragments.get(i);
			if (f != null) {
//				if (f.mIndex < 0) { //如果fragment已经处于inactive状态则不保存状态
//					continue;
//				}
				if (state == null) {
					state = new Bundle();
				}
				String key = "f" + i;
				mFragmentManager.putFragment(state, key, f);
			}
		}
		return state;
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		if (state != null) {
			Bundle bundle = (Bundle) state;
			bundle.setClassLoader(loader);
			// Parcelable[] fss = bundle.getParcelableArray("states");
			// mSavedState.clear();
			mFragments.clear();
			// if (fss != null) {
			// for (int i=0; i<fss.length; i++) {
			// mSavedState.add((Fragment.SavedState)fss[i]);
			// }
			// }
			Iterable<String> keys = bundle.keySet();
			for (String key : keys) {
				if (key.startsWith("f")) {
					int index = Integer.parseInt(key.substring(1));
					try {
						final Fragment f = mFragmentManager.getFragment(bundle,
								key);
						if (f != null) {
							while (mFragments.size() <= index) {
								mFragments.add(null);
							}
							f.setMenuVisibility(false);
							mFragments.set(index, f);
						} else {
							Log.w(TAG, "Bad fragment at key " + key);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
