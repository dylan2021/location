package com.sfmap.library.container;

import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

public final class FragmentNodeAdapter extends NodeAdapter {

    private FragmentContainer mViewPager;

    FragmentNodeAdapter(FragmentManager fm, FragmentContainer viewPager) {
	super(fm);
	mViewPager = viewPager;
    }

    @Override
    public int getCount() {
	return mViewPager.getFragmentSize();
    }

    @Override
    public NodeFragment getItem(int position) {
	return mViewPager.getFragmentByPosition(position);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
	super.finishUpdate(container);
    }

    @Override
    public int getItemPosition(Object object) {
	return mViewPager.getItemPostion(object);
    }
}