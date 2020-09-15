package com.sfmap.route.model;

public class NaviJumpRouteEvent {
    private String filePath;
    private int type;//结束类型 1：未导航结束退出  2：导航结束退出

    public NaviJumpRouteEvent(String filePath, int type) {
        this.filePath = filePath;
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
