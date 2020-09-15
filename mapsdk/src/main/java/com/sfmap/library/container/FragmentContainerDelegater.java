package com.sfmap.library.container;

/**
 * FragmentContainer代理。
 */
public interface FragmentContainerDelegater {
    /**
     * 初始化FragmentContainer容器
     */
    void initFragmentContainer();

    /**
     * 返回容器视图
     * @return  一个FragmentContainer实体
     */
    FragmentContainer getFragmentContainer();
}
