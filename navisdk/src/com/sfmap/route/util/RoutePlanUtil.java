package com.sfmap.route.util;

import com.sfmap.navi.R;
import com.sfmap.route.car.RouteCarDrawMapLineTools;

public class RoutePlanUtil {
    public static int getWalkTypActionIcon(int mWalkType) {
        switch (mWalkType) {
            case 1:
                return R.drawable.action_cross;
            case 2:
                return R.drawable.action_cross;
            case 3:
                return R.drawable.action_cross;
            case 4:
                return R.drawable.action_pass_bridge;
            case 5:
                return R.drawable.action25;
            case 6:
                return 0;
            case 7:
                return 0;
            case 8:
                return R.drawable.action23;
            case 9:
                return R.drawable.action22;
            case 10:
                return 0;
            case 11:
                return 0;
            case 12:
                return R.drawable.action25;
            case 13:
                return R.drawable.action_cross;
            case 14:
                return R.drawable.action28;
            case 15:
                return R.drawable.action29;
            case 16:
                return 0;
        }
        return 0;
    }

    public static int getWalkTypeActionIconForBrowser(int mWalkType) {
        switch (mWalkType) {
            case 1:
                return R.drawable.zou17;
            case 2:
                return R.drawable.zou17;
            case 3:
                return R.drawable.zou17;
            case 4:
                return R.drawable.zou18;
            case 5:
                return R.drawable.zou25;
            case 6:
                return 0;
            case 7:
                return 0;
            case 8:
                return R.drawable.zou22;
            case 9:
                return R.drawable.zou23;
            case 10:
                return 0;
            case 11:
                return 0;
            case 12:
                return R.drawable.zou25;
            case 13:
                return R.drawable.zou17;
            case 14:
                return R.drawable.zou28;
            case 15:
                return R.drawable.zou29;
            case 16:
                return 0;
        }
        //return result;
        return 0;
    }

    public static int getActionIconForBrowser(byte action) {
        switch (action) {
            case 0x00:
                return R.drawable.zou9;
            case 0x01:
                return R.drawable.zou2;
            case 0x02:
                return R.drawable.zou3;
            case 0x03:
                return R.drawable.zou4;
            case 0x09:
                return R.drawable.zou9;
            case 0x04:
                return R.drawable.zou5;
            case 0x0A:
                return R.drawable.zou9;
            case 0x05:
                return R.drawable.zou6;
            case 0x06:
                return R.drawable.zou7;
            case 0x07:
                return R.drawable.zou9;
            case 0x08:
                return R.drawable.zou9;
            case 0x0d:
                return R.drawable.zou9;
            case 0x0e:
                return R.drawable.zou9;

            case 0x0B:
                return R.drawable.action11;
            case 0x0C:
                return R.drawable.action12;
            case 0x41:
            case 0x42:
                return R.drawable.zou31;
            case 0x43: // 坐电梯
                return R.drawable.zou23;
            case 0x44: // 走楼梯
                return R.drawable.zou19;
            case 0x45: // 坐扶梯
                return R.drawable.zou22;
            default:
                return R.drawable.action9;
        }
    }


    public static int getNaviActionIcon(byte action) {
        switch (action) {
            case 0x00:
                return R.drawable.action9;
            case 0x01:
                return R.drawable.action2;
            case 0x02:
                return R.drawable.action3;
            case 0x03:
                return R.drawable.action4;
            case 0x09:
                return R.drawable.action9;
            case 0x04:
                return R.drawable.action5;
            case 0x0A:
                return R.drawable.action9;
            case 0x05:
                return R.drawable.action6;
            case 0x06:
                return R.drawable.action7;
            case 0x07:
                return R.drawable.action8;
            case 0x08:
                return R.drawable.action9;
            case 0x0d:
                return R.drawable.action13;
            case 0x0e:
                return R.drawable.action14;
            case 0x0B:
                return R.drawable.action11;
            case 0x0C:
                return R.drawable.action12;
            case 0x41:
            case 0x42:
                return R.drawable.action24;
            case 0x43: // 坐电梯
                return R.drawable.action22;
            case 0x44: // 走楼梯
                return R.drawable.action21;
            case 0x45: // 坐扶梯
                return R.drawable.action23;
            default:
                return R.drawable.action9;
        }
    }


    public static String getNaviActionStr(byte action) {
        switch (action) {
            case 0x00:
                return "直行";
            case 0x01:
                return "左转";
            case 0x02:
                return "右转";
            case 0x03:
                return "偏左转";
            case 0x09:
                return "直行";
            case 0x04:
                return "偏右转";
            case 0x0A:
                return "直行";
            case 0x05:
                return "左后转";
            case 0x06:
                return "右后转";
            case 0x07:
                return "左转调头";
            case 0x08:
                return "直行";
            case 0x0d:
                return "经过服务区";
            case 0x0e:
                return "经过收费站";
            case 0x0B:
                return "进入环岛";
            case 0x0C:
                return "驶出环岛";
            default:
                return "";
        }

    }


    /**
     * 根据状态值返回对应的颜色值（彩虹蚯蚓）
     *
     * @param state
     * @return
     */
    public static int getLineStateColor(int state) {
        // 0xDD188AFF
        switch (state) {
            case 0x00: // 未知
                return 0xFFBEBEBE;
            case 0x01:// 畅通
                return 0xFF00FF00;
            case 0x02:// 缓行
                return 0xFFFFFF00;
            case 0x03:// 阻塞严重
                return 0xFFFF0000;
            case 0x04://超级拥堵
                return 0xffA00808;
            default:
                return 0xFFBEBEBE;
        }
    }

    public static boolean isColorLineState(int state) {
        switch (state) {
            case 0x00: // 未知
                return false;
            case 0x01:// 畅通
                return true;
            case 0x02:// 堵塞
                return true;
            case 0x03:// 阻塞严重
                return true;
            case 0x04:
                return true;
            default:
                return false;
        }
    }

    public static int getLineStateBitmapIndex(int state) {
        switch (state) {
            case 0x00: // 未知
                return RouteCarDrawMapLineTools.TLINE_TRAFFIC_NO_DATA;
            case 0x01:// 畅通
                return RouteCarDrawMapLineTools.TLINE_TRAFFIC_GREEN;
            case 0x02:// 缓行
                return RouteCarDrawMapLineTools.TLINE_TRAFFIC_SLOW;
            case 0x03:// 阻塞
                return RouteCarDrawMapLineTools.TLINE_TRAFFIC_BAD;
            case 0x04://超级拥堵
                return RouteCarDrawMapLineTools.TLINE_TRAFFIC_BADER;
            default:
                return RouteCarDrawMapLineTools.TLINE_TRAFFIC_NO_DATA;
        }
    }

    public static int getNaviActionIconEx(int mainAction) {
        switch (mainAction) {
            case 0x1:
                return 2;
            case 0x2:
                return 3;
            case 0x3:
                return 4;
            case 0x9:
                return 9;
            case 0x4:
                return 5;
            case 0x0A:
                return 9;
            case 0x5:
                return 6;
            case 0x6:
                return 7;
            case 0x7:
                return 8;
            case 0x8:
                return 9;
            case 0x0B:
                return 11;
            case 0x0C:
                return 12;
            case 0x0F:
                return 15;
            case 0x0d:
                return 13;
            case 0x0e:
                return 14;
        }
        return 9;
    }

}
