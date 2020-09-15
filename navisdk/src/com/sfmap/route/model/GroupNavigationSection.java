package com.sfmap.route.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupNavigationSection implements Serializable {

    public GroupNavigationSection() {
        sectionList = new ArrayList<NavigationSection>();
    }

    // 当前序列
    public int index;
    // 聚合段名称
    public String groupName;
    // 包含导航段个数
    public int segCount;
    // 是否到达途径地
    public int isArrivePass;
    // 起始导航段下标
    public int startSegId;
    // 聚合段长度
    public int distance;
    // 聚合段收费金额
    public int toll;
    // 聚合段红绿灯数（取详情时计算获得）
    public int trafficLights;
    // 状态 0 未知状态 // 1 畅通状态 // 2 缓行状态 // 3 阻塞严重状态 //4 超级严重阻塞状态
    public int status;
    // 速度
    public int speed;
    // 重要性 1表示 重要 // 其中重要的，才是 你要展示的 
    public int isSrucial;

    // 复合 存放导航段个数 的基本数据类型
    public List<NavigationSection> sectionList;

}
