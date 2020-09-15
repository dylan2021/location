package com.sfmap.route.model;

import java.io.Serializable;

public class Station implements Serializable {

    public String name;
    public int x;
    public int y;
    // 是否是最近的站点
    public boolean isNearestStation;
    public String id;
}
