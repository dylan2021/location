package com.sfmap.tbt;

import java.util.HashMap;
import java.util.Map;

/**
 * 原类名：r
 * 鉴权请求信息类
 */
class AuthRequest extends Request {
	private Map<String, String> authHeadPrams;
	private String authURL;
	private Map<String, String> httpHeadPrams;

	AuthRequest() {
		this.authHeadPrams = new HashMap();

		this.httpHeadPrams = new HashMap();
	}

	void setAuthHead(Map<String, String> paramMap) {
		this.authHeadPrams.clear();
		this.authHeadPrams.putAll(paramMap);
	}

	void setURL(String paramString) {
		this.authURL = paramString;
	}

	void setHttpHead(Map<String, String> paramMap) {
		this.httpHeadPrams.clear();
		this.httpHeadPrams.putAll(paramMap);
	}

	public String getURL() {
		return this.authURL;
	}

	@Override
	public String getRh() {
		return null;
	}

	public Map<String, String> getAuthHead() {
		return this.authHeadPrams;
	}

	public Map<String, String> getHttpHead() {
		return this.httpHeadPrams;
	}
}