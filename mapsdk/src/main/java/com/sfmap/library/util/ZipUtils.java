package com.sfmap.library.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.content.Context;

/**
 * ZIP压缩工具
 */
public class ZipUtils {

    public static final String EXT = null;
    private static final String BASE_DIR = null;
    private static final String PATH = File.separator;
    private static final int BUFFER = 1024;

    /**
     * 根据路径解压文件
     * @param srcPath   源文件路径
     * @param listener  解压进度监听
     * @throws Exception    抛出一个异常
     */
    public static void decompress(String srcPath,
                                  ZipCompressProgressListener listener) throws Exception {
        File srcFile = new File(srcPath);
        decompress(srcFile, listener);
    }

    /**
     * 根据文件解压文件
     * @param srcFile  源文件File类型
     * @param listener 解压进度监听
     * @throws Exception    抛出一个异常
     */
    public static void decompress(File srcFile,
                                  ZipCompressProgressListener listener) throws Exception {
        String basePath = srcFile.getParent();
        decompress(srcFile, basePath, listener);
    }

    /**
     * 解压缩，提供了解压缩进度监听功能
     * @param zipFile  压缩文件
     * @param destFile 解压后存放的目录
     * @param listener 解压缩进度监听器
     * @throws Exception
     */
    public static void decompress(File zipFile, File destFile,
                                  ZipCompressProgressListener listener, UnZipFileBrake brake)
            throws Exception {
        long totalSize = 0;
        if (listener != null) { // 如果设置了解压进度监听，则先计算解压后的文件总大小
            try {
                FileInputStream zipCountFis = new FileInputStream(zipFile);
                CheckedInputStream zipCountCis = new CheckedInputStream(
                        zipCountFis, new CRC32());

                ZipInputStream zipCountStream = new ZipInputStream(zipCountCis);
                ZipEntry entry = null;

                while ((entry = zipCountStream.getNextEntry()) != null) {
                    if (brake != null && brake.mIsAborted) {
                        zipCountStream.closeEntry();
                        zipCountStream.close();
                        zipCountCis.close();
                        zipCountFis.close();
                        return;
                    }
                    long maxSize = entry.getSize();
                    totalSize += maxSize; // 先计算出压缩后的文件总大小
                    zipCountStream.closeEntry();
                }
                zipCountStream.close();
                zipCountCis.close();
                zipCountFis.close();
            } catch (Exception e) {
                CatchExceptionUtil.normalPrintStackTrace(e);
            }

        }

        FileInputStream fis = new FileInputStream(zipFile);
        CheckedInputStream cis = new CheckedInputStream(fis, new CRC32());
        ZipInputStream zis = new ZipInputStream(cis);
        decompress(destFile, zis, totalSize, listener, brake);
        zis.close();
        cis.close();
        fis.close();
    }

    /**
     * 解压文件放置指定位置
     * @param srcFile   源文件
     * @param destPath  解压路径
     * @throws Exception    抛出一个异常
     */
    public static void decompress(File srcFile, String destPath,
                                  ZipCompressProgressListener listener) throws Exception {
        decompress(srcFile, new File(destPath), listener, null);
    }

    /**
     * 解压文件放置指定位置
     * @param inputStream   流对象
     * @param destPath      压缩路径
     * @throws Exception    抛出一个异常
     */
    public static void decompress(InputStream inputStream, String destPath)
            throws Exception {
        decompress(inputStream, destPath, 0, null);
    }

    /**
     * 包含监听解压缩进度的解压缩方法
     * @param inputStream   流信息
     * @param destPath      压缩路径
     * @param totalSize     总共大小
     * @param listener      解压监听
     * @throws Exception    抛出一个异常
     */
    public static void decompress(InputStream inputStream, String destPath,
                                  long totalSize, ZipCompressProgressListener listener)
            throws Exception {
        // CheckedInputStream cistream = new CheckedInputStream(inputStream,
        // new CRC32());
        // ZipInputStream zipCountStream = new ZipInputStream(cistream);
        // ZipEntry entry = null;
        // long totalSize = 0;
        // while ((entry = zipCountStream.getNextEntry()) != null) {
        // int maxSize = (int) entry.getSize();
        // totalSize+=maxSize;
        // zipCountStream.closeEntry();
        // }

        CheckedInputStream cis = new CheckedInputStream(inputStream,
                new CRC32());
        ZipInputStream zis = new ZipInputStream(cis);

        decompress(new File(destPath), zis, totalSize, listener, null);
        zis.close();
        cis.close();
    }

    /**
     * 文件 解压缩
     *
     * @param srcPath  源文件路径
     * @param destPath 目标文件路径
     * @throws Exception    抛出一个异常
     */
    public static void decompress(String srcPath, String destPath,
                                  ZipCompressProgressListener listener) throws Exception {

        File srcFile = new File(srcPath);
        decompress(srcFile, destPath, listener);
    }

    /**
     * 文件 解压缩
     *
     * @param destFile 目标文件
     * @param zis      ZipInputStream
     * @throws Exception
     */
    private static void decompress(File destFile, ZipInputStream zis,
                                   long totalSize, ZipCompressProgressListener listener,
                                   UnZipFileBrake brake) throws Exception {
        ZipEntry entry = null;
        int compressedCount = 0;
        int cSize = 0;

        while ((entry = zis.getNextEntry()) != null) {
            if (brake != null && brake.mIsAborted) {
                zis.closeEntry();
                return;
            }

            // 文件
            String dir = destFile.getPath() + File.separator + entry.getName();

            File dirFile = new File(dir);

            // 文件检查
            fileProber(dirFile);

            if (entry.isDirectory()) {
                dirFile.mkdirs();
            } else {
                cSize = decompressFile(dirFile, zis, compressedCount,
                        totalSize, listener, brake);
                compressedCount += cSize;
            }

            zis.closeEntry();
        }
    }

    /**
     * 文件探针
     * <p>
     * <p>
     * 当父目录不存在时，创建目录！
     *
     * @param dirFile
     */
    private static void fileProber(File dirFile) {

        File parentFile = dirFile.getParentFile();
        if (!parentFile.exists()) {

            // 递归寻找上级目录
            fileProber(parentFile);

            parentFile.mkdir();
        }

    }

    public static class UnZipFileBrake {
        public boolean mIsAborted = false;
    }

    /**
     * 文件解压缩
     *
     * @param destFile 目标文件
     * @param zis      ZipInputStream
     * @throws Exception
     */
    private static int decompressFile(File destFile, ZipInputStream zis,
                                      long compressedSize, long totalSize,
                                      ZipCompressProgressListener listener, UnZipFileBrake brake)
            throws Exception {
        int compressCount = 0;
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(destFile));

        int count;
        byte data[] = new byte[BUFFER];

        while ((count = zis.read(data, 0, BUFFER)) != -1) {
            if (brake != null && brake.mIsAborted) {
                bos.close();
                return compressCount;
            }
            bos.write(data, 0, count);
            compressCount += count;
            if (totalSize > 0 && listener != null) {
                long progress = (compressedSize + compressCount) * 100
                        / totalSize;
                if (brake == null || !brake.mIsAborted) {
                    listener.onFinishProgress(progress);
                }
            }
        }
        bos.close();
        return compressCount;
    }

    /**
     * 压缩
     *
     * @param srcFile       源路径
     * @throws Exception    异常
     */
    public static void compress(File srcFile,
                                ZipCompressProgressListener listener) throws Exception {
        String name = srcFile.getName();
        String basePath = srcFile.getParent();
        String destPath = basePath + name + EXT;
        compress(srcFile, destPath, listener);
    }

    /**
     * 压缩
     *
     * @param srcFile   源路径
     * @param destFile  目标路径
     * @param listener  监听器
     * @throws Exception    异常
     */
    public static void compress(File srcFile, File destFile,
                                ZipCompressProgressListener listener) throws Exception {
        long totalSize = srcFile.length();
        // 对输出文件做CRC32校验
        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(
                destFile), new CRC32());

        ZipOutputStream zos = new ZipOutputStream(cos);
        compress(srcFile, zos, BASE_DIR, totalSize, listener);

        zos.flush();
        zos.close();
    }

    /**
     * 压缩文件
     *
     * @param srcFile       源路径
     * @param destPath      目标路径
     * @throws Exception    异常
     */
    public static void compress(File srcFile, String destPath,
                                ZipCompressProgressListener listener) throws Exception {
        compress(srcFile, new File(destPath), listener);
    }

    /**
     * 压缩
     *
     * @param srcFile       源路径
     * @param zos           ZipOutputStream
     * @param basePath      压缩包内相对路径
     * @throws Exception    异常
     */
    private static void compress(File srcFile, ZipOutputStream zos,
                                 String basePath, long totalSize,
                                 ZipCompressProgressListener listener) throws Exception {
        if (srcFile.isDirectory()) {
            compressDir(srcFile, zos, basePath, totalSize, listener);
        } else {
            compressFile(srcFile, zos, basePath, totalSize, listener);
        }
    }

    /**
     * 压缩
     *
     * @param srcPath       压缩路径
     * @throws Exception    压缩监听
     */
    public static void compress(String srcPath,
                                ZipCompressProgressListener listener) throws Exception {
        File srcFile = new File(srcPath);

        compress(srcFile, listener);
    }

    /**
     * 文件压缩
     * @param srcPath      源文件路径
     * @param destPath     目标文件路径
     */
    public static void compress(String srcPath, String destPath,
                                ZipCompressProgressListener listener) throws Exception {
        File srcFile = new File(srcPath);

        compress(srcFile, destPath, listener);
    }


    private static void compressDir(File dir, ZipOutputStream zos,
                                    String basePath, long totalSize,
                                    ZipCompressProgressListener listener) throws Exception {

        File[] files = dir.listFiles();

        // 构建空目录
        if (files.length < 1) {
            ZipEntry entry = new ZipEntry(basePath + dir.getName() + PATH);

            zos.putNextEntry(entry);
            zos.closeEntry();
        }

        for (File file : files) {
            // 递归压缩
            compress(file, zos, basePath + dir.getName() + PATH, totalSize,
                    listener);
        }
    }

    /**
     * 文件压缩
     *
     * @param file 待压缩文件
     * @param zos  ZipOutputStream
     * @param dir  压缩文件中的当前路径
     * @throws Exception
     */
    private static int compressFile(File file, ZipOutputStream zos, String dir,
                                    long totalSize, ZipCompressProgressListener lintener)
            throws Exception {
        int compressCount = 0;
        /**
         * 压缩包内文件名定义
         *
         * <pre>
         * 如果有多级目录，那么这里就需要给出包含目录的文件名
         * 如果用WinRAR打开压缩包，中文名将显示为乱码
         * </pre>
         */
        ZipEntry entry = new ZipEntry(dir + file.getName());

        zos.putNextEntry(entry);
        // long size = file.length(); // 大小 bytes
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                file));

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = bis.read(data, 0, BUFFER)) != -1) {
            zos.write(data, 0, count);
            compressCount += count;
        }
        bis.close();
        zos.closeEntry();
        return compressCount;
    }

    /**
     * 解压监听
     */
    public interface ZipCompressProgressListener {
        /**
         * 解压大小回调
         * @param progress 大小
         */
        public void onFinishProgress(long progress);
    }

    /**
     * 使用zip压缩文件
     * @param zipFile   源文件
     * @param file      新的文件
     * @param context   上下文
     * @throws Exception    抛出异常
     */
    public static void fileZip(File zipFile, File file, Context context)
            throws Exception {
        if (file.isFile()) {
            ZipOutputStream zos = new ZipOutputStream(context.openFileOutput(
                    zipFile.getName(), Activity.MODE_PRIVATE));
            zos.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fis = context.openFileInput(file.getName());
            byte[] bb = new byte[2048];
            int aa = 0;
            while ((aa = fis.read(bb)) != -1) {
                zos.write(bb, 0, aa);
            }
            fis.close();
            zos.close();

        }
    }

    /**
     * 压缩文件
     * @param fileName  需要解压的文件名
     * @param unZipPath 解压后的文件保存路径
     */
    public static void unZipFile(String fileName, String unZipPath) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(fis));
            ZipEntry entry;

            // getNextEntry()！=null时，ZipInputStream读取每个压缩文件，
            while ((entry = zis.getNextEntry()) != null) {
                int size;
                byte[] buffer = new byte[2048];

                FileOutputStream fos = new FileOutputStream(unZipPath + "/"
                        + entry.getName());
                BufferedOutputStream bos = new BufferedOutputStream(fos,
                        buffer.length);

                while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, size);
                }
                bos.flush();
                bos.close();
            }

            zis.close();
            fis.close();
        } catch (IOException e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
    }
}
