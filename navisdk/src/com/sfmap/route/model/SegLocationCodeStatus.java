package com.sfmap.route.model;

import java.io.Serializable;

public class SegLocationCodeStatus implements Serializable {

    // LocationCode
    public int[] code;
    // LocationCode的长度（单位米）
    public int[] length;
    // 时间
    public int[] time;
    // 状态
    public int[] state;
    // 起点坐标索引
    public int[] startIndex;
    // 终点坐标索引
    public int[] endIndex;

    public SegLocationCodeStatus(int num) {
        code = new int[num];
        length = new int[num];
        time = new int[num];
        state = new int[num];
        startIndex = new int[num];
        endIndex = new int[num];
    }
    public void copy(SegLocationCodeStatus status){
        System.arraycopy(status.code,0,code,0,code.length);
        System.arraycopy(status.length,0,length,0,length.length);
        System.arraycopy(status.time,0,time,0,time.length);
        System.arraycopy(status.state,0,state,0,state.length);
        System.arraycopy(status.startIndex,0,startIndex,0,startIndex.length);
        System.arraycopy(status.endIndex,0,endIndex,0,endIndex.length);
    }
}
