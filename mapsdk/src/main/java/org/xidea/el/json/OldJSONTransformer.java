package org.xidea.el.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class OldJSONTransformer extends JSONTransformer {
    static Field getMap;
    static Field getList;
    static{
	Field[] fileds = JSONObject.class.getDeclaredFields();
	for(Field f:fileds){
	    if(Map.class.isAssignableFrom(f.getType())){
		f.setAccessible(true);
		getMap = f;
	    }
	}
	fileds = JSONArray.class.getDeclaredFields();
	for(Field f:fileds){
	    if(List.class.isAssignableFrom(f.getType())){
		f.setAccessible(true);
		getList = f;
	    }
	}
	if(getMap == null || getList == null){
	    throw new InstantiationError("JSON 实现代码 格式异常，找不到内部存储的真实List和Map");
	}
    }
	protected Object require(Object value, Type type) throws Exception {
	    if(value instanceof JSONObject){
		value = getMap.get(value);
	    }else if(value instanceof JSONArray){
		value = getList.get(value);
	    }else if(value == JSONObject.NULL){
		value = null;
	    }
	    return super.require(value,type);
	}
}
