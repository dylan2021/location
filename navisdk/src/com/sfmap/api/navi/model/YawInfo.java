package com.sfmap.api.navi.model;

public class YawInfo {
    private String routeId = "0";
    private boolean hasYaw = false;

    public boolean isHasYaw() {
        return hasYaw;
    }

    public void setHasYaw(boolean hasYaw) {
        this.hasYaw = hasYaw;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
