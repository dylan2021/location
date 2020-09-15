package com.sfmap.route.util;


import android.text.TextUtils;

/**
 * 路径计算类型定义
 */
public class RouteCalType {

    public final static String Busroute_fast = "0";

    // 默认多策略
    public final static String CARROUTE_INDEX_DEFAULT = "9"; //多策略

    // 推荐道路
    public final static String CARROUTE_INDEX_0 = "5";//默认推荐道路
    // 避免收费
    public static final String CARROUTE_INDEX_1 = "1";// 避免收费
    // 距离最短
    public final static String CARROUTE_INDEX_2 = "2";//距离最短
    // 高速优先
    public final static String CARROUTE_INDEX_3 = "3";//高速优先
    // 频次优先
    public final static String CARROUTE_INDEX_4 = "4";//频次优先

    public final static String CARROUTE_INDEX_5 = "0";//时间优先
//    // 避免高速
//    public final static String CARROUTE_INDEX_8 = "5";

//    // 速度优先 0 快速路优先 状态常量
//    public final static String CARROUTE_INDEX_0 = "11";
//    // 多策略路径
//    public static final String CARROUTE_INDEX_1 = "6";
//    // 躲避拥堵
//    public final static String CARROUTE_INDEX_2 = "1";
//    // 避免收费
//    public final static String CARROUTE_INDEX_4 = "0";
//    // 避免高速
//    public final static String CARROUTE_INDEX_8 = "5";
    //}}

    //    public final static String CARROUTE_INDEX_STR_0 = "速度优先";
    public final static String CARROUTE_INDEX_STR_0 = "推荐道路";
    //    public final static String CARROUTE_INDEX_STR_0 = "推荐道路";
    public final static String CARROUTE_INDEX_STR_1 = "避免收费";
    public final static String CARROUTE_INDEX_STR_2 = "距离最短";
    public final static String CARROUTE_INDEX_STR_3 = "高速优先";
    public final static String CARROUTE_INDEX_STR_4 = "频次优先";
    public final static String CARROUTE_INDEX_STR_5 = "时间优先";
//    public final static String CARROUTE_INDEX_STR_8 = "不走高速";

    /**
     * 根据字符串的flag值获取导航策略
     *
     * @param method
     * @return
     */
    public static int getNaviType(String method) {
        int naviType = 0;//默认是推荐道路
        if (TextUtils.isEmpty(method)) {
            return Integer.parseInt(CARROUTE_INDEX_5);
        }
        if (method.contains(CARROUTE_INDEX_0)) {// 推荐道路
            naviType = Integer.parseInt(CARROUTE_INDEX_0);
        } else if (method.contains(CARROUTE_INDEX_1)) {// 避免收费
            naviType = Integer.parseInt(CARROUTE_INDEX_1);
        } else if (method.contains(CARROUTE_INDEX_2)) {//距离最短
            naviType = Integer.parseInt(CARROUTE_INDEX_2);
        } else if (method.contains(CARROUTE_INDEX_3)) {//高速优先
            naviType = Integer.parseInt(CARROUTE_INDEX_3);
        } else if(method.contains(CARROUTE_INDEX_4)){
            naviType = Integer.parseInt(CARROUTE_INDEX_0); //频次优先
        } else if (method.contains(CARROUTE_INDEX_DEFAULT)) {//多策略
            naviType = Integer.parseInt(CARROUTE_INDEX_DEFAULT);
        }else if (method.contains(CARROUTE_INDEX_5)) {//多策略
            naviType = Integer.parseInt(CARROUTE_INDEX_5);
        }
        return naviType;
    }

    /**
     * 获取导航策略的字符串描述
     *
     * @param method
     * @return
     */
    public static String getNaviPloyString(String method) {
        String ployStr = null;
        if (TextUtils.isEmpty(method)) {
            return null;
        }
        if (method.contains(CARROUTE_INDEX_0)) {//推荐道路
            ployStr = RouteCalType.CARROUTE_INDEX_STR_0;
        } else if (method.contains(CARROUTE_INDEX_1)) {// 避免收费
            ployStr = RouteCalType.CARROUTE_INDEX_STR_1;
        } else if (method.contains(CARROUTE_INDEX_2)) {// 距离最短
            ployStr = RouteCalType.CARROUTE_INDEX_STR_2;
        } else if (method.contains(CARROUTE_INDEX_3)) {// 高速优先
            ployStr = RouteCalType.CARROUTE_INDEX_STR_3;
        }else if (method.contains(CARROUTE_INDEX_4)) {// 频次优先
            ployStr = RouteCalType.CARROUTE_INDEX_STR_4;
        }else if (method.contains(CARROUTE_INDEX_5)) {// 时间优先
            ployStr = RouteCalType.CARROUTE_INDEX_STR_5;
        }

        return ployStr;
    }


    public static String getNaviTypes(String method) {
        String naviType = "0";//默认是推荐道路
        if (TextUtils.isEmpty(method)) {
            return CARROUTE_INDEX_5;
        }
        if(method.contains(CARROUTE_INDEX_4)){
            naviType = CARROUTE_INDEX_4; //频次优先
        }else if (method.contains(CARROUTE_INDEX_0)) {// 推荐道路
            naviType = CARROUTE_INDEX_0;
        } else if (method.contains(CARROUTE_INDEX_1)) {// 避免收费
            naviType = CARROUTE_INDEX_1;
        } else if (method.contains(CARROUTE_INDEX_2)) {//距离最短
            naviType = CARROUTE_INDEX_2;
        } else if (method.contains(CARROUTE_INDEX_3)) {//高速优先
            naviType = CARROUTE_INDEX_3;
        } else if (method.contains(CARROUTE_INDEX_DEFAULT)) {//多策略
            naviType = CARROUTE_INDEX_DEFAULT;
        }
        return naviType;
    }

    /**
     * 获取flag值
     *
     * @param method
     * @return
     */

    public static int getNaviFlags(String method) {
        int naviFlags = 0;
        return naviFlags;
    }

    /**
     * 解析辅助动作
     *
     * @param actionIndex
     * @return
     */
    public static byte getAssiActionIconEx(int actionIndex) {
        switch (actionIndex) {
            case 0x21:
                return 13;
            case 0x22:
                return 14;
        }
        return 0;
    }

}
