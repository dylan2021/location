package com.sfmap.tbt;

/**
 * brief     定义事件信息结构体
 */

public class RouteIncident {
	public float x; // /< 经度
	public float y; // /< 纬度
	public String title = null; // /< 标题
	public String desc; // /< 描述

	public int type; // /< 事件类型
	public int priority; // /< 优先级，未使用

	public String getIncidentDesc() {
		if(null == title || title.length() == 0){
			return null;
		}
		if(title.endsWith(".") || title.endsWith("。")|| title.endsWith(",")|| title.endsWith("，")){ //屏蔽掉末尾的标点符号
			return title.substring(0, title.length()-1)+",已为您绕行。";
		}
		return title+",已为您绕行。";
	}
}