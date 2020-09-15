package com.sfmap.adcode;





import com.sfmap.library.util.JsonHelper;

import org.json.JSONObject;

import java.io.Serializable;


public class AdCity implements Comparable<AdCity>, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1L;
    private String province;
    private String city;
    public String code;
    private String adcode;
    private String cx;
    private String cy;
    private String level;
    private String initial;
    private String pinyin;

    public AdCity() {

    }

    public AdCity(String province, String city, String code, String cx,
	    String cy, String level, String initial, String pinyin) {
	this.setProvince(province.trim());
	this.setCity(city.trim());
	this.setCx(cx.trim());
	this.setCy(cy.trim());
	this.setLevel(level.trim());
	this.setInitial(initial.trim());
	this.code = code.trim();
	this.setPinyin(pinyin.trim());
    }

    public void parse(JSONObject jo) {
	province = JsonHelper.getJsonStr(jo, "province");
	city = JsonHelper.getJsonStr(jo, "name");
	code = JsonHelper.getJsonStr(jo, "code");
	adcode = JsonHelper.getJsonStr(jo, "adcode");
	cx = JsonHelper.getJsonStr(jo, "cx");
	cy = JsonHelper.getJsonStr(jo, "cy");
	level = JsonHelper.getJsonStr(jo, "level");
	initial = JsonHelper.getJsonStr(jo, "initial");
	pinyin = JsonHelper.getJsonStr(jo, "pinyin");
    }

    public void setProvince(String province) {
	this.province = province;
    }

    public String getProvince() {
	return province;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getCity() {
	return city;
    }

    public void setCx(String cx) {
	this.cx = cx;
    }

    public String getCx() {
	return cx;
    }

    public void setCy(String cy) {
	this.cy = cy;
    }

    public String getCy() {
	return cy;
    }

    public void setInitial(String initial) {
	this.initial = initial;
    }

    public String getInitial() {
	return initial;
    }

    public void setLevel(String level) {
	this.level = level;
    }

    public String getLevel() {
	return level;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }

    public void setAdCode(String adcode) {
	this.adcode = adcode;
    }

    public String getAdCode() {
	return adcode;
    }

    @Override
    public int compareTo(AdCity another) {
	String initial = ((AdCity) another).initial;
	if (initial.charAt(0) > this.initial.charAt(0)) {
	    return -1;
	} else if (initial.charAt(0) < this.initial.charAt(0)) {
	    return 1;
	} else {
	    return 0;
	}

    }

    public void setPinyin(String pinyin) {
	this.pinyin = pinyin;
    }

    public String getPinyin() {
	return pinyin;
    }
}
