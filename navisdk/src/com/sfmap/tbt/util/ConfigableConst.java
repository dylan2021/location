package com.sfmap.tbt.util;

public class ConfigableConst
{
	public static enum BuildType
	  {
	    PUBLIC, OTHER;
	  }
	
  public static float Resolution = 0.9F; // a
  public static String product = "NaviSDK"; //b
  public static String c = ""; //c
  public static final String desc = "NaviSDK Android 1.0.0"; // d
  public static final BuildType NowBuildType =  BuildType.PUBLIC; //e
  public static volatile SDKInfo sdkInfo; //f
  
}
