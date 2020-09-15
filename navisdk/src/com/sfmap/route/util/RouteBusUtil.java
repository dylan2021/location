package com.sfmap.route.util;

/**
 * 公交路线规划工具类
 */
public class RouteBusUtil {

    /**
     * 处理地铁/轻轨的进出口信息，便于显示
     *
     * @param name  轻轨/地铁的进出口名称
     * @param isOut true代表出站，false代表进站
     * @return 下车(B西南口出)/上车(B西南口进)
     */
    public static String dealSubWayPortName(String name, boolean isOut) {

        if (isOut) { // 出站
            if (name == null)
                return "下车";
            if (name.equals("出口"))
                return "下车";
            name = name.replace("(", "");
            name = name.replace(")", "");
            name = name.replace("出", "");
            name = "下车(" + name + "出)";
        } else { // 进站
            if (name == null)
                return "上车";
            name = name.replace("(", "");
            name = name.replace(")", "");
            name = name.replace("出", "");
            name = "上车(" + name + "进)";
        }
        return name;
    }

    /**
     * 处理地铁/轻轨的进出口简要信息，便于显示
     *
     * @param name  轻轨/地铁的进出口名称
     * @param isOut true代表出站，false代表进站
     * @return B(东北口)进/出口出
     */
    public static String simpleSubwayPortName(String name, boolean isOut) {
        if (isOut) { // 出站
            if (name == null)
                return "";
            if (name.equals("出口"))
                return "";
            name = name.replace("出", "");
            name = "" + name + "出站";
        } else { // 进站
            if (name == null)
                return "";
            name = name.replace("出", "");
            name = name + "进";
        }
        return name;
    }
}
