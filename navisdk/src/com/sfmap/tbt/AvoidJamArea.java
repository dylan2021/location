package com.sfmap.tbt;

public class AvoidJamArea {
	public float x; // 经度
	public float y; // 纬度
	public String roadName; // 路名
	public int length; //  长度
	/**
	 * 0 未知状态 1 光柱状态 畅通状态 2 光柱状态 缓行状态 3 光柱状态 阻塞严重状态 4 超级严重阻塞状态
	 */
	public int state; // /< 拥堵程度
	public int priority; // /< 优先级，未使用

	/**
	 * 获取当前躲避拥堵信息
	 * @return
	 */
	public String getAvoidJamDesc() {
		StringBuilder sbr = new StringBuilder();
		sbr.append(roadName).append("有").append(getLengDesc(length));
		switch (state) {
			case 0:
			case 1:
				return null;
			case 2:
				sbr.append("缓行，已为您绕行。");
				break;
			case 3:
				sbr.append("拥堵，已为您绕行。");
				break;
			case 4:
				sbr.append("严重拥堵，已为您绕行。");
				break;
			default:
				return null;
		}
		return sbr.toString();
	}

	public static String getLengDesc(int meter) {
		if (meter < 1000)
			return meter + "米";
		int kiloMeter = meter / 1000;
		int leftMeter = meter % 1000;
		leftMeter = leftMeter / 100;
		String rs = kiloMeter + "";
		if (leftMeter > 0) {
			rs += "." + leftMeter + "公里";
		} else {
			rs += "公里";
		}
		return rs;
	}
}
