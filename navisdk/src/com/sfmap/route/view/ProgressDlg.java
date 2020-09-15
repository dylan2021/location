package com.sfmap.route.view;


import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sfmap.navi.R;
import com.sfmap.plugin.app.IMPluginDialog;

/**
 *
 */
public class ProgressDlg extends IMPluginDialog {
    private TextView tv_msg;
    OnSearchKeyEvent onSearchKeyEvent = null;

    public static interface OnSearchKeyEvent {
        public void onSearchKeyEvent();
    }

    public void setOnSearchKeyEvent(OnSearchKeyEvent osEvent) {
        onSearchKeyEvent = osEvent;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            if (onSearchKeyEvent != null) {
                onSearchKeyEvent.onSearchKeyEvent();
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event);
    }

    public ProgressDlg(Activity activity){
        this(activity, null);
    }

    public ProgressDlg(Activity context, String msg) {
        super(context, R.style.custom_dlg);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.map_widget_progress_dlg);
        tv_msg = (TextView) findViewById(R.id.msg);
        if (msg != null && !msg.equals("")) {
            tv_msg.setText(msg);
        }
    }

    public ProgressDlg(Activity context, String msg, String additionalMsg) {
        super(context, R.style.custom_dlg);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.map_widget_progress_dlg);
        tv_msg = (TextView) findViewById(R.id.msg);
        TextView tvAdditionalMsg = (TextView) findViewById(R.id.additional_msg);

        if (msg != null && !msg.equals("")) {
            tv_msg.setText(msg);
        }

        if (additionalMsg != null && !additionalMsg.equals("")) {
            tvAdditionalMsg.setText(additionalMsg);
            tvAdditionalMsg.setVisibility(View.VISIBLE);
        } else {
            tvAdditionalMsg.setVisibility(View.GONE);
        }

    }

    public void setMessage(String msg) {
        if (msg != null && !msg.equals("")) {
            tv_msg.setText(msg);
        }
    }

    public void updateMsg(String msg) {
        tv_msg.setText(msg);
    }

}
