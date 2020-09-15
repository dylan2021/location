package com.sfmap.tts;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    /**
     * 拷贝assets/data目录下的文件到指定目录
     *
     * @param context
     * 		    上下文
     * @param dataPath
     * 		    指定目录
     * @return
     */
    protected static void copyAssetsDataFiles(Context context, String dataPath) {
        // 没有文件夹，则创建
        if (!new File(dataPath).exists()) {
            new File(dataPath).mkdirs();
        }

        AssetManager assetMgr = context.getResources().getAssets();
        try {
            String[] filesList = assetMgr.list("data");
            LoopFiles(filesList, assetMgr, "data" + File.separator, dataPath + File.separator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void LoopFiles(String[]filesList, AssetManager assetMgr, String srcPath, String destPath)
    {
        Log.i(TAG, "srcPath="+srcPath);
        Log.i(TAG, "destPath="+destPath);
        for(String string : filesList)
        {
            try {
                String[] file = assetMgr.list(srcPath + string);
                if (file.length == 0) {
                    copyAssetFile(assetMgr, srcPath + string, destPath + string);
                } else {
                    if (!new File(destPath + string).exists()) {
                        new File(destPath + string).mkdirs();
                    }

                    LoopFiles(file, assetMgr,
                            srcPath + string + File.separator, destPath
                                    + string + File.separator);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void copyFile(Context context, String assetsFile, String destination) {
        InputStream is = null;
        FileOutputStream fos = null;
        byte[] buf1 = new byte[512];
        try {
            File des = new File(destination, assetsFile);
            if (des.exists()) {
                return;
            }
            if (!new File(destination).exists()) {
                Log.d(TAG, "mkdirs  " + new File(destination).mkdirs());
            }
            Log.d(TAG, "copy to: " + des.getAbsolutePath());
            fos = new FileOutputStream(des);
            is = context.getAssets().open(assetsFile);
            int readCount;
            while ((readCount = is.read(buf1)) > 0) {
                fos.write(buf1, 0, readCount);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "copy: ", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    protected static void copyAssetFile(AssetManager assetMgr, String src,
                                        String dst) {
        if (assetMgr == null) {
            throw new NullPointerException("Method param assetMgr is null.");
        }

        if (src == null) {
            throw new NullPointerException("Method param src is null.");
        }

        if (dst == null) {
            throw new NullPointerException("Method param dst is null.");
        }

        InputStream is = null;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        DataOutputStream dos = null;
        try {
            is = assetMgr.open(src, AssetManager.ACCESS_RANDOM);
            dis = new DataInputStream(is);

            File file = new File(dst);
            if (file.exists()) {
                file.delete();
                //return;
            }
            file.createNewFile();

            fos = new FileOutputStream(file);
            dos = new DataOutputStream(fos);
            byte[] buffer = new byte[1024];

            int len = 0;
            while ((len = dis.read(buffer, 0, buffer.length)) != -1) {
                dos.write(buffer, 0, len);
                dos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                    dis = null;
                }

                if (is != null) {
                    is.close();
                    is = null;
                }
                if (dos != null) {
                    dos.close();
                    dos = null;
                }

                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static final Pattern pattern = Pattern.compile(".*<s>\\s*(.*)\\s*</s>.*\\n\\s*(.*)\\s*");


    public static StringBuffer getAssetsFile(Context context, String fileName) {
        StringBuffer sb = new StringBuffer(1024 * 10);
        try {
            InputStream in = context.getAssets().open(fileName);
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferReader = new BufferedReader(reader);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            in.close();
            return sb;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }
}
