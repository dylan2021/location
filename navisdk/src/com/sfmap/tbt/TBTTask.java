package com.sfmap.tbt;

import android.content.Context;

import com.sfmap.tbt.util.LogUtil;

import static com.sfmap.tbt.FrameForTBT.recieveData;


/**
 * 原类名:b
 */
public class TBTTask extends BaseTask {
	private final String TAG = this.getClass().getSimpleName();
	public TBTTask(TBTControlDecode tbtControl, Context paramContext,
				   String serverUrl, int postReq, String parameter,
				   int cmdType, int cmdIndex, byte[] postValue, String sf) {
		super(tbtControl, paramContext, serverUrl, postReq, parameter,
				cmdType, cmdIndex, postValue, sf);
	}

	public void a() {
		int i = 2;
		byte[] arrayOfByte = null;
		try {
			arrayOfByte = b();
			if (arrayOfByte != null)
				i = 1;
		}  catch (OperExceptionDecode localThrowable) {
			if (localThrowable.getErrorCode() == 23)
				i = 3;
			else
				i = 2;
		} catch (Exception ex){
			ex.printStackTrace();
			i = 2;
		} finally {
			synchronized (this.tbtControl.lock) {
				 if ((this.tbtControl != null) && (this.tbtControl.tbtControl != null))
				 {
				 if (arrayOfByte != null) {
				 	LogUtil.d(TAG,"fuseNewData a()"+arrayOfByte.length + " cmdType:"+this.cmdType);
					 recieveData(arrayOfByte,this.cmdType);
				 }else
					 LogUtil.d(TAG,"setNetRequestState");
				 this.tbtControl.tbtControl.setNetRequestState(this.cmdType, this.cmdIndex, i);
				 }
			}
		}
	}
}