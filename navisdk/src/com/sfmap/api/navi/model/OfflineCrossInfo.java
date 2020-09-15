package com.sfmap.api.navi.model;

import android.view.View;

/**
 * 下载属性的相关类
 */
public class OfflineCrossInfo {
     long progress;
     long size;
     String adcode;
     String version;
     int state;
    public OfflineCrossInfo(){}
    public OfflineCrossInfo(long progress, long size, String adcode,String version,int state){
        this.progress=progress;
        this.size=size;
        this.adcode=adcode;
        this.version=version;
        this.state=state;
    }

    /**
     * 返回已数据下载的大小
     * @return 己下载的大小 单位,字节
     */
    public long getComplete() {
        return progress;
    }

    /**
     * 设置数据下载的大小
     * @param progress 下载数据的大小
     */
    public void setComplete(long progress) {
        this.progress = progress;
    }


    /**
     * 返回下载数据的总大小
      * @return 下载数据大小 单位,字节
     */
    public long getSize() {
        return size;
    }

    /**
     * 设置下载数据的总大小
     * @param count 下载数据的总大小
     */
    public void setSize(long count) {
        this.size = count;
    }

    /**
     * 返回下载城市编码
     * @return 下载的城市编码
     */
    public String getAdcode() {
        return adcode;
    }

    /**
     * 设置下载的城市编码
     * @param adcode 下载的城市编码
     */
    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    /**
     * 返回下载的数据版本
     * @return 数据版本 本地若无该城市离线数据,则返回 0
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置下载的数据版本
     * @param version 数据版本
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 返回下载的状态
     * @return 下载的状态 类型参考OfflineCrossDownStatus
     */
    public int getState() {
        return state;
    }

    /**
     * 设置下载的状态
     * @param status 下载的状态 类型参考OfflineCrossDownStatus
     */
    public void setState(int status) {
        this.state = status;
    }
}
