package com.sfmap.library.http;


import com.sfmap.library.Callback;
import com.sfmap.library.http.params.ParamEntity;
import com.sfmap.library.task.Priority;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 网络请求工具类.
 * <p/>
 * 主要功能: 发起同步, 异步网络请求, 判断网络类型和连接状态等.
 *
 *
 */
public interface Network {
	/**
	 * 发起异步get请求，可以在任何线程中使用.(使用非注解请求)
	 * callback将在ui线程回调，可以直接操作ui.
	 *
	 * @param callback	Callback回调
	 * @param url		路径url
	 * @param params	参数map映射
	 * @param priority 	优先级
	 */
	public <T extends Object> Callback.Cancelable get(Callback<T> callback, String url, Map<String, Object> params, Priority priority);

	/**
	 * 发起异步get请求,可以在任何线程中使用(使用注解请求)
	 * @param callback	回调
	 * @param paramsEntity	参数模型
	 * @param priority		参数map映射
     * @return	Callback.Cancelabl	类型回调函数
     */
	public <T extends Object> Callback.Cancelable get(Callback<T> callback, ParamEntity paramsEntity, Priority priority);

	/**
	 * 发起异步post请求，可以在任何线程中使用.
	 * callback将在ui线程回调，可以直接操作ui.
	 * @param callback	Callback回调
	 * @param url		路径url
	 * @param params	参数map映射
	 * @param priority 	优先级
	 */
	public <T extends Object> Callback.Cancelable post(Callback<T> callback, String url, Map<String, Object> params, Priority priority);

	/**
	 * 发起异步post请求,可以在任何线程中使用
	 * @param callback	Callback回调
	 * @param paramsEntity	参数模型
	 * @param priority		优先级
     * @return
     */
	public <T extends Object> Callback.Cancelable post(Callback<T> callback, ParamEntity paramsEntity, Priority priority);

	/**
	 * 同步加载字符串
	 * <p/>
	 * 注意: 必需在非UI线程中调用
	 * @param url		路径url
	 * @param params	参数map映射
	 * @return			文件信息
	 * @throws IOException	抛出异常
	 */
	public String loadText(String url, Map<String, Object> params) throws IOException;

	/**
	 * 同步加载网络流
	 * <p/>
	 * 注意: 对网络流的处理要在非UI线程中执行
	 *
	 * @param url		路径url
	 * @param params	参数map映射
	 * @return			文件内容流信息
	 * @throws IOException	抛出异常
	 */
	public InputStream loadStream(String url, Map<String, Object> params) throws IOException;

	/**
	 * 同步加载字节数组
	 * <p/>
	 * 注意: 必需在非UI线程中调用
	 *
	 * @param url			路径url
	 * @param params		参数map映射
	 * @return				字节数组
	 * @throws IOException	抛出异常
	 */
	public byte[] loadBytes(String url, Map<String, Object> params) throws IOException;

	/**
	 * 从缓存中加载字符串
	 * <p/>
	 * 注意: 可能比较耗时, 尽量不要在UI线程使用.
	 *
	 * @param url			路径url
	 * @param params		参数map映射
	 * @return				字符串信息
	 * @throws IOException	抛出异常
	 */
	public String loadCacheText(String url, Map<String, Object> params) throws IOException;

	/**
	 * 同步加载输入流
	 * <p/>
	 * 注意: 必需在非UI线程中调用
	 *
	 * @param url			路径url
	 * @param params		参数map映射
	 * @return				流信息
	 * @throws IOException	抛出异常
	 */
	public InputStream loadCacheStream(String url, Map<String, Object> params) throws IOException;


	/**
	 * 添加wifi状态改变的回调接口
	 *
	 * @param callback wifi状态改变时回调callback#callback(wifiEnabled)
	 * @param page     关联的UI对象, 当page被销毁时自动取消关联的callback.
	 */
	public void addWifiCallback(Callback<Boolean> callback, Object page);

	/**
	 * 添加网络连接状态改变的回调接口
	 *
	 * @param callback 网络连接状态改变时回调callback#callback(wifiEnabled)
	 * @param page     关联的UI对象, 当page被销毁时自动取消关联的callback.
	 */
	public void addConnectedCallback(Callback<Boolean> callback, Object page);

	/**
	 * wifi是否连接
	 * @return true连接否者反之
     */
	public boolean isWifiConnected();

	/**
	 * 网络是否连接
	 * @return true连接否者反之
     */
	public boolean isInternetConnected();

	/**
	 * 获取移动网络类型
	 *
	 * @return -1: 未知网络; 2: 2G网络; 3: 3G网络: 4: 4G网络
	 */
	public int getMobileGeneration();

	/**
	 * 设置全局网络请求回调
	 * @param callback	结果回调
	 */
	public void setGlobalRequestCallback(Callback<Object> callback);

}