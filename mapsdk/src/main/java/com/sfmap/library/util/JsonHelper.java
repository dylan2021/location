package com.sfmap.library.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * json工具类
 */
public class JsonHelper {

    static String NULL_STR = "null";

    /**
     * 将key-value映射加入到json中
     * @param json  一个JSONObject对象
     * @param name  key值
     * @param value value值(字符型)
     */
    public static void putJsonStr(JSONObject json, String name, String value) {
        if (json == null)
            return;
        if (name == null || name.length() == 0)
            return;
        if (value == null || value.length() == 0) {
            value = NULL_STR;// 用这个表示 避免 错误
        }
        try {
            json.put(name, value);
        } catch (JSONException ex) {
            // 键为null或使用json不支持的数字格式(NaN, infinities)
            // throw new RuntimeException(ex);
        }
    }

    /**
     * 将key-value映射加入到json中
     * @param json  一个JSONObject对象
     * @param name  key值
     * @param value value值(整型)
     */
    public static void putJsonStr(JSONObject json, String name, int value) {
        if (json == null)
            return;
        if (name == null || name.length() == 0)
            return;

        try {
            json.put(name, value + "");
        } catch (JSONException ex) {
            // 键为null或使用json不支持的数字格式(NaN, infinities)
            // throw new RuntimeException(ex);
        }
    }

    /**
     * 将key-value映射加入到json中
     * @param json  一个JSONObject对象
     * @param name  key值
     * @param value value值(boolean型)
     */
    public static void putJsonBoolean(JSONObject json, String name,
                                      boolean value) {
        if (json == null)
            return;
        if (name == null || name.length() == 0)
            return;
        try {
            json.put(name, value);
        } catch (JSONException ex) {
            // 键为null或使用json不支持的数字格式(NaN, infinities)
            // throw new RuntimeException(ex);
        }
    }

    /**
     * 返回一个json的值
     * @param json  一个JSONObject对象
     * @param name  key值
     * @return      value值(整型)
     */
    public static int getJsonInt(JSONObject json, String name) {
        String tmp = "";
        if (json == null)
            return -1;
        if (name == null || name.length() == 0)
            return -1;
        try {
            tmp = json.getString(name);
            if (tmp.equals(NULL_STR))
                return -1;
            int index = 0;
            try {
                index = Integer.parseInt(tmp);
                return index;
            } catch (Exception e) {
                return index;
            }
        } catch (JSONException ex) {
            return -1;
        }

    }

    /**
     * 返回一个json的值
     * @param json  一个JSONObject对象
     * @param name  key值
     * @return      value值(字符型)
     */
    public static boolean getJsonBoolean(JSONObject json, String name) {
        Boolean tmp = false;
        if (json == null)
            return tmp;
        if (name == null || name.length() == 0)
            return tmp;
        try {
            tmp = json.getBoolean(name);
            return tmp;
        } catch (JSONException ex) {
            return tmp;
        }

    }

    /**
     * 将字符串的json转换为字符串集合
     * @param jsonArrayStr  json字符串
     * @return 字符串集合
     */
    public static ArrayList<String> JsonArrayStrToArrayList(String jsonArrayStr) {
        ArrayList<String> tmp = null;

        if (jsonArrayStr == null || jsonArrayStr.length() == 0)
            return tmp;
        try {
            JSONArray jArray = new JSONArray(jsonArrayStr);
            int len = 0;
            if (jArray == null || (len = jArray.length()) == 0)
                return tmp;
            tmp = new ArrayList<String>();
            for (int i = 0; i < len; i++) {
                tmp.add(jArray.getString(i));
            }

            return tmp;
        } catch (JSONException ex) {
            return tmp;
        }

    }

    /**
     * 将字符串集合转为json的字符串
     * @param list  字符串集合
     * @return  字符串
     */
    public static String ArraylistToJsonArrayStr(ArrayList<String> list) {
        String jsonArrayStr = null;
        if (list == null || list.size() == 0)
            return jsonArrayStr;
        JSONArray jArray = new JSONArray(list);
        if (jArray == null || jArray.length() == 0)
            return jsonArrayStr;
        jsonArrayStr = jArray.toString();
        return jsonArrayStr;

    }

    /**
     *  返回对应json的String
     * @param json  一个JSONObject对象
     * @param name  key值
     * @return      返回一个String值
     */
    public static String getJsonStr(JSONObject json, String name) {
        String tmp = "";
        if (json == null)
            return null;
        if (name == null || name.length() == 0)
            return null;
        try {
            tmp = json.getString(name);
            if (tmp.equals(NULL_STR))
                return "";

            return tmp;
        } catch (JSONException ex) {
            return tmp;
        }

    }

    /**
     *  返回key值下JSONArray
     * @param json  一个JSONObject对象
     * @param name  key值
     * @return      返回一个JSONArray
     */
    public static JSONArray getJsonArray(JSONObject json, String name) {
        JSONArray ja = null;

        if (json == null)
            return null;
        if (name == null || name.length() == 0)
            return null;
        try {
            ja = json.getJSONArray(name);

            return ja;
        } catch (JSONException ex) {
            return ja;
        }
    }
}
