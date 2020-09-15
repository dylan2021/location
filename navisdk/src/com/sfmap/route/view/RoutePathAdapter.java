package com.sfmap.route.view;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sfmap.navi.R;
import com.sfmap.route.model.RoutePathBean;

import java.util.ArrayList;
import java.util.HashMap;

public class RoutePathAdapter extends BaseQuickAdapter<RoutePathBean, BaseViewHolder> {
    private HashMap<Integer,Boolean > mBooleanArray = new HashMap<>();
    private int mLastCheckedPosition = -1;
    private ArrayList<RoutePathBean> datas = new ArrayList<>();
    private RoutePathAdapter.ItemClickListener listener;

    public void setListener(RoutePathAdapter.ItemClickListener listener) {
        this.listener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(int index);

        void onBehaviorClick();
    }

    public RoutePathAdapter(@Nullable ArrayList<RoutePathBean> data) {
        super(R.layout.route_path_item, data);
        datas = data;
        mLastCheckedPosition = 0;
        for(int i=0;i<datas.size();i++){
            mBooleanArray.put(i,false);
        }
        mBooleanArray.put(0,true);
    }

    @Override
    protected void convert(final BaseViewHolder helper, RoutePathBean item) {
        ((TextView) helper.getView(R.id.tv_path_type)).setText(item.getPathType());
        ((TextView) helper.getView(R.id.tv_path_time)).setText(item.getTime());
        ((TextView) helper.getView(R.id.tv_path_length)).setText(item.getDistance());
        if(null == item.getTrafficLight()){
            ((TextView) helper.getView(R.id.tv_path_signal)).setVisibility(View.GONE);
        }else {
            ((TextView) helper.getView(R.id.tv_path_signal)).setVisibility(View.VISIBLE);
        }
        ((TextView) helper.getView(R.id.tv_path_signal)).setText(item.getTrafficLight());
        ((TextView) helper.getView(R.id.tv_path_tollcost)).setText(item.getTollCost());
        if(mBooleanArray.get(helper.getAdapterPosition())){
            helper.getView(R.id.tv_path_type).setSelected(true);
            helper.getView(R.id.tv_path_time).setSelected(true);
            helper.getView(R.id.tv_path_length).setSelected(true);
            helper.getView(R.id.tv_path_signal).setSelected(true);
            helper.getView(R.id.tv_path_tollcost).setSelected(true);
        }else {
            helper.getView(R.id.tv_path_type).setSelected(false);
            helper.getView(R.id.tv_path_time).setSelected(false);
            helper.getView(R.id.tv_path_length).setSelected(false);
            helper.getView(R.id.tv_path_signal).setSelected(false);
            helper.getView(R.id.tv_path_tollcost).setSelected(false);
        }
        helper.getView(R.id.constraint_line_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLastCheckedPosition == helper.getAdapterPosition()){
//                    listener.onBehaviorClick();
                    return;
                }
                listener.onItemClick(helper.getAdapterPosition());
//                setItemChecked(helper.getAdapterPosition());

            }
        });
    }

    public void setItemChecked(int position) {
        if (mLastCheckedPosition == position){
            listener.onBehaviorClick();
            return;
        }
//        listener.onItemClick(position);
        mBooleanArray.put(position, true);
        mBooleanArray.put(mLastCheckedPosition, false);
        notifyItemChanged(mLastCheckedPosition);
        notifyDataSetChanged();
        mLastCheckedPosition = position;
    }
}
