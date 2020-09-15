package com.sfmap.log.Event;

public class UploadEvent {
    private int type;
    private String meg;

    public UploadEvent(int type, String meg) {
        this.type = type;
        this.meg = meg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMeg() {
        return meg;
    }

    public void setMeg(String meg) {
        this.meg = meg;
    }
}
