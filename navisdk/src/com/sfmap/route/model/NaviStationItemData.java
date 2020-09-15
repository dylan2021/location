package com.sfmap.route.model;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.sfmap.map.util.CatchExceptionUtil;

import java.util.ArrayList;
import java.util.List;

public class NaviStationItemData implements MultiItemEntity {
    // 起点
    public static final int START_POINT_DES = 2;
    // 终点
    public static final int END_POINT_DES = 3;
    // 聚合路线
    public static final int GROUP_POINT_DES = 4;
    // 非聚合普通路线
    public static final int STATION_POINT_DES = 5;
    // 描述类型（上述四种之一）
    public int desType;
    public String streetName;
    public String distanceDes;
    public int actionIcon = 0;
    public byte aciontType;
    public int index;
    // 道路名称
    public String actionDes;
    // 聚合路段描述（距离 费用 红绿灯数）
    public String groupDes;

    public List<SubItem> subList;

    public NaviStationItemData() {
        actionIcon = -1;
        streetName = "";
        distanceDes = "";
        aciontType = 0;
        subList = new ArrayList<SubItem>();
    }

    /**
     * 将聚合线路描述中红绿灯数字标成红色
     *
     * @return
     */
    public SpannableString getGroupDesSP() {
        SpannableString sp = new SpannableString(groupDes);
        try {
            if (groupDes != null && groupDes.contains("红绿灯")) {
                int sIndex = groupDes.indexOf("红绿灯");
                int eIndex = sIndex + groupDes.length();
                if (sIndex >= 0 && eIndex > 0 && sIndex < eIndex) {
                    // 把字符串转换为字符数组
                    char num[] = groupDes.toCharArray();
                    for (int i = sIndex; i < num.length; i++) {
                        /*
                         * 判断输入的数字是否为数字还是字符
                         * 把字符串转换为字符，再调用Character.isDigit(char)方法判断是否是数字，
                         * 是返回True，否则False
                         */
                        if (Character.isDigit(num[i]) || num[i] == '.') {
                            sp.setSpan(
                                    new ForegroundColorSpan(Color.argb(255,
                                            228, 72, 83)), i, i + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return sp;
    }

    @Override
    public int getItemType() {
        return desType;
    }

    /**
     * 聚合线路信息
     */
    public static class SubItem {
        public int stationIndex;
        public String streetName;
        public String distanceDes;
        public int actionIcon;
        public byte aciontType;
        public String actionDes;
    }
}
