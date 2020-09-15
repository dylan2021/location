package com.sfmap.route;

public enum RequestMsgCode {
    /**
     * POI
     */
    CODE_NATIVE_POI_NODATA(-100, "网络不畅，且无离线导航数据，请检查网络后重试。"), CODE_NATIVE_POI_SUCCESS(
            -101, "网络不畅，自动转为离线搜索"),
    // CODE_NATIVE_POI_NORESULT(-102, "网络不畅，且本地搜索无结果，请检查网络后重试。"),
    CODE_NATIVE_POI_NORESULT(-102, "网络不畅，且无离线数据，请检查网络后重试。"),

    /**
     * TBT路线
     */
    CODE_NATIVE_TBT_SUCCESS(-110, "网络不畅，已自动转为离线路线规划"), CODE_NATIVE_TBT_NODATA(
            -111, "网络不畅，且无离线基础功能包，请检查网络后重试。"), CODE_NATIVE_TBT_NORESULT(-112,
            "网络不畅，且无沿途离线数据包，无法规划路线，请检查网络后重试。"), CODE_NATIVE_TBT_NEEDREBOOT(
            -113, "离线导航引擎已经下载完成，需要重启后才可生效，若不重启不能使用离线导航功能。"),

    /**
     * TBT导航
     */
    CODE_NATIVE_TBT_NAVI_OFFLINE(-120, "网络不畅，自动转为离线导航。"), CODE_NATIVE_TBT_NAVI_OFFLINE_AVOIDJAM(
            -121, "网络不畅，无法获取路况数据，无法躲避拥堵路段，自动转为离线导航");

    private int nCode;
    private String strCodeMsg;

    private RequestMsgCode(int nCode, String strCodeMsg) {
        this.nCode = nCode;
        this.strCodeMsg = strCodeMsg;
    }

    public int getnCode() {
        return nCode;
    }

    public void setnCode(int nCode) {
        this.nCode = nCode;
    }

    public String getStrCodeMsg() {
        return strCodeMsg;
    }

    public void setStrCodeMsg(String strCodeMsg) {
        this.strCodeMsg = strCodeMsg;
    }

}