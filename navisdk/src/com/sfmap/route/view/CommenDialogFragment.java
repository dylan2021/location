package com.sfmap.route.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sfmap.navi.R;

import java.lang.reflect.Field;

public class CommenDialogFragment extends DialogFragment {


    private TextView tv_switch, tv_cancel;

    private String content;
    private String confirmText;
    private boolean hideCancel = false;


    public interface OKListener {
        void okClick();

    }

    public OKListener okListener;

    public void setOKListener(OKListener listener) {
        okListener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View root = inflater.inflate(R.layout.dialog_exit, null);
        builder.setView(root);
        builder.create();
        Bundle bundle = getArguments();
        if (bundle != null) {
            content = bundle.getString("content", "确定执行此操作？");
            hideCancel = bundle.getBoolean("hideCancel");
            confirmText = bundle.getString("confirmText","确定");
        }
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置内部背景为透明
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//弹窗不带标题
//        getDialog().getWindow().setWindowAnimations(R.style.AnimBottom);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setDimAmount(0.6f);
//        getDialog().setCanceledOnTouchOutside(true);
        ((TextView) root.findViewById(R.id.tv_code_notice)).setText(content);
        tv_switch = (TextView) root.findViewById(R.id.tv_switch);
        tv_cancel = (TextView) root.findViewById(R.id.tv_cancel);
        tv_cancel.setText(confirmText);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                if(okListener!=null){
                    okListener.okClick();
                }
            }
        });
        tv_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if (hideCancel) {
            tv_switch.setVisibility(View.GONE);
        }
        return root;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            WindowManager manager = this.getActivity().getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(outMetrics);
            int width2 = outMetrics.widthPixels;
            dialog.getWindow().setLayout(width2 * 4 / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }

//    @Override
//    public void show(@NonNull FragmentManager manager,String tag) {
//        try {
//            Class<?> c = Class.forName("androidx.fragment.app.DialogFragment");
//            Field dismissed = c.getDeclaredField("mDismissed");
//            dismissed.setAccessible(true);
//            dismissed.set(this, false);
//            Field shownByMe = c.getDeclaredField("mShownByMe");
//            shownByMe.setAccessible(true);
//            shownByMe.set(this, true);
//            FragmentTransaction fragmentTransaction = manager.beginTransaction();
//            fragmentTransaction.add(this, tag);
//            fragmentTransaction.commitAllowingStateLoss();
////            if (onShowDismissListener != null) onShowDismissListener.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

