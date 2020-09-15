package com.sfmap.library.http.url;



import com.sfmap.library.http.params.ParamEntity;

import org.json.JSONObject;
import org.xidea.el.json.JSONDecoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * 请求处理的注解
 */
public interface URLBuilder {
	/**
	 * 解析url
	 * @param path 注解Path对象
	 * @param cachedFields	所有参数映射 key为参数对象 ,value为Field参数值对象
	 * @param entity		请求参数
	 * @param post			true为post请求否者反之
	 * @throws IllegalAccessException	错误异常类型
     */
	public void parse(Path path, Map<String, Field> cachedFields, ParamEntity entity, boolean post) throws IllegalAccessException;

	/**
	 * 返回url
	 * @return	一个地址字符串
     */
	public String getUrl();

	/**
	 * 返回属性
	 * @return	返回一个请求参数映射map表
     */
	public Map<String, Object> getParams();

	/**
	 * 注解 设置host ,url，URLBuilder(默认 DefaultURLBuilder)
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Path {
		/**
		 * 返回host
		 * @return	一个host字符串
         */
		public String host() default "";

		/**
		 * 返回url
		 * @return	一个url字符串
         */
		public String url();

		/**
		 * 处理参数
		 * @return	继承URLBuilder类类型的对象
         */
		public Class<? extends URLBuilder> builder() default DefaultURLBuilder.class;
	}

	/**
	 * 返回结果的接口
	 */
	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ResultProperty {
		/**
		 * 内容
		 * @return 一个字符串，默认""；
         */
		String value() default "";

		/**
		 * 解析器
		 * @return	继承URLBuilder类类型的对象
         */
		@SuppressWarnings("rawtypes") Class<? extends ResultParser> parser() default DefaultResultParser.class;
	}

	/**
	 * 结果解析接口
	 * @param <Result>
     */
	public interface ResultParser<Result> {
		/**
		 * 将结果解析成对应的内容
		 * @param obj	一个JSONObject对象
         * @return		结果对象
         */
		Result parse(JSONObject obj);
	}

	/**
	 * 默认的解析接口
	 */
	public static class DefaultResultParser implements ResultParser<Object> {

		private Class<?> resultType;

		/**
		 * 构造器
		 * @param resultType
         */
		public DefaultResultParser(Class<?> resultType) {
			this.resultType = resultType;
		}

		/**
		 * 解析方法
		 * @param obj	一个JSONObject对象
         * @return		返回结果数据
         */
		@Override
		public Object parse(JSONObject obj) {
			if (resultType.equals(JSONObject.class)) {
				return obj;
			}
			return JSONDecoder.transform(obj, resultType);
		}
	}

	/**
	 * 默认的参数处理类
	 */
	static class DefaultURLBuilder implements URLBuilder {

		private String url;

		private Map<String, Object> paramsMap;

		/**
		 * 拼接参数
		 * @param path	一个Path类型
		 * @param fields		所有参数映射 key为参数对象 ,value为Field参数值对象
		 * @param entity		请求参数
		 * @param post			true为post请求否者反之
         * @throws IllegalAccessException
         */
		@Override
		public void parse(Path path, Map<String, Field> fields,
		                  ParamEntity entity, boolean post) throws IllegalAccessException {

			url = path.host() + path.url();

			paramsMap = new HashMap<String, Object>();
			if (fields != null) {
				for (Map.Entry<String, Field> entry : fields.entrySet()) {
					Object value = entry.getValue().get(entity);
					if (value != null) {
						paramsMap.put(entry.getKey(), value);
					}
				}
			}

		}

		/**
		 * 返回url
		 * @return	一个字符串
         */
		@Override
		public String getUrl() {
			return url;
		}

		/**
		 * 返回map参数
		 * @return	返回一个请求参数映射map表
         */
		@Override
		public Map<String, Object> getParams() {
			return paramsMap;
		}

	}
}
