package com.sfmap.route.car;

/**
 * 驾车偏好状态常量
 */
public class CarRouteTask {

    // 速度优先 0 快速路优先 状态常量
    public final static String CARROUTE_INDEX_0 = "11";
    // 多策略路径
    public static final String CARROUTE_INDEX_1 = "6";
    // 躲避拥堵
    public final static String CARROUTE_INDEX_2 = "1";
    // 避免收费
    public final static String CARROUTE_INDEX_4 = "0";
    // 避免高速
    public final static String CARROUTE_INDEX_8 = "5";

    public final static String CARROUTE_INDEX_STR_0 = "高速优先";
    public final static String CARROUTE_INDEX_STR_1 = "多策略";
    public final static String CARROUTE_INDEX_STR_2 = "躲避拥堵";
    public final static String CARROUTE_INDEX_STR_4 = "避免收费";
    public final static String CARROUTE_INDEX_STR_8 = "不走高速";

    public static boolean containCarMethod(String method) {
        if (method == null || method.length() <= 0)
            return false;
        if (method.contains(CARROUTE_INDEX_0)
                || method.contains(CARROUTE_INDEX_1)
                || method.contains("|" + CARROUTE_INDEX_2 + "|")
                || method.contains(CARROUTE_INDEX_4)
                || method.contains(CARROUTE_INDEX_8))
            return true;

        return false;
    }
}
