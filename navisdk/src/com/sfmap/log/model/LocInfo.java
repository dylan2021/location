package com.sfmap.log.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class LocInfo implements Parcelable {
    //ac = 65;be = "-1";cd = 1;sl = "-1";sp = "-1";tag = 1;tm = 1597654024306;tp = 1;x = "114.420457";y = "30.456350"
    /**
     *每隔10秒标记一个轨迹点tag=1,其他点为0
     */
    private int tag;

    /**
     *时间戳-秒
     */
    private long tm;

    /**
     *纬度
     */
    private double x;

    /**
     *经度
     */
    private double y;

    /**
     * 坐标系：
     * 1-GCJ02
     * 2-BD09
     * 3-WGS84
     * 输入2或3需进行转换成1后再进行计算
     */
    private int cd ;//坐标系：

    /**
     *瞬时速度
     */
    private float sp;

    /**
     *方位角
     */
    private float be;

    /**
     *定位类型 1:GPS,5:wifi,6:基站
     */
    private int tp;

    /**
     *精度
     */
    private float ac;

    /**
     *卫星数
     */
    private int sl;

    public LocInfo(int tag, Location loc, int sl) {
        this.tag = tag;
        this.tm = loc.getTime()/1000;
        this.x = loc.getLongitude();
        this.y = loc.getLatitude();
        this.cd = 3;
        this.sp = loc.getSpeed();
        this.be = loc.getBearing();
        this.tp = 1;
        this.ac = loc.getAccuracy();
        this.sl = (int)loc.getElapsedRealtimeNanos();
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public long getTm() {
        return tm;
    }

    public void setTm(long tm) {
        this.tm = tm;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getCd() {
        return cd;
    }

    public void setCd(int cd) {
        this.cd = cd;
    }

    public float getSp() {
        return sp;
    }

    public void setSp(float sp) {
        this.sp = sp;
    }

    public float getBe() {
        return be;
    }

    public void setBe(float be) {
        this.be = be;
    }

    public int getTp() {
        return tp;
    }

    public void setTp(int tp) {
        this.tp = tp;
    }

    public float getAc() {
        return ac;
    }

    public void setAc(float ac) {
        this.ac = ac;
    }

    public int getSl() {
        return sl;
    }

    public void setSl(int sl) {
        this.sl = sl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tag);
        dest.writeLong(this.tm);
        dest.writeDouble(this.x);
        dest.writeDouble(this.y);
        dest.writeInt(this.cd);
        dest.writeFloat(this.sp);
        dest.writeFloat(this.be);
        dest.writeInt(this.tp);
        dest.writeFloat(this.ac);
        dest.writeInt(this.sl);
    }

    protected LocInfo(Parcel in) {
        this.tag = in.readInt();
        this.tm = in.readLong();
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.cd = in.readInt();
        this.sp = in.readFloat();
        this.be = in.readFloat();
        this.tp = in.readInt();
        this.ac = in.readFloat();
        this.sl = in.readInt();
    }

    public static final Parcelable.Creator<LocInfo> CREATOR = new Parcelable.Creator<LocInfo>() {
        @Override
        public LocInfo createFromParcel(Parcel source) {
            return new LocInfo(source);
        }

        @Override
        public LocInfo[] newArray(int size) {
            return new LocInfo[size];
        }
    };
}
