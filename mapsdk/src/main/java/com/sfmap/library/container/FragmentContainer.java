package com.sfmap.library.container;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * fragment容器
 */
public class FragmentContainer extends HackyViewPager {

    public static final String TAG = "FragmentContainer";

    /**
     * 当执行包含LaunchMode的Fragment时是否使用老的Fragment而不是重新创建新的Fragment
     */
    public static final boolean USE_CACHE_FRAGMENT = true;

    /**
     * 清除模式
     */
    public static enum CleanMode {
        /**
         * 移除顶端模式
         */
        CLEAN_TOP,
    }

    private boolean mEnabled = true;

    private ArrayList<NodeFragment> mNodeFragmentList = new ArrayList<NodeFragment>();

    private FragmentNodeAdapter mViewMoveAdapter;
    private FragmentActivity mFragmentActivity;

    private OnAnimationFinishListener mOnAnimationFinishListener;

    private WeakReference<onPageScrolleChanngeListener> mOnPageScrolleChanngeListener;

    /**
     * fragment页面滑动事件接口
     */
    public interface onPageScrolleChanngeListener {
        /**
         * fragment页面开始滑动
         */
        public void onPageScolleStart();
    }

    /**
     * 动画接口
     */
    public interface OnAnimationFinishListener {
        /**
         * 动画完成回调
         */
        void onAnimationFinished();
    }

    /**
     * 构造器
     * @param context   上下文
     */
    public FragmentContainer(Context context) {
        this(context, null);
    }

    /**
     * 构造器
     * @param context   上下文
     * @param attrs     属性
     */
    public FragmentContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClipChildren(false);

    }

    /**
     * fragment 是否接受手势事件
     * @param enabled   ture为接受手势事件否者反之
     */
    public void setPagingEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    /**
     *  @deprecated
     * @param arg0
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // Log.d(TAG, "onInterceptTouchEvent mEnabled : " + mEnabled);
        return mEnabled ? super.onInterceptTouchEvent(arg0) : false;
    }

    private void removeFromAdapter(int num) {
        unRegisterOnPageScrolleChanngeListener();
        if (removeObject(num)) {
            mViewMoveAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置fragment到容器内
     * @param obj       一个NodeFragment对象
     * @param position  位置
     */
    public void setObjectForPosition(NodeFragment obj, int position) {
        mNodeFragmentList.add(position, obj);
    }

    /**
     * 返回第position的Fragment View 视图
     * @param position
     * @return  一个View对象
     */
    public View findViewFromObject(int position) {
        Object o = mNodeFragmentList.get(Integer.valueOf(position));
        return findViewByObject(o);
    }

    private View findViewByObject(Object object) {
        if (object == null) {
            return null;
        }
        PagerAdapter a = getAdapter();
        View v;
        for (int i = 0; i < getChildCount(); i++) {
            v = getChildAt(i);
            if (a.isViewFromObject(v, object))
                return v;
        }
        return null;
    }

    /**
     * 判断容器内fragment是否存在
     * @param obj
     * @return  true存在否者反之
     */
    public boolean containsObj(Object obj) {
        return mNodeFragmentList.contains(obj);
    }

    /**
     * @deprecated
     * @param obj
     * @return
     */
    public int getItemPostion(Object obj) {
        for (int i = 0; i < mNodeFragmentList.size(); i++) {
            final Object o = mNodeFragmentList.get(i);
            ItemInfo itemInfo = (ItemInfo) obj;
            if (o == itemInfo.object) {
                if (i == itemInfo.position) {
                    return PagerAdapter.POSITION_UNCHANGED;
                } else {
                    return i;
                }
            }
        }

        return PagerAdapter.POSITION_NONE;
    }

    /**
     * 返回一个fragment 根据postion位置获取
     * @param position    容器内Fragment位置
     * @return  一个NodeFragment对象
     */
    public NodeFragment getFragmentByPosition(int position) {
        return position >= mNodeFragmentList.size() ? null : mNodeFragmentList.get(position);
    }

    /**
     * 添加fragment
     * @param clazz         类类型
     * @param extraData     传入的数据Bundle
     * @param requestCode   请求编码
     * @return  一个NodeFragment对象
     */
    public NodeFragment addFragment(Class<? extends NodeFragment> clazz,
                                    NodeFragmentBundle extraData, int requestCode) {
        return addFragment(clazz, extraData, requestCode, true, false);
    }

    /**
     * 添加fragment
     * @param clazz         类类型
     * @param extraData     传入的数据Bundle
     * @param requestCode   请求编码
     * @param touchEnable   新加入的fragment是否支持点击.
     * @param isAnimated    是否以动画的形式切换到新加入的fragment
     * @return  一个NodeFragment对象
     */
    public NodeFragment addFragment(Class<? extends NodeFragment> clazz, NodeFragmentBundle extraData, int requestCode, boolean touchEnable,
                                    boolean isAnimated) {

        if (isScrolling())
            return null;

        // mState = State.IDLE;
        mEnabled = touchEnable;

        NodeFragment targetFragment = processLaunchMode(clazz);
        if (targetFragment == null) { // 没有符合条件LaunchMode的NodeFragment
            try {
                targetFragment = clazz.newInstance();
                targetFragment.setNodeFragmentBundleArguments(extraData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else { // 根据该Fragment的LaunchMode找到了已经存在的实例
            targetFragment.setNodeFragmentBundleArguments(extraData); // 更新新的数据集合
            if (targetFragment.isAdded()) { // 如果已存在的Nodefragment已经走完正常的初始化流程，则直接回调数据
                targetFragment.onNewNodeFragmentBundle(extraData); // 回调新的数据
            } else { // 已存在的Nodefragment没有走完初始化流程，此时需要把状态进行保存，等Nodefragment执行完onResume后再回调onNewNodeFragmentBundle
                targetFragment.setHasNewBundle();
            }
        }
        targetFragment.mBaseActivity = mFragmentActivity; // 在此处主动attach Activity是为了满足因Scheme调用直接启动某个Fragment的情况
        targetFragment.mRequestCode = requestCode;

        if (targetFragment instanceof DialogNodeFragment) {
            ((DialogNodeFragment) targetFragment).show(
                    mFragmentActivity.getSupportFragmentManager(),
                    targetFragment.getClass().getName());
            return targetFragment;
        }

        if (targetFragment instanceof onPageScrolleChanngeListener) {
            mOnPageScrolleChanngeListener = new WeakReference<onPageScrolleChanngeListener>(
                    (onPageScrolleChanngeListener) targetFragment);
        }
        if (mNodeFragmentList.size() >= 1) {
            Object obj = mNodeFragmentList.get(mNodeFragmentList.size() - 1);
            if (obj != null && obj instanceof NodeFragment) {
                NodeFragment curFragment = (NodeFragment) obj;
                if (curFragment.isResumed() && !curFragment.isPaused()) {
                    curFragment.onPause();
//                    curFragment.performPause();
                }
            }
        }

        mNodeFragmentList.add(targetFragment);
        // mLastItem = mObjs.size() - 1;
        mViewMoveAdapter.notifyDataSetChanged();
        setCurrentItem(mNodeFragmentList.size(), isAnimated);
        return targetFragment;
        // Log.d(TAG, "addFragment 2 mObjs.size()　: " + mObjs.size());
    }

    private NodeFragment processLaunchMode(Class<? extends NodeFragment> clazz) {
        boolean isFind = false;
        NodeFragment targetFragment = null;
        if (LaunchMode.launchModeSingleTopAllowDuplicate.class.isAssignableFrom(clazz)) {
            NodeFragment lastFragment = mNodeFragmentList.get(mNodeFragmentList
                    .size() - 1);
            if (lastFragment != null && lastFragment.getClass() == clazz) { // 如果上一个页面类型相同
                final int maxDuplicateCount = ((LaunchMode.launchModeSingleTopAllowDuplicate) lastFragment)
                        .maxDuplicateCount();
                if (mNodeFragmentList.size() >= maxDuplicateCount) {
                    int firstFindPos = -1; // 栈内最早重复的页面位置
                    int duplicateCount = 0; // 栈内该类型的重复数
                    for (int i = 0; i < mNodeFragmentList.size(); i++) {
                        NodeFragment fm = mNodeFragmentList.get(i);
                        if (fm != null && fm.getClass() == clazz) {
                            if (firstFindPos == -1) {
                                firstFindPos = i;
                            }
                            duplicateCount++;
                        }
                    }
                    if (duplicateCount >= maxDuplicateCount) {
                        return mNodeFragmentList.remove(firstFindPos);
                    }
                }
                return null;
            }
        }
        if (LaunchMode.launchModeSingleTop.class.isAssignableFrom(clazz)) {
            NodeFragment lastFragment = mNodeFragmentList.get(mNodeFragmentList
                    .size() - 1);
            if (lastFragment != null && lastFragment.getClass() == clazz) {
                mNodeFragmentList.remove(mNodeFragmentList.size() - 1);
                targetFragment = lastFragment;
            }
        } else if (LaunchMode.launchModeSingleTask.class.isAssignableFrom(clazz)) {
            for (int i = 0; i < mNodeFragmentList.size(); i++) {
                NodeFragment fm = mNodeFragmentList.get(i);
                if (fm != null && fm.getClass() == clazz) {
                    isFind = true;
                    targetFragment = mNodeFragmentList.get(i);
                }
                if (isFind) {
                    mNodeFragmentList.remove(i--);
                }
            }
        } else if (LaunchMode.launchModeSingleInstance.class.isAssignableFrom(clazz)) {
            for (int i = 0; i < mNodeFragmentList.size(); i++) {
                NodeFragment fm = mNodeFragmentList.get(i);
                if (fm != null && fm.getClass() == clazz) {
                    targetFragment = mNodeFragmentList.remove(i);
                    break;
                }
            }
        }
        return USE_CACHE_FRAGMENT ? targetFragment : null;
    }

    private void unRegisterOnPageScrolleChanngeListener() {
        if (mOnPageScrolleChanngeListener != null) {
            mOnPageScrolleChanngeListener.clear();
            mOnPageScrolleChanngeListener = null;
        }
    }

    private void removeFragmentWithoutAnimation(int step) {
        // mIsRemovingLast = false;
        removeFromAdapter(step);
        notifyAnimationFinishListener();
    }

    private void notifyAnimationFinishListener() {
        if (mOnAnimationFinishListener != null) {
            mOnAnimationFinishListener.onAnimationFinished();
            mOnAnimationFinishListener = null;
        }
    }

    /**
     * 移除最上层的fragment
     * @param l     动画监听
     * @param isAnimated    是否执行动画
     * @return  true移除成功否者反之
     */
    public boolean removeLastFragment(OnAnimationFinishListener l,
                                      boolean isAnimated) {
        return removeFragments(1, l, isAnimated);
    }

    /**
     * 移除根节点下的所有fragment
     * @return  true移除成功否者反之
     */
    public boolean removeAllFragmentsWithoutRoot() {
        return removeFragments(mNodeFragmentList.size() - 1, null, false);
    }

    /**
     * 移除容器内的fragment
     * @param step  第几个
     * @param l     动画监听
     * @param isAnimated    是否执行动画
     * @return  true移除成功否者反之
     */
    public boolean removeFragments(int step, OnAnimationFinishListener l,
                                   boolean isAnimated) {

        if (isScrolling()) {
            return true;
        }

        // 只有一条数据或者当前页面数小于等于需要移除的页面数，则不再移除
        if (mNodeFragmentList.size() == 1 || mNodeFragmentList.size() <= step)
            return false;

        mOnAnimationFinishListener = l;
        if (isAnimated) {

            // mIsRemovingLast = true;
            // mState = State.IDLE;

            // Log.d(TAG, "removeLastFragment 1 mObjs.size() : " +
            // mObjs.size());

            final int lastIndex = mNodeFragmentList.size() - step;
            final int preIndex = lastIndex - 1;
            setCurrentItem(Math.max(preIndex, 0), isAnimated);

            // Log.d(TAG, "removeLastFragment 2 mObjs.size() : " +
            // mObjs.size());
        } else {
            removeFragmentWithoutAnimation(step);
        }

        return true;
    }

    /**
     * 替换frragment
     * @param clazz
     * @param extraData
     * @param requestCode
     * @return  一个NodeFragment对象
     */
    public NodeFragment replaceFragment(Class<? extends NodeFragment> clazz,
                                        NodeFragmentBundle extraData, int requestCode) {
        removeObject(1);
        return addFragment(clazz, extraData, requestCode);
    }

    /**
     * 交换fragment结果数据
     * @param sourceFragment  一个NodeFragment对象
     */
    void transferResultToReceiver(NodeFragment sourceFragment) {
        if (sourceFragment != null) {
            NodeFragment fragmentResultReceiver = mNodeFragmentList
                    .get(mNodeFragmentList.size() - 1);
            if (fragmentResultReceiver != null) {
                if (sourceFragment.mRequestCode != NodeFragment.INVALID_REQUEST_CODE
                        && sourceFragment.hasResult()) {
                    fragmentResultReceiver.onFragmentResult(
                            sourceFragment.getClass(),
                            sourceFragment.getRequestCode(),
                            sourceFragment.getResultType(),
                            sourceFragment.getResult());
                    sourceFragment.cleanResult();
                } else {
                    sourceFragment.setResultReceiver(fragmentResultReceiver);
                }

            }
        }
    }

    /**
     * 移除容器第num个fragment之下的所有fragment
     * @param num  位置
     * @return     true移除成功否者反之
     */
    private boolean removeObject(int num) {
        if (mNodeFragmentList.size() == 1 || mNodeFragmentList.size() <= num)
            return false;
        NodeFragment topFragment = null;
        for (int i = 0; i < num; i++) {
            NodeFragment removedFragment = mNodeFragmentList
                    .remove(mNodeFragmentList.size() - 1);
            if (topFragment == null) {
                topFragment = removedFragment;
            }
        }
        transferResultToReceiver(topFragment);
        return true;
    }

    /**
     * 返回容器内fragment的数量
     * @return  返回fragment的位置信息
     */
    public int getFragmentSize() {
        return mNodeFragmentList.size();
    }

    /**
     * 返回最后一个fragment
     * @return  最后一个NodeFragment对象
     */
    public NodeFragment getLastFragment() {
        return mNodeFragmentList.get(mNodeFragmentList.size() - 1);
    }

    /**
     * 判断当前堆栈最上面一个Fragment和传入的Fragment是否相同
     * @param fragment  传入的Fragment
     * @return          如果返回true是同一个Fragment,否者返回false
     */
    public boolean isTopActiveFragment(NodeFragment fragment) {
        // 普通情况下判断当前堆栈最上面一个Fragment和当前Fragment是否相同，当Fragment因为被移除而触发onPause时判断当前集合中是否还有该Fragment。
        return getLastFragment() == fragment
                || !mNodeFragmentList.contains(fragment);
    }

    /**
     * @deprecated
     * @param fragmentActivity
     */
    public void init(FragmentActivity fragmentActivity) {
        mFragmentActivity = fragmentActivity;
        mViewMoveAdapter = new FragmentNodeAdapter(
                mFragmentActivity.getSupportFragmentManager(), this);
        setAdapter(mViewMoveAdapter);
    }

    /**
     * @deprecated
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Log.d(TAG, "dispatchTouchEvent isScrolling : " + isScrolling());
        return isScrolling() ? false : super.dispatchTouchEvent(ev);
    }


    /**
     * 发送Intent信息代理。
     */
    public interface IntentControlerDelegater {
        /**
         *  将意图信息发送给上层
         * @param intent    意图信息
         */
        public void solveSchema(Intent intent);
    }
}
