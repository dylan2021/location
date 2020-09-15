package com.sfmap.api.navi;

import android.content.Context;
import android.os.PowerManager;

import com.sfmap.tbt.AppInfoUtilDecode;
import com.sfmap.tbt.NaviUtilDecode;
import com.sfmap.tbt.TBTControlDecode;

class NaviSettingDecode {
	PowerManager.WakeLock wakeLock;
	boolean monitorCamera = true;
	private boolean trafficInfoUpdate = true;
	private boolean enableCameraInfoUpdate = true;
	private boolean wakeLockEnable = false;
	private Context context;
	private TBTControlDecode tbtControl;
	private PowerManager powerManager;

	NaviSettingDecode(Context paramContext, TBTControlDecode paramh) {
		try {
			this.context = paramContext.getApplicationContext();
			this.tbtControl = paramh;
			if (this.context != null) {
				this.powerManager = ((PowerManager) this.context.getSystemService("power"));
				this.wakeLock = this.powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
						AppInfoUtilDecode.WAKE_LOCK);
				this.wakeLock.setReferenceCounted(false);
			}
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	public void enableTrafficInfoUpdate(boolean paramBoolean) {
		this.trafficInfoUpdate = paramBoolean;
		if (this.tbtControl != null)
			this.tbtControl.enableTrafficRadio(this.trafficInfoUpdate);
	}

	public void enabelCameraInfoUpdate(boolean enableUpdate) {
		this.enableCameraInfoUpdate = enableUpdate;
		if (this.tbtControl != null)
			this.tbtControl.enabelCameraInfoUpdate(this.enableCameraInfoUpdate);
	}

	public boolean isMonitorCamera() {
		return this.monitorCamera;
	}

	public void enableMonitorCamera(boolean paramBoolean) {
		this.monitorCamera = paramBoolean;
	}

	public void enableScreenAlwaysBright(boolean paramBoolean) {
		this.wakeLockEnable = paramBoolean;
		try {
			if (this.wakeLockEnable) {
				this.wakeLock.acquire();
			} else if (this.wakeLock.isHeld())
				this.wakeLock.release();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		}
	}

	void releaseWakeLock() {
		try {
			if (this.wakeLock.isHeld())
				this.wakeLock.release();
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			NaviUtilDecode.a(localThrowable);
		} finally {
		}
	}
}