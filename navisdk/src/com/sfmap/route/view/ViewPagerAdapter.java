package com.sfmap.route.view;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.sfmap.map.util.CatchExceptionUtil;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    // 界面列表
    private ArrayList<View> views;

    public ViewPagerAdapter(ArrayList<View> views) {
        this.views = views;
    }

    public void clear() {

        this.views.clear();
        this.notifyDataSetChanged();
    }

    public void replace(ArrayList<View> views) {
        this.views.clear();
        this.views.addAll(views);
    }

    /**
     * 获得当前界面数
     */
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    /**
     * 初始化position位置的界面
     */
    @Override
    public Object instantiateItem(View view, int position) {
        if (view == null) {
            return null;
        }
        ((ViewGroup) view).addView(views.get(position), 0);

        return views.get(position);
    }

    /**
     * 判断是否由对象生成界面
     */
    @Override
    public boolean isViewFromObject(View view, Object arg1) {
        return (view == arg1);
    }

    /**
     * 销毁position位置的界面
     */
    @Override
    public void destroyItem(View view, int position, Object arg2) {
        try {
            if (views.size() > 0) {
                ((ViewGroup) view).removeView(views.get(position));
            }
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
    }

}
