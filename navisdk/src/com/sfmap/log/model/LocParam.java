package com.sfmap.log.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class LocParam implements Parcelable {
    /**
     *ak值
     */
    private String ak;

    /**
     *车牌号
     */
    private String carNo;

    /**
     *设备号
     */
    private String deviceId;

    /**
     *轨迹点集合
     */
    private ArrayList<LocInfo> locations;

    public LocParam(String ak, String carNo, String deviceId, ArrayList<LocInfo> locations) {
        this.ak = ak;
        this.carNo = carNo;
        this.deviceId = deviceId;
        this.locations = locations;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public ArrayList<LocInfo> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<LocInfo> locations) {
        this.locations = locations;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ak);
        dest.writeString(this.carNo);
        dest.writeString(this.deviceId);
        dest.writeTypedList(this.locations);
    }

    protected LocParam(Parcel in) {
        this.ak = in.readString();
        this.carNo = in.readString();
        this.deviceId = in.readString();
        this.locations = in.createTypedArrayList(LocInfo.CREATOR);
    }

    public static final Parcelable.Creator<LocParam> CREATOR = new Parcelable.Creator<LocParam>() {
        @Override
        public LocParam createFromParcel(Parcel source) {
            return new LocParam(source);
        }

        @Override
        public LocParam[] newArray(int size) {
            return new LocParam[size];
        }
    };
}
