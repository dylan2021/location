package com.sfmap.util;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {
    /*
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private String encoded = "Nzg0OTE1NjQzMjU4NzQ2MQ==";//encoded，加密的key
    private String ivParameter = "1201230125462244";//偏移量,4*4矩阵
    private static AesUtil instance = null;

    private AesUtil() {

    }

    public static AesUtil getInstance() {
        if (instance == null)
            instance = new AesUtil();
        return instance;
    }

    /**
     * 加密
     * @param encData 要加密的内容
     * @param secretKey 加密的秘钥
     * @param vector 偏移量
     * @return 加密后的字符串
     * @throws Exception encrypt exception
     */
    @SuppressWarnings("unused")
    public static String encrypt(String encData ,String secretKey,String vector) throws Exception {
        if(secretKey == null) {
            return null;
        }
        if(secretKey.length() != 16) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = secretKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(encData.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }


    /**
     * 加密
     * @param sSrc 要加密的内容
     * @return 加密后的字符串
     * @throws Exception encrypt exception
     */
    public String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = Base64.decode(encoded, Base64.DEFAULT);
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
//        return new BASE64Encoder().encode(encrypted).replaceAll("\r|\n", "");// 此处使用BASE64做转码。
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    /**
     * 解密
     * @param sSrc 要解密的内容
     * @return 解密后的字符串
     */
    public String decrypt(String sSrc) {
        try {
            byte[] raw = Base64.decode(encoded, Base64.DEFAULT);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.decode(sSrc, Base64.NO_WRAP);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }
}

