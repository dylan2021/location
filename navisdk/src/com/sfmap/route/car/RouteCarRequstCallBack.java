package com.sfmap.route.car;

import com.sfmap.library.Callback;
import com.sfmap.library.http.cache.HttpCacheEntry;
import com.sfmap.route.RouteRequestCallBack;
import com.sfmap.route.RouteUtil;
import com.sfmap.route.model.ICarRouteResult;
import com.sfmap.route.model.POI;
import com.sfmap.route.model.RouteType;

import java.util.List;

/**
 * 驾车路线规划请求的回调
 */
public class RouteCarRequstCallBack extends RouteRequestCallBack<CarRouteResponsor> {
    private boolean hasExecCacheCallback = false;

    public RouteCarRequstCallBack(Callback<CarRouteResponsor> callaback,
                                  POI startPOI, List<POI> midPOIList, POI endPOI, String method) {
        super(callaback, startPOI, midPOIList, endPOI, method);
    }

    @Override
    public void callback(CarRouteResponsor result) {
        if (callback != null && !hasExecCacheCallback)
            callback.callback(result);
    }

    @Override
    public void error(Throwable ex, boolean callbackError) {
        if (callback != null)
            callback.error(ex, callbackError);
    }

    @Override
    public CarRouteResponsor prepare(byte[] rawData) {
        ICarRouteResult carRouteResult = new RouteCarResultData(null);
        carRouteResult.setFromPOI(startPOI);
        carRouteResult.setToPOI(endPOI);
        carRouteResult.setMidPOIs(midPOIList);
        carRouteResult.setMethod(method);
        CarRouteResponsor responsor = new CarRouteResponsor(
                carRouteResult);
        responsor.parser(rawData);
        return responsor;
    }

    @Override
    public CachePolicy getCachePolicy() {
        return CachePolicy.Any;
    }

    @Override
    public String getCacheKey() {
//        String key = generateCarResultKey(startPOI, midPOIList, endPOI, method);
//        return key;
        return "";
    }

    /**
     * 生成驾车规划结果的用于存放在数据中心的key值
     * @param fromPoi
     *               起点
     * @param midPOIs
     *               途经点
     * @param toPOI
     *             终点
     * @param method
     *              偏好
     * @return
     */
    public String generateCarResultKey(POI fromPoi, List<POI> midPOIs, POI toPOI,
                                       String method) {
        return RouteUtil.generateResultKey(RouteType.CAR.getValue(), fromPoi,
                midPOIs, toPOI, method);
    }

    @Override
    public boolean cache(CarRouteResponsor result, HttpCacheEntry cache) {
        if (callback != null && result.errorCode == 0 && cache.isMemCache) {
            callback.callback(result);
            hasExecCacheCallback = true;
        } else {
            return false;
        }
        return cache.isMemCache;
    }
}
