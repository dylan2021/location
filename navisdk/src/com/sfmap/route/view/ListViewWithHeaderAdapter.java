package com.sfmap.route.view;

import android.widget.BaseAdapter;

public abstract class ListViewWithHeaderAdapter extends BaseAdapter {

    private OnNotifyViewChangeListener notifyViewChangeListener;

    public void setOnNotifyViewChangeListener(OnNotifyViewChangeListener l) {
        notifyViewChangeListener = l;
    }

    public interface OnNotifyViewChangeListener {
        void onViewChange();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        if (notifyViewChangeListener != null)
            notifyViewChangeListener.onViewChange();
    }
}
