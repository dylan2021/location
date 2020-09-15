package com.sfmap.route.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RoutePathBean implements Parcelable {
    private String pathType;
    private String time;
    private String distance;
    private String tollCost;
    private String trafficLight;
    private int id;

    public RoutePathBean() {
    }

    public String getPathType() {
        return pathType;
    }

    public void setPathType(String pathType) {
        this.pathType = pathType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTollCost() {
        return tollCost;
    }

    public void setTollCost(String tollCost) {
        this.tollCost = tollCost;
    }

    public String getTrafficLight() {
        return trafficLight;
    }

    public void setTrafficLight(String trafficLight) {
        this.trafficLight = trafficLight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pathType);
        dest.writeString(this.time);
        dest.writeString(this.distance);
        dest.writeString(this.tollCost);
        dest.writeString(this.trafficLight);
        dest.writeInt(this.id);
    }

    protected RoutePathBean(Parcel in) {
        this.pathType = in.readString();
        this.time = in.readString();
        this.distance = in.readString();
        this.tollCost = in.readString();
        this.trafficLight = in.readString();
        this.id = in.readInt();
    }

    public static final Creator<RoutePathBean> CREATOR = new Creator<RoutePathBean>() {
        @Override
        public RoutePathBean createFromParcel(Parcel source) {
            return new RoutePathBean(source);
        }

        @Override
        public RoutePathBean[] newArray(int size) {
            return new RoutePathBean[size];
        }
    };
}
