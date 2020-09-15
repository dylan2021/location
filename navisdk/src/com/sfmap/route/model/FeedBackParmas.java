package com.sfmap.route.model;

import java.util.List;

public class FeedBackParmas {
    private String ak;
    private String account;
    private String file_name;
    private String device_id;
    private String device_type;
    private String record_time;
    private String app_version;
    private List<Anwers> anwsers;

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getRecord_time() {
        return record_time;
    }

    public void setRecord_time(String record_time) {
        this.record_time = record_time;
    }

    public List<Anwers> getAnwsers() {
        return anwsers;
    }

    public void setAnwsers(List<Anwers> anwsers) {
        this.anwsers = anwsers;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

}

