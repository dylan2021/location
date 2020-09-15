package com.sfmap.route.model;

import java.io.Serializable;
import java.util.HashMap;

public class LocationCodeResult implements Serializable {

    public int res_stat;
    public String res_msg;
    // LocationCode的时间（单位秒）
    public int res_time;

    public HashMap<String, SegLocationCodeStatus> res_hash = new HashMap<String, SegLocationCodeStatus>();

}
