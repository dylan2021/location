package com.sfmap.api.navi.model;

public class EndNaviEvent {
    private boolean endNavi;

    public EndNaviEvent(boolean endNavi) {
        this.endNavi = endNavi;
    }

    public boolean isEndNavi() {
        return endNavi;
    }

    public void setEndNavi(boolean endNavi) {
        this.endNavi = endNavi;
    }
}
