package com.sfmap.route.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sfmap.route.view.ListViewWithHeaderAdapter.OnNotifyViewChangeListener;

public abstract class ListViewWithHeader extends ListView implements
        android.widget.AdapterView.OnItemClickListener,
        OnNotifyViewChangeListener {

    private View view_header;
    private View view_footer;
    private int footerHight;

    private OnItemClickListener onItemClickListener;

    public ListViewWithHeader(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public void initView() {

        view_header = initHeaderView();
        view_footer = initFooterView();
        footerHight = getFooterHight();

        setupHeaderView();
        setupFooterView();

        setOnItemClickListener(this);
    }

    private void setupHeaderView() {

        if (view_header != null) {
            addHeaderView(view_header);
            setHeaderDividersEnabled(true);
        }
    }

    private void setupFooterView() {
        if (view_footer != null) {
            addFooterView(view_footer);
            setFooterDividersEnabled(false);
        }
    }

    protected void setOnChildItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0 && onItemClickListener != null)
            onItemClickListener.onItemClick(parent, view, view_header == null ? position : position - 1, id);
    }

    private void goneFooterView() {
        int beforePaddingTop = view_footer.getPaddingTop();

        if (beforePaddingTop != -footerHight) {
            view_footer.setPadding(0, -footerHight, 0, 0);
        }

        view_footer.setVisibility(View.GONE);
    }

    private void visiableFooterView() {
        int beforePaddingTop = view_footer.getPaddingTop();

        if (beforePaddingTop != 0) {
            resetFooterPadding();
        }
        view_footer.setVisibility(View.VISIBLE);
    }

    private void resetFooterPadding() {
        view_footer.setPadding(0, 0, 0, 0);
    }

    @Override
    public void onViewChange() {
        final BaseAdapter adapter = (BaseAdapter) ((HeaderViewListAdapter) this
                .getAdapter()).getWrappedAdapter();

        if (adapter != null) {
            final int count = adapter.getCount();
            if (count <= 0) {
                goneFooterView();
            } else {
                visiableFooterView();
            }
        }
    }

    @Deprecated
    @Override
    public void setAdapter(ListAdapter adapter) {
        throw new RuntimeException("please use setAdapter with ListViewWithHeaderAdapter !!!");
    }

    public void setAdapter(ListViewWithHeaderAdapter adapter) {
        if (adapter != null) {
            adapter.setOnNotifyViewChangeListener(this);
            super.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    protected abstract View initHeaderView();

    protected abstract View initFooterView();

    protected abstract int getFooterHight();
}
