package com.sfmap.tbt.util;

import java.net.Proxy;

public class DownloadManager
{
  private HttpUrlUtil httpUtil;
  private Request request;
  
  public DownloadManager(Request paramdp)
  {
    this(paramdp, 0L, -1L);
  }
  
  public DownloadManager(Request paramdp, long paramLong1, long paramLong2)
  {
    this.request = paramdp;
    Proxy proxy;
    if (paramdp.proxy == null) {
      proxy = null;
    } else {
      proxy = paramdp.proxy;
    }
    this.httpUtil = new HttpUrlUtil(this.request.connTimeout, this.request.readTimeout, proxy);
    
    this.httpUtil.b(paramLong2);
    this.httpUtil.a(paramLong1);
  }
  
  public void makeGetRequest(DownloadListener listener)
  {
    this.httpUtil.makeGetRequest(this.request.getUrl(), this.request.getHeadMaps(), this.request.getRequestParam(), listener);
  }
  
  public void cancle()
  {
    this.httpUtil.cancle();
  }
  
  public static abstract interface DownloadListener
  {
    public abstract void onDownLoading(byte[] paramArrayOfByte, long total, long paramLong);
    
    public abstract void onStart();
    
    public abstract void onCancle(long length);

    public abstract void onFinish();
    public abstract void onError(Throwable paramThrowable);
  }
}
