package com.sfmap.route.model;

import org.json.JSONObject;

public class RouteBean {

    private String X1;

    private String Y1;


    private String X2;


    private String Y2;


    private String Type;


    private String Json;

    private String Date;


    private String SType;

    private String EType;

    private String Viapoint;

    private String Flag;


    private String Plate;


    private String vehicle;


    private String Weight;


    private String Height;


    private String AxleNumber;


    private String AxleWeight;

    private String Time;

    /**
     * 包名
     */

    private String appPackageName;

    /**
     * sha1
     */

    private String appCerSha1;



    public String getX1() {
        return X1;
    }


    public void setX1(String x1) {
        X1 = x1;
    }


    public String getY1() {
        return Y1;
    }


    public void setY1(String y1) {
        Y1 = y1;
    }


    public String getX2() {
        return X2;
    }


    public void setX2(String x2) {
        X2 = x2;
    }


    public String getY2() {
        return Y2;
    }


    public void setY2(String y2) {
        Y2 = y2;
    }


    public String getType() {
        return Type;
    }


    public void setType(String type) {
        Type = type;
    }


    public String getJson() {
        return Json;
    }


    public void setJson(String json) {
        Json = json;
    }


    public String getDate() {
        return Date;
    }


    public void setDate(String date) {
        Date = date;
    }


    public String getSType() {
        return SType;
    }


    public void setSType(String sType) {
        SType = sType;
    }


    public String getEType() {
        return EType;
    }


    public void setEType(String eType) {
        EType = eType;
    }


    public String getViapoint() {
        return Viapoint;
    }


    public void setViapoint(String viapoint) {
        Viapoint = viapoint;
    }


    public String getFlag() {
        return Flag;
    }


    public void setFlag(String flag) {
        Flag = flag;
    }


    public String getPlate() {
        return Plate;
    }


    public void setPlate(String plate) {
        Plate = plate;
    }


    public String getVehicle() {
        return vehicle;
    }


    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }


    public String getWeight() {
        return Weight;
    }


    public void setWeight(String weight) {
        Weight = weight;
    }


    public String getHeight() {
        return Height;
    }


    public void setHeight(String height) {
        Height = height;
    }


    public String getAxleNumber() {
        return AxleNumber;
    }


    public void setAxleNumber(String axleNumber) {
        AxleNumber = axleNumber;
    }


    public String getAxleWeight() {
        return AxleWeight;
    }


    public void setAxleWeight(String axleWeight) {
        AxleWeight = axleWeight;
    }


    public String getTime() {
        return Time;
    }


    public void setTime(String time) {
        Time = time;
    }


    public String getAppPackageName() {
        return appPackageName;
    }


    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }


    public String getAppCerSha1() {
        return appCerSha1;
    }


    public void setAppCerSha1(String appCerSha1) {
        this.appCerSha1 = appCerSha1;
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        try{
            obj.put("X1",X1);
            obj.put("Y1",Y1);
            obj.put("X2",X2);
            obj.put("Y2",Y2);
            obj.put("Type",Type);
            obj.put("Json",Json);
            obj.put("Date",Date);
            obj.put("SType",SType);
            obj.put("EType",EType);
            obj.put("Viapoint",Viapoint);
            obj.put("Flag",Flag);
            obj.put("Plate",Plate);
            obj.put("vehicle",vehicle);
            obj.put("Weight",Weight);
            obj.put("Height",Height);
            obj.put("AxleNumber",AxleNumber);
            obj.put("AxleWeight",AxleWeight);
            obj.put("Time",Time);
            obj.put("appPackageName",appPackageName);
            obj.put("appCerSha1",appCerSha1);
        }catch (Exception e){
        }
        return obj.toString();
    }

}