package com.sfmap.api.navi.model;

import com.sfmap.tbt.Camera;

/**
 * 用于描述电子眼信息的类
 */

public class NaviCamera {
    /**
     * 电子眼类型，0 为测速摄像头，1为监控摄像头
     */
    public int m_CameraType;

    /**
     * 电子眼限速，若无限速信息则为0
     */
    public int m_CameraSpeed;

    /**
     * 电子眼所在位置的坐标
     */
    public NaviLatLng coords;

    public static NaviCamera[] getAllCameraForArray(Camera[] array){
        if(array==null)return null;
        NaviCamera[] cameras=new NaviCamera[array.length];
        for(int i=0;i<array.length;i++){
            cameras[i]=new NaviCamera();
            cameras[i].coords=new NaviLatLng(array[i].m_Latitude,array[i].m_Longitude);
            cameras[i].m_CameraSpeed=array[i].m_CameraSpeed;
            cameras[i].m_CameraType=array[i].m_CameraType;
        }
        return cameras;
    }
}
