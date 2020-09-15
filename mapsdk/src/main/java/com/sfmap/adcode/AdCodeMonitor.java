package com.sfmap.adcode;

import android.content.Context;

public class AdCodeMonitor {

	private static AdCode adCode;
	
	
	public static void init(Context context){
		if(adCode == null && context != null){
			synchronized(AdCodeMonitor.class){
				adCode = new AdCode(context);
			}
		}
	}
	
	public static AdCode getAdCodeInst(){
		if(adCode == null){
			throw new IllegalStateException("AdCode need to be init first!");
		}
		return adCode;
	}
	
	public static void destroyAdCodeInst(){
		if(adCode != null){
			adCode.destroy();
			adCode = null;
		}
	}
	
}
