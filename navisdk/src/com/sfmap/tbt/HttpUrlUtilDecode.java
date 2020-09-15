package com.sfmap.tbt;

import android.os.Build;
import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.sfmap.api.mapcore.util.AppInfo;
import com.sfmap.api.maps.DesUtil;
import com.sfmap.api.navi.model.ETARoute;
import com.sfmap.api.navi.model.ETATmc;
import com.sfmap.api.navi.model.YawArgs;
import com.sfmap.tbt.util.GzipUtils;
import com.sfmap.tbt.util.LogUtil;

import org.apache.commons.codec.binary.Hex;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import static com.sfmap.tbt.FrameForTBT.uncompressToString;

public class HttpUrlUtilDecode {
    private String TAG = HttpUrlUtilDecode.class.getSimpleName();
    private static NetCompleteListener netCompleteListener;
    private int connectTimeout;
    private int readTimeout;
    private boolean e;
    private SSLContext f;
    private Proxy g;
    private volatile boolean h = false;
    private long i = -1L;
    private String rh = "";
    HostnameVerifier a = new HostnameVerifierImpl();

    HttpUrlUtilDecode(int paramInt1, int paramInt2, Proxy paramProxy, boolean paramBoolean, String rh) {
        this.connectTimeout = paramInt1;
        this.readTimeout = paramInt2;
        this.g = paramProxy;
        this.e = paramBoolean;
//        this.e = true;
        this.rh = rh;
        if (paramBoolean)
            try {
                SSLContext localSSLContext = SSLContext.getInstance("TLS");
                localSSLContext.init(null, null, null);

                this.f = localSSLContext;
            } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
                BasicLogHandler.a(localNoSuchAlgorithmException, "HttpUrlUtil", "HttpUrlUtil");

                localNoSuchAlgorithmException.printStackTrace();
            } catch (Throwable localThrowable) {
                BasicLogHandler.a(localThrowable, "HttpUtil", "HttpUtil");
                localThrowable.printStackTrace();
            }
    }

    ResponseEntity doGet(String paramString, Map<String, String> paramMap1,
                         Map<String, String> paramMap2) throws OperExceptionDecode {
        try {
            String str = urlPramsToString(paramMap2);
            StringBuffer localStringBuffer = new StringBuffer();
            localStringBuffer.append(paramString);
            if (str != null)
                localStringBuffer.append("?").append(str);
                URL localURL = new URL(localStringBuffer.toString());
                android.util.Log.i(TAG,"请求参数get："+localStringBuffer.toString());
                HttpURLConnection localHttpURLConnection = getHttpURLConnection(localURL);
                addHeadToHttp(paramMap1, localHttpURLConnection);
                localHttpURLConnection.setRequestMethod("GET");
                localHttpURLConnection.setDoInput(true);
                localHttpURLConnection.connect();
            return doConnection(localHttpURLConnection);
        } catch (ConnectException localConnectException) {
            throw new OperExceptionDecode("http连接失败 - ConnectionException");
        } catch (MalformedURLException localMalformedURLException) {
            throw new OperExceptionDecode("url异常 - MalformedURLException");
        } catch (UnknownHostException localUnknownHostException) {
            throw new OperExceptionDecode("未知主机 - UnKnowHostException");
        } catch (SocketException localSocketException) {
            throw new OperExceptionDecode("socket 连接异常 - SocketException");
        } catch (SocketTimeoutException localSocketTimeoutException) {
            throw new OperExceptionDecode(
                    "socket 连接超时 - SocketTimeoutException");
        } catch (IOException localIOException) {
            throw new OperExceptionDecode("IO 操作异常 - IOException");
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
            throw new OperExceptionDecode("未知的错误");
        }
    }

    ResponseEntity doNet(String paramString, Map<String, String> paramMap1, Map<String, String> paramMap2) throws OperExceptionDecode {
        String str = urlPramsToString(paramMap2);
        if (str == null)
            return doPost(paramString, paramMap1, new byte[0]);
        try {
            return doPost(paramString, paramMap1, str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
            BasicLogHandler.a(localUnsupportedEncodingException, "HttpUrlUtil", "postRequest1");
            localUnsupportedEncodingException.printStackTrace();
        }
        return doPost(paramString, paramMap1, str.getBytes());
    }

    ResponseEntity doNet(String paramString, Map<String, String> paramMap1, Map<String, String> paramMap2, byte[] paramArrayOfByte) throws OperExceptionDecode {
        try {
            if (paramMap2 != null) {
                String str = urlPramsToString(paramMap2);
                StringBuffer localStringBuffer = new StringBuffer();
                localStringBuffer.append(paramString);
                if (str != null)
                    localStringBuffer.append("?").append(str);

                paramString = localStringBuffer.toString();
            }
            android.util.Log.i(TAG,paramString);
            if(paramString.equals(AppInfo.getSfRouteTmcURL())){
                return requestETATmcPost(paramString, paramMap1, paramArrayOfByte);
            }else if(paramString.equals(AppInfo.getSfRouteEtaURL())){
                return requestETANaviPost(paramString, paramMap1, paramArrayOfByte);
            }else if(paramString.equals(AppInfo.getSfReRouteEtaURL())){
                return requestETAYawPost(paramString, paramMap1, paramArrayOfByte);
            }
        }
        catch (Throwable localThrowable) {
            BasicLogHandler.a(localThrowable, "HttpUrlUtil", "PostReqeust3");
            localThrowable.printStackTrace();
        }
        return null;
    }

    public ResponseEntity requestETATmcPost(String paramString, Map<String, String> paramMap1, byte[] postValue){
        String soapContent = com.sfmap.tbt.util.Utils.byteArrayToStr(postValue);
//        paramString = "http://10.82.248.13:8085/mms/eta/noYawc";

        ETATmc etaTmc = new ETATmc();
        JSONObject obj = null;
        String swid = "";
        String ak1 = "";
        try {
            obj = new JSONObject(soapContent);
            swid = obj.getString("swid");
            ak1 = obj.getString("ak");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
//        etaTmc.setAk(ak1);
        etaTmc.setSwid(swid);
        etaTmc.setCc(3);
        etaTmc.setCompress(1);
        etaTmc.setNaviId(com.sfmap.tbt.util.AppInfo.getNaviId());
        etaTmc.setRouteId(com.sfmap.tbt.util.AppInfo.getRouteId());
        etaTmc.setStartX(com.sfmap.tbt.util.AppInfo.getStartX());
        etaTmc.setStartY(com.sfmap.tbt.util.AppInfo.getStartY());
        etaTmc.setTaskId(com.sfmap.tbt.util.AppInfo.getTaskId());
        etaTmc.setReqTime(String.valueOf(System.currentTimeMillis()));
//        etaTmc.setAppPackageName(AppInfo.getPackageName());
//        etaTmc.setAppCerSha1(AppInfo.getSHA1());
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        etaTmc.setPlanDate(sdf.format(dt));

        String soapContentParam = new Gson().toJson(etaTmc);

        String packageName = AppInfo.getPackageName();
        String Sha1 = AppInfo.getSHA1();
        String ak = AppInfo.getAppApiKey();

        LogUtil.d(TAG,"请求参数requestTmcPost："+soapContentParam);
        ak =  "1aca21cfd4204548bad2fd32dca24b8b";
        String param = null;
        try {
//            param = new DesUtil().encrypt(soapContentParam);

            byte[] compress = GzipUtils.compress(soapContentParam);
            byte[] encrypt= new DesUtil().encryptByte(compress);
            param = new DesUtil().bytes2hex(encrypt);

        } catch (IllegalBlockSizeException e1) {
            e1.printStackTrace();
        } catch (BadPaddingException e1) {
            e1.printStackTrace();
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String content = "ak=" + ak + "&param=" + param;
        LogUtil.d(TAG,"tmc url:"+paramString+" param:"+content);
        try {
            return doPost(paramString, paramMap1, content.getBytes());
        } catch (OperExceptionDecode operExceptionDecode) {
            operExceptionDecode.printStackTrace();
        }
        return null;
    }

    public ResponseEntity requestETAYawPost(String paramString, Map<String, String> paramMap1, byte[] postValue){
        String soapContent = com.sfmap.tbt.util.Utils.byteArrayToStr(postValue);
        LogUtil.d(TAG,"requestETAYawPost :"+soapContent);
        Map<String,String> urlRequest = URLRequest(soapContent);

        YawArgs yawArgs = new YawArgs();

        yawArgs.setNaviId(com.sfmap.tbt.util.AppInfo.getNaviId());
        yawArgs.setRouteId(com.sfmap.tbt.util.AppInfo.getRouteId());
        yawArgs.setTaskId(com.sfmap.tbt.util.AppInfo.getTaskId());
        yawArgs.setVehicle(com.sfmap.tbt.util.AppInfo.getCarPlate());

        yawArgs.setDestDeptCode(com.sfmap.tbt.util.AppInfo.getDestDeptCode());
        yawArgs.setSrcDeptCode(com.sfmap.tbt.util.AppInfo.getSrcDeptCode());
        yawArgs.setY2(String.valueOf(com.sfmap.tbt.util.AppInfo.getEndY()));
        yawArgs.setX2(String.valueOf(com.sfmap.tbt.util.AppInfo.getEndX()));
        yawArgs.setCompress("1");
        yawArgs.setCc("1");
        yawArgs.setReqTime(String.valueOf(System.currentTimeMillis()));
        yawArgs.setVehicleType("1");
        yawArgs.setDriverId(com.sfmap.tbt.util.AppInfo.getUserId());
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        yawArgs.setPlanDate(sdf.format(dt));
        String velDir = urlRequest.get("VehicleDir");
        String appVer = urlRequest.get("appVer");
        yawArgs.setAppVer(appVer);
        yawArgs.setVehicleDir(velDir);
//        yawArgs.setSwids(com.sfmap.tbt.util.AppInfo.getSwids());
//        if(!velDir.equals("-1")){
//            yawArgs.setVehicleDir("0");
//        }
        yawArgs.setTracks(com.sfmap.tbt.util.AppInfo.getTracks());

        Map<String,String> params = URLRequest(soapContent);
        String swids = params.get("sw_id");
        yawArgs.setSwids(swids);

        String soapContentParam = new Gson().toJson(yawArgs);

        String packageName = AppInfo.getPackageName();
        String Sha1 = AppInfo.getSHA1();
        String ak = AppInfo.getAppApiKey();

        LogUtil.d(TAG,"请求参数requestETAYawPost："+soapContentParam);
        ak =  "1aca21cfd4204548bad2fd32dca24b8b";
        String param = null;
        try {
            param = new DesUtil().encrypt(soapContentParam);
//            param = "25357fdb1ec7b61d025478c0d75bb94e261b7597bb1291f3b698f122d8d345d33e97de1e2b4301319460c92d04c7445e0d6f42c301d92b9f5f754214c8bb6f80554c30487b38c708e488625e11f9c211552706da747a3a69379e24bd97076c53552706da747a3a698ad456f00799c97a881f726d9e75547bfac0f7dc154be40e13c2d7f7ed9c3363fe5ff4918975bd3fb73c00c57184470f73643d981af3a5b72e38adbf3772a565f330d9b6af087a0aaa2c79b0ad1f635af5a64cf36a0084144e29ac7b88340983433b8d30fed62870ae51eaeb47ec20cb48d2732c817c91d10b6ad69d189eb425f17ed297b8002cff4766d97ed8132fbaae346a3eb7665dbf06c616c77e03f0f853f35ef03e6feb5a93c5d2e2571554c182029979fbe285e7b6242ff6b8f3197c";

        } catch (IllegalBlockSizeException e1) {
            e1.printStackTrace();
        } catch (BadPaddingException e1) {
            e1.printStackTrace();
        }
        String content = "param=" + param + "&ak=" + ak;
        LogUtil.d(TAG,"tmc url:"+paramString+" param:"+content);
        try {
            return doPost(paramString, paramMap1, content.getBytes());
        } catch (OperExceptionDecode operExceptionDecode) {
            operExceptionDecode.printStackTrace();
        }
        return null;
    }

    public ResponseEntity requestTmcPost(String paramString, Map<String, String> paramMap1, byte[] postValue){
        XmlBean xmlBean = new XmlBean();
        String soapContent = com.sfmap.tbt.util.Utils.byteArrayToStr(postValue);
        soapContent = soapContent.replaceAll("\"strategy\": \"9\"", "\"strategy\": \"5\"");

//        LogUtil.d(TAG,"请求参数requestTmcPost1："+soapContent);
//        soapContent += ("&opt="+rh);
        String log = soapContent.replaceAll("\r|\n", "").trim();
        String log1 = log.replaceAll("\\s*", "");

        EventBus.getDefault().postSticky("#RQTYPE,"+2+"\n" +"#RQURL,"+paramString+"\n" + "#RQPARAM,"+log1+"\n");
//        PARAM_LOG = soapContent;
        xmlBean.setSoapContent(soapContent);
//        xmlBean.setRouteType("1");

        String packageName = AppInfo.getPackageName();
        String Sha1 = AppInfo.getSHA1();
        String ak = AppInfo.getAppApiKey();

//        String packageName = "com.sfmap.map.internal";
//        String Sha1 =  "9D:BC:99:5F:6D:39:20:7A:91:54:BA:2C:9E:A2:50:1B:F6:2E:CE:72";
//        String ak =  "1aca21cfd4204548bad2fd32dca24b8b";
//        String ak =  "532b8a9fa6164ead9c56662208e6422b";

        LogUtil.d(TAG,"请求参数requestTmcPost："+soapContent);
        String param = null;
        try {
            param = new DesUtil().encrypt(soapContent);
//            param = "b7a7971354a6e7f4286982d69743dea2e3802071a949e91fdbe317df39bfdbba4f87b619284dfd00dad100515351bb20db19f681f10170b6f201c710b08a903ef96819203aeff91a43bed216eb46042f24cd6ab28795f33179880d968966510dcc5b60bbee31d949097264ad247c0132aefb832efe1b4543054f1504620743188b614cbee8eb92aa8cd63b504c916bf0da938d0fbc2ccaf126169de8030254b266a6c215d538f125ec1aa8a521ffdb14f73725f43af30cb2f64b6f0926b6fe1185376df37c9be0f46362104871d4c6eb9951a5d081a797804a24bfe7909eaa7ef81a7c3493125e659d638b4c43ea5f8d683d773171eec4315ecc9cc9f0c60ace39312a9a313ddbc671b4f2dc7867bdeb99f4ae29cb4bef116362104871d4c6ebf6c74168895616e339312a9a313ddbc671b4f2dc7867bdeb9124a254d0e531326362104871d4c6eb0ddec3472327ffee4a24bfe7909eaa7ef81a7c3493125e659b93cce1ee699cbd400f8f8fe1a3f79e6ae3ef82db5991175f419259fb753a3bc19e0d06e40eec642997a9e564c7b985fe9357cd88f023b4b1bcc11130d3e4ca256dc8a5b841f214286e24dbbc7cf1fb88be37c847e84f33dcfe9aca5b7fc5ada1346d7ccbf25cd837e1803016df9839e58b5e3cc2b94989afd63fd4122309496884cdfded05c83ffb3fb8e0888fc0c2f3f6e75e54ff3b92aca7336ead61999827e77d7c43e7d7d5ab0c2fcda0947b73e08ba872cc1373ac47cd637652b43a35adc650e5099bf499d8fda49d447e6342b2650e2378ee5bc04d925c8e69b325712210a0d0ee9b412431c6c2ee2f1dd52d7b053cb8094100b2f2700d0eb060657ce925b38608d2655fb3bcc7587a7c848bd8a58b49d823bd3315a0a943dfcdc8cdf2981df413fb5fd1a88a5ace570e87888d50dd8157735228591f980c0e74db779556ce712c053ba9a63101f9ad53fcfcbfae3dcb64af4e8d3a0caf20d4fdb0bc1603126fec5144f3c9df73ba7dcdfd6d7eae1a932af6aef09d69343db11359f4099c05d615c8c14b5d397771ae692035e2c81f1ccfb3cb6cc9d4bc9dc3c00f4ca11a9063f0483c15dbf6e943c1fd747e5d397771ae692035e2c81f1ccfb3cb6c3fbf427eef0e1442a11a9063f0483c1547acac467eb006e25d397771ae692035e2c81f1ccfb3cb6c335f5f346034a2cda11a9063f0483c15713c12492c006b835d397771ae692035e2c81f1ccfb3cb6c51cb6befa45be875ede9c49d90fbcb23ca70313f8dfe06f073ed7b58fe1e5958a5b88d0f4f1f681b31795032a78a8fca75cc0c226167612298dadc1abddd2ea0ab2664f84a05369a6b35d7aefefca0514789ca03e1665d2e9e4daf2550c6f566cc1600ad16ccd3642b51cb11bbf86553d20fa0f87ae02fb457e3640ba1295ca6fcfad6d44b7ec1c08c82a53b209cd7877192a8016401f7206cd4d88306db863f0abf657bc57fc716ede9c49d90fbcb23b7ff7b8b49b185117192a8016401f720e0e2dfe4e2d3b70db595c479d9d59d508eb95eef106856d2a6228d84ba5c9a4c86b72eb37c0ac21576d992d5ef5f5a3c437255edaa190be111da64082cd25c89b492c3122e05389fb4935a1209d27a707f70326edbbc293490a2fa28092aae2eb7ccdc2de901c2acfb47b15268b7f54d408c75e553ac57fbddd2b08d56916e5ff8d91d7dc255288b4c6e389be4b8fc2dfcdf9af1d3f492d3153baf751e5fe2555cbc45360cbdfc294f268732ca82a55d08cebb20ee64d74ce1046297408981ce182cdb91228d7c8771eac419dfd5a9e29cebc6f24f2dfcdb3c5954f7db7267325be0dec923a4d3bbadf02ca32eeba5cf153c3b9d6255d50558f1058314b556f5f3ad14a2066d8658976824ddbb30614488be37c847e84f33749368b76065b0dafdb76a721a8f0c388e00e30bfd4fdfd87bcb61d640ac7d2cb5744f1e78967055516d15ca2587d225d8a6e549921d81e87d81ffdfde18fc9f7639361abe7623ad69becd0e5cd19e8a3b30c1d3be4829650e5760dcd9eae431f001100d849bdf3402da3ee50a30756a8f5b111ccd38e824ebf4f946d4f3bd5981e3e017956c46396e5fdcf917aab2a3bf8552d35b2e949d2da25452871682aa6c37eb2aa2512785e79b9258068ac12fa96c471bea5d44af3d87afffc06152a4abadecb53e22f9d32d6dcd25e835730f8a6efb749a9730758384db369751bb6d32ebe675f9a7390d5637c9862ba02fe0358b1674108505c0ac237836fd6a57995a4ae11bddd83542b9c2b2ea93ae211082bf66ad8733b40ab7c48557fae5498a77f7301aa452a9024855fb9d26819e48827b32cf3fedc01a845a2d6048daa5717bcc4a705f71a13b7113d95cdc19b1b9659a4e04d9d133409c8c4c6a253456d3";
//            param = new DesUtil().encrypt(xmlBean.toString());
        } catch (IllegalBlockSizeException e1) {
            e1.printStackTrace();
        } catch (BadPaddingException e1) {
            e1.printStackTrace();
        }
        String content = "param=" + param + "&ak=" + ak;
        LogUtil.d(TAG,"tmc url:"+paramString+" param:"+content);
        try {
            return doPost(paramString, paramMap1, content.getBytes());
        } catch (OperExceptionDecode operExceptionDecode) {
            operExceptionDecode.printStackTrace();
        }
        return null;
    }


    public ResponseEntity requestETANaviPost(String paramString, Map<String, String> paramMap1, byte[] postValue) {
        String soapContent = com.sfmap.tbt.util.Utils.byteArrayToStr(postValue);
        String swid = "";
        String y2 = "";
        String y1 = "";
        String x2 = "";
        String x1 = "";
        String destDeptCode = "";
        String srcDeptCode = "";
        String vehicle = "";
        String taskId = "";
        String naviId = "";
        String appVer = "";

        String weight = "";
        String strategy = "";
        String height = "";
        String axleWeight = "";
        String axleNumber = "";
        String mload = "";
        String vehicleDir = "";
        String passport = "";

        try {
            Map<String,String> param = URLRequest(soapContent);

            x2 = param.get("x2");
            y2 = param.get("y2");

            x1 = String.valueOf(com.sfmap.tbt.util.AppInfo.getStartX());
            y1 = String.valueOf(com.sfmap.tbt.util.AppInfo.getStartY());

            destDeptCode = com.sfmap.tbt.util.AppInfo.getDestDeptCode();
            srcDeptCode = com.sfmap.tbt.util.AppInfo.getSrcDeptCode();
            vehicle = param.get("Vehicle");
            taskId = com.sfmap.tbt.util.AppInfo.getTaskId();
            naviId = com.sfmap.tbt.util.AppInfo.getNaviId();
            appVer = param.get("appVer");

            weight = param.get("Weight");
            strategy = param.get("Strategy");
            height = param.get("Height");
            axleWeight = param.get("AxleWeight");
            axleNumber = param.get("AxleNumber");
            mload = param.get("Mload");
            vehicleDir = param.get("VehicleDir");
//            vehicleDir = "100";
            passport = param.get("passport");
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        ETARoute etaRoute = new ETARoute();
//        etaRoute.setAk(ak1);
        etaRoute.setX2(x2);
        etaRoute.setY2(y2);

        etaRoute.setX1(x1);
        etaRoute.setY1(y1);
        etaRoute.setDestDeptCode(destDeptCode);
        etaRoute.setVehicle(com.sfmap.tbt.util.AppInfo.getCarPlate());

        etaRoute.setTaskId(taskId);
        etaRoute.setSrcDeptCode(srcDeptCode);
        etaRoute.setAppVer(appVer);
        etaRoute.setNaviId(naviId);
//
        etaRoute.setEmitStand("5");
        etaRoute.setCompress("1");
//        etaRoute.setFrequency();
//        etaRoute.setLength();
        etaRoute.setMload(mload);
//        etaRoute.setFencedist();
        etaRoute.setPassport(passport);
//        etaRoute.setEtype();
        etaRoute.setPlateColor("1");
//        etaRoute.setReRoute();
//        etaRoute.setRticDur();
        etaRoute.setEnergy("1");
//        etaRoute.setStype();
        etaRoute.setHeight(height);
//        etaRoute.setTest();
//        etaRoute.setTolls();
        etaRoute.setDriverId(com.sfmap.tbt.util.AppInfo.getUserId());
//        etaRoute.setVehicleDir(vehicleDir);
        etaRoute.setVehicleDir("-1");
//        if(!vehicleDir.equals("-1")){
//            etaRoute.setVehicleDir(vehicleDir);
//        }
        etaRoute.setVehicleType("1");
        etaRoute.setWeight(weight);
        etaRoute.setWidth("2");
        etaRoute.setCc("1");
        etaRoute.setAxleWeight(axleWeight);
        etaRoute.setAxleNumber(axleNumber);
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        etaRoute.setPlanDate(sdf.format(dt));
        etaRoute.setReqTime(String.valueOf(System.currentTimeMillis()));

        LogUtil.d(TAG," soapContent:"+new Gson().toJson(etaRoute));

        PARAM_LOG = soapContent;

        String ak =  "1aca21cfd4204548bad2fd32dca24b8b";
//        String ak =  "89946b389666462a820d698ac67a4300";

        String packageName = AppInfo.getPackageName();
        String Sha1 = AppInfo.getSHA1();
//        String ak = AppInfo.getAppApiKey();

        String param = null;
        try {
            param = new DesUtil().encrypt(new Gson().toJson(etaRoute));
        } catch (IllegalBlockSizeException e1) {
            e1.printStackTrace();
        } catch (BadPaddingException e1) {
            e1.printStackTrace();
        }
        String content = "";

//        param = "25357fdb1ec7b61d025478c0d75bb94e261b7597bb1291f3b698f122d8d345d33e97de1e2b4301319460c92d04c7445edf3a2df52fb24db0fb4e93a2d56857988c08927b38f6aebe8294174f858e96f78b293009a5d156dd69b6ef027eee4f3f79b62ccb1371663020bf431fa04d771347a68a765348d8f6241c85768027515c36133b922742a3591127700f7f893f8a4c86d7501a8402f196f7cd2ccb54f5e9315c9fbff4a7f8d2a9b195eb6393227342f9ed3e592265af72e310798888a171d45c3321f4210fcb35ab03338cc0630e053a6fc3dabbb718290cfe19a7cc4435650890bd516b6aef8c88145ed3ab25132e00753ceb557dd83ba04b3e86776e9a3a4762d89ff1f8bb81f92603e0aca897bde46dfbd49ce9b9acb20ef9026bf146269aaa0a5acbf82c8fb0b64afb28a012f6dd3bdc00ade90f20bf431fa04d77134f2df966189c215f56de18da0e4d914c2b13b67d6fc9d661b37d66e7cdef0a32073c4d2cd230b7dad4c37b8fa9ed90ba07d91b624544c171c06434bd2ce9dd28238212188404e0f2ac18deed3d10d8bf72be5e625a055dd279a554fab0c1a93c42f9ed3e592265afc8f98d83b2124402eb027cc9861d9f58020ed13f4b3b8782b8b0b9d734ffee2d409d586fd2e79ae7b60e9015bd199e7f12ba7612e345b69f541fc2ae6477534fd643219b4836d94da983e335dba8e6b25fd0583e6290a677";
        content = "param=" + param + "&ak=" + ak;
//        content = "param=" + param + "&ak=" + ak;
//        }else {
//            content = "param=" + "25357fdb1ec7b61d025478c0d75bb94e261b7597bb1291f3b698f122d8d345d33e97de1e2b4301319460c92d04c7445edf3a2df52fb24db0fb4e93a2d56857988c08927b38f6aebe8294174f858e96f78b293009a5d156dd69b6ef027eee4f3f79b62ccb1371663020bf431fa04d771347a68a765348d8f6241c85768027515c36133b922742a3591127700f7f893f8a4c86d7501a8402f196f7cd2ccb54f5e9315c9fbff4a7f8d2a9b195eb6393227342f9ed3e592265af72e310798888a171d45c3321f4210fcb35ab03338cc0630e053a6fc3dabbb718290cfe19a7cc4435650890bd516b6aef8c88145ed3ab25132e00753ceb557dd83ba04b3e86776e9a3a4762d89ff1f8bb81f92603e0aca897bde46dfbd49ce9b9acb20ef9026bf146269aaa0a5acbf82c8fb0b64afb28a012f6dd3bdc00ade90f20bf431fa04d77134f2df966189c215f56de18da0e4d914c2b13b67d6fc9d661b37d66e7cdef0a32073c4d2cd230b7dad4c37b8fa9ed90ba07d91b624544c171c06434bd2ce9dd28238212188404e0f2ac18deed3d10d8bf72be5e625a055dd279a554fab0c1a93c42f9ed3e592265afc8f98d83b2124402eb027cc9861d9f58020ed13f4b3b8782b8b0b9d734ffee2d409d586fd2e79ae7b60e9015bd199e7f12ba7612e345b69f541fc2ae6477534fd643219b4836d94da983e335dba8e6b25fd0583e6290a677" + "&ak=" + ak;
//        }

        //{"ak":"f4703a6f20a84d11a341d3e5d653f342","appVer":"1","axleNumber":"4","axleWeight":"2.3","cc":"1","destDeptCode":"755WF",
        // "driverId":"100","emitStand":"5","energy":"1","height":"2.1","length":"3.8","mload":"2.8","naviId":"123",
        // "passport":"110000%7C310000%7C610100","planDate":"202008081225","plateColor":"1","srcDeptCode":"755W",
        // "taskId":"755Y00001","vehicle":"粤B0001","vehicleDir":"220","vehicleType":"1","weight":"2.1","width":"3.1",
        // "x1":"113.92865","x2":"113.887474","y1":"22.532501","y2":"22.572334"}

        PARAM_TIME = getTimeStampDetail();
        LogUtil.d(TAG,"url:"+paramString+" param:"+content);
        try {
            return doPost(paramString, paramMap1, content.getBytes());
        } catch (OperExceptionDecode operExceptionDecode) {
            operExceptionDecode.printStackTrace();
        }
        return null;
    }



    public static String PARAM_LOG;
    public static String PARAM_TIME;
    public ResponseEntity requestNaviPost(String paramString, Map<String, String> paramMap1, byte[] postValue) {
        XmlBean xmlBean = new XmlBean();
        String soapContent = com.sfmap.tbt.util.Utils.byteArrayToStr(postValue);
        LogUtil.d(TAG," soapContent:"+soapContent);
        soapContent += ("&opt="+"sf");
        if("jy0".equals(rh)){
//            paramString = "http://10.202.43.231:7000/navi2";
            paramString = "https://gis.sf-express.com/mms/navi";
            soapContent = soapContent.replaceAll("strategy=5", "strategy=0"); //经验路线策略支持8 频次优先
//            soapContent = soapContent.replaceAll("pathCount=3", "pathCount=1"); //经验路线策略支持8 频次优先
//            soapContent += ("&merge="+5); //经验路线固定参数
//            soapContent += ("&fixedRoute="+2);//经验路线固定参数
        }else {
            soapContent = soapContent.replaceAll("strategy=5", "strategy=0");
        }
        android.util.Log.i(TAG,"soapContent1："+soapContent);
        EventBus.getDefault().postSticky("#RQTYPE,"+1+"\n" +"#RQURL,"+paramString+"\n" + "#RQPARAM,"+soapContent+"\n");
        PARAM_LOG = soapContent;
        xmlBean.setSoapContent(soapContent);
        xmlBean.setRouteType("1");
        xmlBean.setOpt("sf");
//        xmlBean.setOpt(rh);
//        String packageName = "com.sfmap.map.internal";
//        String Sha1 =  "9D:BC:99:5F:6D:39:20:7A:91:54:BA:2C:9E:A2:50:1B:F6:2E:CE:72";
        String ak =  "1aca21cfd4204548bad2fd32dca24b8b";

        String packageName = AppInfo.getPackageName();
        String Sha1 = AppInfo.getSHA1();
//        String ak = AppInfo.getAppApiKey();

        xmlBean.setAppPackageName(packageName);
        xmlBean.setAppCerSha1(Sha1);

        String param = null;
        try {
            param = new DesUtil().encrypt(xmlBean.toString());
        } catch (IllegalBlockSizeException e1) {
            e1.printStackTrace();
        } catch (BadPaddingException e1) {
            e1.printStackTrace();
        }
        String content = "";
        content = "param=" + param + "&ak=" + ak;
        EventBus.getDefault().postSticky("#REQUERST,"+paramString+"?"+content+"\n");
        PARAM_TIME = getTimeStampDetail();
        LogUtil.d(TAG,"url:"+paramString+" param:"+content);
        try {
            return doPost(paramString, paramMap1, content.getBytes());
        } catch (OperExceptionDecode operExceptionDecode) {
            operExceptionDecode.printStackTrace();
        }
        return null;
    }

    /*
    post网络访问
     */
    ResponseEntity doPost(String paramString, Map<String, String> paramMap,
                          byte[] paramArrayOfByte) throws OperExceptionDecode {
        try {
            URL localURL = new URL(paramString);
            HttpURLConnection localHttpURLConnection = getHttpURLConnection(localURL);
            addHeadToHttp(paramMap, localHttpURLConnection);
            localHttpURLConnection.setRequestMethod("POST");
            localHttpURLConnection.setDoInput(true);
            localHttpURLConnection.setDoOutput(true);
            localHttpURLConnection.setUseCaches(false);

            if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 0)) {
                DataOutputStream localDataOutputStream = new DataOutputStream(
                        localHttpURLConnection.getOutputStream());
                localDataOutputStream.write(paramArrayOfByte);
                localDataOutputStream.close();
            }
            localHttpURLConnection.connect();
            return doConnection(localHttpURLConnection);
        } catch (ConnectException localConnectException) {
            localConnectException.printStackTrace();
            EventBus.getDefault().postSticky("#RQERROR,http连接失败 - ConnectionException"+"\n");
            throw new OperExceptionDecode("http连接失败 - ConnectionException");
        } catch (MalformedURLException localMalformedURLException) {
            localMalformedURLException.printStackTrace();
            EventBus.getDefault().postSticky("#RQERROR,url异常 - MalformedURLException"+"\n");
            throw new OperExceptionDecode("url异常 - MalformedURLException");
        } catch (UnknownHostException localUnknownHostException) {
            localUnknownHostException.printStackTrace();
            EventBus.getDefault().postSticky("#RQERROR,未知主机 - UnKnowHostException"+"\n");
            throw new OperExceptionDecode("未知主机 - UnKnowHostException");
        } catch (SocketException localSocketException) {
            localSocketException.printStackTrace();
            EventBus.getDefault().postSticky("#RQERROR,socket 连接异常 - SocketException"+"\n");
            throw new OperExceptionDecode("socket 连接异常 - SocketException");
        } catch (SocketTimeoutException localSocketTimeoutException) {
            localSocketTimeoutException.printStackTrace();
            EventBus.getDefault().postSticky("#RQERROR,socket 连接超时 - SocketTimeoutException"+"\n");
            throw new OperExceptionDecode(
                    "socket 连接超时 - SocketTimeoutException");
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
            EventBus.getDefault().postSticky("#RQERROR,IO 操作异常 - IOException"+"\n");
            throw new OperExceptionDecode("IO 操作异常 - IOException");
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
            EventBus.getDefault().postSticky("#RQERROR,未知的错误"+"\n");
            BasicLogHandler.a(localThrowable, "HttpUrlUtil", "makePostReqeust");
            throw new OperExceptionDecode("未知的错误");
        }
    }

    /*
    添加连接头信息
     */
    private void addHeadToHttp(Map<String, String> paramMap, HttpURLConnection paramHttpURLConnection) {
        if (paramMap != null) {
            for (String str : paramMap.keySet()) {
                android.util.Log.i("url", "key:" + str + ",value:" + (String) paramMap.get(str));
                paramHttpURLConnection.addRequestProperty(str, (String) paramMap.get(str));
            }
        }
        paramHttpURLConnection.setConnectTimeout(this.connectTimeout);
        paramHttpURLConnection.setReadTimeout(this.readTimeout);
    }

    private HttpURLConnection getHttpURLConnection(URL paramURL) throws IOException {
        Object localObject1;
        Object localObject2;
        DeviceInfoDecode.setTraficTag();

        if (this.g != null)
            localObject2 = paramURL.openConnection(this.g);
        else
            localObject2 = (HttpURLConnection) paramURL.openConnection();

        if (this.e) {
            localObject1 = (HttpsURLConnection) localObject2;

            ((HttpsURLConnection) localObject1).setSSLSocketFactory(this.f
                    .getSocketFactory());

            ((HttpsURLConnection) localObject1).setHostnameVerifier(this.a);
        } else {
            localObject1 = (HttpURLConnection) localObject2;
        }

        if ((Build.VERSION.SDK != null) && (Build.VERSION.SDK_INT > 13)) {
            ((HttpURLConnection) localObject1).setRequestProperty("Connection",
                    "close");
        }

        return ((HttpURLConnection) (HttpURLConnection) localObject1);
    }

    /*
    执行连接
     */
    private ResponseEntity doConnection(HttpURLConnection paramHttpURLConnection) throws OperExceptionDecode, IOException {
        ByteArrayOutputStream localByteArrayOutputStream = null;
        InputStream localInputStream = null;
        Object localObject1 = null;
        PushbackInputStream localPushbackInputStream = null;
        try {
            ResponseEntity localba2;
            Map localMap = paramHttpURLConnection.getHeaderFields();

            int k = paramHttpURLConnection.getResponseCode();
            if (k != 200) {
                LogUtil.d(TAG,"网络异常状态码："+k);
                throw new OperExceptionDecode("网络异常原因："
                        + paramHttpURLConnection.getResponseMessage()
                        + " 网络异常状态码：" + k);

            }

            localByteArrayOutputStream = new ByteArrayOutputStream();
            localInputStream = paramHttpURLConnection.getInputStream();
            localPushbackInputStream = new PushbackInputStream(
                    localInputStream, 2);

            byte[] arrayOfByte1 = new byte[2];
            localPushbackInputStream.read(arrayOfByte1);
            localPushbackInputStream.unread(arrayOfByte1);

            if ((arrayOfByte1[0] == 31) && (arrayOfByte1[1] == -117))
                localObject1 = new GZIPInputStream(localPushbackInputStream);
            else {
                localObject1 = localPushbackInputStream;
            }

            int l = 0;

            byte[] arrayOfByte2 = new byte[1024];
            while ((l = ((InputStream) localObject1).read(arrayOfByte2)) != -1)
                localByteArrayOutputStream.write(arrayOfByte2, 0, l);

            if (netCompleteListener != null)
                netCompleteListener.complete();

            ResponseEntity localba1 = new ResponseEntity();
            localba1.a = localByteArrayOutputStream.toByteArray();
            LogUtil.d(TAG,"接收到请求数据"+localba1.a.length);
            localba1.b = localMap;
            return localba1;
        } catch (IOException localIOException1) {
        } finally {
            if (localByteArrayOutputStream != null)
                try {
                    localByteArrayOutputStream.close();
                } catch (IOException localIOException2) {
                    EventBus.getDefault().postSticky("#RQERROR,HttpUrlUtil1"+"\n");
                    BasicLogHandler.a(localIOException2, "HttpUrlUtil", "parseResult");

                    localIOException2.printStackTrace();
                }

            if (localInputStream != null)
                try {
                    localInputStream.close();
                } catch (Exception localException1) {
                    EventBus.getDefault().postSticky("#RQERROR,HttpUrlUtil2"+"\n");
                    BasicLogHandler.a(localException1, "HttpUrlUtil", "parseResult");

                    localException1.printStackTrace();
                }

            if (localPushbackInputStream != null)
                try {
                    localPushbackInputStream.close();
                } catch (Exception localException2) {
                    EventBus.getDefault().postSticky("#RQERROR,HttpUrlUtil3"+"\n");
                    BasicLogHandler.a(localException2, "HttpUrlUtil", "parseResult");

                    localException2.printStackTrace();
                }

            if (localObject1 != null)
                try {
                    ((InputStream) localObject1).close();
                } catch (Exception localException3) {
                    EventBus.getDefault().postSticky("#RQERROR,HttpUrlUtil4"+"\n");
                    BasicLogHandler.a(localException3, "HttpUrlUtil", "parseResult");

                    localException3.printStackTrace();
                }

            if (paramHttpURLConnection != null)
                try {
                    paramHttpURLConnection.disconnect();
                } catch (Throwable localThrowable) {
                    EventBus.getDefault().postSticky("#RQERROR,HttpUrlUtil5"+"\n");
                    BasicLogHandler.a(localThrowable, "HttpUrlUtil", "parseResult");

                    localThrowable.printStackTrace();
                }
        }

        return null;//
    }

    /*
    拼接url参数成字符串
     */
    private String urlPramsToString(Map<String, String> paramMap) {
        if (paramMap != null) {
            StringBuilder localStringBuilder = new StringBuilder();
            for (Map.Entry localEntry : paramMap.entrySet()) {
                if (localStringBuilder.length() > 0)
                    localStringBuilder.append("&");

                localStringBuilder.append(URLEncoder.encode((String) localEntry
                        .getKey()));
                localStringBuilder.append("=");
                localStringBuilder.append(URLEncoder.encode((String) localEntry
                        .getValue()));
            }

            return localStringBuilder.toString();
        }

        return null;
    }

    public String getTimeStampDetail() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(dt);
    }

    /**
     * 解析出url参数中的键值队
     * 如 "www.xx.int?Action=int&id12345"，解析出Action:int,id:12345存入map中
     * @param URL  url地址
     * @return  url请求参数部分
     */
    public Map<String, String> URLRequest(String URL)
    {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit=null;

        String strUrlParam=URL;
        if(strUrlParam==null)
        {
            return mapRequest;
        }
        //每个键值为一组 www.2cto.com
        arrSplit=strUrlParam.split("[&]");
        for(String strSplit:arrSplit)
        {
            String[] arrSplitEqual=null;
            arrSplitEqual= strSplit.split("[=]");

            //解析出键值
            if(arrSplitEqual.length>1)
            {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            }
            else
            {
                if(arrSplitEqual[0]!="")
                {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    public static final String GZIP_ENCODE_UTF_8 = "UTF-8";


    public static byte[] compress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static byte[] compress(String str) throws IOException {
        return compress(str, GZIP_ENCODE_UTF_8);
    }
}