package com.sfmap.log.model;

import com.sfmap.api.navi.model.CrossImgInfo;
import com.sfmap.api.navi.model.LaneImgInfo;
import com.sfmap.api.navi.model.NaviEndInfo;
import com.sfmap.api.navi.model.OperateInfo;
import com.sfmap.api.navi.model.RouteInfo;
import com.sfmap.api.navi.model.VoiceInfo;
import com.sfmap.api.navi.model.YawInfo;

public class LogParam {
    private String ak;
    private int type;
    private String naviId;
    private String taskId;
    private String sdkVersion;
    private long reportTime;
    private RouteInfo routeInfo;
    private YawInfo yawInfo;
    private NaviEndInfo naviEndInfo;
    private LaneImgInfo laneImgInfo;
    private CrossImgInfo crossImgInfo;
    private VoiceInfo voiceInfo;
    private OperateInfo operateInfo;

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNaviId() {
        return naviId;
    }

    public void setNaviId(String naviId) {
        this.naviId = naviId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public RouteInfo getRouteInfo() {
        return routeInfo;
    }

    public void setRouteInfo(RouteInfo routeInfo) {
        this.routeInfo = routeInfo;
    }

    public YawInfo getYawInfo() {
        return yawInfo;
    }

    public void setYawInfo(YawInfo yawInfo) {
        this.yawInfo = yawInfo;
    }

    public NaviEndInfo getNaviEndInfo() {
        return naviEndInfo;
    }

    public void setNaviEndInfo(NaviEndInfo naviEndInfo) {
        this.naviEndInfo = naviEndInfo;
    }

    public LaneImgInfo getLaneImgInfo() {
        return laneImgInfo;
    }

    public void setLaneImgInfo(LaneImgInfo laneImgInfo) {
        this.laneImgInfo = laneImgInfo;
    }

    public CrossImgInfo getCrossImgInfo() {
        return crossImgInfo;
    }

    public void setCrossImgInfo(CrossImgInfo crossImgInfo) {
        this.crossImgInfo = crossImgInfo;
    }

    public VoiceInfo getVoiceInfo() {
        return voiceInfo;
    }

    public void setVoiceInfo(VoiceInfo voiceInfo) {
        this.voiceInfo = voiceInfo;
    }

    public OperateInfo getOperateInfo() {
        return operateInfo;
    }

    public void setOperateInfo(OperateInfo operateInfo) {
        this.operateInfo = operateInfo;
    }
}
