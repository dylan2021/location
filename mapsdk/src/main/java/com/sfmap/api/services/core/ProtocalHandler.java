package com.sfmap.api.services.core;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
/**
 * ProtocalHandler
 * 
 * <p>
 * Base class with code to manage the network connecting
 * 
 * @author
 * @version 1.0 2010.01.22<br>
 * 
 */
public  abstract class ProtocalHandler<T, V> {
	private String TAG = ProtocalHandler.class.getSimpleName();
	public ProtocalHandler(Context context, T tsk, Proxy prx, String device) {
		this.context=context;
		proxy = prx;
		task = tsk;
		maxTry = HttpTool.MaxTry;
		timeoutSeconds = HttpTool.TimeoutSeconds;
		waitSeconds = HttpTool.WaitSeconds;
		mAgent = device;
		mKey = AppInfo.getSystemAk(context);
		this.scode = AppInfo.getScode(context);
	}

	public ProtocalHandler(Context context, Proxy prx, String device) {
		this.context=context;
		proxy = prx;
		maxTry = HttpTool.MaxTry;
		timeoutSeconds = HttpTool.TimeoutSeconds;
		waitSeconds = HttpTool.WaitSeconds;
		mAgent = device;
		mKey = AppInfo.getSystemAk(context);
		this.scode = AppInfo.getScode(context);
	}

	/*****************************************************************************/
	// Class Methods
	/*****************************************************************************/
	public void setTask(T tsk) {
		task = tsk;
	}

	/**
	 * 是否为get请求
	 * 
	 * @return true get,false post
	 */
	abstract protected boolean getRequestType();

	abstract protected int getServiceCode();

	abstract protected String[] getRequestLines();

	abstract protected V loadData(InputStream inputStream)
			throws SearchException;

	abstract protected String getUrl();

	protected byte[] makePostRequestBytes() {
		//post请求
//		String[] params=getRequestLines();
		StringBuilder sb = new StringBuilder(makeJSONRequest());
		Log.d(TAG,sb.toString());
		if (!sb.toString().equals("")) {
			sb.append("&");
		}
		getKeyAndScode(sb);
		return sb.toString().getBytes();
	}

	private String makeJSONRequest() {
		String[] lines = getRequestLines();
		if (lines == null) {
			return "";
		}
		StringBuilder request = new StringBuilder();
		if (lines != null) {
			for (String line : lines) {
				request.append(line);
			}
		}
		Log.d(TAG,request.toString());
		return request.toString();
	}

	public V GetData() throws SearchException {
		V result = null;
		if (null != task) {
			result = GetDataMayThrow();
		}
		return result;
	}

	private V GetDataMayThrow() throws SearchException {
		HttpURLConnection conn = null;
		InputStream input = null;
		OutputStream output = null;
		V result = null;
		int trytime = 0;
		while (trytime < maxTry) {
			try {
				// 表示post请求
				if (getRequestType() == false) {
					recommandURL = getUrl();
					byte[] entityBytes = makePostRequestBytes();
					conn = HttpTool.makePostRequest(recommandURL, entityBytes,
							proxy);
				} else { // get 请求
					StringBuilder sb = new StringBuilder(makeJSONRequest());
//					if (!sb.toString().equals("")) {
//						sb.append("&");
//					}
//					getKeyAndScode(sb);

//					String ak = "1aca21cfd4204548bad2fd32dca24b8b";

					if(getUrl().contains("search")){  //临时处理 需要跟逆地理编码加密一致
						String ak = AppInfo.getMetaValue(context, AppInfo.CONFIG_MAP_API_KEY);
						String params = dealReqest(sb.toString());
						recommandURL = getUrl() + "param="+ params +"&ak="+ak;
						Log.d(TAG,recommandURL);
						conn = HttpTool.makeGetRequest(recommandURL, proxy);

						input = sendRequest(conn);

						String errMsgDecrypted = "";
						try {
							String str = convertStreamToString(input);
							Log.d(TAG,str);
							errMsgDecrypted = new DesUtil().decrypt(str.trim(), "UTF-8");
							Log.d(TAG,errMsgDecrypted);
						} catch (IOException e) {
							e.printStackTrace();
						}catch (IllegalBlockSizeException e) {
							e.printStackTrace();
						} catch (BadPaddingException e) {
							e.printStackTrace();
						}

						result = parseResponse(new ByteArrayInputStream(errMsgDecrypted.getBytes()));
					}else {
//						if (!sb.toString().equals("")) {
//							sb.append("&");
//						}
//						getKeyAndScode(sb);
						Log.d(TAG,sb.toString());
						conn = HttpTool.makePostRequest(getUrl(),sb.toString().getBytes(),proxy);

						input = sendRequest(conn);

						result = parseResponse(input);
					}
				}

//				result = parseResponse(input);
				trytime = maxTry;
			} catch (SearchException ex) {
				trytime++;
				if (trytime < maxTry) {
					try {
						Thread.sleep(waitSeconds * 1000);
					} catch (InterruptedException e) {
						throw new SearchException(e.getMessage());
					}
				} else {
					result = onExceptionOccur();
					throw new SearchException(ex.getErrorMessage());
				}
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						throw new SearchException(SearchException.ERROR_IO);
					}
					input = null;
				}
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						throw new SearchException(SearchException.ERROR_IO);
					}
					output = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			}
		}
		return result;
	}

	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public String dealReqest(String paramStr){
		//String paramStr = "a=a1&b=b1&c=c1";
		String requestParams = "";
		String[] params = paramStr.split("&");
		JSONObject obj = new JSONObject();
		for (int i = 0; i < params.length; i++) {
			String[] param = params[i].split("=");
			if (param.length >= 2) {
				String key = param[0];
				String value = param[1];
				for (int j = 2; j < param.length; j++) {
					value += "=" + param[j];
				}
				try {
					if(!key.equals("ak")){
						obj.put(key,value);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			obj.put("appPackageName",AppInfo.getPackageName(context));
			obj.put("appCerSha1",AppInfo.getSHA1(context));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			Log.d(TAG,obj.toString());
			requestParams = new DesUtil().encrypt(obj.toString());
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return requestParams;
	}

	private void getKeyAndScode(StringBuilder sb) {
		if (sb != null) {
			sb.append("ak=").append(this.mKey);
//			sb.append("&scode=").append(this.scode);
		}
	}

	protected InputStream sendRequest(HttpURLConnection conn)
			throws SearchException {
		InputStream inStream = null;
		try {
			inStream = conn.getInputStream();
		} catch (ProtocolException e) {
			throw new SearchException(SearchException.ERROR_PROTOCOL);
		} catch (UnknownHostException e) {
			throw new SearchException(SearchException.ERROR_UNKNOW_HOST);
		} catch (UnknownServiceException e) {
			throw new SearchException(SearchException.ERROR_UNKNOW_SERVICE);
		} catch (IOException e) {
			throw new SearchException(SearchException.ERROR_IO);
		}
		return inStream;
	}

	private V parseResponse(InputStream inputStream) throws SearchException {
		return loadData(getStreamData(inputStream));
		// return loadData(inputStream);
	}

	protected void protocalAssert(boolean carteiea) throws IOException {
		if (!carteiea) {
			throw new IOException();
		}
	}

	protected V onExceptionOccur() {
		return null;
	}

	public int getError() {
		return error;
	}

	protected String makeTag(String tagName, boolean head) {
		String fmt = head ? "<%s>" : "</%s>";
		return String.format(fmt, tagName);
	}

	protected String makeContentLine(String tag, String content) {
		if (content == null) {
			content = "";
		}
		String line = makeTag(tag, true);
		line += content;
		line += makeTag(tag, false);
		return line;
	}

	private static InputStream getStreamData(InputStream is)
			throws SearchException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream arrayInputStream = null;
		byte[] bufferByte = new byte[2048];
		int readed = -1;
		// int downloadSize = 0;
		// byte[] btemp = null;
		try {
			while ((readed = is.read(bufferByte)) > -1) {
				// downloadSize += readed;
				out.write(bufferByte, 0, readed);
				out.flush();
			}
			arrayInputStream = new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new SearchException(SearchException.ERROR_IO);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					throw new SearchException(SearchException.ERROR_IO);
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new SearchException(SearchException.ERROR_IO);
				}
			}
		}
		return arrayInputStream;
	}

	protected byte[] getBytes(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int iReadByt = -1;
		try {
			while ((iReadByt = inputStream.read()) != -1) {
				bytestream.write(iReadByt);
			}
		} catch (IOException ex) {
		}
		byte imgdata[] = bytestream.toByteArray();
		try {
			bytestream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bytestream = null;
		return imgdata;
	}

	protected int getByte(InputStream inputStream) throws IOException {
		return inputStream.read();
	}

	protected int getInt(InputStream inputStream) throws IOException {
		byte[] b = new byte[4];
		inputStream.read(b, 0, 4);

		int result = ((b[3] & 0xff) << 24) + ((b[2] & 0xff) << 16)
				+ ((b[1] & 0xff) << 8) + (b[0] & 0xff);
		return result;
	}

	protected int getShort(InputStream inputStream) throws IOException {
		byte[] b = new byte[2];
		inputStream.read(b, 0, 2);

		int result = ((b[1] & 0xff) << 8) + (b[0] & 0xff);
		return result;
	}

	protected String getString(InputStream inputStream) throws IOException {
		return readString(inputStream, getShort(inputStream));
	}

	protected String getIntString(InputStream inputStream) throws IOException {
		return readString(inputStream, getInt(inputStream));
	}

	protected String get1BString(InputStream inputStream) throws IOException {
		return readString(inputStream, getByte(inputStream));
	}

	private String readString(InputStream inputStream, int len)
			throws IOException {
		byte[] b = new byte[len];
		try {
			int cnt = 0;
			while (cnt < len) {
				cnt += inputStream.read(b, cnt, len - cnt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(b, "utf-8");
	}

	protected String readString(InputStream inputStream) throws SearchException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = (inputStream.read(buffer))) != -1) {
				outStream.write(buffer, 0, length);
				outStream.flush();
			}
		} catch (IOException e) {
			throw new SearchException(SearchException.ERROR_IO);
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					throw new SearchException(SearchException.ERROR_IO);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new SearchException(SearchException.ERROR_IO);
				}
			}
		}
		try {
			return new String(outStream.toByteArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new String();
	}

	public String getmKey() {
		return mKey;
	}

	public void setmKey(String mKey) {
		this.mKey = mKey;
	}

	public String getScode() {
		return scode;
	}

	public void setScode(String scode) {
		this.scode = scode;
	}

	/******************************************************************************/
	// Class Members
	/******************************************************************************/
	protected Proxy proxy;
	protected T task;
	protected int maxTry = 1;
	protected int timeoutSeconds = 20;
	protected int waitSeconds = 0;
	protected int error = 0;
	protected String mKey = "";
	protected String scode = "";
	// private final String OGTail = "</og>";
	protected Context context;
	protected String mAgent;
	protected String recommandURL = "";// ServerURL;
	public static final int IOError = -999;
	public static final int SocketError = -1000;
	public static final int NoError = 0;
}
