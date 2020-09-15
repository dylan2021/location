package com.sfmap.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 图片工具类
 */
public class ImageUtil {

    private final static int IO_BUFFER_SIZE = 30 * 1024;

    /**
     * 流转为byte[]
     * @param inStream      输入流
     * @return              byte数组
     * @throws Exception    异常
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    /**
     * 根据url获取一个Drawable的资源图片
     * @param url   url地址
     * @return      一个Drawable对象
     */
    public static Drawable loadImageFromUrl(String url) {
        URL m;
        InputStream i = null;
        try {
            m = new URL(url);
            i = (InputStream) m.getContent();
        } catch (MalformedURLException e1) {
            CatchExceptionUtil.normalPrintStackTrace(e1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(i, "src");
        return d;
    }

    /**
     * 获取圆角图片
     * @param bitmap    一个bitmap对象
     * @param pixels     圆角大小
     * @return          一个新的圆角bitmap对象
     */
    public static Bitmap getRoundBitmapForBitmap(Bitmap bitmap, int pixels) {
        return toRoundCorner(bitmap, pixels);
    }

    /**
     * byte转为bitmap
     * @param byteArray 一个byte数组
     * @return          一个bitmap对象
     */
    public static Bitmap byteToBitmap(byte[] byteArray) {
        if (byteArray.length != 0) {
            return BitmapFactory
                    .decodeByteArray(byteArray, 0, byteArray.length);
        } else {
            return null;
        }
    }

    /**
     * byte转drawable
     * @param byteArray 一个byte数组
     * @return          一个Drawable对象
     */
    public static Drawable byteToDrawable(byte[] byteArray) {
        ByteArrayInputStream ins = new ByteArrayInputStream(byteArray);
        return Drawable.createFromStream(ins, null);
    }

    /**
     * bitmap 转 byte[]
     * @param bm        一个Bitmap对象
     * @return          byte数组
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 获取控件resID的图片资源
     * @param context   上下文
     * @param resID     资源ID
     * @return
     */
    public static Bitmap drawable2Bitmap(Context context, int resID) {
        Drawable drawable = context.getResources().getDrawable(resID);
        if (drawable == null) {
            return null;
        }
        return drawableToBitmap(drawable);
    }

    /**
     *  drawable 转 bitmap
     * @param drawable  一个Drawable对象
     * @return          一个Bitmap对象
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 图片去色,返回灰度图片
     *
     * @param bmpOriginal
     *            传入的图片
     * @return 去色后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap
                .createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * 去色同时加圆角
     *
     * @param bmpOriginal
     *              原图
     * @param pixels
     *              圆角弧度
     * @return
     *              修改后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
        return toRoundCorner(toGrayscale(bmpOriginal), pixels);
    }

    /**
     * 把图片变成圆角
     *
     * @param bitmap
     *              需要修改的图片
     * @param pixels
     *              圆角的弧度
     * @return
     *              圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 将bitmap以jpej格式存文件
     *
     * @param bitmap    一个Bitmap对象
     * @param fileName  文件名称
     * @return          true保存成功否者反之
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, String fileName) {
        boolean bReturn = true;
        try {
            // saveBmp(bitmap);
            FileOutputStream bos = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            bReturn = true;
        }
        catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
            bReturn = false;
        } finally {

        }

        return bReturn;
    }

    /**
     * 把图片变成圆角
     * @param res           Resources对象
     * @param resourceId    资源id
     * @param pixels        圆角弧度
     * @return              修改后的图片
     */
    public static Bitmap toRoundCorner(Resources res, int resourceId, int pixels) {
        return toRoundCorner(BitmapFactory.decodeResource(res, resourceId),
                pixels);
    }

    /**
     * 把图片变成圆角
     * @param url       请求地址
     * @param pixels    圆角弧度
     * @return          修改后的图片
     */
    public static Bitmap toRoundCorner(String url, int pixels) {
        Bitmap orgBitmap = getScaledBitmap(url);
        if (orgBitmap != null) {
            Bitmap dstBitmap = toRoundCorner(orgBitmap, pixels);
            orgBitmap.recycle();
            return dstBitmap;
        }
        return null;
    }

    /**
     *  返回一张变换后的图片
     * @param srcBitmap
     * @param mRoundPx
     *            圆角大小
     * @param mContext
     *
     * @param requestSize
     *            需要图片大小
     * @return
     *            变换后的图片
     */
    public static Bitmap getScaledBitmap(Bitmap srcBitmap, int mRoundPx,
                                         int requestSize, Context mContext) {
        int scale = 1;
        final int REQUIRED_SIZE = DeviceUtil.dipToPixel(mContext, requestSize);
        int tempWidth = srcBitmap.getWidth();
        int tempHeight = srcBitmap.getHeight();
        while (true) {
            if (tempWidth / 2 < REQUIRED_SIZE || tempHeight / 2 < REQUIRED_SIZE) {
                break;
            }
            tempWidth /= 2;
            tempHeight /= 2;
            scale++;
        }

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = (int) Math.pow(2d, scale - 1);
        Bitmap destBitmap = null;
        Rect rectSrc = null;
        Rect rectDest = null;

        if (tempWidth >= tempHeight) {
            destBitmap = Bitmap.createBitmap(tempHeight, tempHeight,
                    Bitmap.Config.ARGB_8888);
            rectSrc = new Rect((tempWidth - tempHeight) / 2, 0, tempWidth
                    - (tempWidth - tempHeight) / 2, tempHeight);
            rectDest = new Rect(0, 0, tempHeight, tempHeight);

        } else if (tempHeight > tempWidth) {
            destBitmap = Bitmap.createBitmap(tempWidth, tempWidth,
                    Bitmap.Config.ARGB_8888);
            rectSrc = new Rect(0, (tempHeight - tempWidth) / 2, tempWidth,
                    tempHeight - (tempHeight - tempWidth) / 2);
            rectDest = new Rect(0, 0, tempWidth, tempWidth);

        }
        Canvas canvas = new Canvas(destBitmap);
        final int color = 0xff424242;
        final Paint paint = new Paint();

        final RectF rectF = new RectF(rectDest);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, mRoundPx, mRoundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, rectSrc, rectDest, paint);

        return destBitmap;
    }

    /**
     *返回一张变换后的图片
     * @param url
     *            图片地址
     * @param mRoundPx
     *            圆角大小
     * @param mContext
     *            上下文
     * @param requestSize
     *            需要图片大小
     * @return
     */
    public static Bitmap getScaledBitmap(String url, int mRoundPx,
                                         int requestSize, Context mContext) {
        // Decode with inSampleSize
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, opts);

        int scale = 1;
        final int REQUIRED_SIZE = DeviceUtil.dipToPixel(mContext, requestSize);
        int tempWidth = opts.outWidth;
        int tempHeight = opts.outHeight;
        while (true) {
            if (tempWidth / 2 < REQUIRED_SIZE || tempHeight / 2 < REQUIRED_SIZE) {
                break;
            }
            tempWidth /= 2;
            tempHeight /= 2;
            scale++;
        }

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = (int) Math.pow(2d, scale - 1);
        Bitmap srcBitmap = BitmapFactory.decodeFile(url, o);
        Bitmap destBitmap = null;
        Rect rectSrc = null;
        Rect rectDest = null;

        if (tempWidth >= tempHeight) {
            destBitmap = Bitmap.createBitmap(tempHeight, tempHeight,
                    Bitmap.Config.ARGB_8888);
            rectSrc = new Rect((tempWidth - tempHeight) / 2, 0, tempWidth
                    - (tempWidth - tempHeight) / 2, tempHeight);
            rectDest = new Rect(0, 0, tempHeight, tempHeight);

        } else if (tempHeight > tempWidth) {
            destBitmap = Bitmap.createBitmap(tempWidth, tempWidth,
                    Bitmap.Config.ARGB_8888);
            rectSrc = new Rect(0, (tempHeight - tempWidth) / 2, tempWidth,
                    tempHeight - (tempHeight - tempWidth) / 2);
            rectDest = new Rect(0, 0, tempWidth, tempWidth);

        }
        Canvas canvas = new Canvas(destBitmap);
        final int color = 0xff424242;
        final Paint paint = new Paint();

        final RectF rectF = new RectF(rectDest);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, mRoundPx, mRoundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, rectSrc, rectDest, paint);

        return destBitmap;
    }

    /**
     * 根据路径返回图片
     * @param url   请求路径
     * @return      图片Bitmap对象
     */
    public static Bitmap getScaledBitmap(String url) {
        Bitmap bm = null;
        File f = new File(url);
        if (f.exists()) { /* 产生Bitmap对象 */
            bm = BitmapFactory.decodeFile(url);
        }
        f = null;
        return bm;
    }

    /**
     * 根据文件路径(文件路径+文件名的形式)删除对应的文件
     * @param filePath  文件路径
     */
    public static void delFile(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
            f = null;
        }
    }

    /**
     * 使圆角功能支持BitampDrawable
     *
     * @param bitmapDrawable     一个BitmapDrawable对象
     * @param pixels             圆角大小
     * @return                  修改后的BitmapDrawable
     */
    public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable,
                                               int pixels) {
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
        return bitmapDrawable;
    }

    /**
     * 按正方形裁切图片
     * @param bitmap    一个bitmap对象
     * @return          修改后的bitmap对象
     */
    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }

    /**
     * 通过相册获取图片的时候，对于长高比例不适合的图片进行裁剪。
     * @param bitmap    一个bitmap对象
     * @param pixels    圆角大小
     * @return          修改后的图片
     */
    public static Bitmap GalleryImage(Bitmap bitmap, int pixels) {
        Bitmap round = ImageCrop(bitmap);
        return toRoundCorner(round, pixels);
    }

    /**
     * 保存图片
     * @param bitmap    bitmap图片
     * @return          保存路径
     */
    public static String saveBmp(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        // 存储文件名
        String filename = "/sdcard/SaveBmpTest.bmp";
        // 位图大小
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        // 图像数据大小
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
        try {

            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileos = new FileOutputStream(filename);
            // bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
            // 保存bmp文件头
            writeWord(fileos, bfType);
            writeDword(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDword(fileos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头
            writeDword(fileos, biSize);
            writeLong(fileos, biWidth);
            writeLong(fileos, biHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDword(fileos, biCompression);
            writeDword(fileos, biSizeImage);
            writeLong(fileos, biXpelsPerMeter);
            writeLong(fileos, biYPelsPerMeter);
            writeDword(fileos, biClrUsed);
            writeDword(fileos, biClrImportant);
            // 像素扫描
            byte bmpData[] = new byte[bufferSize];
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
                    int clr = bitmap.getPixel(wRow, nCol);
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color
                            .blue(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color
                            .green(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color
                            .red(clr);
                }
            fileos.write(bmpData);
            fileos.flush();
            fileos.close();
        }
        catch (FileNotFoundException e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        catch (IOException e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return filename;
    }

    private static void writeWord(FileOutputStream stream, int value)
            throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    private static void writeDword(FileOutputStream stream, long value)
            throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    private static void writeLong(FileOutputStream stream, long value)
            throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    /**
     * 功能：按指定高度和宽度放大或者缩小图片
     *
     * @param bitmap
     *            图片对象
     * @param w
     *            指定的宽度
     * @param h
     *            指定的高度
     * @return 新的图片对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);

        // bitmap.recycle();
        bitmap = null;
        return newBmp;
    }

    /**
     * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
     *
     * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
     *
     * B.本地路径:url="file://mnt/sdcard/photo/image.png";
     *
     * C.支持的图片格式 ,png, jpg,bmp,gif等等
     *
     * @param url
     * @return Bitmap
     */
    public static Bitmap GetLocalOrNetBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(),
                    IO_BUFFER_SIZE);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        }
        catch (IOException e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    /**
     * 获得图像
     *
     * @param path
     * @param options
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static Bitmap getBitmapByPath(String path, BitmapFactory.Options options,
                                         int screenWidth, int screenHeight) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        FileInputStream in = null;
        in = new FileInputStream(file);
        if (options != null) {
            Rect r = getScreenRegion(screenWidth, screenHeight);
            int w = r.width();
            int h = r.height();
            int maxSize = w > h ? w : h;
            int inSimpleSize = computeSampleSize(options, maxSize, w * h);
            options.inSampleSize = inSimpleSize; // 设置缩放比例
            options.inJustDecodeBounds = false;
        }
        Bitmap b = BitmapFactory.decodeStream(in, null, options);
        try {
            in.close();
        }
        catch (IOException e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return b;
    }

    /**
     * 获得设置信息
     * @param path  图片路径
     * @return      BitmapFactory,Options配置对象
     */
    public static BitmapFactory.Options getOptions(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 只描边，不读取数据
        BitmapFactory.decodeFile(path, options);
        return options;
    }

    private static Rect getScreenRegion(int width, int height) {
        return new Rect(0, 0, width, height);
    }

    /**
     * 获取需要进行缩放的比例，即options.inSampleSize
     * @param options           配置对象
     * @param minSideLength     最小尺寸
     * @param maxNumOfPixels    最大像素
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
                                        int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
                .ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
                .min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED)
                && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static final int UNCONSTRAINED = -1;

    /**
     * 旋转图片
     * @param angle     角度
     * @param bitmap    原始图片
     * @return Bitmap   修改后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // System.out.println("angle2=" + angle);
        // 创建新的图片
        try {
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return resizedBitmap;
        }
        catch (OutOfMemoryError ex) {
            CatchExceptionUtil.normalPrintStackTrace(ex);
        }
        return bitmap;

    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        }
        catch (IOException e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return degree;
    }
}
