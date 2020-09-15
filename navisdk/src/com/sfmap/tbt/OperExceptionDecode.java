package com.sfmap.tbt;

public class OperExceptionDecode extends Exception {
	private String errorMessage = "未知的错误";
	private int errorCode = -1;

	public OperExceptionDecode(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
		errorString2Code(errorMessage);
	}

	public int getErrorCode() {
		return this.errorCode;
	}

	private void errorString2Code(String paramString) {
		if ("IO 操作异常 - IOException".equals(paramString))
			this.errorCode = 21;
		else if ("socket 连接异常 - SocketException".equals(paramString))
			this.errorCode = 22;
		else if ("socket 连接超时 - SocketTimeoutException".equals(paramString))
			this.errorCode = 23;
		else if ("无效的参数 - IllegalArgumentException".equals(paramString))
			this.errorCode = 24;
		else if ("空指针异常 - NullPointException".equals(paramString))
			this.errorCode = 25;
		else if ("url异常 - MalformedURLException".equals(paramString))
			this.errorCode = 26;
		else if ("未知主机 - UnKnowHostException".equals(paramString))
			this.errorCode = 27;
		else if ("服务器连接失败 - UnknownServiceException".equals(paramString))
			this.errorCode = 28;
		else if ("协议解析错误 - ProtocolException".equals(paramString))
			this.errorCode = 29;
		else if ("http连接失败 - ConnectionException".equals(paramString))
			this.errorCode = 30;
		else if ("未知的错误".equals(paramString))
			this.errorCode = 31;
		else if ("key鉴权失败".equals(paramString))
			this.errorCode = 32;
		else if ("requeust is null".equals(paramString))
			this.errorCode = 1;
		else if ("request url is empty".equals(paramString))
			this.errorCode = 2;
		else if ("response is null".equals(paramString))
			this.errorCode = 3;
		else if ("thread pool has exception".equals(paramString))
			this.errorCode = 4;
		else if ("sdk name is invalid".equals(paramString))
			this.errorCode = 5;
		else if ("sdk info is null".equals(paramString))
			this.errorCode = 6;
		else if ("sdk packages is null".equals(paramString)) {
			this.errorCode = 7;
		} else if ("线程池为空".equals(paramString)) {
			this.errorCode = 8;
		} else if ("获取对象错误".equals(paramString)) {
			this.errorCode = 101;
		} else
			this.errorCode = -1;
	}
}