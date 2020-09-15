package com.sfmap.route.model;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.sfmap.library.util.CalculateUtil;
import com.sfmap.library.util.DateTimeUtil;
import com.sfmap.map.util.CatchExceptionUtil;
import com.sfmap.map.util.RoutePathHelper;
import com.sfmap.tbt.RestrictionArea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.sfmap.library.util.DateTimeUtil.getTimeStr;

public class NavigationPath implements Serializable {
    //画路况的最小路线距离
    public final static int MIN_TMC_DISTANCE = 100 * 1000;
    //画路况的最大路线距离
    public final static int MAX_TMC_DISTANCE = 500 * 1000;
    public NavigationSection[] sections;
    public RestrictionArea[] restrictionAreas;
    public int sectionNum;
    public int dataLength;
    public int pathlength;
    public int taxiFee = -1;
    public LocationCodeResult location_code_result = null;
    // 当前路径的旅行时间
    public int costTime = 0;
    // 红绿灯数目
    public int trafficNum = 0;
    // 道路通行费
    public int tollCost = 0;
    /**
     * 当前路径的策略 * * 0 速度优先（推荐道路） * * 1 费用优先（尽量避开收费道路） * * 2 距离优先（距离最短） * * 3
     * 普通路优先（不走快速路，包含高速路） * * 4 考虑实时路况的路线（时间优先） * * 12 TMC最快且不走收费道路
     */
    public int pathStrategy = -1;
    // 规避限行路段标识
    public int limitRoadFlag;
    // 是否存在更省时间的tmc路径，能节省多少时间 (分钟)
    public int tmcTime;
    // 路线聚合
    public List<GroupNavigationSection> groupNaviSectionList;
    // 拥堵段信息
    public List<JamInfo> jamInfo;

    // 躲避拥堵区域信息
    public String avoidJamAreaStr = null;
    // 该条路线是否已经显示过躲避拥堵区域
    public boolean hasShowAvoidJamArea = false;
    // 路况事件信息
    public String incidentStr = null;
    // 是否已经显示路况时间信息
    public boolean hasShowIncident = false;

    public NavigationPath() {
        groupNaviSectionList = new ArrayList<GroupNavigationSection>();
        jamInfo = new ArrayList<JamInfo>();
    }

    public String getMainDesStr() {
        StringBuilder sbr = new StringBuilder();
        sbr.append(getTimeStr(costTime));
        sbr.append("  " + CalculateUtil.getLengDesc(pathlength));
        return sbr.toString();
    }

    public SpannableString getSubDesSP() {
        StringBuilder sbr = new StringBuilder();
//        if (trafficNum > 0) {
            sbr.append("红绿灯" + trafficNum + "个");
//        }
//        if (sbr.length() == 0) {
//            return new SpannableString("");
//        }
        return RoutePathHelper.textNumberHighlightBlack(sbr.toString());
    }

    public String getGroupDes() {
        StringBuilder sbr = new StringBuilder();
        for (GroupNavigationSection section : groupNaviSectionList) {
            if (section.isSrucial == 1) {
                sbr.append(section.groupName).append("→");
            }
        }
        if (sbr.length() > 0) {
            return sbr.subSequence(0, sbr.length() - 1).toString();
        }
        return null;
    }

    public String getTaxiFeeStr() {
        if (taxiFee <= 0) {
            return "";
        }
        String text = "  打车约" + taxiFee + "元";
        return text;
    }

    public SpannableString getTmcTimeDescSP() {
        StringBuilder sbr = new StringBuilder();
        sbr.append("有躲避拥堵方案，可为您节约").append(
                DateTimeUtil.getTimeStr(tmcTime * 60));
        SpannableString sp = new SpannableString(sbr.toString());
        try {
            // 将关键字颜色标记为白色
            sp.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 255, 255)),
                    1, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // 将关键字颜色标记为白色
            sp.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 255, 255)),
                    11, sbr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            CatchExceptionUtil.normalPrintStackTrace(e);
        }
        return sp;
    }

    public RestrictionArea[] getRestrictionAreas() {
        return restrictionAreas;
    }
}
