package com.sfmap.library.io;

import android.content.ContentValues;
import android.content.Context;

import com.sfmap.library.Callback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * 描述一个java类到sqlite的存储映射， 通过 {@link com.sfmap.library.io.StorageFactory#getSQLiteStorage(Class, Context)}自动实现。
 * 
 * <pre>
 *  数据库名：TestBeam，表结构：[id(int),name(string),description(string)]
 * 
 * 	var mapper = ishowmap.getSQLiteMapper('com.ishowmap.vmap.TestBeam');
 * 
 *  // 简单的通过主键活的数据
 *  //同步
 *  var item = mapper.get(1);
 *  //异步
 *  mapper.get(function (item){
 *  	console.log(JSON.stringify(item));//output: {"id":1,"name":"....","description":"....."}
 *  })
 *  
 *  //按key查询
 *  //同步
 *  var item = mapper.getByKey("name","dawei.jin");
 *  //异步
 *  mapper.getByKey(function(item){
 *  	console.log('get:'+item.name);
 *  },"name","dawei.jin");
 *  
 *  //sqlite 查询
 *  var items = mapper.query("name like ?% AND description is NOT NULL","dawei.")；
 *  mapper.query(function(item){
 *  	console.log('query:'+item.name)
 *  },"name like ?% AND description is NOT NULL","dawei.");
 *  
 *  //sqlite 删除
 *  var success = mapper.delete(1);
 *  
 *  //存储【可暂不提供】
 *  var item = {name:'dawei.jin',description:'....'};
 *  //同步
 *  var item = mapper.save(item);//item.id = [auto generated id]
 *  //异步
 *  mapper.save(function(item){
 *  	console.log('saved:'+item.name+';generated id:'+item.id)
 *  },item);
 * </pre>
 * 
 * @param <T>
 */
public interface SQLiteMapper<T> {

	/**
	 * 通过主键获取对象
	 * 
	 * @param id
	 * @return
	 */
	public abstract T get(Object id);

	/**
	 * 通过主键获取对象,对象以回调的方式返回
	 * @param receiver
	 * @param id
     * @return
     */
	public abstract SQLiteMapper<T> get(Callback<T> receiver, Object id);

	/**
	 * 
	 * 通过任意字段获取第一个匹配对象
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public abstract T getByKey(String field, Object value);

	/**
	 * 通过任意字段获取第一个匹配对象,对象以回调的方式返回
	 * @param receiver
	 * @param field
	 * @param value
     * @return
     */
	public abstract SQLiteMapper<T> getByKey(Callback<T> receiver,
											 String field, Object value);

	/**
	 * 通过where 语句 查找匹配的全部对象
	 * @param where
	 * @param selectionArgs
     * @return
     */
	public abstract List<T> query(String where, Object... selectionArgs);

	/**
	 * 通过where 语句 查找匹配的全部对象,对象以回调方式返回
	 * @param callback
	 * @param where
	 * @param selectionArgs
     * @return
     */
	public abstract SQLiteMapper<T> query(Callback<List<T>> callback,
										  String where, Object... selectionArgs);

	
	/**
	 * @return 数据库表中的记录总数
	 */
	public abstract int count();
	
	
	/**
	 * 条件计数
	 * @param where where 语句  
	 * @param selectionArgs 如果where语句是需要格式化拼凑，既可以用String.format(String format, Object...args);
	 * 也可以直接在这里书写，此时where参数表示的是格式字符串，而selectionArgs表示需要填充的参数，例如
	 * count("where id > ? and name like '%uto%'",  1) = count("where id > 1 and name like '%uto%'"), 通配符
	 * 的值用被1替换了;但需要注意这里字段名称不能用通配符 ，所以能用String.format就不要用selectionArgs这个麻烦的东西吧。
	 * @return
	 */
	public abstract int count(String where, Object... selectionArgs);

	/**
	 * 保存对象
	 * @param t
	 * @return	对象
     */
	public abstract T save(T t);

	/**
	 * 保存或更新对象
	 * @param t
	 * @return
     */
	public T saveOrUpdate(T t);

	/**
	 * 保存对对象,对象以回调形式返回
	 * @param callback
	 * @param t
     * @return
     */
	public abstract SQLiteMapper<T> save(Callback<T> callback, T t);

	/**
	 * 保存集合对象,对象以回调形式返回
	 * @param callback
	 * @param t
     * @return
     */
	public abstract SQLiteMapper<T> save(Callback<List<T>> callback, List<T> t);

	/**
	 * 更新对象
	 * @param t
	 * @return
     */
	public abstract boolean update(T t);

	/**
	 * 更新对象
	 * @param contents	条件
	 * @return
     */
	public abstract boolean update(ContentValues contents);

	/**
	 * 更新对象,是否成功以回调形式返回
	 * @param callback
	 * @param t
     * @return
     */
	public abstract SQLiteMapper<T> update(Callback<Boolean> callback, T t);

	/**
	 * 更新对象,是否成功以回调形式返回
	 * @param callback
	 * @param contents
     * @return
     */
	public abstract SQLiteMapper<T> update(Callback<Boolean> callback,
										   ContentValues contents);

	/**
	 * 移除对象
	 * @param id
	 * @return
     */
	public abstract boolean remove(Object id);

	/**
	 * 移除对象,是否成功以回调形式返回
	 * @param callback
	 * @param id
     * @return
     */
	public abstract SQLiteMapper<T> remove(Callback<Boolean> callback, Object id);

	/**
	 * 属性注解， 注解内容为对应sqlite 字段定义（字段名除外）
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public @interface SQLiteProperty {
		/**
		 * 数据库字段定义
		 * @return
         */
		public String value() default "";

		/**
		 *	是否创建索引
		 * @return
         */
		public boolean index() default false;
	}

	/**
	 * 数据库版本信息注解， value 标注版本号，默认为1
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SQLiteEntry {
		/**
		 * 返回数据库版本
		 * @return
         */
		public int version() default 1;

		/**
		 * 返回数据库名称
		 * @return
         */
		public String name() default "";
	}

	/**
	 * upgrade 标注数据库升级方法名， 这个方法必须为静态方法，且带有两个固定参数：（SQLiteDatabase db, String
	 * table）
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface SQLiteUpdate {
		/**
		 * 数据库版本更新版本
		 * @return
         */
		public int value();
	}

}