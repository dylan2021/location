package com.sfmap.tbt.util;


import java.net.Proxy;

public class BaseNetManager//dk
{
  private static BaseNetManager Instance;
  
  public static BaseNetManager getInstance()
  {
    if (Instance == null) {
      Instance = new BaseNetManager();
    }
    return Instance;
  }
  
  public byte[] a(Request paramdp)
    throws Exception
  {
    ResponseEntity localdr = null;
    try
    {
      localdr = a(paramdp, true);
    }
    catch (Exception localbl)
    {
      throw localbl;
    }
    catch (Throwable localThrowable)
    {
      throw new Exception("未知的错误");
    }
    if (localdr != null) {
      return localdr.resBytes;
    }
    return null;
  }
  
  public byte[] makeSyncPostRequest(Request paramdp)
    throws Exception
  {
    ResponseEntity localdr = null;
    try
    {
      localdr = a(paramdp, false);
    }
    catch (NaviCommException localbl)
    {
      throw localbl;
    }
    catch (Throwable localThrowable)
    {
      BasicLogHandler.a(localThrowable, "BaseNetManager", "makeSyncPostRequest");
      
      throw new NaviCommException("未知的错误");
    }
    if (localdr != null) {
      return localdr.resBytes;
    }
    return null;
  }
  
  protected void checkUrl(Request request)
    throws Exception
  {
    if (request == null) {
      throw new NaviCommException("requeust is null");
    }
    if ((request.getUrl() == null) || ("".equals(request.getUrl()))) {
      throw new NaviCommException("request url is empty");
    }
  }
  
  protected ResponseEntity a(Request request, boolean isSSL)
    throws NaviCommException
  {
    ResponseEntity localdr = null;
    try
    {
      checkUrl(request);
      Proxy proxy;
      if (request.proxy == null) {
        proxy = null;
      } else {
        proxy = request.proxy;
      }
      HttpUrlUtil http = new HttpUrlUtil(request.connTimeout, request.readTimeout, proxy, isSSL);
      localdr = http.makePostReqeust(request.getUrlAndParam(), request.getHeadMaps(), request.getConnectionDatas());
    }
    catch (NaviCommException localbl)
    {
      throw localbl;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      throw new NaviCommException("未知的错误");
    }
    return localdr;
  }
}
