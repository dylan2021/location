package com.sfmap.mapcore;

import com.sfmap.api.mapcore.util.LogManager;
import java.util.ArrayList;
import java.util.HashMap;

public class VMapDataCache
{
  private static final int MAXSIZE = 400;
  HashMap<String, Recoder> vMapDataHs = new HashMap<String, Recoder>();
  ArrayList<String> vMapDataList = new ArrayList<String>();
  HashMap<String, Recoder> vCancelMapDataHs = new HashMap<String, Recoder>();
  ArrayList<String> vCancelMapDataList = new ArrayList<String>();
  private static VMapDataCache instance;
  
  public static VMapDataCache getInstance()
  {
    if (instance == null) {
      instance = new VMapDataCache();
    }
    return instance;
  }
  
  public synchronized void reset()
  {
    LogManager.writeLog(LogManager.productInfo, hashCode() + " VMapData reset, clear all list", 111);
    this.vMapDataHs.clear();
    this.vMapDataList.clear();
    
    this.vCancelMapDataHs.clear();
    this.vCancelMapDataList.clear();
  }
  
  public int getSize()
  {
    return this.vMapDataHs.size();
  }
  
  static String getKey(String paramString, int paramInt)
  {
    return paramString + "-" + paramInt;
  }
  
  public synchronized Recoder getRecoder(String paramString, int paramInt)
  {
    LogManager.writeLog(LogManager.productInfo, hashCode() + " VMapData GetData " + paramString + "-" + paramInt, 111);
    Recoder locale = (Recoder)this.vMapDataHs.get(getKey(paramString, paramInt));
    if (locale != null) {
      locale.hitCount += 1;
    }
    return locale;
  }
  
  public synchronized Recoder getCancelRecoder(String paramString, int paramInt)
  {
    LogManager.writeLog(LogManager.productInfo, hashCode() + " VMapData GetCancelData " + paramString + "-" + paramInt, 111);
    Recoder locale = (Recoder)this.vCancelMapDataHs.get(getKey(paramString, paramInt));
    if (locale != null) {
      if (System.currentTimeMillis() / 1000L - locale.timestamp > 10L) {
        return null;
      }
    }
    return locale;
  }
  
  public synchronized Recoder putRecoder(byte[] paramArrayOfByte, String paramString, int paramInt)
  {
    Recoder locale = new Recoder(paramString, paramInt);
    if (locale.string == null) {
      return null;
    }
    if (this.vMapDataHs.size() > 400)
    {
      this.vMapDataHs.remove(this.vMapDataList.get(0));
      this.vMapDataList.remove(0);
    }
    this.vMapDataHs.put(getKey(paramString, paramInt), locale);
    this.vMapDataList.add(getKey(paramString, paramInt));
    
    LogManager.writeLog(LogManager.productInfo, hashCode() + " VMapData putData " + locale.string + "-" + paramInt, 111);
    
    return locale;
  }
  
  public synchronized Recoder putCancelRecoder(byte[] paramArrayOfByte, String paramString, int paramInt)
  {
    if (getRecoder(paramString, paramInt) != null) {
      return null;
    }
    Recoder recoder = new Recoder(paramString, paramInt);
    if (recoder.string == null) {
      return null;
    }

    if (this.vCancelMapDataHs.size() > 400)
    {
      this.vCancelMapDataHs.remove(this.vMapDataList.get(0));
      this.vCancelMapDataList.remove(0);
    }

    this.vCancelMapDataHs.put(getKey(paramString, paramInt), recoder);
    this.vCancelMapDataList.add(getKey(paramString, paramInt));
    
    return recoder;
  }
}
