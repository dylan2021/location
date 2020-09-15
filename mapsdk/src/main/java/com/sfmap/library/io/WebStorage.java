package com.sfmap.library.io;

/**
 * html 与 native 共享存储
 * 动态存储keyValue 数据。
 * 遍历存储key
 */
public interface WebStorage extends KeyValueStorage<WebStorage>,Iterable<String>{
    /**
     * 根据key获取结果
     * @param key	自定义key
     * @return		WebStorage接口实例
     */
    public String get(String key);

    /**
     * 保存进缓存
     * @param key	自定义key
     * @param value	获取对应的value
     * @return		WebStorage接口实例
     */
    public WebStorage set(String key, String value);

    /**
     * 移除缓存
     * @param key	自定义key
     * @return		WebStorage接口实例
     */
    public WebStorage remove(String key);

    /**
     * 返回缓存大小
     * @return	缓存大小
     */
    public int size();
}
