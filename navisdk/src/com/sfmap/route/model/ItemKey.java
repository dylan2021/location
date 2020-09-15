package com.sfmap.route.model;

import com.sfmap.map.util.CatchExceptionUtil;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ItemKey implements Serializable {

    public final static String ID = "id";
    public final static String TYPE = "type";
    // md5值
    public String id = "";
    // 线路和poi的区别
    public int type = -1;

    public void generateKeyId() {

    }

    /**
     * 32 位 md5
     *
     * @param str
     * @return
     */
    public static String createMD5(String str) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        } catch (UnsupportedEncodingException e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }

        if (messageDigest == null)
            return "";

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }


}

