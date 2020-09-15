package com.sfmap.api.navi.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sfmap.api.navi.model.NaviTrafficStatus;
import com.sfmap.tbt.ResourcesUtil;

import java.util.List;

public class TmcViewManager {
    private String TAG = "TmcViewManager";
    private final int UNKNOWNSTATUS = -1;
    
    /** 光柱状态 畅通状态 */
    private final int UNBLOCKSTATE = 1;
    /** 光柱状态 缓行状态 */
    private final int SLOWSTATE = 2;
    /** 光柱状态 阻塞严重状态 */
    private final int BLOCKSTATE = 3;
    /** 超级严重阻塞状态 */
    private final int SUPBLOCKSTATE = 4;
    
    private RelativeLayout mBaseView;
    private ImageView mCursor;
    private LinearLayout mTmcContainer;
    private int miRouteTotalLength;
    private int mTmcViewLength;
    private int mViewWidth;
    private int mRestLength;
    private float mCursorPos;
    private Context mContext;
    private boolean isValid = false;
    private TextView backgroud;

    public TmcViewManager(){
        
    }
    
    public void initView(Context context, View view){
        mContext=context;
        mBaseView = (RelativeLayout) view.findViewById(ResourcesUtil.baseView);
        mCursor = (ImageView) view.findViewById(ResourcesUtil.navi_tmc_cursor);
        mTmcContainer = (LinearLayout) view.findViewById(ResourcesUtil.tmc_view_container);
        mTmcViewLength = dp2px(context, 150);
        mViewWidth = dp2px(context, 13);
    }
    
    private int dp2px(Context context,int paramInt) {
        if (paramInt == 0)
            return 0;
        
        if (context == null)
            return paramInt;
        try {
            float f = TypedValue.applyDimension(1, paramInt, context.getResources().getDisplayMetrics());
            
            return (int) f;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return paramInt;
    }
    public void updateRouteTotalLength(int totalLength){
        miRouteTotalLength = totalLength;
        if(mTmcViewLength == 0 && mTmcContainer != null){
            mTmcViewLength = mTmcContainer.getMeasuredHeight();
            mViewWidth = mTmcContainer.getMeasuredWidth();
        }
    }
    
    public void createTmcBar(List<NaviTrafficStatus> m_Tbitem) {
        if(miRouteTotalLength == 0 || mTmcViewLength ==0 ){
            return;
        }
        Log.i(TAG,"debug--createTmcBar"+"  mCursorPos="+mCursorPos);
        isValid = true;
        mTmcContainer.removeAllViews();
//        mTmcContainer.setBackgroundColor(Color.parseColor("#6db24f"));
        mTmcContainer.setBackgroundColor(Color.parseColor("#cecece"));
        if(m_Tbitem==null){
            addViewByActualLength(UNKNOWNSTATUS, (int)mCursorPos);
            return;
        }

        int tmcBarLength = m_Tbitem.size();
        int totalChildLength = 0;
        for (int i = 0; i< tmcBarLength; i++) {
            NaviTrafficStatus mItem = m_Tbitem.get(i);
//            LogUtil.d("mylog","status: "+mItem.getStatus() + " length:  "+mItem.getLength());
            totalChildLength +=
                    addViewBytbitem(mItem.getStatus(), mItem.getLength());
        }
        //补全光柱图由于超长距离引起的误差
        if(totalChildLength < mCursorPos){
            int deltaLength = (int)(mCursorPos - totalChildLength);
            addViewByActualLength(UNKNOWNSTATUS, deltaLength);
        }
        
        mTmcContainer.invalidate();
    }
    
    private int addViewBytbitem(int status, int length){
        int curLength = length;
        int bmcBarHeight =  (int)Math.round(((curLength * mTmcViewLength * 1.0) / (miRouteTotalLength * 1.0)));
        addViewByActualLength(status, bmcBarHeight);
        return bmcBarHeight;
    }
    private void addViewByActualLength(int status, int length){
        TextView _tmpView = createViewByInfo(mContext,status);
        _tmpView.setHeight(length);
        _tmpView.setWidth(mViewWidth);
        mTmcContainer.addView(_tmpView, 0);
    }
    
    private TextView createViewByInfo(Context context, int status){
        TextView _tmpView = new TextView(context);
        _tmpView.setBackgroundColor(getColor(status));
        return _tmpView;
    }
    
    private int getColor(int status){
//        LogUtil.d("mylog","status===="+status);t
        switch(status){
            //服务端会返回无路况的路段，状态值为0，客户端默认取成畅通状态
            case 0:
            case UNBLOCKSTATE:
                return Color.argb(0xff,  109, 178, 79);
//                return Color.argb(0xff,  0, 220, 98);
            case SLOWSTATE:
                return Color.argb(0xff,  241, 192, 96);
//                return Color.argb(0xff,  255, 255, 0);
            case BLOCKSTATE:
                return  Color.argb(0xff, 206, 72, 57);
//                return  Color.argb(0xff, 228, 26, 26);
            case SUPBLOCKSTATE:
                return Color.argb(0xff,142, 43, 33);
//                return Color.argb(0xff,160, 8, 8);
            default:
                return Color.argb(0xff, 109, 178, 79);
//                return Color.argb(0xff, 26, 166, 239);
        }
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setCurorPosition(int restLength) {
        if(mTmcViewLength == 0
           || miRouteTotalLength == 0
           || mCursor == null
           || restLength == 0){
            return;
        }
        mRestLength = restLength;
        mCursorPos = (float) ((restLength * 1.0) / (miRouteTotalLength) * mTmcViewLength);
        mCursor.setTranslationY(mCursorPos-15);
        mCursor.invalidate();
        
    }
    
    public void relaseTmcInfo(){
        isValid = false;
        if(mTmcContainer != null){
            mTmcContainer.removeAllViews();
        }
        miRouteTotalLength = 0;
        mRestLength = 0;
    }
    
    public boolean isValid() {
        return this.miRouteTotalLength > 0 && isValid;
    }
}

