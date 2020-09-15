package com.sfmap.route.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RouteErrorType implements Parcelable {
    //当为路线报错、地点报错、道路属性或当为道路报错且为道路变更或道路不通时，才有值，支持多选

    /**
     * 变更类型：
     * 1       2      3      4        5
     * 车道数变更， 可掉头，  可转弯，  不可掉头，  不可转弯
     * 道路不通：
     * 6  7    8    9
     * 禁行，施工，事故，限牌限行
     * 路线报错：
     * 10    11
     * 绕路，未到达目的地
     * 地点报错：
     * 12     13    14
     * 位置错误，信息错误，地点缺失
     * 道路属性：
     * 15  16  17   18  19
     * 限高，限牌，限重，限长，限宽
     */

    private String errorName;
    private int errorType;

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.errorName);
        dest.writeInt(this.errorType);
    }

    public RouteErrorType() {
    }

    protected RouteErrorType(Parcel in) {
        this.errorName = in.readString();
        this.errorType = in.readInt();
    }

    public static final Parcelable.Creator<RouteErrorType> CREATOR = new Parcelable.Creator<RouteErrorType>() {
        @Override
        public RouteErrorType createFromParcel(Parcel source) {
            return new RouteErrorType(source);
        }

        @Override
        public RouteErrorType[] newArray(int size) {
            return new RouteErrorType[size];
        }
    };
}
