package com.sfmap.map.util;

import android.content.Context;

import com.sfmap.adcode.AdCity;
import com.sfmap.adcode.AdProvince;
import com.sfmap.plugin.IMPluginManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提取一些URL作为常量，便于复用
 */

public class ConfigerHelper {

    private static ConfigerHelper instance = null;
    private Context appContext;
    private static final String CONFIGER_FILE = "sfmap_configer.data";
    private static final String DEPT_FILE = "wuhan_shenzhen_dept.csv";//武汉深圳网点边界数据
    private static final String ZNO_FILE = "export_zno_list.csv";//武汉深圳网点边界数据
    public static final String FEEDBACK_URL = "feedback_url";
    //url配置
    public static final String UPDATA_URL = "updata_url";
    public static final String SFMAP_API_URL = "api_url";
    //登录url配置
//    public static final String SFMAP_USER_URL = "user_url";
    public static final String SFMAP_FLASH_SCREEN_URL = "flashscreen_url";//闪屏URL
    public static final int BUS_ROUTE_COLOR = 0xDD188AFF;
    public static final int BUS_ROUTE_WIDTH = 4;
    public static final int BUS_WALK_COLOR = 0xDD666666;
    public static final int CAR_ROUTE_COLOR = 0xDD188AFF;
    public static final int CAR_ROUTE_WIDTH = 12;
    //配置信息容器
    HashMap<String, String> mConfStrList;
    HashMap<String, String> mDeptStrList;
    HashMap<String, String> mZnoBelongList;
    public static ArrayList<AdCity> allLocaList = null;

    public static final String ROUTE_FOOT_URL = "route_foot_url";           //骑行导航服务地址
    public static final String ROUTE_CAR_URL = "route_car_url";             //汽车和货车导航服务地址
    public static final String SEARCH_CCB_URL = "search_ccb_url";           //查询劳动者港湾服务地址
    public static final String SEARCH_POIID_URL = "search_poiid_url";           //查询POI详细信息服务地址
    public static final String SEARCH_DETAIL_URL = "search_detail_url";           //查询POI详细信息服务地址
    public static final String SF_LOGINAPP_URL = "sf_loginApp_url";         //用户名密码登录服务地址
    public static final String SF_TOKEN_URL = "sf_token_url";               //自动登录服务地址
    public static final String SF_BRANCHUSER_URL = "sf_branchUser_url";     //网点小哥位置服务地址
    public static final String SF_ERROR_REPORT_URL = "sf_error_report_url";     //上报和报错接口服务地址
    public static final String SF_POSHISPERIOD_URL = "sf_poshisPeriod_url"; //小哥轨迹服务地址
    public static final String SF_ZNO_URL = "sf_zno_url";                   //查询权限网点服务地址
    public static final String SF_TNO_URL = "sf_tno_url";                   //查询屏幕内权限网点服务地址
    public static final String SYSTEM_AK = "system_ak";                     //图咕系统配置的KEY
    public static final String WHITE_USER_LIST_URL = "white_uesr_list_url";  //内部版本白名单地址


    public static ConfigerHelper getInstance() {
        if (instance == null) {
            instance = new ConfigerHelper();
        }
        return instance;
    }

    private ConfigerHelper() {
        appContext = IMPluginManager.getApplication();
        mConfStrList = new HashMap<String, String>();
        mDeptStrList = new HashMap<String, String>();
        mZnoBelongList = new HashMap<String, String>();
        readConfiger();
        readDept();
        readZnoBelong();
    }

    public String getKeyValue(String key) {
        String val = mConfStrList.get(key);

        return val == null ? "" : val;
    }

    public String getDeptKeyValue(String key) {
        String val = mDeptStrList.get(key);
        return val == null ? "" : val;
    }

    public List<String> getZnoBelongKeyValue(String key) {
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, String> entry: mZnoBelongList.entrySet())
        {
            if(key.equals(entry.getValue()) && !key.equals(entry.getKey())){
                list.add(entry.getKey());
            }
            System.out.println("Key: "+ entry.getKey()+ " Value: "+entry.getValue());
        }
        return list;
    }

    /**
     * 读取配置文件
     *
     * @return
     */
    public InputStream GetConfigerFile() {
        InputStream tmpStream = null;
        try {
            tmpStream = appContext.getResources().getAssets()
                    .open(CONFIGER_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return appContext.getResources().getAssets()
                        .open(CONFIGER_FILE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return tmpStream;
    }

    private void readConfiger() {
        try {
            InputStream tmpStream = GetConfigerFile();
            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(tmpStream, "UTF-8"));
            String str;
            while ((str = bufReader.readLine()) != null) {
                if (str != null && str.length() > 0
                        && str.startsWith("#") == false) {
                    String[] tmp = str.split("=");
                    if (tmp != null && tmp.length >= 2) {
                        String name = tmp[0];
                        String value = tmp[1];
                        for (int i = 0; i < tmp.length - 2; i++) {
                            value += "=";
                            value += tmp[i + 2];
                        }
                        if (name != null && value != null) {
                            name = name.trim();
                            value = value.trim();
                            if (name.length() > 0 && value.length() > 0)
                                mConfStrList.put(name, value);
                        }
                    }
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
    public InputStream GetDeptFile() {
        InputStream tmpStream = null;
        try {
            tmpStream = appContext.getResources().getAssets()
                    .open(DEPT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return appContext.getResources().getAssets()
                        .open(DEPT_FILE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return tmpStream;
    }

    /**
     * 读取配置文件
     *
     * @return
     */
    public InputStream GetZnoBelongFile() {
        InputStream tmpStream = null;
        try {
            tmpStream = appContext.getResources().getAssets()
                    .open(ZNO_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return appContext.getResources().getAssets()
                        .open(ZNO_FILE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return tmpStream;
    }

    private void readDept() {
        try {
            InputStream tmpStream = GetDeptFile();
            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(tmpStream, "UTF-8"));
            String str;
            while ((str = bufReader.readLine()) != null) {
                if (str != null && str.length() > 0) {
                    String name = str.substring(0,str.indexOf("，"));
                    String value = str.substring(str.indexOf("，")+2,str.length()-1);
                    if (name != null && value != null) {
                        name = name.trim();
                        value = value.trim();
                        if (name.length() > 0 && value.length() > 0)
                            mDeptStrList.put(name, value);
                    }
                }
            }
            bufReader.close();
            tmpStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readZnoBelong() {
        try {
            InputStream tmpStream = GetZnoBelongFile();
            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(tmpStream, "GBK"));
            String str;
            while ((str = bufReader.readLine()) != null) {
                if (str != null && str.length() > 0) {
                    String[] strings = str.split(",");
                    String name = strings[0];
                    String value = strings[2];
                    if (name != null && value != null) {
                        name = name.trim();
                        value = value.trim();
                        if (name.length() > 0 && value.length() > 0)
                            mZnoBelongList.put(name, value);
                    }
                }
            }
            bufReader.close();
            tmpStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

