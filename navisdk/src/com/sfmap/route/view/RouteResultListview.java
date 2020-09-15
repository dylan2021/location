package com.sfmap.route.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class RouteResultListview extends ListView {

    public interface onResultListviewListener {
        void onFooterShow(int visibility);
    }

    private onResultListviewListener mResultListviewListener;

    public void setOnResultListviewListener(onResultListviewListener l) {
        this.mResultListviewListener = l;
    }

    private boolean isFooterShow = true;
    private final int RAW_Y_LIMIT = 30;
    private float y = -1;

    public RouteResultListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
