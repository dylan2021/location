package com.sfmap.tbt;

import java.util.Map;

/**
 * 原类名称:c
 */
public class TBTRequest extends Request {
	Map<String, String> a;
	Map<String, String> b;
	String c;
	byte[] d;
	String e;

	public TBTRequest() {
		this.a = null;
		this.b = null;
		this.c = "";
		this.d = null;
	}

	public void setParameterMap(Map<String, String> paramMap) {
		this.a = paramMap;
	}

	public Map<String, String> getAuthHead() {
		return this.a;
	}

	public Map<String, String> getHttpHead() {
		return this.b;
	}

	public String getURL() {
		return this.c;
	}

	public String getRh() {
		return this.e;
	}

	public void a(String paramString) {
		this.c = paramString;
	}

	public byte[] d() {
		return this.d;
	}

	public void a(byte[] paramArrayOfByte) {
		this.d = paramArrayOfByte;
	}

	public void b(String e) {
		this.e = e;
	}
}