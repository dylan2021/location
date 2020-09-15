package com.sfmap.tbt;

import java.io.Serializable;

public class RestrictionArea implements Serializable {
    /**
     * 是否已经避开
     */
    public boolean bAvoided;

    /**
     * 坐标
     */
    public double x;
    public double y;

    /**
     * 类型
     * 无法避免的类型：
     * 0：无
     * 1：限高
     * 2：限重
     * 3：限宽
     * 4：转向禁止（路口处，禁止左转，右转，直行的标识)
     * 5：策略限行
     * 成功避免的类型：
     * 0：无
     * 1：限高
     * 2：限重
     * 3：限宽
     * 4：策略限行
     */
    public int btType;

    /**
     * 描述长度
     */
    public int nDescLen;

    /**
     * 描述
     * 限行描述：
     * 如果是政策限行，则记录政策限行的ID，后续通过ID去获取政策限行详细信息
     * 如果是限高，限宽，限重，则直接记录对应的限行值
     */
    public String pwDesc;
}
