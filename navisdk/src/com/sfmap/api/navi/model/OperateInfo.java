package com.sfmap.api.navi.model;

public class OperateInfo {
    private String operate;
    private String content;

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OperateInfo(String operate, String content) {
        this.operate = operate;
        this.content = content;
    }
}
