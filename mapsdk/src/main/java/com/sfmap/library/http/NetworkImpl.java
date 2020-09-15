package com.sfmap.library.http;



import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sfmap.library.Callback;
import com.sfmap.library.http.params.ParamEntity;
import com.sfmap.library.http.params.RequestParams;
import com.sfmap.library.http.url.URLBuilder;
import com.sfmap.library.http.url.URLBuilderFactory;
import com.sfmap.library.task.Priority;
import com.sfmap.library.task.TaskHandler;
import com.sfmap.library.util.DesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

//import dalvik.system.VMStack;

/**
 */
public class NetworkImpl extends NetworkState implements Network {
	private String TAG = NetworkImpl.class.getSimpleName();
	private NetworkImpl() {

	}

	private volatile static NetworkImpl instance; //声明成 volatile
	private final static ConcurrentHashMap<ParamEntity, HttpAsyncTask<?>> TASK_MAP = new ConcurrentHashMap<ParamEntity, HttpAsyncTask<?>>();

	/**
	 * 返回Network实现类的单例
	 * @return
     */
	public static NetworkImpl getInstance() {
		if (instance == null) {
			synchronized (NetworkImpl.class) {
				if (instance == null) {
					instance = new NetworkImpl();
				}
			}
		}
		return instance;
	}

	/**
	 * @deprecated
	 * @param callback	Callback回调
	 * @param url		路径url
	 * @param params	参数map映射
	 * @param priority 	优先级
	 * @param <T>
     * @return
     */
	@Override
	public <T> TaskHandler get(Callback<T> callback, String url, Map<String, Object> params, Priority priority) {
		RequestParams requestParams;
		if(url.contains("search")){
			params.remove("ak");
			params.put("appPackageName","com.sfmap.map.internal");
			params.put("appCerSha1","CD:B6:21:64:52:78:42:74:88:E0:0F:03:C9:32:48:ED:5F:74:83:31");
			String map= JSON.toJSONString(params);
			String requestParam= "";
			try {
				requestParam = new DesUtil().encrypt(map);
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
			Map<String,Object> param = new HashMap<>();
			param.put("param",requestParam);
			param.put("ak","1aca21cfd4204548bad2fd32dca24b8b");
			Log.d(TAG,map);

			 requestParams = MapParamsConverter.map2Params(callback, param, true);
		}else {
			 requestParams = MapParamsConverter.map2Params(callback, params, true);
		}

//		if(url.contains("https://gis.sit.sf-express.com:45001/rpmp/navi/binary")){
//			requestParams.addHeader("ak","0376a9aa84724dd2a995f858dd963346");
//			Log.i("==================","Head 添加ak");
//		}

		HttpAsyncTask<T> task = new HttpAsyncTask<T>(url, HttpMethod.GET, requestParams, callback);
		Log.e("===================", "networkimpl \n"+task.toString());
		Executor executor = null;
		if (requestParams != null) {
			executor = requestParams.getExecutor();
			task.setPriority(requestParams.getPriority());
		}
		if (priority != null) {
			task.setPriority(priority);
		}
		if (executor != null) {
			return task.executeOnExecutor(executor);
		} else {
			return task.execute();
		}
	}

	/**
	 * @deprecated
	 * @param callback	回调
	 * @param paramsEntity	参数模型
	 * @param priority		参数map映射
	 * @param <T>
     * @return
     */
	@Override
	public <T> Callback.Cancelable get(Callback<T> callback, ParamEntity paramsEntity, Priority priority) {
		Iterator<Map.Entry<ParamEntity, HttpAsyncTask<?>>> iterator = TASK_MAP.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<ParamEntity, HttpAsyncTask<?>> entry = iterator.next();
			if (entry.getValue().isStopped()) {
				iterator.remove();
			}
		}
		HttpAsyncTask<T> task = (HttpAsyncTask<T>) TASK_MAP.get(paramsEntity);
		if (task != null && task.replaceCallback(callback)) {
			return task;
		}

		URLBuilder parser = URLBuilderFactory.build(paramsEntity, false);
		return this.get(callback, parser.getUrl(), parser.getParams(), priority);
	}

	/**
	 * @deprecated
	 * @param callback	Callback回调
	 * @param url		路径url
	 * @param params	参数map映射
	 * @param priority 	优先级
	 * @param <T>
     * @return
     */
	@Override
	public <T> TaskHandler post(Callback<T> callback, String url, Map<String, Object> params, Priority priority) {
		RequestParams requestParams = MapParamsConverter.map2Params(callback, params, false);
		HttpAsyncTask<T> task = new HttpAsyncTask<T>(url, HttpMethod.POST, requestParams, callback);
		Executor executor = null;
		if (requestParams != null) {
			executor = requestParams.getExecutor();
			task.setPriority(requestParams.getPriority());
		}
		if (priority != null) {
			task.setPriority(priority);
		}
		if (executor != null) {
			return task.executeOnExecutor(executor);
		} else {
			return task.execute();
		}
	}

	/**
	 * @deprecated
	 * @param callback	Callback回调
	 * @param paramsEntity	参数模型
	 * @param priority		优先级
	 * @param <T>
     * @return
     */
	@Override
	public <T> Callback.Cancelable post(Callback<T> callback, ParamEntity paramsEntity, Priority priority) {
		URLBuilder parser = URLBuilderFactory.build(paramsEntity, true);
		return this.post(callback, parser.getUrl(), parser.getParams(), priority);
	}

	/**
	 * @deprecated
	 * @param url		路径url
	 * @param params	参数map映射
	 * @return
	 * @throws IOException
     */
	@Override
	public String loadText(String url, Map<String, Object> params) throws IOException {
//		RequestParams requestParams = MapParamsConverter.map2Params(null, params, true);
//		ClassLoader callingClassLoader = VMStack.getCallingClassLoader();
//		ResponseStream sendRequest = new SyncHttpHandler(url, HttpMethod.GET, requestParams, callingClassLoader).sendRequest();
//		return sendRequest == null ? null : sendRequest.readString();
		return null;
	}

	/**
	 * @deprecated
	 * @param url		路径url
	 * @param params	参数map映射
	 * @return
	 * @throws IOException
     */
	@Override
	public InputStream loadStream(String url, Map<String, Object> params) throws IOException {
//		RequestParams requestParams = MapParamsConverter.map2Params(null, params, true);
//		ClassLoader callingClassLoader = VMStack.getCallingClassLoader();
//		ResponseStream sendRequest = new SyncHttpHandler(url, HttpMethod.GET, requestParams, callingClassLoader).sendRequest();
//		return sendRequest == null ? null : sendRequest.getBaseStream();
		return null;
	}

	/**
	 * @deprecated
	 * @param url			路径url
	 * @param params		参数map映射
	 * @return
	 * @throws IOException
     */
	@Override
	public byte[] loadBytes(String url, Map<String, Object> params) throws IOException {
//		RequestParams requestParams = MapParamsConverter.map2Params(null, params, true);
//		ClassLoader callingClassLoader = VMStack.getCallingClassLoader();
//		ResponseStream sendRequest = new SyncHttpHandler(url, HttpMethod.GET, requestParams, callingClassLoader).sendRequest();
//		return sendRequest == null ? null : sendRequest.readAllBytes();
		return null;
	}

	/**
	 * @deprecated
	 * @param url			路径url
	 * @param params		参数map映射
	 * @return
	 * @throws IOException
     */
	@Override
	public String loadCacheText(String url, Map<String, Object> params) throws IOException {
//		RequestParams requestParams = MapParamsConverter.map2Params(null, params, true);
//		if (requestParams == null) {
//			requestParams = new RequestParams();
//		}
//		requestParams.setCachePolicy(Callback.CachePolicyCallback.CachePolicy.CacheOnly);
//		ClassLoader callingClassLoader = VMStack.getCallingClassLoader();
//		ResponseStream sendRequest = new SyncHttpHandler(url, HttpMethod.GET, requestParams, callingClassLoader).sendRequest();
//		return sendRequest == null ? null : sendRequest.readString();
		return null;
	}

	/**
	 * @deprecated
	 * @param url			路径url
	 * @param params		参数map映射
	 * @return
	 * @throws IOException
     */
	@Override
	public InputStream loadCacheStream(String url, Map<String, Object> params) throws IOException {
//		RequestParams requestParams = MapParamsConverter.map2Params(null, params, true);
//		if (requestParams == null) {
//			requestParams = new RequestParams();
//		}
//		requestParams.setCachePolicy(Callback.CachePolicyCallback.CachePolicy.CacheOnly);
//		ClassLoader callingClassLoader = VMStack.getCallingClassLoader();
//		ResponseStream sendRequest = new SyncHttpHandler(url, HttpMethod.GET, requestParams, callingClassLoader).sendRequest();
//		return sendRequest == null ? null : sendRequest.getBaseStream();
		return null;
	}

	/**
	 * @deprecated
	 */
	/*package*/ static Callback<Object> globalRequestCallback;

	/**
	 * @deprecated
	 * @param callback	结果回调
     */
	@Override
	public void setGlobalRequestCallback(Callback<Object> callback) {
		globalRequestCallback = callback;
	}
}
