package com.sfmap.library.container;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Dialog类型的fragment
 */
public class DialogNodeFragment extends NodeFragment implements
        DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {
    /**
     * 基本样式 {@link #setStyle(int, int)}:
     */
    public static final int STYLE_NORMAL = 0;

    /**
     * 没有title的样式 {@link #setStyle(int, int)}
     */
    public static final int STYLE_NO_TITLE = 1;

    /**
     * 没有绘制任何框架的样式
     */
    public static final int STYLE_NO_FRAME = 2;

    /**
     * 无法接收touch事件的样式
     */
    public static final int STYLE_NO_INPUT = 3;

    private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
    private static final String SAVED_STYLE = "android:style";
    private static final String SAVED_THEME = "android:theme";
    private static final String SAVED_CANCELABLE = "android:cancelable";
    private static final String SAVED_SHOWS_DIALOG = "android:showsDialog";
    private static final String SAVED_BACK_STACK_ID = "android:backStackId";

    int mStyle = STYLE_NORMAL;
    int mTheme = 0;
    boolean mCancelable = true;
    boolean mShowsDialog = true;
    int mBackStackId = -1;

    Dialog mDialog;
    boolean mViewDestroyed;
    boolean mDismissed;
    boolean mShownByMe;

    /**
     * 设置样式
     *
     * @param style 支持的样式：{@link #STYLE_NORMAL},
     *              {@link #STYLE_NO_TITLE}, {@link #STYLE_NO_FRAME}, or
     *              {@link #STYLE_NO_INPUT}.
     * @param theme 自定义标题,如果未设置采用默认样式
     */
    public void setStyle(int style, int theme) {
        mStyle = style;
        if (mStyle == STYLE_NO_FRAME || mStyle == STYLE_NO_INPUT) {
            mTheme = android.R.style.Theme_Panel;
        }
        if (theme != 0) {
            mTheme = theme;
        }
    }

    /**
     * 显示dialog
     *
     * @param manager FragmentManager
     * @param tag     fragment的tag {@link FragmentTransaction#add(Fragment, String)
     *                FragmentTransaction.add}
     */
    public void show(FragmentManager manager, String tag) {
        mDismissed = false;
        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commit();
        isHide = false;
    }

    /**
     * 显示dialog 通过事务处理方式
     *
     * @param transaction 存在的transaction
     * @param tag         fragment的tag {@link FragmentTransaction#add(Fragment, String)
     *                    FragmentTransaction.add}
     * @return 提交事务的标示
     */
    int show(FragmentTransaction transaction, String tag) {
        mDismissed = false;
        mShownByMe = true;
        transaction.add(this, tag);
        mViewDestroyed = false;
        mBackStackId = transaction.commit();
        isHide = false;
        return mBackStackId;
    }

    private boolean isHide = false;

    /**
     * 隐藏fragment
     */
    public void hideFragment() {
        if (mDialog != null) {
            mDialog.hide();
            isHide = true;
        }
    }

    /**
     * @deprecated
     */
    @Override
    public void onResume() {
        super.onResume();
        if (isHide) {
            hideFragment();
        }
    }

    /**
     * 结束fragmnet
     */
    @Override
    public void finishFragment() {
        getFragmentContainerDelegater().getFragmentContainer()
                .transferResultToReceiver(this);
        dismissAllowingStateLoss();
    }


    /**
     * dialog弹框消失
     * (如果fragment被添加到容器内,将处于容器的最底层。否者会移除fragment)
     */
    public void dismiss() {
        dismissInternal(false);
    }


    /**
     * 移除fragment(允许状态值丢失的)
     * {@link FragmentTransaction#commitAllowingStateLoss()
     * FragmentTransaction.commitAllowingStateLoss()}.
     */
    public void dismissAllowingStateLoss() {
        dismissInternal(true);
    }

    private void dismissInternal(boolean allowStateLoss) {
        if (mDismissed) {
            return;
        }
        mDismissed = true;
        mShownByMe = false;
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mViewDestroyed = true;
        if (mBackStackId >= 0) {
            getFragmentManager().popBackStack(mBackStackId,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mBackStackId = -1;
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(this);
            if (allowStateLoss) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commit();
            }
        }
    }

    /**
     * 返回dialog
     *
     * @return dialog
     */
    public Dialog getDialog() {
        return mDialog;
    }

    /**
     * 返回主题
     * @return  主题样式的id索引值
     */
    public int getTheme() {
        return mTheme;
    }

    /**
     * 设置点击窗口外dialog是否会消失
     * @param cancelable true可以消失否者反之
     */
    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        if (mDialog != null)
            mDialog.setCancelable(cancelable);
    }

    /**
     *
     * 判断点击窗口外dialog窗口是否会消失
     * @return
     */
    public boolean isCancelable() {
        return mCancelable;
    }

    /**
     * 设置dialog显示状态
     *
     * @param showsDialog true显示 false不显示
     */
    public void setShowsDialog(boolean showsDialog) {
        mShowsDialog = showsDialog;
    }

    /**
     * 判断dialog是否会被显示
     *
     * @return true显示 false不显示
     */
    public boolean getShowsDialog() {
        return mShowsDialog;
    }

    /**
     * 关联activity触发
     * @param activity
     * @deprecated
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!mShownByMe) {
            // If not explicitly shown through our API, take this as an
            // indication that the dialog is no longer dismissed.
            mDismissed = false;
        }
    }

    /**
     * 和activity分离时候触发
     * @deprecated
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (!mShownByMe && !mDismissed) {
            // The fragment was not shown by a direct call here, it is not
            // dismissed, and now it is being detached... well, okay, thou
            // art now dismissed. Have fun.
            mDismissed = true;
        }
    }

    /**
     * @deprecated
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowsDialog = true;
        setStyle(DialogFragment.STYLE_NO_FRAME,
                android.R.style.Theme_Holo_Dialog);

    }

    /**
     * @deprecated
     */
    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        if (!mShowsDialog) {
            return super.getLayoutInflater(savedInstanceState);
        }

        mDialog = onCreateDialog(savedInstanceState);
        switch (mStyle) {
            case STYLE_NO_INPUT:
                mDialog.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                // fall through...
            case STYLE_NO_FRAME:
            case STYLE_NO_TITLE:
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (mDialog != null) {
            return (LayoutInflater) mDialog.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        return (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     *
     *@deprecated
     * @param savedInstanceState
     * @return 返回一个创建的dialog
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme());
    }

    /**
     * @deprecated
     * @param dialog
     */
    public void onCancel(DialogInterface dialog) {
    }

    /**
     * dialog声明周期,该生命周期等同fragment生命周期
     * @deprecated
     * @param dialog
     * @deprecated
     */
    public void onDismiss(DialogInterface dialog) {
        if (!mViewDestroyed) {
            // Note: we need to use allowStateLoss, because the dialog
            // dispatches this asynchronously so we can receive the call
            // after the activity is paused. Worst case, when the user comes
            // back to the activity they see the dialog again.
            dismissInternal(true);
        }
    }

    /**
     * @deprecated
     * 关联activity的时候执行
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!mShowsDialog) {
            return;
        }

        View view = getView();
        if (view != null) {
            if (view.getParent() != null) {
                throw new IllegalStateException(
                        "DialogFragment can not be attached to a container view");
            }
            mDialog.setContentView(view);
        }
        mDialog.setOwnerActivity(getActivity());
        mDialog.setCancelable(mCancelable);
        mDialog.setOnCancelListener(this);
        mDialog.setOnDismissListener(this);
        if (savedInstanceState != null) {
            Bundle dialogState = savedInstanceState
                    .getBundle(SAVED_DIALOG_STATE_TAG);
            if (dialogState != null) {
                mDialog.onRestoreInstanceState(dialogState);
            }
        }
    }

    /**
     * dialog声明周期,该生命周期等同fragment生命周期
     */
    @Override
    public void onStart() {
        super.onStart();
        if (mDialog != null) {
            mViewDestroyed = false;
            mDialog.show();
        }
    }

    /**
     * 用Bundle保存数据或者状态
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDialog != null) {
            Bundle dialogState = mDialog.onSaveInstanceState();
            if (dialogState != null) {
                outState.putBundle(SAVED_DIALOG_STATE_TAG, dialogState);
            }
        }
        if (mStyle != STYLE_NORMAL) {
            outState.putInt(SAVED_STYLE, mStyle);
        }
        if (mTheme != 0) {
            outState.putInt(SAVED_THEME, mTheme);
        }
        if (!mCancelable) {
            outState.putBoolean(SAVED_CANCELABLE, mCancelable);
        }
        if (!mShowsDialog) {
            outState.putBoolean(SAVED_SHOWS_DIALOG, mShowsDialog);
        }
        if (mBackStackId != -1) {
            outState.putInt(SAVED_BACK_STACK_ID, mBackStackId);
        }
    }

    /**
     * 暂停dialog,调用该方法会隐藏dialog
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mDialog != null) {
            mDialog.hide();
        }
    }

    /**
     * 销毁dialog
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDialog != null) {
            // Set removed here because this dismissal is just to hide
            // the dialog -- we don't want this to cause the fragment to
            // actually be removed.
            mViewDestroyed = true;
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
