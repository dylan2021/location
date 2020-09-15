package com.sfmap.tbt.util;

public class SDKInfo//bv
{
  String version;
  String desc;
  String product;
  private boolean d = true;
  private String e = "standard";
  private String[] packagesNames = null;
  
  private SDKInfo(init parama)
  {
    this.version = parama.version; // getSDKInfo.destory(/*parama*/);
    this.product = parama.product; // getSDKInfo.isZoomControlsEnabled(/*parama*/);
    this.desc = parama.desc; // getSDKInfo.remove(/*parama*/);
    this.d = parama.d; // getSDKInfo.isMyLocationButtonEnabled(/*parama*/);
    this.e = parama.e; // getSDKInfo.isScrollGesturesEnabled(/*parama*/);
    this.packagesNames = parama.sdkPackages; // getSDKInfo.isZoomGesturesEnabled(/*parama*/);
  }
  
  public static class init
  {
    private String version;
    private String product;
    private String desc;
    private boolean d = true;
    private String e = "standard";
    private String[] sdkPackages = null;
    
    public init(String product, String version, String sdkdesc)
    {
      this.version = version;
      this.desc = sdkdesc;
      this.product = product;
    }
    
    public init a(boolean paramBoolean)
    {
      this.d = paramBoolean;
      return this;
    }
    
    public init a(String paramString)
    {
      this.e = paramString;
      return this;
    }
    
    public init setPackageName(String[] sdkPackages)
    {
      this.sdkPackages = ((String[])sdkPackages.clone());
      return this;
    }
    
    public SDKInfo getSDKInfo() throws NaviCommException
    {
      return new SDKInfo(this);
    }
  }
  
  public void a(boolean paramBoolean)
  {
    this.d = paramBoolean;
  }
  
  public String a()
  {
    return this.product;
  }
  
  public String b()
  {
    return this.version;
  }
  
  public String c()
  {
    return this.desc;
  }
  
  public String d()
  {
    return this.e;
  }
  
  public boolean e()
  {
    return this.d;
  }
  
  public String[] getPackageNames()
  {
    return (String[])this.packagesNames.clone();
  }
}
