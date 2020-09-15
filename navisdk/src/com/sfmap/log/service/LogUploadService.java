package com.sfmap.log.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.sfmap.api.maps.DesUtil;
import com.sfmap.api.navi.model.ResultBean;
import com.sfmap.log.model.LocParam;
import com.sfmap.log.model.LogParam;
import com.sfmap.util.CommUtil;
import com.sfmap.util.IOUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Locale;

import static com.sfmap.util.CommUtil.isConnect;

public class LogUploadService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    public String naviLogPath = "";
    private ArrayList<LogParam> logParams = new ArrayList<>();
    private int mHttpTimeOut = 30000;
    private boolean mGZip = true;
    private LogParam lastLogParam;

    @Override
    public void onCreate() {
        super.onCreate();
        naviLogPath = Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/01SfNaviSdk/" + "click_log.txt";
        startLocalLogUpload();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {

        public void ReciveClick(LogParam logParam) {
            //调用办证的方法
            reciveClick(logParam);
        }
    }

    public void reciveClick(LogParam logParam) {
        lastLogParam = logParam;
        Log.d(TAG, "EventBus reciveLog " + new Gson().toJson(logParam));
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadLog(logParam);
            }
        }).start();

    }

    // 上传实时日志
    private boolean uploadLog(LogParam log) {
        return uploadPost(null, log, 5);
    }

    private void startLocalLogUpload() {
        if (!isConnect(this)) {
            return;
        }
        readNaviLog();
        if (logParams.isEmpty()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (logParams.size() > 0) {
                    uploadHisLog(logParams.get(0));
                }
            }
        }).start();
    }

    private void readNaviLog() {
        try {
            InputStream tmpStream = GetLogFile(naviLogPath);
            if (null == tmpStream) {
                return;
            }
            logParams.clear();
            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(tmpStream, "UTF-8"));
            String str;
            while ((str = bufReader.readLine()) != null) {
                if (str != null && str.length() > 0) {
                    LogParam locParam = new Gson().fromJson(str, LogParam.class);
                    logParams.add(locParam);
                }
            }
            bufReader.close();
            tmpStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取配置文件
     *
     * @return
     */
    public InputStream GetLogFile(String logPath) {
        InputStream tmpStream = null;
        String filePath = logPath;
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            tmpStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tmpStream;
    }

    // 上传历史日志
    private boolean uploadHisLog(LogParam log) {
        return uploadPost(null, log, 7);
    }

    /*
     * 上传数据
     * list: 上传的数据
     * DATA_TYPE:上传数据类型:1:实时定位数据,3:历史定位,5:实时日志,7:历史日志
     */
    private boolean uploadPost(LocParam list, LogParam log, int DATA_TYPE) {
        //网络异常
        if (!isConnect(this)) {
            if (DATA_TYPE == 1 || DATA_TYPE == 5) {//实时
                sendMessage(DATA_TYPE + 1, "网络异常");
            }
            CommUtil.d(this, "location", "net is not connect");
            return false;
        }

        boolean out;
        BufferedReader bin = null;
        BufferedOutputStream bos = null;
        HttpURLConnection conn = null;

        StringBuilder sbUrl = new StringBuilder();

        if (DATA_TYPE < 4) {
            sbUrl.append("http://");
            sbUrl.append("gis-rss-eta-navi-track.sf-express.com:2099");
            sbUrl.append("/navitrack/api/uploadJ");
        } else {
            sbUrl.append("https://gis.sit.sf-express.com:45001/mms/eta/addNaviLog");
        }

        try {
            String strJson = new Gson().toJson(DATA_TYPE < 4 ? list : log);//定位, 日志
            if (null == strJson || strJson.isEmpty()) {
                CommUtil.d(this, TAG, "location json is empty");
                return false;
            }

            String args = URLDecoder.decode(strJson, "UTF-8");

            byte[] buffer = null;
            if (DATA_TYPE < 4) {
                //压缩类型 deflate/gzip
                String mCompressType = "gzip";

                if (mGZip) {
                    buffer = CommUtil.compress(args, mCompressType);
                    if (null == buffer) {
                        CommUtil.d(this, "netlocator", "request err,args :" + args);
                        throw new Exception(mCompressType + " compress fail");
                    }
                } else {
                    buffer = args.getBytes(CommUtil.UTF8);
                }
                Log.d(TAG, "requesturl 加密前" + sbUrl.toString() + args);
            } else {
                String requset = "param=" + new DesUtil().encrypt(args) + "&ak=" + "1aca21cfd4204548bad2fd32dca24b8b";
                buffer = requset.getBytes();
                Log.d(TAG, "requesturl 加密后" + requset);
            }
            URL realUrl = new URL(sbUrl.toString());
            CommUtil.d(this, "url", sbUrl.toString());

            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);


            if (mGZip) {
//                if (DATA_TYPE < 4 || DATA_TYPE == 7) {
                if (DATA_TYPE < 4 ) {
                    conn.setRequestProperty("Content-Type", "application/json");
                } else {
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                }

            }

            conn.setConnectTimeout(mHttpTimeOut);
            conn.setReadTimeout(mHttpTimeOut);

            bos = new BufferedOutputStream(conn.getOutputStream());
            bos.write(buffer);
            bos.flush();


            StringBuilder resultBuffer = new StringBuilder();
            bin = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), CommUtil.UTF8));
            String line;
            while ((line = bin.readLine()) != null) {
                resultBuffer.append(line);
            }
            if (conn.getResponseCode() == 200) {
                String res = "";
                if (DATA_TYPE < 4) {
                    res = resultBuffer.toString();
                } else {
                    res = DesUtil.getInstance().decrypt(resultBuffer.toString().trim(),
                            "UTF-8");
                    Log.d(TAG, "requestdata 解密后" + res);
                }

                ResultBean resultBean = new Gson().fromJson(res, ResultBean.class);

                if (resultBean.getStatus() == 0) {
                    handler.sendEmptyMessage(DATA_TYPE);
                    if (DATA_TYPE == 3) {
                    }
                } else {
                    sendMessage(DATA_TYPE + 1, resultBean.getResult().getMsg());
                    if (DATA_TYPE == 3) {
                        CommUtil.d(this, TAG, "上传失败：" + resultBean.getResult().getMsg());
                    }
                }
                if (DATA_TYPE == 1) {
                    CommUtil.d(this, TAG, String.format(Locale.US, "Upload %d locations successfully.", list.getLocations().size()));
                }
                out = true;
            } else {
                sendMessage(DATA_TYPE + 1, "网络异常");
                Log.d(TAG, "requesterror " + "网络异常");
                CommUtil.d(this, "NetLocator",
                        "request err, content :" + resultBuffer.toString());
                out = false;
            }

        } catch (Throwable e) {
            sendMessage(DATA_TYPE + 1, "网络异常");
            e.printStackTrace();
            CommUtil.d(this, "req err", CommUtil.getStackTrace(e));
            out = false;
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(bin);
            if (null != conn) {
                conn.disconnect();
            }
        }


        return out;
    }

    private void sendMessage(int code, Object msg) {
        if (msg == null) {
            handler.sendEmptyMessage(code);
            return;
        }
        Message message = new Message();
        message.what = code;
        message.obj = msg;
        handler.sendMessage(message);
    }

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 5:
                    Log.d(TAG, "上传日志成功");
                    startLocalLogUpload();
                    break;
                case 6:
                    saveRouteInfo(naviLogPath, new Gson().toJson(lastLogParam) + "\n");
                    Log.d(TAG, "上传日志失败");
                    break;
                case 7:
                    if (logParams.isEmpty()) {
                        return true;
                    }
                    logParams.remove(0);
                    StringBuffer local1 = new StringBuffer();
                    for (LogParam logParam : logParams) {
                        local1.append(new Gson().toJson(logParam) + "\n");
                    }
                    saveRouteInfo1(naviLogPath, local1.toString());
                    logParams.clear();
                    startLocalLogUpload();
                    Log.d(TAG, "上传日志成功");
                    break;
                case 8:
                    Log.d(TAG, "上传日志失败");
                    break;
            }
            return true;
        }
    };
    public Handler handler = new Handler(callback);

    private void saveRouteInfo(String path, String infos) {
        Log.d(TAG, path + "\n" + infos);
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(infos.getBytes());
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRouteInfo1(String path, String infos) {
        Log.d(TAG, path + "\n" + infos);
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            raf.seek(0);
            raf.write(infos.getBytes());
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
