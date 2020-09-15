package com.sfmap.library.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import org.apache.http.util.EncodingUtils;

/**
 * 资源工具类
 */
public class ResUtil {
    /**
     * 根据资源ID获取Bitmap
     *
     * @param context    上下文
     * @param resourceID 资源id
     * @return bitmap图片
     */
    public static Bitmap decodeBitmapWithAdaptiveSize(
            android.content.Context context, int resourceID) {
        BitmapFactory.Options tmpOpts = new BitmapFactory.Options();
        tmpOpts.inScaled = false;
        tmpOpts.inDensity = 0;
        tmpOpts.inTargetDensity = 0;

        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
                resourceID, tmpOpts);
        bmp.setDensity(0);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float unit = 2.0f / dm.density;

        if (unit == 1.0) {
            return bmp;
        }

        Bitmap bm = Bitmap.createScaledBitmap(bmp,
                (int) (bmp.getWidth() / unit), (int) (bmp.getHeight() / unit),
                false);

        bmp.recycle();
        return bm;
    }

    /**
     * 根据图片名称获取bitmap
     *
     * @param context 上下文
     * @param resName 资源名称
     * @return        bitmap图片
     */
    public static Bitmap decodeAssetBitmap(android.content.Context context,
                                           String resName) {
        // on 1.6 later

        AssetManager assetManager = context.getAssets();
        Bitmap iBitmap = null;
        InputStream is;
        try {
            is = assetManager.open(resName);
            iBitmap = BitmapFactory.decodeStream(is);
            iBitmap.setDensity(0);
            is.close();
        } catch (IOException e) {

        }
        return iBitmap;
    }

    /**
     * 根据图片名称获取所需大小的bitmap
     *
     * @param context 上下文
     * @param resName 资源名称
     * @param width   图片宽度
     * @param height  图片高度
     * @return width * height 大小的 bitmap
     */
    public static Bitmap decodeAssetBitmapWithSize(
            android.content.Context context, String resName, int width,
            int height) {
        // on 1.6 later

        AssetManager assetManager = context.getAssets();
        Bitmap iBitmap = null;
        InputStream is;
        try {
            is = assetManager.open(resName);
        /*
	     * BitmapFactory.Options tmpOpts = new BitmapFactory.Options();
	     * tmpOpts.inScaled = false; tmpOpts.inDensity = 0;
	     * tmpOpts.inTargetDensity = 0;
	     */
            iBitmap = BitmapFactory.decodeStream(is);
            // iBitmap.setDensity(0);
            is.close();

            Bitmap bm = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            Rect srcRect = new Rect();
            Rect dstRect = new Rect();
            srcRect.set(0, 0, iBitmap.getWidth(), iBitmap.getHeight());
            dstRect.set(0, 0, width, height);

            c.drawBitmap(iBitmap, srcRect, dstRect, null);
            iBitmap.recycle();
            iBitmap = null;
            return bm;

        } catch (IOException e) {

        }
        return null;

    }

    /**
     * 根据图片获取图片数组
     *
     * @param context 上下文
     * @param resName 图片名称
     * @return 图片数组
     */
    public static byte[] decodeAssetResData(android.content.Context context,
                                            String resName) {
        // on 1.6 later

        AssetManager assetManager = context.getAssets();

        InputStream is;
        try {
            is = assetManager.open(resName);
            java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();

            byte[] bufferByte = new byte[1024];
            int l = -1;
            while ((l = is.read(bufferByte)) > -1) {
                bout.write(bufferByte, 0, l);
            }
            byte[] rBytes = bout.toByteArray();
            bout.close();
            is.close();
            return rBytes;

        } catch (IOException e) {
            return null;
        } catch (java.lang.OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * 从string.xml资源文件中获取字符串
     *
     * @param context 上下文
     * @param resId   资源id
     * @return        内容字符串
     */
    public static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    /**
     * 根据文件名称读取asets下的文件
     *
     * @param context       上下文
     * @param fileName      文件名称
     * @return
     */
    public static String readStringFromAsets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "utf-8");
            in.close();
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * 获取图片名称获取图片的资源id的方法

     * @param imageName 文件民称
     * @return          资源id
     */
    public static int getResourceByReflect(Context context, String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable",
                context.getPackageName());
    }
}
