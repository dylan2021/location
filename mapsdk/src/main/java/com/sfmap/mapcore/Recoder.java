package com.sfmap.mapcore;

class Recoder
{
  String string;
  int timestamp;
  int intField;
  int hitCount = 0;
  
  public Recoder(String paramString, int paramInt)
  {
    try
    {
      if ((paramString == null) || (paramString.length() == 0)) {
        return;
      }
      this.timestamp = ((int)(System.currentTimeMillis() / 1000L));
      this.intField = paramInt;
      this.string = paramString;
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      this.string = null;
    }
  }
}
