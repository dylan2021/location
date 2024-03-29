package com.sfmap.api.services.poisearch;

import android.text.TextUtils;
import android.util.Log;

import com.sfmap.api.services.core.JsonResultHandler;
import com.sfmap.api.services.core.LatLonPoint;
import com.sfmap.api.services.poisearch.PoiSearch.Query;
import com.sfmap.api.services.poisearch.PoiSearch.SearchBound;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class PoiJsonParser {
    private String TAG = PoiJsonParser.class.getSimpleName();
    /**
     * poi搜索解析
     * @param query
     * @param mBound
     * @param obj
     * @return
     */
	PoiResult Poiparser(Query query, SearchBound mBound, JSONObject obj) {
		PoiResult result =null;
		boolean tag = obj.has("result");
		if (tag == false) {
			return null;
		}

		try {
			JSONArray resultArray = obj.getJSONArray("result");
			if (resultArray == null)
				return null;

			ArrayList<PoiItem> list = new ArrayList<PoiItem>();
            int length = resultArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject itemObj = resultArray.getJSONObject(i);
                PoiItem poiItem = new PoiItem();
                parsePoiItem(itemObj,poiItem);
                parseDetailInfo(itemObj,poiItem);

				list.add(poiItem);
			}
			result = new PoiResult(query, mBound, list);

            if (obj.has("message")) {
                result.setMessage(obj.getString("message"));
            }
            if (obj.has("total")) {
                if (!"".equals(obj.getString("total")))
                    result.setTotal(Integer.valueOf(obj.getString("total")));
            }
		} catch (JSONException e) {
            throw new IllegalArgumentException("json解析失败");
		}
		return result;
	}

    private void parseDetailInfo(JSONObject itemObj, PoiItem poiItem) throws  JSONException{
        if (itemObj.has("detail_info")) {
            JSONObject detailinfo = itemObj.getJSONObject("detail_info");
            if(!"".equals(detailinfo.getString("type"))){
                poiItem.setTypeDes(detailinfo.getString("type"));
            }
            if(!"".equals(detailinfo.getString("distance"))){
                poiItem.setDistance(Double.parseDouble(detailinfo.getString("distance")));
            }
        }

    }

    /**
     * poiid解析
     * @param obj
     * @return
     */
    List<PoiItem> PoiIdparser(JSONObject obj) {
        List<PoiItem> poiItems =null;
        boolean tag = obj.has("result");
        if (tag == false) {
            return null;
        }

        try {
            JSONArray resultArray = obj.getJSONArray("result");
            if (resultArray == null){
                return null;
            }else {
                 poiItems = new ArrayList<PoiItem>();
                int length =  resultArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject itemObj = resultArray.getJSONObject(i);
                    PoiItem poiItem = new PoiItem();
                    parsePoiItem(itemObj,poiItem);
                    poiItems.add(poiItem);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return poiItems;
    }

    /**
     * 解析poiItem
     * @param obj
     * @param poiItem
     * @throws JSONException
     */
    private void parsePoiItem(JSONObject obj , PoiItem poiItem)  throws JSONException{
        poiItem.setPoiId(JsonResultHandler.getJsonStringValue(obj,"uid",""));
        poiItem.setSnippet(JsonResultHandler.getJsonStringValue(obj, "address", ""));
        poiItem.setTitle(JsonResultHandler.getJsonStringValue(obj, "name", ""));
        poiItem.setTel(JsonResultHandler.getJsonStringValue(obj,"telephone",""));
        poiItem.setTypeDes(JsonResultHandler.getJsonStringValue(obj,"type",""));
        poiItem.setExttype(JsonResultHandler.getJsonStringValue(obj,"exttype",""));
        poiItem.setExtinfo(JsonResultHandler.getJsonStringValue(obj,"extinfo",""));
        Log.i(TAG,"extinfo:"+poiItem.getExtinfo());
        poiItem.setDeepinfos(JsonResultHandler.getJsonStringValue(obj,"deep_infos",""));
        if(obj.has("location")){
           JSONObject location = obj.getJSONObject("location");
            Double lng= Double.parseDouble(JsonResultHandler.getJsonStringValue(location,"lng",""));
            Double lat= Double.parseDouble(JsonResultHandler.getJsonStringValue(location,"lat",""));
           poiItem.setLatLonPoint(new LatLonPoint(lat,lng));
        }
    }

//    private String dealExtInfo(String extinfo) {
//    if(!TextUtils.isEmpty(extinfo)){
//        String extinfos = SecurtUtil.unsecret(extinfo,SecurtUtil.key);
//        Log.d(TAG,"extinfo:"+extinfo);
//        return extinfos;
//    }
//    return extinfo;
//    }
}
