package com.sfmap.api.navi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sfmap.api.navi.enums.OfflineCrossDownStatus;
import com.sfmap.tbt.AppInfoUtilDecode;
import com.sfmap.tbt.CrossCityInfo;
import com.sfmap.tbt.util.AppInfo;
import com.sfmap.tbt.util.AuthManager;
import com.sfmap.tbt.util.DownloadManager;
import com.sfmap.api.navi.model.OfflineCrossInfo;
import com.sfmap.tbt.util.Request;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OfflineCrossManager {
    private Context mContext;
    private String base=Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + AppInfoUtilDecode.CONFIT_KEY_COMPANY + AppInfoUtilDecode.PATH_CROSS;
    private String path = base+"cityVersion.cfg";
    String url = "/mobile/route/cross?";
    String key = "";
    private StringBuffer netWorkData = null;
    private List<CrossCityInfo> downReauest;
    private Map<String,DownRunable> downThread;
    private OfflineCrossDownloadListener listener;
    private static Map<String ,Long> downProgress;
    private static Map<String ,Long> downLength;
    private int cancleStatus= OfflineCrossDownStatus.STOP;
    private Map<String, OfflineCrossInfo> localMap;

    Handler  handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(listener==null||msg==null)return;
        if(msg.what==10){
            String[] str= (String[]) msg.obj;
            listener.onCheckUpdata(msg.arg1==0?false:true,str);
        }
            else{
                OfflineCrossInfo info= (OfflineCrossInfo) msg.obj;
                listener.onDownload(msg.what,info.getComplete(),info.getSize(),info.getAdcode());
            }
        }
    };


    public OfflineCrossManager(Context context){
        this.mContext=context;
        downProgress =new HashMap<String, Long>();
        downLength=new HashMap<String,Long>();
        key="&ak=" + AppInfo.getKey(mContext);
        localMap=readConfigFile(path);
        if(localMap==null)localMap=new HashMap<>();
    }

    /**
     * 判断传入的城市是否有更新的离线数据包。
     * @param adcode 城市的ADCODE。
     */
    public void checkUpdata(String adcode){
        Map<String,String> map=new HashMap<>();
        String version="0";
        OfflineCrossInfo info=localMap.get(adcode);
        if(info!=null)version=localMap.get(adcode).getVersion();
        map.put(adcode,version);
        new CheckUpdata(map,false).start();
    }

    /**
     * 根据给定的城市adcode下载该城市的离线放大图数据.如果有注册OfflineCrossDownloadListener监听，下载状态会回调onDownload方法
     * @param adcode 城市adcode
     */
    public void downloadByAdcode(String adcode){
        Map<String,String> map=new HashMap<>();
        String version="0";
        OfflineCrossInfo info=localMap.get(adcode);
        if(info!=null)version=localMap.get(adcode).getVersion();
        map.put(adcode,version);
        new CheckUpdata(map,true).start();
    }

    /**
     * 根据城市编码查询 OfflineCrossInfo对象,查询本地数据信息
     * @param code 城市编码
     * @return OfflineCrossInfo对象
     */
    public OfflineCrossInfo getItemByCityCode(String code){
        OfflineCrossInfo crossInfo=localMap.get(code);
        if(crossInfo==null)crossInfo=new OfflineCrossInfo(0,0,code+"","0",OfflineCrossDownStatus.NOT_DATA);
        return crossInfo;
    }

    /**
     *设置离线数据下载监听
     * @param listener 下载监听器
     */
    public void setOfflineCrossDownloadListener(OfflineCrossDownloadListener listener){
        this.listener=listener;
    }

    protected String getDownAddress()
    {
        return AppInfo.getRouteCarURL(mContext);
    }

    /**
     * 暂停指定adcode的下载
     * @param adcode 要暂停下载的adcode
     */
    public void pause(String adcode){
        if(downThread!=null&&downThread.size()>0&&downThread.get(adcode)!=null)
        downThread.get(adcode).cancel();
        cancleStatus=OfflineCrossDownStatus.PAUSE;
    }

//    /**
//     * 继续指定adcod的下载
//     * @param adcode 要继续下载的adcode
//     */
//    public void restart(String adcode){
//        downloadByAdcode(adcode);
//    }

    /**
     * 删除指定adcode的离线数据
     * @param code 要删除的adcode
     */
    public void remove(String code){
        boolean isDel=delFile(base+code+"/CROSS.data")||delFile(base+code+"/CROSS.crs");
        if(isDel){
            localMap.remove(code);
            downProgress.remove(code);
            downLength.remove(code);
            saveConfig(localMap);
        }
        if(listener!=null){
            listener.onRemove(isDel,code);
        }
    }

    /**
     * 停止指定adcode的下载
     * @param adcode 城市ADCODE。
     */
    public void stop(String adcode){
        pause(adcode);
        cancleStatus=OfflineCrossDownStatus.STOP;
    }
    private class CheckUpdata extends Thread {

        private Map<String,String> adcodeMap;
        private boolean isDown;
        String adcodes = "adcodes=";
        CheckUpdata(Map adcode,boolean isDown){
            this.adcodeMap=adcode;
            this.isDown=isDown;
        }

        @Override
        public void run() {
            boolean frist = true;
            String adcode="";
            if (adcodeMap == null && adcodeMap.size() <= 0) return;
                for (String keys : adcodeMap.keySet()) {
                    if (frist) {
                        adcodes += keys + "," + adcodeMap.get(keys);
                        adcode=keys;
                    }
                    else adcodes += ";" + keys + "," + adcodeMap.get(keys);
                    frist = false;
                }
            String downUrl = getDownAddress() + url + adcodes + key;
//            Log.i("naviLog",downUrl);
            String json = null;
             List<CrossCityInfo> infos=null;
            try {
                json = doNet(downUrl);
            } catch (NaviException e) {
                e.printStackTrace();
                Message message=new Message();
                message.what=OfflineCrossDownStatus.NET_ERROR;
                OfflineCrossInfo info=new OfflineCrossInfo(0,0,adcode,"",message.what);
                message.obj=info;
                handler.sendMessage(message);
            }
            try {
                 infos = parseConfig(json);
            } catch (NaviException e) {
                e.printStackTrace();
                Message message=new Message();
                message.what=OfflineCrossDownStatus.ERROR;
                OfflineCrossInfo info=new OfflineCrossInfo(0,0,adcode,"",message.what);
                message.obj=info;
                handler.sendMessage(message);
            }
//            android.util.Log.i("naviLog", downUrl);


            if(isDown){

                if(infos!=null){
                    if(isNeedUpdata(infos)){
                        if(localMap!=null&&localMap.get(infos.get(0).adcode)!=null&&localMap.get(infos.get(0).adcode).getState()==OfflineCrossDownStatus.LOADING)return;
                        //		鉴权
                        if(AuthManager.authResult != -1 && AuthManager.authResult != 0)
                        {
                            Message message=new Message();
                            message.what=OfflineCrossDownStatus.ERROR;
                            OfflineCrossInfo info=new OfflineCrossInfo(0,0,adcode,"",message.what);
                            message.obj=info;
                            handler.sendMessage(message);
                            Log.e("OfflineCrossDown","鉴权失败");
                            return;
                        }
                        startDown(infos);
                    }
                    else
                    {
                        Message message=new Message();
                        message.what=OfflineCrossDownStatus.SUCCESS;
                        OfflineCrossInfo info=new OfflineCrossInfo(0,0,adcode,"",message.what);
                        message.obj=info;
                        handler.sendMessage(message);
                    }
                }
                else {
                    Message message=new Message();
                    message.what=OfflineCrossDownStatus.NOT_DATA;
                    OfflineCrossInfo info=new OfflineCrossInfo(0,0,adcode,"",message.what);
                    message.obj=info;
                    handler.sendMessage(message);
                }
                return;}
            if (listener!=null) {
                boolean hasnew=isNeedUpdata(infos);
                if(hasnew){
                String[] adcodes=new String[downReauest.size()];
                for(int i=0;i<downReauest.size();i++){
                    adcodes[i]=downReauest.get(i).adcode;
                }
//                listener.onCheckUpdata(hasnew, adcodes);
                    Message message=new Message();
                    message.what=10;
                    message.obj=adcodes;
                    message.arg1=1;
                    handler.sendMessage(message);
                }else{
                    Message message=new Message();
                    message.what=10;
                    message.obj=new String[]{};
                    message.arg1=0;
                    handler.sendMessage(message);
//                listener.onCheckUpdata(hasnew,new String[]{});
                }
            }

        }

    }
    private boolean isNeedUpdata(final List<CrossCityInfo> info){
        if(info==null||info.size()==0) return false;
        //比较版本
        boolean down=false;
        downReauest=new ArrayList<>();
        if(localMap ==null|| localMap.size()==0){
            down=true;
        for(CrossCityInfo infoItem:info){
            downReauest.add(infoItem );
            }
        }else{
        for(int i=0;i<info.size();i++) {
            String version = info.get(i).version;
            String oldVersion = "0";
            if(localMap.get(info.get(i).adcode)!=null)oldVersion= localMap.get(info.get(i).adcode).getVersion();
            if (oldVersion != null && version != null && version.equals(oldVersion)&&localMap.get(info.get(i).adcode).getState()==OfflineCrossDownStatus.SUCCESS) {
                down=false;
            }else{
                down = true;
                downReauest.add(info.get(i));
            }
        }
        }
        return down;
    }
    private String  doNet(String url) throws NaviException {
        String json="";
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
        HttpConnectionParams.setSoTimeout(httpParameters, 30000);
        HttpConnectionParams.setSocketBufferSize(httpParameters, 1000);
        InputStream inputStream = null;
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
//        ProxyUtil.setProxyHttpHost(httpClient, mContext);
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpresponse;

        try {
            httpresponse = httpClient.execute(httpGet);
            if (HttpStatus.SC_OK == httpresponse.getStatusLine().getStatusCode()) {

                HttpEntity entity = httpresponse.getEntity();
                inputStream = entity.getContent();
                BufferedReader  reader= new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                netWorkData = new StringBuffer();
                String line="";
                while ((line = reader.readLine()) !=null ) {
                    netWorkData.append(line).append("\n");
                }
                json=netWorkData.toString();
            }else{
                throw new NaviException("网络连接失败");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            throw new NaviException("网络错误");
        } catch (IOException e) {
            e.printStackTrace();
            throw new NaviException("网络错误");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return json;
    }


    private void startDown(List<CrossCityInfo> infos) {
        //如果网络环境为wifi环境下,启动下载
        NetworkInfo networkInfo = ((ConnectivityManager)(mContext.getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                DownRunable thread= new DownRunable(infos);
                if(downThread==null)downThread=new HashMap<>();
                Thread temp=downThread.get(infos.get(0).adcode);
                if(temp!=null)return;
                downThread.put(infos.get(0).adcode,thread);
                thread.start();
            }
        }
    }
    private void saveConfig(Map<String,OfflineCrossInfo> map){
        //每一个文件下载完成 保存对应版本到config文件中
        if(map==null||map.size()==0)
            map=new HashMap<>();
        StringBuffer config=new StringBuffer();
        for (String key:map.keySet()) {
            OfflineCrossInfo configInfo=map.get(key);
            config.append(configInfo.getAdcode()+","+configInfo.getVersion()+","+configInfo.getState()+","+configInfo.getSize()+","+configInfo.getComplete()+"\n");
        }
        saveFile(path,config.toString().getBytes(),false);
    }


    private void saveFile(String path,byte[] data,boolean append){
    File saveFile=new File(path);
    if(!saveFile.exists()){
        File dir=new File(saveFile.getParent());
        dir.mkdirs();
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    FileOutputStream outputStream;
    try {
        outputStream=new FileOutputStream(saveFile,append);
        outputStream.write(data);
        outputStream.close();
    } catch (IOException e) {
        e.printStackTrace();
    }

}


    private Map<String ,OfflineCrossInfo>readConfigFile(String path){
        File file= new File(path);
        if(file.isDirectory()||!file.exists()){
            return null;
        }
        StringBuffer content=new StringBuffer();
        Map<String,OfflineCrossInfo> maps=new HashMap<String ,OfflineCrossInfo>();
        try {
            InputStream inputStream=new FileInputStream(file);
            if(inputStream !=null){
                InputStreamReader inputReader=new InputStreamReader(inputStream);
                BufferedReader bufferedReader=new BufferedReader(inputReader);
                String line;
                while ((line = bufferedReader.readLine())!=null) {
                    String[] str=line.split(",");
                    if(str.length>0){
                        OfflineCrossInfo info=new OfflineCrossInfo();
                        info.setAdcode(str[0]);
                        info.setVersion(str[1]);
                        info.setState(Integer.valueOf(str[2]));
                        info.setSize(Long.valueOf(str[3]));
                        info.setComplete(Long.valueOf(str[4]));
                        maps.put(str[0],info);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){

        }
        return maps;

    }

    private boolean delFile(String path){
        File file=new File(path);
        if(file.exists()){file.delete();return true;}
        return false;
    }

private List<CrossCityInfo> parseConfig(String string) throws NaviException {
    JSONObject obj = null;
    if(string==null||"".equals(string)) return null;
    List<CrossCityInfo> infoList=null;
    String status="";
    try {
        obj = new JSONObject(string);
         status=obj.getString("status");
        if(status==null||!"0".equals(status)){
            if(!"0".equals(status))
                android.util.Log.i("OfflineCrossDown",obj.getString("message"));

            return null;}
        JSONArray resultArray = obj.optJSONArray("result");
            if(resultArray!=null&&resultArray.length()>0){
                infoList=new ArrayList<>();
                for(int i=0;i<resultArray.length();i++){
                    JSONObject  city = resultArray.getJSONObject(i);
                    if(city!=null){
                    CrossCityInfo info=new CrossCityInfo();
                    if(city.optString("downloadUrl")!=null)
                        info.setUrl(city.optString("downloadUrl"));
//                        info.setUrl("http://sw.bos.baidu.com/sw-search-sp/software/b96c4e2d7e5a4/youkuclient_setup_7.0.4.9121.exe");//测试下载
                        if(city.optString("version")!=null)
                            info.setVersion(city.optString("version"));
                        if(city.optString("adcode")!=null)
                            info.setAdcode(city.optString("adcode"));
                        infoList.add(info);
                }
                }
            }
    } catch (JSONException e) {
        e.printStackTrace();
        throw new NaviException("解析错误");
    }
    return infoList;
}

    private  class DownRunable extends Thread{
        private volatile boolean canceled=false;
        private List<CrossCityInfo> cityinfos;
        private long start;
        private boolean isFinish=false;
        DownRunable(List<CrossCityInfo> info){
            this.cityinfos=info;
            if(downProgress !=null){
                Long index= downProgress.get(info.get(0).adcode);
                if(index==null){
                    if(localMap.get(info.get(0).adcode)!=null)
                    start=localMap.get(info.get(0).adcode).getComplete();
                }else{
                    start=index;
                }
            }
        }
        DownRunable(List<CrossCityInfo> info, long paramlong){
            this.cityinfos=info;
            this.start=paramlong;
        }
        @Override
        public void run() {
                doDownFile(0,start,-1L);
        }

        private  void cancel() {
            canceled = true;
        }

        private void doDownFile(final int index ,long param1,long param2){
            if(cityinfos==null||index>=cityinfos.size()) return;
             final CrossCityInfo cityInfo=cityinfos.get(index);

             final DownloadManager down = new DownloadManager(new Request() {
                @Override
                public Map<String, String> getHeadMaps() {
                    return null;
                }

                @Override
                public Map<String, String> getRequestParam() {
                    return null;
                }

                @Override
                public String getUrl() {
                    return cityInfo.getUrl();
                }
            },param1,param2);
            down.makeGetRequest(new DownloadManager.DownloadListener() {
                String tmpePath=base+cityInfo.adcode+"/CROSS.data";
                String filePath=base+cityInfo.adcode+"/CROSS.crs";
                String oldVersion=localMap.get(cityInfo.adcode)==null?"0":localMap.get(cityInfo.adcode).getVersion();
                @Override
                public void onDownLoading(byte[] paramArrayOfByte, long total, long paramLong) {
                    saveFile(tmpePath,paramArrayOfByte,true);
                    downProgress.put(cityInfo.adcode,paramLong);
                    downLength.put(cityInfo.adcode,total+start);
                    Message message=new Message();
                    message.what= OfflineCrossDownStatus.LOADING;
                    OfflineCrossInfo info=new OfflineCrossInfo(paramLong,total+start,cityInfo.adcode,oldVersion,OfflineCrossDownStatus.LOADING);
                    message.obj=info;
                    handler.sendMessage(message);
                    if(canceled)down.cancle();
                }

                @Override
                public void onStart() {
                   if(start==0) delFile(tmpePath);
                    localMap.put(cityInfo.adcode, new OfflineCrossInfo(0,0,cityInfo.adcode,oldVersion,OfflineCrossDownStatus.LOADING));
                }

                @Override
                public void onCancle(long length) {
                    downThread.remove(cityInfo.adcode);
                    Message message=new Message();
                    message.what=cancleStatus;
                    message.obj=new OfflineCrossInfo(downProgress.get(cityInfo.adcode),downLength.get(cityInfo.adcode),cityInfo.adcode,oldVersion,cancleStatus);
                    handler.sendMessage(message);
                    localMap.put(cityInfo.adcode, (OfflineCrossInfo) message.obj);
                }

                @Override
                public void onFinish() {
                    isFinish=true;
                    File tmep=new File(tmpePath);
                   if(tmep.exists()) delFile(filePath);
                    downThread.remove(cityInfo.adcode);
                    downProgress.remove(cityInfo.adcode);
                    tmep.renameTo(new File(filePath));
                    Message message=new Message();
                    message.what=OfflineCrossDownStatus.SUCCESS;
                    message.obj=new OfflineCrossInfo(0,0,cityInfo.adcode,cityInfo.version,OfflineCrossDownStatus.SUCCESS);
                    handler.sendMessage(message);
                    localMap.put(cityInfo.adcode, (OfflineCrossInfo) message.obj);
                    saveConfig(localMap);
                }


                @Override
                public void onError(Throwable paramThrowable) {
                    paramThrowable.printStackTrace();
                    downThread.remove(cityInfo.adcode);
                    if(!isFinish){
                    Message message=new Message();
                    message.what=OfflineCrossDownStatus.NET_ERROR;
                    message.obj=new OfflineCrossInfo(downProgress.get(cityInfo.adcode)==null?0:downProgress.get(cityInfo.adcode),
                            downLength.get(cityInfo.adcode)==null?0:downLength.get(cityInfo.adcode),
                            cityInfo.adcode,oldVersion,OfflineCrossDownStatus.NET_ERROR);
                    handler.sendMessage(message);
                        localMap.put(cityInfo.adcode, (OfflineCrossInfo) message.obj);
                        saveConfig(localMap);
                    }
                }
            });
        }
    }

    /**
     * 离线放大图的下载监听接口。
     */
    public static interface OfflineCrossDownloadListener{
        /**
         *当调用checkUpdata 等检查更新函数的时候会被调用。
         * @param hasNew true表示有更新，说明官方有新版或者本地未下载。
         * @param adcodeArray 需要更新的城市adcode
         */
        public abstract void onCheckUpdata(boolean hasNew, String[] adcodeArray);

        /**
         * 下载状态回调，在调用downloadByAdcode 等下载方法的时候会启动。
         * @param status 参照OfflineCrossDownStatus。
         * @param completeCode 已下载文件大小
         * @param total 文件总大小
         * @param adcode 当前所下载的城市的adcode
         */
        public abstract void onDownload(int status,long completeCode, long total, String adcode);

        /**
         * 当调用OfflineMapManager.remove(String)方法时，如果有设置监听，会回调此方法
         * @param success true 删除成功
         * @param adcode 删除城市的adcode
         */
        public abstract void onRemove(boolean success,String adcode);
    }

    /**
     * 销毁offlineManager中的资源
     */
    public void destroy(){
        saveConfig(localMap);
        listener=null;
        downLength=null;
        downProgress=null;
        downThread=null;
        downReauest=null;
        localMap=null;
    }
}
