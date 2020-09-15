package com.sfmap.adcode;




import com.sfmap.library.util.JsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class AdProvince implements Serializable {
    private static final long serialVersionUID = -1L;
    protected String mName = "";
    private String adcode;
    private String cx;
    private String cy;
    private String level;
    protected ArrayList<AdCity> mCities = new ArrayList<AdCity>();

    public AdProvince() {

    }

    public String getName() {
	return mName;
    }

    public void SetName(String aName) {
	mName = aName;
    }

    public ArrayList<AdCity> getCitiesList() {
	return mCities;
    }

    public void Parse(JSONObject jo) {
	mName = JsonHelper.getJsonStr(jo, "name");
	adcode = JsonHelper.getJsonStr(jo, "adcode");
	cx = JsonHelper.getJsonStr(jo, "cx");
	cy = JsonHelper.getJsonStr(jo, "cy");
	level = JsonHelper.getJsonStr(jo, "level");

	mCities.clear();

	try {
	    JSONArray cities = JsonHelper.getJsonArray(jo, "cities");
	    for (int i = 0; i < cities.length(); i++) {
		JSONObject cityJo = cities.getJSONObject(i);
		AdCity adCity = new AdCity();
		adCity.parse(cityJo);
		mCities.add(adCity);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void setAdCode(String adcode) {
	this.adcode = adcode;
    }

    public String getAdCode() {
	return adcode;
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

    public void setLevel(String level) {
	this.level = level;
    }

    public String getLevel() {
	return level;
    }

}
