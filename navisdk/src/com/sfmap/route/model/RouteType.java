package com.sfmap.route.model;
/**
 * 路线规划方式枚举类型
 */
public enum RouteType {

    //路线规划类型(默认值)
    DEFAULT("默认值", -1),
    //路线规划类型（公交车）
    BUS("公交", 0),
    //路线规划类型（驾车）
    CAR("驾车", 1),
    //路线规划类型（步行）
    ONFOOTBIKE("骑行", 2),
    //路线规划类型（货运）
    TRUCK("货车", 3),
    //路线规划类型（货运）
    ONFOOT("步行", 4);

    // 定义私有变量
    private int value;
    private String name;

    /**
     * 构造函数，枚举类型只能为私有
     *
     * @param name
     * @param value
     */
    private RouteType(String name, int value) {
        this.value = value;
        this.name = name;
    }

    /**
     * 根据值取枚举常量
     *
     * @param value
     * @return
     */
    public static RouteType getType(int value) {
        for (RouteType r : RouteType.values()) {
            if (r.getValue() == value) {
                return r;
            }
        }
        return CAR;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
