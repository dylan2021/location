package com.sfmap.route;

import android.app.Dialog;


import com.sfmap.library.Callback;
import com.sfmap.library.http.HttpAsyncTask;
import com.sfmap.route.model.POI;
import com.sfmap.route.view.DialogHelper;

import java.util.List;


public abstract class RouteRequestCallBack<T> implements Callback.PrepareCallback<byte[], T>,
        Callback.CachePolicyCallback, Callback.CacheCallback<T>, Callback.CustomDlgCallback {

    protected Callback<T> callback;
    protected POI startPOI, endPOI;
    protected List<POI> midPOIList;
    protected String method;
    private String loadString;

    public RouteRequestCallBack(Callback<T> callaback, POI startPOI, List<POI> midPOIList,
                                POI endPOI, String method) {
        this.callback = callaback;
        this.startPOI = startPOI;
        this.midPOIList = midPOIList;
        this.endPOI = endPOI;
        this.method = method;
    }

    public abstract String getCacheKey();

    public void setLoadingMessage(String loadString) {
        this.loadString = loadString;
    }

    @Override
    public Dialog onLoadDlg(HttpAsyncTask<?> task) {
        return DialogHelper.createLoadingDialog(null,task, loadString);
    }
}
