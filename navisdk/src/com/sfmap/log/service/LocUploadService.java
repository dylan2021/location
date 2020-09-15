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
import com.sfmap.library.util.DateTimeUtil;
import com.sfmap.log.model.LocInfo;
import com.sfmap.log.model.LocParam;
import com.sfmap.log.model.LogParam;
import com.sfmap.api.navi.model.ResultBean;
import com.sfmap.log.util.RxTimer;
import com.sfmap.tbt.util.AppInfo;
import com.sfmap.util.CommUtil;
import com.sfmap.util.IOUtils;

import org.apache.http.HttpRequest;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.sfmap.util.CommUtil.isConnect;

public class LocUploadService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    //    private HashMap<Integer,LocParam> listHashMap = new HashMap<>();//保存前五分钟的定位坐标点
    private ArrayList<LocParam> locParams = new ArrayList<>();
    private ArrayList<LogParam> logParams = new ArrayList<>();
    private ArrayList<Location> lastOneLoc = new ArrayList<>();//保存最近一分钟的定位坐标点
    //    private ArrayList<LogParam> lastOneLog = new ArrayList<>();//保存最近一分钟的定位坐标点
    private Location lastLoc;//保存最近的定位坐标点
    private int mHttpTimeOut = 30000;
    private boolean mGZip = true;
    private int satellite = 0;//卫星数量
    public String gpsLogPath = "";
    public String naviLogPath = "";
    private LocParam lastLocParam;
    private LogParam lastLogParam;

    @Override
    public void onCreate() {
        super.onCreate();
        gpsLogPath = Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/01SfNaviSdk/" + "gps_log.txt";
        naviLogPath = Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/01SfNaviSdk/" + "navi_log.txt";
        CheckTime();
        startLocalLocUpload();
        startLocalLogUpload();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rxTimer != null) {
            rxTimer.cancel();
        }
    }

    private void uploadCurOneLoc() {

    }

    /**
     *
     */
    RxTimer rxTimer;

    private void CheckTime() {
        if (rxTimer == null) {
            rxTimer = new RxTimer();
            rxTimer.interval(60 * 1000, new RxTimer.RxAction() {
                @Override
                public void action(long number) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadLocOneMin(getParam());
                        }
                    }).start();
                }
            });
        }
    }

    private LocParam getParam() {
//        lastOneLoc.clear();
//        String testText = "{\"ak\":\"de37d1ce653e44dd92d97d3dba614d6c\",\"carNo\":\"E15915\",\"deviceId\":\"90F2CC314DD645D7830163CDF3748859\",\"locations\":[{\"tag\":\"1\",\"tm\":1598078344000,\"x\":22.528104999999996,\"y\":113.93485166666666,\"sp\":0.34692279,\"be\":\"54.01\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"1\",\"tm\":1598078345000,\"x\":22.52812333333333,\"y\":113.93484833333332,\"sp\":0.39942443,\"be\":\"29.19\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078346000,\"x\":22.52813666666667,\"y\":113.93485000000001,\"sp\":0.4488378,\"be\":\"26.53\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078348000,\"x\":22.52815333333333,\"y\":113.93485000000001,\"sp\":0.51884,\"be\":\"37.0\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"1\",\"tm\":1598078350000,\"x\":22.528166666666667,\"y\":113.93484333333332,\"sp\":0.53891414,\"be\":\"68.66\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078351000,\"x\":22.528158333333334,\"y\":113.93484833333332,\"sp\":0.5394289,\"be\":\"90.16\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078352000,\"x\":22.528148333333334,\"y\":113.93485166666666,\"sp\":0.55178225,\"be\":\"107.3\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078353000,\"x\":22.52813833333333,\"y\":113.93485666666666,\"sp\":0.5661944,\"be\":\"110.45\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078358000,\"x\":22.528149999999997,\"y\":113.93486499999999,\"sp\":0.0,\"be\":\"103.11\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078384000,\"x\":22.52813666666667,\"y\":113.93487333333333,\"sp\":0.14669584,\"be\":\"352.48\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078386000,\"x\":22.528125,\"y\":113.93489166666669,\"sp\":0.21721278,\"be\":\"22.84\",\"tp\":1,\"ac\":5,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078387000,\"x\":22.528118333333335,\"y\":113.93490500000001,\"sp\":0.18015277,\"be\":\"40.43\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078388000,\"x\":22.528151666666666,\"y\":113.93492833333333,\"sp\":0.2177275,\"be\":\"59.29\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078389000,\"x\":22.528223333333337,\"y\":113.934945,\"sp\":0.24809611,\"be\":\"50.56\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078417000,\"x\":22.528211666666667,\"y\":113.93495499999999,\"sp\":0.20383,\"be\":\"53.05\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078418000,\"x\":22.528203333333334,\"y\":113.93495,\"sp\":0.37162945,\"be\":\"52.37\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078419000,\"x\":22.528185,\"y\":113.93495499999999,\"sp\":0.4313372,\"be\":\"65.01\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"1\",\"tm\":1598078421000,\"x\":22.528211666666667,\"y\":113.93495999999999,\"sp\":0.5054572,\"be\":\"60.72\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078426000,\"x\":22.528178333333337,\"y\":113.93496333333333,\"sp\":0.39736557,\"be\":\"96.96\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078427000,\"x\":22.527936666666665,\"y\":113.93490166666668,\"sp\":0.51884,\"be\":\"92.77\",\"tp\":1,\"ac\":6,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078429000,\"x\":22.52794833333333,\"y\":113.93492833333333,\"sp\":0.63516724,\"be\":\"69.6\",\"tp\":1,\"ac\":5,\"sl\":-1,\"cd\":1},{\"tag\":\"1\",\"tm\":1598078430000,\"x\":22.527935000000003,\"y\":113.93492999999998,\"sp\":0.657815,\"be\":\"120.03\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078431000,\"x\":22.528045,\"y\":113.93494666666666,\"sp\":0.46170583,\"be\":\"86.31\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078432000,\"x\":22.528056666666664,\"y\":113.93494833333334,\"sp\":0.5811214,\"be\":\"89.81\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078434000,\"x\":22.528065,\"y\":113.93495666666668,\"sp\":0.43957278,\"be\":\"86.76\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078436000,\"x\":22.528045,\"y\":113.93496833333332,\"sp\":0.60582805,\"be\":\"92.64\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078437000,\"x\":22.52802166666667,\"y\":113.93498333333334,\"sp\":0.71443444,\"be\":\"103.2\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078438000,\"x\":22.528005,\"y\":113.93498833333334,\"sp\":0.57494473,\"be\":\"112.59\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078440000,\"x\":22.527981666666665,\"y\":113.93500666666665,\"sp\":0.5878128,\"be\":\"122.39\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078441000,\"x\":22.527975,\"y\":113.93501833333333,\"sp\":0.60171026,\"be\":\"109.53\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078442000,\"x\":22.527963333333332,\"y\":113.93503000000001,\"sp\":0.602225,\"be\":\"110.16\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078443000,\"x\":22.527951666666663,\"y\":113.93504,\"sp\":0.6274464,\"be\":\"116.98\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078444000,\"x\":22.527936666666665,\"y\":113.93504999999999,\"sp\":0.6588445,\"be\":\"113.63\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078445000,\"x\":22.527921666666664,\"y\":113.93506166666666,\"sp\":0.68509525,\"be\":\"114.14\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078446000,\"x\":22.52791,\"y\":113.93507,\"sp\":0.69384557,\"be\":\"112.08\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078447000,\"x\":22.527896666666667,\"y\":113.93507666666667,\"sp\":0.68921304,\"be\":\"113.24\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078448000,\"x\":22.52788666666667,\"y\":113.93508166666668,\"sp\":0.6840658,\"be\":\"112.58\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078449000,\"x\":22.527873333333332,\"y\":113.93508833333333,\"sp\":0.670683,\"be\":\"104.63\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078450000,\"x\":22.527865,\"y\":113.93509999999999,\"sp\":0.6773744,\"be\":\"95.83\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078451000,\"x\":22.52785833333333,\"y\":113.93511333333332,\"sp\":0.69847804,\"be\":\"103.72\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078452000,\"x\":22.52785666666667,\"y\":113.93512333333335,\"sp\":0.71958166,\"be\":\"101.74\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078453000,\"x\":22.52786,\"y\":113.93513499999999,\"sp\":0.73553807,\"be\":\"103.63\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078454000,\"x\":22.527855,\"y\":113.93514333333333,\"sp\":0.72781724,\"be\":\"96.1\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078455000,\"x\":22.52786,\"y\":113.93515166666666,\"sp\":0.70414,\"be\":\"99.85\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078456000,\"x\":22.527866666666668,\"y\":113.93516666666667,\"sp\":0.7092872,\"be\":\"97.51\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078457000,\"x\":22.527888333333333,\"y\":113.93517833333333,\"sp\":0.70516944,\"be\":\"80.92\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078458000,\"x\":22.527878333333334,\"y\":113.93518333333334,\"sp\":0.70362526,\"be\":\"87.72\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078459000,\"x\":22.52787,\"y\":113.93519333333333,\"sp\":0.7139197,\"be\":\"101.05\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"1\",\"tm\":1598078460000,\"x\":22.527865,\"y\":113.93520333333332,\"sp\":0.70259583,\"be\":\"105.08\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078461000,\"x\":22.527875,\"y\":113.93521666666666,\"sp\":0.6923014,\"be\":\"104.36\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078462000,\"x\":22.527890000000003,\"y\":113.93523166666667,\"sp\":0.69127196,\"be\":\"99.17\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078464000,\"x\":22.527885,\"y\":113.93524333333335,\"sp\":0.68921304,\"be\":\"100.53\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078466000,\"x\":22.527896666666667,\"y\":113.93523666666667,\"sp\":0.6094311,\"be\":\"115.68\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078469000,\"x\":22.527906666666663,\"y\":113.935245,\"sp\":0.5502381,\"be\":\"73.12\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078472000,\"x\":22.527911666666668,\"y\":113.93525333333334,\"sp\":0.49053028,\"be\":\"80.27\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078477000,\"x\":22.527921666666664,\"y\":113.935255,\"sp\":0.5018542,\"be\":\"78.16\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078478000,\"x\":22.52793,\"y\":113.93525999999999,\"sp\":0.51884,\"be\":\"81.7\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078479000,\"x\":22.527939999999997,\"y\":113.93526333333332,\"sp\":0.54251724,\"be\":\"73.36\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078481000,\"x\":22.527953333333336,\"y\":113.93527,\"sp\":0.55487055,\"be\":\"74.85\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078482000,\"x\":22.527963333333332,\"y\":113.93527333333334,\"sp\":0.5538411,\"be\":\"63.1\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078485000,\"x\":22.527965000000002,\"y\":113.93528333333333,\"sp\":0.5981072,\"be\":\"80.84\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078488000,\"x\":22.52797333333333,\"y\":113.93529500000001,\"sp\":0.60171026,\"be\":\"72.66\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078490000,\"x\":22.52799333333333,\"y\":113.935305,\"sp\":0.58163613,\"be\":\"61.95\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078492000,\"x\":22.52798833333333,\"y\":113.93531666666668,\"sp\":0.5754594,\"be\":\"97.14\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078493000,\"x\":22.527995,\"y\":113.93532333333333,\"sp\":0.5610472,\"be\":\"87.23\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078495000,\"x\":22.52799,\"y\":113.93533333333335,\"sp\":0.5347964,\"be\":\"101.41\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078497000,\"x\":22.527986666666667,\"y\":113.93534999999999,\"sp\":0.5564147,\"be\":\"89.44\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078499000,\"x\":22.527983333333335,\"y\":113.93536833333334,\"sp\":0.58163613,\"be\":\"91.35\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078501000,\"x\":22.527984999999997,\"y\":113.93537999999998,\"sp\":0.6145783,\"be\":\"97.65\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078503000,\"x\":22.52800166666667,\"y\":113.93539166666666,\"sp\":0.6397997,\"be\":\"85.02\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078505000,\"x\":22.52801833333333,\"y\":113.93540333333334,\"sp\":0.65421194,\"be\":\"83.36\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078507000,\"x\":22.528016666666666,\"y\":113.93541833333332,\"sp\":0.6181814,\"be\":\"92.69\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078509000,\"x\":22.528041666666663,\"y\":113.93543666666667,\"sp\":0.60840166,\"be\":\"76.36\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078510000,\"x\":22.528043333333336,\"y\":113.93544666666666,\"sp\":0.6186961,\"be\":\"81.14\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078511000,\"x\":22.52804833333333,\"y\":113.935455,\"sp\":0.62693167,\"be\":\"86.15\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078513000,\"x\":22.52806333333333,\"y\":113.93546833333333,\"sp\":0.6094311,\"be\":\"79.53\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078514000,\"x\":22.528056666666664,\"y\":113.93547666666667,\"sp\":0.60840166,\"be\":\"82.1\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078515000,\"x\":22.528056666666664,\"y\":113.93548833333334,\"sp\":0.6145783,\"be\":\"86.24\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078516000,\"x\":22.528041666666663,\"y\":113.93549499999999,\"sp\":0.62693167,\"be\":\"116.39\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078518000,\"x\":22.52795833333333,\"y\":113.93551166666666,\"sp\":0.6274464,\"be\":\"134.78\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078519000,\"x\":22.527878333333334,\"y\":113.93554833333334,\"sp\":0.6264169,\"be\":\"139.79\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078520000,\"x\":22.52787166666667,\"y\":113.93555666666667,\"sp\":0.6120047,\"be\":\"133.39\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078522000,\"x\":22.52785833333333,\"y\":113.93557000000001,\"sp\":0.5981072,\"be\":\"139.39\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078525000,\"x\":22.527849999999997,\"y\":113.93558166666665,\"sp\":0.55178225,\"be\":\"135.07\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078527000,\"x\":22.52784166666667,\"y\":113.93559166666667,\"sp\":0.49876583,\"be\":\"104.19\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078529000,\"x\":22.527834999999996,\"y\":113.93560666666666,\"sp\":0.41280723,\"be\":\"6.75\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078531000,\"x\":22.527831666666664,\"y\":113.93562500000002,\"sp\":0.0,\"be\":\"318.21\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078534000,\"x\":22.52782,\"y\":113.93562833333333,\"sp\":0.20280056,\"be\":\"176.19\",\"tp\":1,\"ac\":2,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078538000,\"x\":22.527806666666667,\"y\":113.93563666666665,\"sp\":0.4488378,\"be\":\"145.54\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078570000,\"x\":22.52788666666667,\"y\":113.93561833333334,\"sp\":9.2469845,\"be\":\"353.34\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078571000,\"x\":22.527971666666666,\"y\":113.93557666666666,\"sp\":10.485406,\"be\":\"341.06\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078572000,\"x\":22.528059999999996,\"y\":113.93552166666665,\"sp\":10.3891535,\"be\":\"338.41\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078573000,\"x\":22.528116666666666,\"y\":113.93548833333334,\"sp\":9.431255,\"be\":\"336.06\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078574000,\"x\":22.528206666666666,\"y\":113.93536666666665,\"sp\":9.527509,\"be\":\"334.13\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078575000,\"x\":22.528265,\"y\":113.93532333333333,\"sp\":8.938666,\"be\":\"332.04\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078576000,\"x\":22.528281666666665,\"y\":113.93530333333335,\"sp\":7.6374483,\"be\":\"331.41\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078577000,\"x\":22.528284999999997,\"y\":113.93528833333333,\"sp\":6.454102,\"be\":\"332.47\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078578000,\"x\":22.528301666666668,\"y\":113.93527,\"sp\":5.6166487,\"be\":\"332.31\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078579000,\"x\":22.528278333333333,\"y\":113.93526500000002,\"sp\":4.2619,\"be\":\"331.29\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078580000,\"x\":22.528248333333334,\"y\":113.93525166666669,\"sp\":1.7742475,\"be\":\"325.59\",\"tp\":1,\"ac\":4,\"sl\":-1,\"cd\":1},{\"tag\":\"0\",\"tm\":1598078581000,\"x\":22.528281666666665,\"y\":113.93526500000002,\"sp\":1.5312986,\"be\":\"317.01\",\"tp\":1,\"ac\":3,\"sl\":-1,\"cd\":1}]}";
//        LocParam locParam = new Gson().fromJson(testText,LocParam.class);
//        for(LocInfo locInfo : locParam.getLocations()){
//            Location location = new Location("gps");
//            location.setTime(locInfo.getTm());
//            location.setLatitude(locInfo.getX());
//            location.setLongitude(locInfo.getY());
//            location.setSpeed(locInfo.getSp());
//            location.setBearing(locInfo.getBe());
//            location.setAccuracy(locInfo.getAc());
//            lastOneLoc.add(location);
//        }

        LocParam locParam = new LocParam("de37d1ce653e44dd92d97d3dba614d6c", AppInfo.getCarPlate(), "90F2CC314DD645D7830163CDF3748859", null);
        ArrayList<LocInfo> locInfos = new ArrayList<>();
        ArrayList<String> times = new ArrayList<>();
        for (int i = 0; i < lastOneLoc.size(); i++) {
            Calendar localCalendar = Calendar.getInstance(Locale.CHINA);
            localCalendar.setTimeInMillis(lastOneLoc.get(i).getTime());
            int second = localCalendar.get(Calendar.SECOND);
            String time = String.valueOf(second);
            if (second < 10) {
                time = "0" + second;
            }
            times.add(time);
        }
        boolean zero = false;
        boolean one = false;
        boolean two = false;
        boolean thire = false;
        boolean four = false;
        boolean five = false;

        int mzero = -1;
        int mone = -1;
        int mtwo = -1;
        int mthire = -1;
        int mfour = -1;
        int mfive = -1;
        for (int i = 0; i < times.size(); i++) {
            if (String.valueOf(times.get(i)).startsWith("0") && !zero) {
                mzero = i;
                zero = true;
            }
            if (String.valueOf(times.get(i)).startsWith("1") && !one) {
                mone = i;
                one = true;
            }
            if (String.valueOf(times.get(i)).startsWith("2") && !two) {
                mtwo = i;
                two = true;
            }
            if (String.valueOf(times.get(i)).startsWith("3") && !thire) {
                mthire = i;
                thire = true;
            }
            if (String.valueOf(times.get(i)).startsWith("4") && !four) {
                mfour = i;
                four = true;
            }
            if (String.valueOf(times.get(i)).startsWith("5") && !five) {
                mfive = i;
                five = true;
            }
        }

        for (Location location : lastOneLoc) {
            LocInfo locInfo = new LocInfo(0, location, -1);
            locInfos.add(locInfo);
        }

        if (mzero != -1) locInfos.get(mzero).setTag(1);
        if (mone != -1) locInfos.get(mone).setTag(1);
        if (mtwo != -1) locInfos.get(mtwo).setTag(1);
        if (mthire != -1) locInfos.get(mthire).setTag(1);
        if (mfour != -1) locInfos.get(mfour).setTag(1);
        if (mfive != -1) locInfos.get(mfive).setTag(1);

        locParam.setLocations(locInfos);
        return locParam;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public void reciveLoc(Location location) {
        Log.d(TAG, "EventBus reciveLoc " + new Gson().toJson(location));
        location.setElapsedRealtimeNanos(satellite); //用这个字段保存下卫星数
        lastOneLoc.add(location);
    }

    public void reciveLog(LogParam logParam) {
        lastLogParam = logParam;
        Log.d(TAG, "EventBus reciveLog " + new Gson().toJson(logParam));
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadLog(logParam);
            }
        }).start();

    }

    public void reciveSatellite(int satellite) {
        this.satellite = satellite;
    }
    //[1]定义中间人对象(IBinder)

    public class MyBinder extends Binder {

        public void ReciveLoc(Location location) {
            //调用办证的方法
            reciveLoc(location);
        }

        public void ReciveSatellite(int satellite) {
            //调用办证的方法
            reciveSatellite(satellite);
        }

        public void ReciveLog(LogParam logParam) {
            //调用办证的方法
            reciveLog(logParam);
        }

        public void EndNavi() {
            //调用办证的方法
            endNavi();
        }
    }

    private void endNavi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadLocOneMin(getParam());
            }
        }).start();
    }

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

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    lastOneLoc.clear();
                    startLocalLocUpload();
                    Log.d(TAG, "上传日志成功");
                    break;
                case 2:
                    saveRouteInfo(gpsLogPath, new Gson().toJson(lastLocParam) + "\n");
                    lastOneLoc.clear();
                    Log.d(TAG, "上传日志失败");
                    break;
                case 3:
                    locParams.remove(0);
                    StringBuffer local = new StringBuffer();
                    for (LocParam locParam : locParams) {
                        local.append(new Gson().toJson(locParam) + "\n");
                    }
                    saveRouteInfo1(gpsLogPath, local.toString());
                    locParams.clear();
                    startLocalLocUpload();
                    break;
                case 4:
                    Log.d(TAG, "上传本地log报错");
                    break;
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

    private void readTrace() {
        try {
            InputStream tmpStream = GetLogFile(gpsLogPath);
            if (null == tmpStream) {
                return;
            }
            locParams.clear();
            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(tmpStream, "UTF-8"));
            String str;
            while ((str = bufReader.readLine()) != null) {
                if (str != null && str.length() > 0) {
                    LocParam locParam = new Gson().fromJson(str, LocParam.class);
                    locParams.add(locParam);
                }
            }
            bufReader.close();
            tmpStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void startLocalLocUpload() {
        if (!isConnect(this)) {
            return;
        }
        readTrace();
        if (locParams.isEmpty()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (locParams.size() > 0) {
                    uploadHisLoc(locParams.get(0));
                }
            }
        }).start();
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

    /**
     * 向指定URL发送GET方法的请求
     */
    public static String sendGet(String url) throws IOException {
        String rtn = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);

            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf8"));
            String line;
            while ((line = in.readLine()) != null) {
                rtn += line;
            }
        } catch (IOException e) {
            throw e;
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
            }
        }
        return rtn;
    }

    /**
     * 向指定URL发送GET方法的请求
     */
    public static String sendGet(String url, String param) throws IOException {
        if (param != null) {
            url = url + "?" + param;
        }
        return sendGet(url);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     */
    public static String sendPost(String url, String param) throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 " +
                    "(compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new
                    InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            throw e;
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
        return result;
    }

    /**
     * 上传实时定位数据:每分钟
     */
    private boolean uploadLocOneMin(LocParam list) {
        return uploadPost(list, null, 1);
    }

    //上传历史定位数据
    private boolean uploadHisLoc(LocParam list) {
        return uploadPost(list, null, 3);
    }

    // 上传实时日志
    private boolean uploadLog(LogParam log) {
        return uploadPost(null, log, 5);
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
        if (DATA_TYPE < 4) {
            if (list.getLocations().size() < 1) {
                return false;
            }
            CommUtil.d(this, "location", "sendPostion:" + list.getLocations().size());
        }

        //网络异常
        if (!isConnect(this)) {
            if (DATA_TYPE == 1 || DATA_TYPE == 5) {//实时
                sendMessage(DATA_TYPE + 1, "网络异常");
            }
            CommUtil.d(this, "location", "net is not connect");
            return false;
        }

        if (DATA_TYPE == 1) {//实时
            lastLocParam = list;
        }

        boolean out;
        BufferedReader bin = null;
        BufferedOutputStream bos = null;
        HttpURLConnection conn = null;

        StringBuilder sbUrl = new StringBuilder();


        if (DATA_TYPE < 4) {
            sbUrl.append("http://");
            sbUrl.append("gis-rss-eta-navi-track.sit.sf-express.com:2099");
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

            CommUtil.d(this, "url", "zip:" + mGZip + ":conn");

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
                        CommUtil.d(this, TAG, String.format(Locale.US,
                                "Upload %d locations successfully.", lastOneLoc.size()));
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

}

