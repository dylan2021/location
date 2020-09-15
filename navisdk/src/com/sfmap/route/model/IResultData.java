package com.sfmap.route.model;

import java.io.Serializable;

/**
 * 基础数据接口
 */
public interface IResultData extends Serializable {
    /**
     * save data to file
     */
    public void saveData();

    /**
     * restore data from file
     */
    public void restoreData();

    /**
     * reset data
     */
    public void reset();

    /**
     * 是否已经填充了数据
     * <p/>
     * 包括startpoi,endpoi,resultData
     *
     * @return
     */
    public boolean hasData();

    /**
     * 保存在数据中心时的唯一标识
     *
     * @return
     */
    public String getKey();

    public void setKey(String key);

    /**
     * 返回实现类的type
     *
     * @return
     */
    public Class<?> getClassType();

}
