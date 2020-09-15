package com.sfmap.route;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.SfMapLocationClientOption;
import com.sfmap.api.location.SfMapLocationListener;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.LocationSource;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.MyLocationStyle;
import com.sfmap.api.maps.model.MyTrafficStyle;
import com.sfmap.api.maps.model.PolylineOptions;
import com.sfmap.api.navi.Navi;
import com.sfmap.api.navi.enums.PathPlanningErrCode;
import com.sfmap.api.navi.model.NaviLatLng;
import com.sfmap.api.navi.model.NaviPath;
import com.sfmap.api.navi.model.OperateInfo;
import com.sfmap.library.model.GeoPoint;
import com.sfmap.library.util.DateTimeUtil;
import com.sfmap.log.model.LogParam;
import com.sfmap.log.service.LogUploadService;
import com.sfmap.map.navi.LogcatFileManager;
import com.sfmap.map.navi.NaviActivity;
import com.sfmap.map.navi.NaviBaseActivity;
import com.sfmap.navi.R;
import com.sfmap.navi.R2;
import com.sfmap.map.navi.TruckInfo;
import com.sfmap.map.util.MapSharePreference;
import com.sfmap.map.util.RoutePathHelper;
import com.sfmap.route.car.CarRouteResponsor;
import com.sfmap.route.car.RouteCarDrawMapLineTools;
import com.sfmap.route.car.RouteCarResultData;
import com.sfmap.route.model.ICarRouteResult;
import com.sfmap.route.model.IRouteResultData;
import com.sfmap.route.model.NaviJumpRouteEvent;
import com.sfmap.route.model.NavigationPath;
import com.sfmap.route.model.NavigationResult;
import com.sfmap.route.model.POI;
import com.sfmap.route.model.POIFactory;
import com.sfmap.route.model.RoutePathBean;
import com.sfmap.route.model.RouteType;
import com.sfmap.route.model.TruckItem;
import com.sfmap.route.util.RouteCalType;
import com.sfmap.route.view.CommenDialogFragment;
import com.sfmap.route.view.RoutePathAdapter;
import com.sfmap.route.widget.RouteFailPopupWindow;
import com.sfmap.tbt.ResourcesUtil;
import com.sfmap.tbt.util.AppInfo;
import com.sfmap.tbt.util.LogUtil;
import com.sfmap.tts.TtsManager;
import com.sfmap.util.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sfmap.route.model.RouteType.CAR;
import static com.sfmap.route.model.RouteType.TRUCK;

public class RouteActivity extends NaviBaseActivity implements RoutePathAdapter.ItemClickListener, SfMapLocationListener, LocationSource {
    private final String TAG = this.getClass().getSimpleName();
    @BindView(R2.id.recycler)
    RecyclerView recycler;
    @BindView(R2.id.tv_route_start_navi)
    TextView tvRouteStartNavi;
    @BindView(R2.id.tv_route_map_show)
    TextView tvRouteMapShow;
    @BindView(R2.id.con_bottom)
    ConstraintLayout conBottom;
    @BindView(R2.id.con_parent)
    ConstraintLayout conParent;
    @BindView(R2.id.img_traffic)
    ImageView imgTraffic;
    private RecyclerView recyclerView;
    private MapView mapView;
    private Navi mNavi;
    //算路终点坐标
    protected NaviLatLng mEndLatlng = new NaviLatLng(34.71806, 113.741719);
    //算路起点坐标
    protected NaviLatLng mStartLatlng;
    private int planMode = 9;
    private int routeType = 1;
    protected TruckInfo mTruckInfo;
    private LocationManager mLocationManager;
    //    //存储算路起点的列表
//    protected final List<NaviLatLng> startPoints = new ArrayList<>();
//    //存储算路终点的列表
//    protected final List<NaviLatLng> endPoints = new ArrayList<>();
    private ArrayList<RoutePathBean> routePathBeans = new ArrayList<>();
    private int currentItem = 0; //线路序号
    private IRouteResultData routeResultData;
    private boolean mIsActive = true;
    private RouteCarDrawMapLineTools drawMapLineTools;
    private MapController mapController;
    private RouteOperateLineStation routeOperateLineStation;
    private int index = 0; //货车禁行政策号
    private int adcode = 110000; //货车禁行城市代码
    private POI startPOI, endPOI = null;
    private List<POI> midPOIList = new ArrayList<>();
    public static RouteType type = RouteType.CAR;//默认是骑行规划
    //开始坐标
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    //结束坐标
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    //途径坐标
    private List<NaviLatLng> wayPointList = new ArrayList<NaviLatLng>();

    private int selectPos = 0;
    private SfMapLocationClient mSfMapLocationClient;
    private OnLocationChangedListener mListener;
    private SfMapLocation sfMapLocation;
    boolean fisrtLocation = true;
    /**
     * 前后台状态
     */
    public boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mIsActive = true;
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);
        initView(savedInstanceState);

        String startLatlng = getIntent().getStringExtra("startLatlng");
        String endLatlng = getIntent().getStringExtra("endLatlng");
        String truckInfo = getIntent().getStringExtra("truckInfo");
        String carPlate = getIntent().getStringExtra("carPlate");
        String taskId = getIntent().getStringExtra("taskId");
        String driverId = getIntent().getStringExtra("driverId");
        String destDeptCode = getIntent().getStringExtra("destDeptCode");
        String srcDeptCode = getIntent().getStringExtra("srcDeptCode");
        routeType = getIntent().getIntExtra("routeType", 1);
        planMode = getIntent().getIntExtra("planMode", 0);

        if (!TextUtils.isEmpty(startLatlng)) {
            mStartLatlng = new Gson().fromJson(startLatlng, NaviLatLng.class);
//            startList.add(mStartLatlng);
        }
        if (!TextUtils.isEmpty(endLatlng)) {
            mEndLatlng = new Gson().fromJson(endLatlng, NaviLatLng.class);
//            endList.add(mEndLatlng);
        }
        if (!TextUtils.isEmpty(truckInfo)) {
            mTruckInfo = new Gson().fromJson(truckInfo, TruckInfo.class);
            AppInfo.setCarPlate(mTruckInfo.getPlate());
        }

        AppInfo.setCarPlate(carPlate);
        AppInfo.setUserId(driverId);
        AppInfo.setTaskId(taskId);
        AppInfo.setSrcDeptCode(srcDeptCode);
        AppInfo.setDestDeptCode(destDeptCode);
        if(mStartLatlng != null){
            AppInfo.setStartX(mStartLatlng.getLongitude());
            AppInfo.setStartY(mStartLatlng.getLatitude());
        }
        AppInfo.setEndX(mEndLatlng.getLongitude());
        AppInfo.setEndY(mEndLatlng.getLatitude());
        initLocation();
        try {
            LogcatFileManager.getInstance().start(Environment
                    .getExternalStorageDirectory().getAbsolutePath() + "/01SfNaviSdk");
        } catch (Exception e) {
        }
        initNaviData();
        if(mStartLatlng != null){
            startRoute(routeType);
        }
        TtsManager ttsManager = TtsManager.getInstance(this);

        Intent intent = new Intent(this, LogUploadService.class);
        conn = new MyConn();
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private MyConn conn;
    private LogUploadService.MyBinder myBinder;//我定义的中间人对象

    //监视服务的状态
    private class MyConn implements ServiceConnection {

        //当服务连接成功调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取中间人对象
            myBinder = (LogUploadService.MyBinder) service;


        }

        //失去连接
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private LogParam getParam(Object object) {
        LogParam logParam = new LogParam();
        logParam.setAk("13e638bc4b21403585bcddcc728a17ee");
        logParam.setTaskId(AppInfo.getTaskId());
        logParam.setNaviId(AppInfo.getNaviId());
        logParam.setSdkVersion(AppInfo.getSdkVersion());
        logParam.setReportTime(System.currentTimeMillis());
        if (object instanceof OperateInfo) {
            logParam.setOperateInfo((OperateInfo) object);
            logParam.setType(7);
        }
        return logParam;
    }


    private void initView(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapController = mapView.getMap();
        mapView.getMap().getUiSettings().setCompassEnabled(false);
        mapView.getMap().getUiSettings().setZoomControlsEnabled(false);
        boolean trafficEnable = (boolean)SPUtils.get(this,"trafficEnable",false);
        imgTraffic.setImageResource(trafficEnable ? R.drawable.map_traffic_on_sf : R.drawable.map_traffic_off_sf);
        mapController.setTrafficEnabled(trafficEnable);
        mapController.setLocationSource(this);
        mapController.setMyLocationEnabled(true);
        mapController.setMyLocationType(1);
//        mapController.setMyTrafficStyle();
        recyclerView = findViewById(R.id.recycler);
        mapController.setOnMapLongClickListener(new MapController.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mEndLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                endList.clear();
                endList.add(mEndLatlng);
                mapController.clear(true);
                startRoute(routeType);
            }
        });

        tvRouteStartNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                intent.putExtra("currentItem", currentItem);
                intent.putExtra("emulatorNavi", false);
                intent.setClass(getApplicationContext(), NaviActivity.class);
                startActivity(intent);
            }
        });

        tvRouteMapShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBinder != null) {
                    OperateInfo operateInfo = new OperateInfo("1","返回任务列表");
                    myBinder.ReciveClick(getParam(operateInfo));
                }
//                finish();
                Intent intent = new Intent();
                intent.putExtra("currentItem", currentItem);
                intent.putExtra("emulatorNavi", true);
                intent.setClass(getApplicationContext(), NaviActivity.class);
                startActivity(intent);
            }
        });

        imgTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTrafficStatue();
            }
        });
    }

    private void setTrafficStatue() {
        boolean bool = mapController.isTrafficEnabled();
        if (bool){
            if (myBinder != null) {
                OperateInfo operateInfo = new OperateInfo("1","tmc关");
                myBinder.ReciveClick(getParam(operateInfo));
            }
            imgTraffic.setImageResource(R.drawable.map_traffic_off_sf);
        }else{
            if (myBinder != null) {
                OperateInfo operateInfo = new OperateInfo("1","tmc开");
                myBinder.ReciveClick(getParam(operateInfo));
            }
            imgTraffic.setImageResource(R.drawable.map_traffic_on_sf);
        }

        mapController.setTrafficEnabled(!(bool));
        SPUtils.put(this,"trafficEnable",!bool);
    }

    private void initRecyclerView() {
        initPathData();
//        Log.d(TAG, "mNavi.getNaviPaths().size():" + mNavi.getNaviPaths().size());
        if (mNavi == null || routePathBeans.isEmpty()) {
//            finish();
            return;
        }
        LogUtil.d(TAG,new Gson().toJson(routePathBeans));
//        NavigationResult result = ((ICarRouteResult) routeResultData).getNaviResultData();
//        NavigationPath[] navi_paths = result.paths;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), routePathBeans.size());
        gridLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

//        List<NaviPath> naviPaths = new ArrayList<>();
//        if (mNavi.getNaviPaths() == null) {
//            naviPaths.add(mNavi.getNaviPath());
//        } else {
//            for (int i = 0; i < mNavi.getNaviPaths().size(); i++) {
//                naviPaths.add(mNavi.getNaviPaths().get(i));
//            }
//        }

        RoutePathAdapter pathAdapter = new RoutePathAdapter(routePathBeans);
        recyclerView.setAdapter(pathAdapter);
        pathAdapter.setListener(this);
        pathAdapter.setItemChecked(currentItem);
    }

    private void initNaviData() {
        mNavi = Navi.getInstance(this);
        mNavi.addNaviListener(this);
        //设置模拟导航的行车速度
        mNavi.setEmulatorNaviSpeed(300);
        startList.add(mStartLatlng);
        endList.add(mEndLatlng);
        mNavi.setSoTimeout(15000);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        if (mapView.getMap() != null) {
            setTrafficStyle(1);
        }
        mapView.onResume();

//        startRoute(routeType);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        isForeground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsActive = false;
        if (mSfMapLocationClient != null) {
            mSfMapLocationClient.destroy();
        }
        try {
            mapView.onDestroy();
            mNavi.destroy();
        } catch (Exception e) {

        }
        Intent intent = new Intent(this, LogUploadService.class);
        unbindService(conn);
        stopService(intent);
        EventBus.getDefault().unregister(this);
    }

    private void startCarNavigation() {
        // 驾车算路
        mNavi.calculateDriveRoute(
                startList,//指定的导航起点。支持多个起点，起点列表的尾点为实际导航起点，其他坐标点为辅助信息，带有方向性，可有效避免算路到马路的另一侧；
                endList,//指定的导航终点。支持一个终点。
                new ArrayList<NaviLatLng>(), //途经点，同时支持最多16个途经点的路径规划；
                0, //驾车路径规划的计算策略
//                planMode, //驾车路径规划的计算策略
                false //是否为本地算路,true 本地算路,false 网络算路
        );
    }

    private void startTruckNavigation() {
        if (null == mTruckInfo) {
            Toast.makeText(getApplicationContext(), "请先设置货车信息", Toast.LENGTH_LONG).show();
            return;
        }
        String carVehicle = mTruckInfo.getTruckType(); //1:小车 4:拖挂车 5:微型货车 6:轻型货车 7:中型货车 8:中型货车 9:危险品运输车
        String carWeight = mTruckInfo.getWeight();
        String carAxleNumber = mTruckInfo.getAxleNum();
        String carHeight = mTruckInfo.getHeight();
        String carPlate = mTruckInfo.getPlate();

        mNavi.setPlate(carPlate);
        mNavi.setVehicle(Integer.parseInt(carVehicle));
        mNavi.setWeight(Double.parseDouble(carWeight));
        mNavi.setHeight(Double.parseDouble(carHeight));
        mNavi.setAxleNumber(Integer.parseInt(carAxleNumber.substring(0, 1)));
        mNavi.calculateDriveRoute(startList, endList, new ArrayList<NaviLatLng>(), planMode, false);
    }

    /**
     * 导航创建成功时的回调函数。
     */
    @Override
    public void onInitNaviSuccess() {

    }

    /**
     * 驾车路径规划成功后的回调函数。
     */
    @Override
    public void onCalculateRouteSuccess() {
        if(isForeground){

            conBottom.setVisibility(View.VISIBLE);
//        HashMap<Integer, NaviPath> naviPathHashMap = new HashMap<>();
//        naviPathHashMap.put(0, mNavi.getNaviPath());
//        drawLines(naviPathHashMap);
            Log.d(TAG, "onCalculateRouteSuccess");
            ICarRouteResult iCarRouteResult = parsePathDataEx();
            if (iCarRouteResult != null) {
                callback(iCarRouteResult, type);
            }
        }
        finishProgress();

    }


    /**
     * 驾车路径规划失败后的回调函数。
     *
     * @param errorInfo - 计算路径的错误码，参见 {@link PathPlanningErrCode }。
     */
    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        if(isForeground){
            Log.d(TAG, "onCalculateRouteFailure");
            try {
                String value = new String(mNavi.getDecodeRouteError().getBytes("gbk"),"utf-8");
                Toast.makeText(getApplicationContext(),"路径规划错误：" + value,Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

//        Toast.makeText(getApplicationContext(), "路径规划失败，错误码：" + errorInfo, Toast.LENGTH_LONG).show();

            showFailedPopupWindow();
            mapController.setMapCenter(new LatLng(mStartLatlng.getLatitude(),mStartLatlng.getLongitude()));
        }

        finishProgress();
    }

    /**
     * 多路线算路成功回调。
     *
     * @param routeIds - 路线id数组
     */
    @Override
    public void onCalculateMultipleRoutesSuccess(int[] routeIds) {
        if(isForeground){

            conBottom.setVisibility(View.VISIBLE);
//        drawLines(mNavi.getNaviPaths());
            Log.d(TAG, "onCalculateMultipleRoutesSuccess");
            ICarRouteResult iCarRouteResult = parsePathDataEx();
            if (iCarRouteResult != null) {
                callback(iCarRouteResult, type);
            }
        }
        finishProgress();
    }

    private void drawStartMarker() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(mStartLatlng.getLatitude(), mStartLatlng.getLongitude()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bubble_start));
        mapView.getMap().addMarker(markerOptions);
    }

    private void drawEndMarker() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(mEndLatlng.getLatitude(), mEndLatlng.getLongitude()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bubble_end));
        mapView.getMap().addMarker(markerOptions);
    }

    private void drawLines(HashMap<Integer, NaviPath> naviPaths) {
        initRecyclerView();
        drawStartMarker();
        drawEndMarker();
        for (int i = 0; i < naviPaths.size(); i++) {
            NaviPath naviPath = naviPaths.get(i);
            drawLine(naviPath.getCoordList());
        }
        setLatLngsCenter(200);
    }

    private void drawLine(List<NaviLatLng> naviLatLngs) {
        PolylineOptions polylineOptions = new PolylineOptions();
        for (NaviLatLng naviLatLng : naviLatLngs) {
            polylineOptions.add(new LatLng(naviLatLng.getLatitude(), naviLatLng.getLongitude()));
        }
        polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.routetexture_green));
        polylineOptions.width(60);
        mapView.getMap().addPolyline(polylineOptions);
    }

    /**
     * 根据传入的地址明细，计算得出地图的缩放比，确保在初始界面中加载所有传入的地址
     */
    public void setLatLngsCenter(int padding) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLngBounds bounds;

        builder.include(new LatLng(mStartLatlng.getLatitude(), mStartLatlng.getLongitude()));//把你所有的坐标点放进去
        builder.include(new LatLng(mEndLatlng.getLatitude(), mEndLatlng.getLongitude()));//把你所有的坐标点放进去

        bounds = builder.build();
        mapView.getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    private void initPathData() {
        routePathBeans.clear();
        NavigationResult result = ((ICarRouteResult) routeResultData).getNaviResultData();
        NavigationPath[] navi_paths = result.paths;
        for (int j = 0; j < navi_paths.length; j++) {
            if (j > 2) {
                continue;
            }
            NavigationPath navigationPath = navi_paths[j];
            RoutePathBean routePathBean = new RoutePathBean();
            routePathBean.setId(j);
            Log.d(TAG, "navigationPath.pathStrategy" + navigationPath.pathStrategy);
            if (navigationPath.pathStrategy == 0) {
                routePathBean.setPathType("推荐道路");
            } else if (navigationPath.pathStrategy == 1) {
                routePathBean.setPathType("避免收费");
            } else if (navigationPath.pathStrategy == 2) {
                routePathBean.setPathType("距离最短");
            } else if (navigationPath.pathStrategy == 3) {
                routePathBean.setPathType("高速优先");
            } else if (navigationPath.pathStrategy > 9) {
                routePathBean.setPathType("频次优先");
            }

            int route_length = navigationPath.pathlength;
            String res_map_sub_des = RoutePathHelper.createCarSubDesNoPrice(route_length);
            routePathBean.setDistance(res_map_sub_des);

            routePathBean.setTime(DateTimeUtil.getTimeStr(navigationPath.costTime));

            routePathBean.setTrafficLight(navigationPath.trafficNum + "");

            if (navigationPath.tollCost != 0) {
                routePathBean.setTollCost("¥" + navigationPath.tollCost);
            } else {
                routePathBean.setTollCost("");
            }
            routePathBeans.add(routePathBean);
            Log.d(TAG, "NaviType:" + RouteCalType.getNaviType(RouteUtil.getCarUserMethod()));
            for (int i = 0; i < routePathBeans.size(); i++) {
                if (i == 0) {
                    if (navigationPath.pathStrategy == 6) {
                        routePathBeans.get(i).setPathType("常走路线");
                    } else {
                        routePathBeans.get(i).setPathType("躲避拥堵");
                    }
                }
                if (i == 1) {
                    if (navigationPath.pathStrategy == 6) {
                        routePathBeans.get(i).setPathType("常走路线一");
                    } else {
                        routePathBeans.get(i).setPathType("躲避拥堵一");
                    }
                }
                if (i == 2) {
                    if (navigationPath.pathStrategy == 6) {
                        routePathBeans.get(i).setPathType("常走路线二");
                    } else {
                        routePathBeans.get(i).setPathType("躲避拥堵二");
                    }
                }
            }
        }
    }

    @Override
    public void onItemClick(int index) {
        currentItem = index;

        mNavi.selectRouteId(index);
        if (mNavi.GetRouteUID(currentItem) != null) {
            AppInfo.setRouteId(mNavi.GetRouteUID(currentItem));
        }
        Log.d(TAG, "GetRouteUID:" + AppInfo.getRouteId());
//        AppInfo.setRouteId("0");
        ((ICarRouteResult) routeResultData).setFocusRouteIndex(currentItem);
        ((ICarRouteResult) routeResultData).setFocusStationIndex(-1);
        handler.removeCallbacks(carRunnable);
        // 切换线路前，先把界面上的线路和起终点消除，避免叠加问题
        mapController.clear(true);
        handler.post(carRunnable);
        if (myBinder != null) {
            OperateInfo operateInfo = new OperateInfo("1",routePathBeans.get(currentItem).getPathType());
            myBinder.ReciveClick(getParam(operateInfo));
        }
    }

    @Override
    public void onBehaviorClick() {

    }


    private Runnable carRunnable = new Runnable() {
        @Override
        public void run() {
            addCarRouteToMap((ICarRouteResult) routeResultData);
            initRecyclerView();
        }
    };

    private void addCarRouteToMap(final ICarRouteResult result) {
        mapView.getMeasuredHeight();
        if (!mIsActive) {
            return;
        }
//        if (drawMapLineTools == null) {
        drawMapLineTools = new RouteCarDrawMapLineTools(getApplicationContext(), (ICarRouteResult) routeResultData, mapController);
//        }
        if (routeOperateLineStation == null) {
            routeOperateLineStation = new RouteOperateLineStation(getApplicationContext(), mapController, mapView);
        }
        drawMapLineTools.addLineToOverlays(false);
        try {
            routeOperateLineStation.getBounds(result, currentItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        checkHasTruckRule();
    }

    private void setIndexAndAdcode(String describe) {
        if (describe.isEmpty()) {
            return;
        }
        try {
            String[] dsb = describe.split("\\|");
            adcode = Integer.parseInt(dsb[0]);
            index = Integer.parseInt(dsb[1]);
        } catch (Exception e) {

        }
        ;
    }

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mNavi == null) {
                        return true;
                    }
                    if (null != mapController) {
                        mapController.clear(true);
                    }
                    initNavi();
                    currentItem = 0;
                    startList.clear();
                    endList.clear();
                    wayPointList.clear();
                    for (POI poi : midPOIList) {
                        wayPointList.add(new NaviLatLng(poi.getPoint().getLatitude(), poi.getPoint().getLongitude()));
                    }
//                    RouteRequestManager.requestRoute(type, startPOI, endPOI, midPOIList, RouteUtil.getCarUserMethod(), RoutesFragment.this);
//                    if ("我的位置".equals(startPOI.getName())) {
//                        startPOI.setPoint(InMap.getLatestPosition());
//                    }
                    startList.add(mStartLatlng);
                    endList.add(mEndLatlng);
                    if (type == TRUCK) {
                        setTruckInfo();
                    } else {
//                        setCarInfo();
                    }
                    if (mNavi == null) {
                        return true;
                    }
                    mNavi.calculateDriveRoute(startList, endList, wayPointList, RouteCalType.getNaviType(RouteUtil.getCarUserMethod()), false);
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private Handler handler = new Handler(callback);

    public void callback(IRouteResultData result, RouteType routeType) {
//        dialog.dismiss();
//        routeFailed = false;
        if (type == CAR || type == TRUCK) {
            routeResultData = dealData(result);
            mNavi.selectRouteId(currentItem);
//            AppInfo.setRouteId("0");
            if (mNavi.GetRouteUID(currentItem) != null) {
                AppInfo.setRouteId(mNavi.GetRouteUID(currentItem));
            }
            Log.d(TAG, "GetRouteUID:" + AppInfo.getRouteId());
            ((ICarRouteResult) routeResultData).setFocusRouteIndex(currentItem);
        } else {
            routeResultData = result;
        }
        switch (type) {
            case CAR:
                handler.removeCallbacks(carRunnable);
                handler.postDelayed(carRunnable, 50);
                break;
            case TRUCK:
                handler.removeCallbacks(carRunnable);
                handler.postDelayed(carRunnable, 50);
//                if (checkHasTruckRule()) {
//                    constraintRuleDialog.setVisibility(VISIBLE);
//                    imgTruckTip.setVisibility(VISIBLE);
//                    dissmissDialog();
//                }
                break;
            case ONFOOTBIKE:
                handler.removeCallbacks(carRunnable);
                break;
            case ONFOOT:
                handler.removeCallbacks(carRunnable);
                break;
            default:
                break;
        }
    }

    /**
     * Decoder2.2版本的解析
     *
     * @param
     * @param
     * @return
     */
    private ICarRouteResult parsePathDataEx() {
        ICarRouteResult carRouteResult = new RouteCarResultData(getApplicationContext());
        POI fromPOI = POIFactory.createPOI();
        fromPOI.setPoint(new GeoPoint(AppInfo.getStartX(), AppInfo.getStartY()));
        POI endPOI = POIFactory.createPOI();
        endPOI.setPoint(new GeoPoint(mEndLatlng.getLongitude(), mEndLatlng.getLatitude()));
        carRouteResult.setFromPOI(fromPOI);
        carRouteResult.setToPOI(endPOI);
        carRouteResult.setMidPOIs(midPOIList);
        carRouteResult.setMethod(RouteCalType.getNaviTypes(RouteUtil.getCarUserMethod()) + "");
        CarRouteResponsor responsor = new CarRouteResponsor(
                carRouteResult);
        responsor.parser();
        return carRouteResult;
    }

    private void initNavi() {
        mNavi = Navi.getInstance(getApplicationContext());
        if (mNavi == null) {
            Log.e(TAG, "sfNavi == null");
            finish();
            return;
        }
        mNavi.addNaviListener(this);
    }

    private IRouteResultData dealData(IRouteResultData routeResultData) {
        NavigationResult result = ((ICarRouteResult) routeResultData).getNaviResultData();
        NavigationPath[] navi_paths = result.paths;
        NavigationPath[] dealNaviPaths = new NavigationPath[3];
        if (navi_paths.length > 3) {
            if (navi_paths[2].pathStrategy < 10) {
                dealNaviPaths[0] = navi_paths[0];
                dealNaviPaths[1] = navi_paths[1];
                dealNaviPaths[2] = navi_paths[3];
                result.paths = dealNaviPaths;
                ((ICarRouteResult) routeResultData).setNaviResultData(routeResultData.getFromPOI(), routeResultData.getToPOI(), result, routeResultData.getMethod());
            }
        }
        return routeResultData;
    }


    private void setTruckInfo() {
        String carVehicle = "轻型货";
        String carWeight = "4";
        String carAxleNumber = "2轴";
        String carHeight = "2.2";
        MapSharePreference mapSharePreference = new MapSharePreference(MapSharePreference.SharePreferenceName.truck_setting);
        String carPlate = mapSharePreference.getStringValue(MapSharePreference.SharePreferenceKeyEnum.CarDone, "");
        TruckItem truckItem = new TruckItem("鄂a12124", "1", "1", "50", "50", "10", "2", "5", "4");
        if (truckItem != null) {
            carVehicle = truckItem.getTruckType();
            carWeight = truckItem.getWeight();
            carAxleNumber = truckItem.getAxleNum();
            carHeight = truckItem.getHeight();
        }
        int carType;
        if (carVehicle.equals("小车")) {
            carType = 1;
        } else if (carVehicle.equals("拖挂车")) {
            carType = 4;
        } else if (carVehicle.equals("微型货车")) {
            carType = 5;
        } else if (carVehicle.equals("轻型货车")) {
            carType = 6;
        } else if (carVehicle.equals("中型货车")) {
            carType = 7;
        } else if (carVehicle.equals("重型货车")) {
            carType = 8;
        } else if (carVehicle.equals("危险品运输车")) {
            carType = 9;
        } else {
            carType = 1;
        }
        if (mNavi == null) {
            return;
        }
        mNavi.setPlate(carPlate);
        //暂时固定 需要定义各种车型对应的整形
        mNavi.setVehicle(carType);
//        sfmapNavi.setVehicle(Integer.parseInt(carVehicle));
        mNavi.setWeight(Double.parseDouble(carWeight));
        mNavi.setHeight(Double.parseDouble(carHeight));
        mNavi.setAxleNumber(Integer.parseInt(carAxleNumber.substring(0, 1)));

    }

    private void setCarInfo() {
        if (mNavi == null) {
            return;
        }
        mNavi.setPlate("");
        mNavi.setVehicle(1);
        mNavi.setWeight(0.0f);
        mNavi.setHeight(0.0f);
        mNavi.setAxleNumber(0);
    }

    public void setTrafficStyle(int type) { //0:首页白天 1:路径规划白天 2:导航黑夜
        switch (type) {
            case 0:
                MyTrafficStyle dayStyle = new MyTrafficStyle();
                dayStyle.setSmoothColor(0xff5BB64C);
                dayStyle.setSlowColor(0xffDFC289);
                dayStyle.setCongestedColor(0xff780A28);
                dayStyle.setSeriousCongestedColor(0xff780A28);
                mapView.getMap().setMyTrafficStyle(dayStyle);
                break;
            case 1:
                MyTrafficStyle dayRouteStyle = new MyTrafficStyle();
                dayRouteStyle.setSmoothColor(0xff9DD394);
                dayRouteStyle.setSlowColor(0xffEBDAB9);
                dayRouteStyle.setCongestedColor(0xffD76C8A);
                dayRouteStyle.setSeriousCongestedColor(0xffAE6C7E);
                mapView.getMap().setMyTrafficStyle(dayRouteStyle);
                break;
            case 2:
                MyTrafficStyle nightNaviStyle = new MyTrafficStyle();
                nightNaviStyle.setSmoothColor(0xff356733);
                nightNaviStyle.setSlowColor(0xff948467);
                nightNaviStyle.setCongestedColor(0xff7E1137);
                nightNaviStyle.setSeriousCongestedColor(0xff53102A);
                mapView.getMap().setMyTrafficStyle(nightNaviStyle);
                break;
            default:
                mapView.getMap().setMyTrafficStyle(new MyTrafficStyle());
                break;
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        LogUtil.d(TAG,"activate");
        mListener = onLocationChangedListener;
        if (mSfMapLocationClient == null) {
            mSfMapLocationClient = new SfMapLocationClient(this.getApplicationContext());
            // 设置定位监听
            //初始化定位参数
            SfMapLocationClientOption locationOption = new SfMapLocationClientOption();

            //设置定位间隔 或者设置单词定位
            locationOption.setInterval(1000);
            locationOption.setOnceLocation(false);

            locationOption.setLocationMode(SfMapLocationClientOption.SfMapLocationMode.High_Accuracy);
            locationOption.setUseGjc02(true);
            locationOption.setNeedAddress(true);

            //设置参数
            mSfMapLocationClient.setLocationOption(locationOption);
            mSfMapLocationClient.setLocationListener(this);
            mSfMapLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mSfMapLocationClient != null) {
            mSfMapLocationClient.stopLocation();
        }
    }

    Marker marker;
    @Override
    public void onLocationChanged(SfMapLocation location) {
        LogUtil.d(TAG,"onLocationChanged");
        if (mListener != null && location != null) {
            if (location.isSuccessful() && location.getLatitude() > 0) {
//                mListener.onLocationChanged(location);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if(null == sfMapLocation){
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
                    marker = mapView.getMap().addMarker(markerOptions);
                }else {
                    marker.setPosition(latLng);
                }
                sfMapLocation = location;
                if(fisrtLocation && mStartLatlng == null){
                    fisrtLocation = false;
                    NaviLatLng naviLatLng = new NaviLatLng(sfMapLocation.getLatitude(),sfMapLocation.getLongitude());
                    startList.clear();
                    startList.add(naviLatLng);
                    AppInfo.setStartY(naviLatLng.getLatitude());
                    AppInfo.setStartX(naviLatLng.getLongitude());
                    startRoute(routeType);
                }
            }
        }
    }


    //位置管理器
    private LocationManager locationManager;

    private void initLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航", Toast.LENGTH_SHORT).show();
            //返回开启GPS导航设置界面

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }
        //添加卫星状态改变监听
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        locationManager.addGpsStatusListener(statusListener);
        //1000位最小的时间间隔，1为最小位移变化；也就是说每隔1000ms会回调一次位置信息
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        MyLocationStyle style = new MyLocationStyle();
        style.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        // 圆的颜色
        style.radiusFillColor(Color.argb(25, 225, 2, 43));
        // 外圈的颜色
        style.strokeColor(Color.argb(25, 225, 2, 43));
        // 外圈的宽度
        style.strokeWidth(2);
        mapController.setMyLocationStyle(style);
    }

    private void gotoFeedBack(String fileName) {

        CommenDialogFragment commenDialogFragment = new CommenDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content", "针对此次导航，您有一份问卷需要填写");
        bundle.putString("confirmText", "开始填写");
        commenDialogFragment.setArguments(bundle);
        commenDialogFragment.setCancelable(false);
        commenDialogFragment.show(this.getFragmentManager(), "commenDialogFragment");
        commenDialogFragment.setOKListener(new CommenDialogFragment.OKListener() {
            @Override
            public void okClick() {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), FeedBackActivity.class);
                intent.putExtra("fileName", fileName);
                startActivity(intent);
                commenDialogFragment.dismiss();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusReceived(NaviJumpRouteEvent data) {
        if (data.getType() == 2) {
            AppInfo.setStartX(mStartLatlng.getLongitude());
            AppInfo.setStartY(mStartLatlng.getLatitude());
        }
        initNaviData();
        startRoute(routeType);
        if (!isFinishing()) {
            Log.d(TAG, "gotoFeedBack");
            try{
                gotoFeedBack(data.getFilePath());
            }catch (Exception e){
                LogUtil.d(TAG,e.getMessage());
            }

        }

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!isFinishing()) {
//                    Log.d(TAG, "gotoFeedBack");
//                    gotoFeedBack(data.getFilePath());
//                }
//            }
//        }, 1000);
    }

    ProgressDialog routeCalProgress;    //路径计算进度对话框

    private void showProgress() {
        if (this.routeCalProgress == null)
            this.routeCalProgress = new ProgressDialog(this);
        routeCalProgress.setProgressStyle(0);
        routeCalProgress.setIndeterminate(false);
        routeCalProgress.setCancelable(false);
        routeCalProgress.setMessage("路线请求中...");
        routeCalProgress.show();
        conBottom.setVisibility(View.GONE);
    }

    private void finishProgress() {
        if ((this.routeCalProgress != null) && (this.routeCalProgress.isShowing()))
            routeCalProgress.dismiss();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);

    }

    private void startRoute(int routeType) {
        currentItem = 0;
        AppInfo.dealNaviId(getApplicationContext());
        showProgress();
        hideFailedPopupWindow();
        mapController.clear(true);
        switch (routeType) {
            case 1:
                startCarNavigation();
                break;
            case 3:
                startTruckNavigation();
                break;
            default:
                break;
        }
    }

    private RouteFailPopupWindow routeFailPopupWindow;

    private void showFailedPopupWindow() {
        if(isFinishing()){
            return;
        }
        if (routeFailPopupWindow != null && routeFailPopupWindow.isShowing()) {
            return;
        }
        routeFailPopupWindow = new RouteFailPopupWindow(this);
        routeFailPopupWindow.showAtLocation(conParent, Gravity.BOTTOM, 0, 0);
        routeFailPopupWindow.setRouteFailListener(new RouteFailPopupWindow.RouteFailListener() {
            @Override
            public void reRoute() {
                startRoute(routeType);
                hideFailedPopupWindow();
            }
        });
    }

    private void hideFailedPopupWindow() {
        if (routeFailPopupWindow != null && routeFailPopupWindow.isShowing()) {
            routeFailPopupWindow.dismiss();
        }
    }





}
