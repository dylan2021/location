package com.sfmap.route.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sfmap.library.util.DeviceUtil;
import com.sfmap.navi.R;
import com.sfmap.navi.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RouteFailPopupWindow extends PopupWindow {


    @BindView(R2.id.img_latest_icon)
    ImageView imgLatestIcon;
    @BindView(R2.id.tv_retry)
    TextView tvRetry;

    public interface RouteFailListener {
        void reRoute();

    }

    public RouteFailListener routeFailListener;

    public void setRouteFailListener(RouteFailListener listener) {
        routeFailListener = listener;
    }

    private static final String info = "规划路线失败，请重试";

    public RouteFailPopupWindow(final Activity context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mMenuView = inflater.inflate(R.layout.route_fail_layout, null);
        ButterKnife.bind(this, mMenuView);

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager manager = context.getWindowManager();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width2 = outMetrics.widthPixels;
        this.setWidth(width2 - DeviceUtil.dip2px(context,20));
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
//        this.setFocusable(false);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.transparent));
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        initText(context);
        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeFailListener.reRoute();
                dismiss();
            }
        });
    }

    private void initText(Context context) {
        SpannableString msp = new SpannableString(info);
        msp.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.box_red)), info.length() - 2, info.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置下划线
        msp.setSpan(new UnderlineSpan(), info.length() - 2, info.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRetry.setText(msp);
    }
}
