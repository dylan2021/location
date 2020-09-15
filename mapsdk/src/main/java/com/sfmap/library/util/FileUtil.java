package com.sfmap.library.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 文件工具类
 */
public class FileUtil {

    /**
     * 文件复制
     * @param context           上下文
     * @param sourceFilename    目标路径
     * @param dest              文件对象
     * @throws Exception        异常抛出
     */
    public static void copy(Context context, String sourceFilename, File dest)
            throws Exception {
        dest.delete();
        InputStream ins = context.getAssets().open(sourceFilename);
        byte[] bytes = new byte[ins.available()];
        ins.read(bytes);
        ins.close();
        FileOutputStream fos = new FileOutputStream(dest);
        fos.write(bytes);
        fos.close();
    }

    /**
     * 文件是否可读写
     * @param file      文件路径
     * @return          true文件可读否者反之
     */
    public static boolean isFileLocked(String file) {
        RandomAccessFile raf = null;
        FileChannel channel = null;
        FileLock lock = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            channel = raf.getChannel();
            lock = channel.tryLock();
            if (lock != null) {
                return false;
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            try {
                if (lock != null) {
                    lock.release();
                }
                if (channel != null) {
                    channel.close();
                }
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {

            }

        }
        return true;
    }

    /**
     * 检测文件是否存在
     * @param fileName  文件名称
     * @return boolean  true文件存在
     */
    public static boolean isFileExists(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {// 2014-02-25 Kang, Leo add
            return new File(fileName).exists();
        } else {
            return false;
        }
    }

    /**
     * 获取文本文件内容
     * @param fileName  文件名称
     * @return string   文本内通
     * @throws java.io.IOException  抛出读写异常
     */

    public static String getFileContents(String fileName) throws IOException {
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        File fHandler = new File(fileName);
        FileInputStream inputStream = new FileInputStream(fHandler);
        while ((inputStream.read(bytes)) != -1) {
            arrayOutputStream.write(bytes, 0, bytes.length);
        }
        String content = new String(arrayOutputStream.toByteArray());
        inputStream.close();
        arrayOutputStream.close();
        return content.trim();
    }

    /**
     * 循环删除文件及文件夹
     * @param file  文件对象
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0, len = files.length; i < len; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }

    /**
     * 循环删除文件及文件夹
     * @param context   上下文
     * @param name      文件或者文件夹名称
     * @param file       File对象
     */
    public static void copyFromAssert(Context context, String name, File file) {
        String md5 = null;
        if (file.exists()) {
            md5 = MD5Util.getFileMD5(file.getAbsolutePath());
        }
        byte[] data = ResUtil.decodeAssetResData(context, name);
        if (data != null) {
            String md5_ = MD5Util.getByteArrayMD5(data);
            if (md5_ != null && !md5_.equals(md5)) {
                file.delete();
            }

            try {
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(data);
                fout.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 将字符串保存到指定的文件(如果文件存在删除)
     * @param file     File类型对象
     * @param data     文本内容
     */
    public static void writeTextFile(File file, String data) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.WriteLock writelock = lock.writeLock();
        writelock.lock();
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                parent.mkdirs();
            }
            if (file.exists()) { // 判断当前文件是否存在
                file.delete(); // 存在就删除
            }
            file.createNewFile();
            byte[] bytes = data.getBytes();
            OutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.flush();
            os.close();
        } catch (Exception ex) {
            CatchExceptionUtil.normalPrintStackTrace(ex);
        } finally {
            writelock.unlock();
        }
    }

    /**
     * 将字符数组保存到指定的文件
     *
     * @param file  File类型对象
     * @param data  byte数组
     */
    public static void writeTextFile(File file, byte[] data) {
        try {
            if (file.exists()) { // 判断当前文件是否存在
                file.delete(); // 存在就删除
            }
            file.createNewFile();
            OutputStream os = new FileOutputStream(file);
            os.write(data);
            os.flush();
            os.close();
        } catch (Exception ex) {
            CatchExceptionUtil.normalPrintStackTrace(ex);
        }
    }

    /**
     * 将byte数据保存到文件
     *
     * @param fileName  文件地址
     * @param data      byte数组
     */
    public static void writeDatasToFile(String fileName, byte[] data) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.WriteLock writelock = lock.writeLock();
        writelock.lock();
        try {
            if (data == null || data.length == 0)
                return;
            File file = new File(fileName);
            if (file.exists()) { // 判断当前文件是否存在
                file.delete(); // 存在就删除
            }
            file.createNewFile();
            OutputStream os = new FileOutputStream(file);
            os.write(data);
            os.flush();
            os.close();
        } catch (Exception ex) {
            CatchExceptionUtil.normalPrintStackTrace(ex);
        } finally {
            writelock.unlock();
        }
    }

    /**
     * 获取文件内容
     * @param fileName  文件名称
     * @return          byte数组
     */
    public static byte[] readFileContents(String fileName) {
        try {
            File fHandler = new File(fileName);
            if (!fHandler.exists()) { // 判断当前文件是否存在
                return null;
            }

            FileInputStream inputStream = new FileInputStream(fHandler);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

            outStream.close();
            inputStream.close();
            return outStream.toByteArray();

        } catch (Exception ex) {
            CatchExceptionUtil.normalPrintStackTrace(ex);
        }
        return null;
    }

    /**
     * 文件下载完成之后，将临时文件改成.zip文件名
     * @param tempFile  临时文件地址
     * @param apkFile   新文件地址
     */
    public static void dealTheFileByCompelete(String tempFile, String apkFile) {
        // 将.xxx.tmp扩展名改成.xxx
        if (TextUtils.isEmpty(tempFile) || TextUtils.isEmpty(apkFile))
            return;

        File tmpFile = new File(tempFile);
        File zipFile = new File(apkFile);
        if (!tmpFile.exists()) {
            return;
        }
        if (zipFile.exists()) {// 文件是否存在
            zipFile.delete();
            if (!tmpFile.renameTo(zipFile)) {
                // 文件重命名成功
            }
        } else {
            if (!tmpFile.renameTo(zipFile)) {
                // 文件重命名成功
            }
        }
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param fileName 要删除的根目录名称
     * @param isCancel 是否取消删除
     */
    public static void recursionDeleteFile(String fileName, boolean isCancel) {
        if (isCancel)
            return;
        File file = new File(fileName);
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                recursionDeleteFile(f.getName(), isCancel);
            }
            file.delete();
        }
    }

    /**
     * 追加文件
     * @param msg       内容
     * @param path      路径
     * @param fileName  文件名称
     */
    public static void wirteAppend(String msg,String path,String fileName){
        String path1 = path + "/" + fileName;
        File file = new File(path1);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(msg);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
