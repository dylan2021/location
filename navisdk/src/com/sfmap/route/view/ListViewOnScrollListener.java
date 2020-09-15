package com.sfmap.route.view;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.view.View;
import android.widget.AbsListView;

import com.sfmap.route.util.ViewHelper;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * ListView监听器，可在ListView滑动的时候监听显示或隐藏底部footer或者顶部header
 */
public class ListViewOnScrollListener implements AbsListView.OnScrollListener {

    private int minFooterTranslation;
    private int minHeaderTranslation;
    private int prevScrollY = 0;
    private int headerDiffTotal = 0;
    private int footerDiffTotal = 0;
    private int lastVisiblePosition = 0;
    private int lastVisiblePositionY = 0;
    private View view_header;
    private View view_footer;
    private ListenerType listenerType;

    public enum ListenerType {
        HEADER, FOOTER, BOTH
    }

    private static Dictionary<Integer, Integer> sListViewItemHeights = new Hashtable<Integer, Integer>();

    /**
     * 只设置底部footer
     *
     * @param footerView
     * @param footerTranslation
     */
    public ListViewOnScrollListener(View footerView, int footerTranslation) {
        this.listenerType = ListenerType.FOOTER;
        this.view_footer = footerView;
        this.minFooterTranslation = footerTranslation;
    }

    /**
     * 可同时设置顶部和底部footer
     *
     * @param quickReturnType
     * @param headerView
     * @param headerTranslation
     * @param footerView
     * @param footerTranslation
     */
    public ListViewOnScrollListener(ListenerType quickReturnType, View headerView,
                                    int headerTranslation, View footerView, int footerTranslation) {
        listenerType = quickReturnType;
        view_header = headerView;
        minHeaderTranslation = headerTranslation;
        view_footer = footerView;
        minFooterTranslation = footerTranslation;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {

            int midHeader = -minHeaderTranslation / 2;
            int midFooter = minFooterTranslation / 2;

            switch (listenerType) {
                case HEADER:
                    if (-headerDiffTotal > 0 && -headerDiffTotal < midHeader) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                            ViewHelper.setTranslationY(view_header, 0);
                        } else {
                            ObjectAnimator anim = ObjectAnimator.ofFloat(view_header,
                                    "translationY", ViewHelper.getTranslationX(view_header), 0);
                            anim.setDuration(100);
                            anim.start();
                        }
                        headerDiffTotal = 0;
                    } else if (-headerDiffTotal < -minHeaderTranslation
                            && -headerDiffTotal >= midHeader) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                            ViewHelper.setTranslationY(view_header, minHeaderTranslation);
                        } else {
                            ObjectAnimator anim = ObjectAnimator.ofFloat(view_header,
                                    "translationY", ViewHelper.getTranslationX(view_header),
                                    minHeaderTranslation);
                            anim.setDuration(100);
                            anim.start();
                        }
                        headerDiffTotal = minHeaderTranslation;
                    }
                    break;
                case FOOTER:
                    // 未滚动到底部，第二次拖至底部都初始化
                    lastVisiblePosition = 0;
                    lastVisiblePositionY = 0;
                    if (-footerDiffTotal > 0 && -footerDiffTotal < midFooter) { // slide
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                            ViewHelper.setTranslationY(view_footer, 0);
                        } else {
                            ObjectAnimator anim = ObjectAnimator.ofFloat(view_footer,
                                    "translationY", ViewHelper.getTranslationX(view_footer), 0);
                            anim.setDuration(100);
                            anim.start();
                        }
                        footerDiffTotal = 0;
                    } else if (-footerDiffTotal < minFooterTranslation
                            && -footerDiffTotal >= midFooter) { // slide down
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                            ViewHelper.setTranslationY(view_footer, -minFooterTranslation);
                        } else {
                            ObjectAnimator anim = ObjectAnimator.ofFloat(view_footer,
                                    "translationY", ViewHelper.getTranslationX(view_footer),
                                    minFooterTranslation);
                            anim.setDuration(100);
                            anim.start();
                        }

                        footerDiffTotal = -minFooterTranslation;
                    }
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        View v = (View) view.getChildAt(view.getChildCount() - 1);
                        int[] location = new int[2];
                        // 获取在整个屏幕内的绝对坐标
                        v.getLocationOnScreen(location);
                        int y = location[1];

                        if (view.getLastVisiblePosition() != lastVisiblePosition
                                && lastVisiblePositionY != y)// 第一次拖至底部
                        {
                            lastVisiblePosition = view.getLastVisiblePosition();
                            lastVisiblePositionY = y;
                            ViewHelper.setTranslationY(view_footer, 0);
                        } else if (view.getLastVisiblePosition() == lastVisiblePosition
                                && lastVisiblePositionY == y)// 第二次拖至底部
                        {
                            ViewHelper.setTranslationY(view_footer, 0);
                        }
                    }
                    break;
                case BOTH:
                    if (-headerDiffTotal > 0 && -headerDiffTotal < midHeader) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                            ViewHelper.setTranslationY(view_header, 0);
                        } else {
                            ObjectAnimator anim = ObjectAnimator.ofFloat(view_header,
                                    "translationY", ViewHelper.getTranslationX(view_header), 0);
                            anim.setDuration(100);
                            anim.start();
                        }
                        headerDiffTotal = 0;
                    } else if (-headerDiffTotal < -minHeaderTranslation
                            && -headerDiffTotal >= midHeader) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                            ViewHelper.setTranslationY(view_header, minHeaderTranslation);
                        } else {
                            ObjectAnimator anim = ObjectAnimator.ofFloat(view_header,
                                    "translationY", ViewHelper.getTranslationX(view_header),
                                    minHeaderTranslation);
                            anim.setDuration(100);
                            anim.start();
                        }
                        headerDiffTotal = minHeaderTranslation;
                    }

                    if (-footerDiffTotal > 0 && -footerDiffTotal < midFooter) { // slide up
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                            ViewHelper.setTranslationY(view_footer, 0);
                        } else {
                            ObjectAnimator anim = ObjectAnimator.ofFloat(view_footer,
                                    "translationY", ViewHelper.getTranslationX(view_footer), 0);
                            anim.setDuration(100);
                            anim.start();
                        }
                        footerDiffTotal = 0;
                    } else if (-footerDiffTotal < minFooterTranslation
                            && -footerDiffTotal >= midFooter) { // slide down
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                            ViewHelper.setTranslationY(view_footer, -minFooterTranslation);
                        } else {
                            ObjectAnimator anim = ObjectAnimator.ofFloat(view_footer,
                                    "translationY", ViewHelper.getTranslationX(view_footer),
                                    minFooterTranslation);
                            anim.setDuration(100);
                            anim.start();
                        }
                        footerDiffTotal = -minFooterTranslation;
                    }
                    break;
            }

        }
    }

    @Override
    public void onScroll(AbsListView listview, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        int scrollY = getListViewScrollY(listview);
        int diff = prevScrollY - scrollY;
        if (diff != 0) {
            switch (listenerType) {
                case HEADER:
                    if (diff < 0) { // scrolling down
                        headerDiffTotal = Math.max(headerDiffTotal + diff,
                                minHeaderTranslation);
                    } else { // scrolling up
                        headerDiffTotal = Math.min(Math.max(headerDiffTotal
                                + diff, minHeaderTranslation), 0);
                    }
                    ViewHelper.setTranslationY(view_header, headerDiffTotal);
                    break;
                case FOOTER:

                    if (diff < 0) { // scrolling down
                        footerDiffTotal = Math.max(footerDiffTotal + diff,
                                -minFooterTranslation);
                    } else { // scrolling up
                        footerDiffTotal = Math.min(Math.max(footerDiffTotal
                                + diff, -minFooterTranslation), 0);
                    }
                    ViewHelper.setTranslationY(view_footer, -footerDiffTotal);
                    break;
                case BOTH:
                    if (diff < 0) { // scrolling down
                        headerDiffTotal = Math.max(headerDiffTotal + diff,
                                minHeaderTranslation);
                        footerDiffTotal = Math.max(footerDiffTotal + diff,
                                -minFooterTranslation);
                    } else { // scrolling up
                        headerDiffTotal = Math.min(Math.max(headerDiffTotal
                                + diff, minHeaderTranslation), 0);
                        footerDiffTotal = Math.min(Math.max(footerDiffTotal
                                + diff, -minFooterTranslation), 0);
                    }
                    ViewHelper.setTranslationY(view_header, headerDiffTotal);
                    ViewHelper.setTranslationY(view_footer, -footerDiffTotal);
                    break;
            }
        }

        prevScrollY = scrollY;
    }

    public static int getListViewScrollY(AbsListView lv) {
        View c = lv.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = lv.getFirstVisiblePosition();
        int scrollY = -(c.getTop());
        sListViewItemHeights.put(lv.getFirstVisiblePosition(), c.getHeight());
        if (scrollY < 0)
            scrollY = 0;

        for (int i = 0; i < firstVisiblePosition; ++i) {
            if (sListViewItemHeights.get(i) != null)
                scrollY += sListViewItemHeights.get(i);
        }
        return scrollY;
    }
}
