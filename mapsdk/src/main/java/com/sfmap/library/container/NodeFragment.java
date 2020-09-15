package com.sfmap.library.container;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.sfmap.library.Page;
import com.sfmap.library.container.FragmentContainer.IntentControlerDelegater;
import com.sfmap.library.container.PerformanceAnalyzer.Category;
import com.sfmap.library.container.annotation.ViewInjector;
import com.sfmap.library.util.CatchExceptionUtil;
import com.sfmap.map.api.BuildConfig;
import com.sfmap.plugin.IMPluginManager;
import com.sfmap.plugin.IMPluginMsg;
import com.sfmap.plugin.MsgCallback;
import com.sfmap.plugin.app.IMPageHelper;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 通用的自定义Fragment
 */
public abstract class NodeFragment extends Fragment implements Page {

    // private static final String TAG = "NodeFragment";
    public static final int INVALID_REQUEST_CODE = -1;

    private NodeFragmentBundle mNodeFragmentBundle;

    /**
     * 跳转Fragment之间数据的状态
     */
    public static enum ResultType {
        /**
         * 默认状态
         */
        NONE,
        /**
         * 传递数据类型可以标示
         */
        OK,
        /**
         * 传递类型不可用标示
         */
        CANCEL
    }

    /**
     * 调用onBackPressed时候的枚举类型
     */
    public static enum ON_BACK_TYPE {
        /**
         * Fragment没有处理此事件，按正常逻辑交给Activity处理
         */
        TYPE_NORMAL,
        /**
         * 通知Activity直接退出整个程序
         */
        TYPE_FINISH,
        /**
         * Fragment响应了此事件，Activity会忽略此事件
         */
        TYPE_IGNORE,
    }

    /**
     * 构造器
     */
    public NodeFragment(){

    }

    protected FragmentActivity mBaseActivity;
    protected ResultType mResultType = ResultType.NONE;
    protected NodeFragmentBundle mResultData;
    protected int mRequestCode = INVALID_REQUEST_CODE;
    private WeakReference<NodeFragment> mFragmentResultReciver;
    private boolean mHasNewBundle;
    private boolean mIsPaused;
    private boolean mIsActive;

     /**
     * 提供给子类重写,调用super.onViewCreated(view, savedInstanceState);
     * 时候执行Fragment的{@linkplain Fragment#onViewCreated(View view, Bundle savedInstanceState)}
     * @param view                  视图对象
     * @param savedInstanceState    Bundle对象
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsActive = true;
        ViewInjector.inject(this, view);// 注册注解方式的点击时间响应
    }

    /**
     * 提供给子类重写,调用super.onHiddenChanged(hidden),
     * 时候执行Fragment的{@linkplain Fragment#onHiddenChanged(boolean hidden)}。
     * @param hidden    是否隐藏
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (pageStateListener != null) {
                pageStateListener.onPageHidden();
            }
            performPause();
        } else {
        // performResume();
            if(getLifecycle().getCurrentState()==Lifecycle.State.RESUMED){
                onResume();
            }
        }
    }

    private PageStateListener pageStateListener;

    /**
     * 设置页面监听
     * @param listener
     */
    @Override
    public void setPageStateListener(PageStateListener listener) {
        pageStateListener = listener;
    }

    /**
     * NodeFragment附属的Activity执行
     * {@linkplain Activity#onWindowFocusChanged(boolean)}
     * 时会同时执行堆栈顶部的NodeFragment的{@linkplain #onWindowFocusChanged(boolean)}。
     * @param hasFocus
     */
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    /**
     * 返回container的BaseActivity
     * @return
     */
//     public FragmentActivity getActivity() {
//        return super.getActivity() != null ? super.getActivity()
//                : mBaseActivity;
//    }

    /**
     * 返回FragmentContainer代理
     * @return  一个FragmentContainerDelegater对象
     */
    public FragmentContainerDelegater getFragmentContainerDelegater() {
        final Activity activity = getActivity();
        if (activity == null) {
            throw new RuntimeException("Fragment没有attach到Activity中或者已经detach！");
        }
        if (activity instanceof FragmentContainerDelegater) {
            return (FragmentContainerDelegater) activity;
        }
        throw new RuntimeException(
                "Fragment附属的Activity没有实现FragmentContainerDelegater接口！");
    }

    /**
     * 返回Intent信息代理
     * @return  一个IntentControlerDelegater对象
     */
    public IntentControlerDelegater getIntentControlerDelegater() {
        final Activity activity = getActivity();
        if (activity == null) {
            throw new RuntimeException("Fragment没有attach到Activity中或者已经detach！");
        }
        if (activity instanceof IntentControlerDelegater) {
            return (IntentControlerDelegater) activity;
        }
        throw new RuntimeException(
                "Fragment附属的Activity没有实现IntentControlerDelegater接口！");
    }

    /**
     * 根据intent意图启动一个fragmnet
     * @param intent 意图
     */
    public void startScheme(Intent intent) {
        IntentControlerDelegater intentControlerDelegater = getIntentControlerDelegater();
        intentControlerDelegater.solveSchema(intent);
    }

    /**
     * 添加一个fragment
     *
     * @param clazz 类类型
     * @return      一个启动的NodeFragment对象
     */
    public NodeFragment startFragment(Class<? extends NodeFragment> clazz) {
        return startFragment(clazz, null);
    }

    /**
     * 添加一个Fragment,传入NodeFragmentBundle模型数据。
     *
     * @param clazz     类类型
     * @param extraData 一个NodeFragmentBundle对象
     * @return          一个启动的NodeFragment对象
     */
    public NodeFragment startFragment(Class<? extends NodeFragment> clazz, NodeFragmentBundle extraData) {
        return getFragmentContainerDelegater().getFragmentContainer()
                .addFragment(clazz, extraData, INVALID_REQUEST_CODE, true,
                        false);
    }

    /**
     * 添加一个Fragment,页面结束时候可以回传数据。
     *
     * @param clazz         类类型
     * @param requestCode   启动请求码
     * @return              一个启动的NodeFragment对象
     */
    public NodeFragment startFragmentForResult(
            Class<? extends NodeFragment> clazz, int requestCode) {
        return startFragmentForResult(clazz, null, requestCode);
    }

    /**
     * 添加一个Fragment,传入NodeFragmentBundle模型数据,页面结束时候可以回传数据。
     *
     * @param clazz         类类型
     * @param extraData     一个NodeFragmentBundle对象
     * @param requestCode   启动请求码
     * @return              一个启动的NodeFragment对象
     */
    public NodeFragment startFragmentForResult(
            Class<? extends NodeFragment> clazz, NodeFragmentBundle extraData,
            int requestCode) {
        return getFragmentContainerDelegater().getFragmentContainer()
                .addFragment(clazz, extraData, requestCode, true, false);
    }

    /**
     * 添加一个Fragment,启动方式为intent意图开启。
     * @param intent   一个intent意图对象
     */
    public void startFragment(final Intent intent) {
        IMPluginMsg msg = new IMPluginMsg(intent.getPackage(),
                IMPageHelper.PAGE_INFO_CMD);
        msg.put("action", intent.getAction());
        IMPluginManager.sendMsg(msg, new MsgCallback() {
            @Override
            public void callback(Map<String, Object> result) {
                Class<? extends NodeFragment> cls = (Class<? extends NodeFragment>) result
                        .get(IMPageHelper.PAGE_CLASS_KEY);
                startFragment(cls, new NodeFragmentBundle(intent));
            }

            @Override
            public void error(Throwable ex, boolean isCallbackError) {
                CatchExceptionUtil
                        .normalPrintStackTrace(ex);
            }
        });
    }

    /**
     * 替换容器内存在的一个Fragment
     * @param clazz     类类型
     * @return          一个NodeFragment对象
     */
    public NodeFragment replaceFragment(
            Class<? extends NodeFragment> clazz) {
        return replaceFragment(clazz, null);
    }

    /**
     * 替换容器内存在的一个Fragment,并且传入NodeFragmentBundle模型数据。
     * @param clazz     类类型
     * @param extraData 一个NodeFragmentBundle对象
     * @return          一个NodeFragment对象
     */
    public NodeFragment replaceFragment(
            Class<? extends NodeFragment> clazz, NodeFragmentBundle extraData) {
        return getFragmentContainerDelegater().getFragmentContainer()
                .replaceFragment(clazz, extraData, INVALID_REQUEST_CODE);
    }

    /**
     * 结束当前Fragment(关闭软键盘)
     */
    public void finishFragment() {
        finishFragment(true);
    }

    /**
     * 结束当前Fragment(是否需要关闭软键盘)
     * @param isHiddenSoftkeyboard  是否需要关闭软键盘
     */
    public void finishFragment(boolean isHiddenSoftkeyboard) {
        if (isHiddenSoftkeyboard) {
            final Activity activity = getActivity();
            if (activity != null) {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    final View view = activity.getCurrentFocus();
                    if (view != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        }
        if (getActivity() != null) {
            getFragmentContainerDelegater().getFragmentContainer()
                    .removeLastFragment(null, false);
        }
    }

    /**
     * 如果Fragment设置了LaunchMode(例如singleTask)，并且当前Fragment栈内已经有该类型的Fragment，
     * 则重用Fragment并通过该方法回传新数据。
     * @param newExtraData  一个NodeFragmentBundle对象
     */
    protected void onNewNodeFragmentBundle(NodeFragmentBundle newExtraData) {

    }

    /**
     * 标示当前Fragment因为特殊的LaunchMode(例如singleTask)，在没有初始化生命周期前就又被某些逻辑再次启动，
     * 此时不能直接把新数据通过{@linkplain #onNewNodeFragmentBundle(NodeFragmentBundle)}
     * 传递给Fragment。
     */
    final protected void setHasNewBundle() {
        mHasNewBundle = true;
    }

    /**
     * 关闭堆栈中指定位置以下的fragment
     *
     * @param step 需要关闭的Fragment的数量。
     */
    final public void finishFragmentByStep(int step) {
        getFragmentContainerDelegater().getFragmentContainer().removeFragments(
                step, null, false);
    }

    /**
     * 除了容器最上层的Fragment的其他Fragment全部关闭。
     */
    final public void finishAllFragmentsWithoutRoot() {
        getFragmentContainerDelegater().getFragmentContainer()
                .removeAllFragmentsWithoutRoot();
    }

     protected boolean isTopActiveFragment() {
        if (getFragmentContainerDelegater().getFragmentContainer()
                .isTopActiveFragment(this)) {
            return true;
        }
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof NodeFragment) {
            return getFragmentContainerDelegater().getFragmentContainer()
                    .isTopActiveFragment((NodeFragment) getParentFragment());
        }
        return false;
    }

    /***
     * 提供给子类复写
     * @return
     */
    public ON_BACK_TYPE onBackPressed() {
        return ON_BACK_TYPE.TYPE_NORMAL;
    }

    /**
     * 监听手机屏幕上的按键
     * @param keyCode
     * @param event
     * @return
     * @deprecated
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    protected void onRequestDisallowTouchEvent(MotionEvent event) {
        getFragmentContainerDelegater().getFragmentContainer()
                .onInterceptTouchEvent(event);
        getFragmentContainerDelegater().getFragmentContainer()
                .requestDisallowInterceptTouchEvent(false);
    }

    protected void setResultReceiver(NodeFragment receiver) {
        mFragmentResultReciver = new WeakReference<NodeFragment>(receiver);
    }

    protected ResultType getResultType() {
        return mResultType;
    }

    protected NodeFragmentBundle getResult() {
        return mResultData;
    }

    protected int getRequestCode() {
        return mRequestCode;
    }

    /**
     * fragment结束前调用,通过该方法可以像上一个fragment传递数据。
     * @param resultType    返回结果类型
     */
    public void setResult(ResultType resultType) {
        setResult(resultType, null);
    }

    /**
     * fragment结束前调用,通过该方法可以像上一个fragment传递数据。
     * @param resultType    返回结果类型
     */
    public final void setResult(ResultType resultType,
                                NodeFragmentBundle resultData) {
        mResultType = resultType;
        mResultData = resultData;
        if (mFragmentResultReciver != null
                && mFragmentResultReciver.get() != null) {
            mFragmentResultReciver.get().onFragmentResult(this.getClass(),
                    getRequestCode(), mResultType, mResultData);
        }
    }

    protected final void cleanResult() {
        mResultType = ResultType.NONE;
        mResultData = null;
    }

    protected boolean hasResult() {
        return mResultType != ResultType.NONE || mResultData != null;
    }

    /**
     *
     * 接收启动的其他AnimationBaseFragment传递回来的数据。
     * @param clazz       传递数据的AnimationBaseFragment的具体类名，以便标示到底是哪个Fragment传递的数据。
     * @param requestCode 请求的code
     * @param resultType  数据的状态
     * @param extraData   包含具体数据的Intent
     */
    public void onFragmentResult(Class<? extends NodeFragment> clazz,
                                 int requestCode, ResultType resultType, NodeFragmentBundle extraData) {

    }

    protected void registerReceiver(BroadcastReceiver receiver,
                                    IntentFilter filter) {
        if (isAdded()) {
            getActivity().registerReceiver(receiver, filter);
        }
    }

    protected void unregisterReceiver(BroadcastReceiver receiver) {
        if (getActivity() != null && receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }

    /**
     * 提供给子类重写,调用super.onDestroy()时候执行NodeFragment的{@linkplain NodeFragment#onResume(),
     * 并且同时执行堆栈顶部的Fragment的{@linkplain #onResume()}。
     * */
    @Override
    public void onResume() {
        super.onResume();
        if (mHasNewBundle) { // 如果有保存的未被传递的新数据，此时回传
            onNewNodeFragmentBundle(getNodeFragmentArguments());
            mHasNewBundle = false;
        }
        if (BuildConfig.DEBUG) {
            System.out.println(this + "onResume~~~~~~");
        }
        mIsPaused = false;
    }

    /**
     * 提供给子类重写,调用super.onPause()时候执行NodeFragment的{@linkplain NodeFragment#onPause(),
     * 并且同时执行堆栈顶部的Fragment的{@linkplain #onPause()}。
     * */
    @Override
    public void onPause() {
        PerformanceAnalyzer.startRecordPerformance(this, Category.Fragment,
                "onPause");
        super.onPause();
        mIsPaused = true;
        if (BuildConfig.DEBUG) {
            System.out.println(this + "onPause~~~~~~");
        }
        PerformanceAnalyzer.stopRecordPerformance(this, Category.Fragment,
                "onPause");
    }

    /**
     * 当前页面跳转到其他页面前将回调此方法，该方法不同于onPause，onPause可以被屏幕熄灭等事件触发，而该方法只有真正要跳转其他页面时才会触发。
     */
    public void onTurnPage() {

    }

    /**
     * 判断页面是否处于暂停状态
     * @return  true暂停否者反之
     */
    public boolean isPaused() {
        return mIsPaused;
    }

    /**
     * 判断页面是否处于活跃状态
     * @return  true活跃状态否者反之
     */
    public boolean isActive() {
        return mIsActive;
    }

    protected final void performPause() {
        if (isTopActiveFragment()) {
//            onPause();
        }
    }
//
//    @Override
//    protected final void performResume() {
//        if (isTopActiveFragment()) {
//            PerformanceAnalyzer.startRecordPerformance(this, Category.Fragment, "onResume");
//            onResume();
//            if (mHasNewBundle) { // 如果有保存的未被传递的新数据，此时回传
//                onNewNodeFragmentBundle(getNodeFragmentArguments());
//                mHasNewBundle = false;
//            }
//
//            PerformanceAnalyzer.stopRecordPerformance(this, Category.Fragment, "onResume");
//        }
//    }
//
//    protected final void performStart() {
//        if (isTopActiveFragment()) {
//            PerformanceAnalyzer.startRecordPerformance(this, Category.Fragment, "onStart");
//            onStart();
//            PerformanceAnalyzer.stopRecordPerformance(this, Category.Fragment, "onStart");
//        }
//    }
//
//    protected final void performStop() {
//        if (isTopActiveFragment()) {
//            PerformanceAnalyzer.startRecordPerformance(this, Category.Fragment, "onStop");
//            onStop();
//            PerformanceAnalyzer.stopRecordPerformance(this, Category.Fragment, "onStop");
//        }
//    }
//
//    protected final void performCreate(Bundle savedInstanceState) {
//        PerformanceAnalyzer.startRecordPerformance(this, Category.Fragment, "onCreate");
//        onCreate(savedInstanceState);
//        mIsActive = true;
//        PerformanceAnalyzer.stopRecordPerformance(this, Category.Fragment, "onCreate");
//    }
//
//    protected View performCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        PerformanceAnalyzer.startRecordPerformance(this, Category.Fragment, "onCreateView");
//        View view = onCreateView(inflater, container, savedInstanceState);
//        PerformanceAnalyzer.stopRecordPerformance(this, Category.Fragment, "onCreateView");
//        return view;
//    }
//
//    protected void performDestroyView() {
//        PerformanceAnalyzer.startRecordPerformance(this, Category.Fragment,
//                "onDestroyView");
//        onDestroyView();
//        PerformanceAnalyzer.stopRecordPerformance(this, Category.Fragment,
//                "onDestroyView");
//    }
//
//    protected void performDestroy() {
//        PerformanceAnalyzer.startRecordPerformance(this, Category.Fragment,
//                "onDestroy");
//        onDestroy();
//        mIsActive = false;
//        PerformanceAnalyzer.stopRecordPerformance(this, Category.Fragment,
//                "onDestroy");
//    }

    /**
     * 设置Fragment页面的NodeFragmentBundle数据模型
     * @param args  一个NodeFragmentBundle对象
     */
    public void setNodeFragmentBundleArguments(NodeFragmentBundle args) {
        this.mNodeFragmentBundle = args;
    }

    /**
     * 返回NodeFragmentBundle数据模型
     * @return  返回一个NodeFragmentBundle对象
     */
    public NodeFragmentBundle getNodeFragmentArguments() {
        return mNodeFragmentBundle;
    }

    /**
     * 提供给子类重写,调用super.onDestroy()时候执行NodeFragment的{@linkplain NodeFragment#onDestroy(),
     * 并且同时执行堆栈顶部的Fragment的{@linkplain #onDestroy()}。
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsActive = false;
    }

    /**
     * UI运行方法
     * @param r 一个runnable线程
     */
    public void runOnUiThread(Runnable r) {
        final Activity activity = getActivity();
        if (activity != null && r != null)
            activity.runOnUiThread(r);
    }
}
