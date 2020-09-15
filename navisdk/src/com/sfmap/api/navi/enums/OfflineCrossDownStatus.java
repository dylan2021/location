package com.sfmap.api.navi.enums;


public class OfflineCrossDownStatus {
    /**
     * 正在下载
     */
    public final static int LOADING=0;
    /**
     * 暂停下载
     */
    public final static int PAUSE=1;
    /**
     * 停止下载
     */
    public final static int STOP=2;
    /**
     * 下载成功
     */
    public final static int SUCCESS=3;
    /**
     * 网络异常
     */
    public final static int NET_ERROR=4;
    /**
     * 错误
     */
    public final static int ERROR=5;
    /**
     * 无下载数据
     */
    public final static int NOT_DATA=6;

}
