package com.sf.collect.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sfmap.api.navi.model.NaviLatLng;
import com.sfmap.map.demo.R;
import com.sfmap.map.navi.TruckInfo;
import com.sfmap.route.RouteActivity;
import com.sfmap.util.KeyConst;
import com.sfmap.util.SPUtils;

import java.io.File;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShunLuDemoActivity extends Activity {
    private static final int LOCATION_PICKER_REQUEST_CODE = 10000;
    public static final String EXTRA_KEY_ADD_VIEW_ID = "addViewId";
    public static final String EXTRA_KEY_LATITUDE = "latitude";
    public static final String EXTRA_KEY_LONGITUDE = "longitude";
    private static String[] PERMISSIONS_REQUEST = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO
    };
    Unbinder unbinder;
    @BindView(R.id.edit_task_id)
    AutoEditTextView editTaskId;
    @BindView(R.id.edit_car_plate)
    AutoEditTextView editCarPlate;
    @BindView(R.id.edit_start_num)
    AutoEditTextView editStartNum;
    @BindView(R.id.edit_end_num)
    AutoEditTextView editEndNum;
    @BindView(R.id.edit_start_lat)
    AutoEditTextView editStartLat;
    @BindView(R.id.edit_start_lon)
    AutoEditTextView editStartLon;
    @BindView(R.id.edit_end_lat)
    AutoEditTextView editEndLat;
    @BindView(R.id.edit_end_lon)
    AutoEditTextView editEndLon;
    @BindView(R.id.edit_dirver_id)
    AutoEditTextView editDirverId;
    @BindView(R.id.btn_go_navi)
    Button btnGoNavi;


    @OnClick({R.id.buttonAddStart, R.id.buttonAddEnd})
    public void onLocationPickerClick(View button) {
        switch (button.getId()) {
            case R.id.buttonAddStart:
                pickerLocationFromMap(R.id.buttonAddStart);
                break;
            case R.id.buttonAddEnd:
                pickerLocationFromMap(R.id.buttonAddEnd);
                break;
        }
    }

    private void pickerLocationFromMap(int addViewId) {
        Intent intent = new Intent(this, LocationMapPickerActivity.class);
        intent.putExtra(EXTRA_KEY_ADD_VIEW_ID, addViewId);
        startActivityForResult(intent, LOCATION_PICKER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOCATION_PICKER_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                onMapLocationPicked(data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onMapLocationPicked(Intent data) {
        int viewId = data.getIntExtra(EXTRA_KEY_ADD_VIEW_ID, 0);
        if(viewId != 0) {
            double latitude = data.getDoubleExtra(EXTRA_KEY_LATITUDE, 0);
            double longitude = data.getDoubleExtra(EXTRA_KEY_LONGITUDE, 0);
            switch (viewId) {
                case R.id.buttonAddStart:
                    editStartLat.setText(formatDouble(latitude));
                    editStartLon.setText(formatDouble(longitude));
                    break;
                case R.id.buttonAddEnd:
                    editEndLat.setText(formatDouble(latitude));
                    editEndLon.setText(formatDouble(longitude));
                    break;
            }
        }
    }

    private String formatDouble(double latitude) {
        return String.format(Locale.US, "%.6g", latitude);
    }

    private String TAG = this.getClass().getSimpleName();

    TruckInfo truckInfo;
    double startlat = 22.524552;
    double startlon = 113.939944;
    double endlat = 22.983875;
    double endlon = 113.71945;
    private ShunLuDemoActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        context = this;
        SPUtils.setSPFileName(SPUtils.FILE_NAME_NAVI);
        unbinder = ButterKnife.bind(this);
        requestPermission();

        getConfigInfoFromSP();
    }

    private void getConfigInfoFromSP() {
        initSP2View(editStartLat,KeyConst.SP_START_LAT);
        initSP2View(editStartLon,KeyConst.SP_START_LNG);
        initSP2View(editEndLat,KeyConst.SP_END_LAT);
        initSP2View(editEndLon,KeyConst.SP_END_LNG);

        initSP2View(editTaskId,KeyConst.SP_TASK_ID);
        initSP2View(editDirverId,KeyConst.SP_DIRVER_ID);
        initSP2View(editCarPlate,KeyConst.SP_CAR_PLATE);
        initSP2View(editStartNum,KeyConst.SP_START_POINT_NUM);
        initSP2View(editEndNum,KeyConst.SP_END_POINT_NUM);
    }

    private void initSP2View(AutoEditTextView et, String key) {
        String value = (String) SPUtils.get(context, key, "");
        if (!TextUtils.isEmpty(value)) {
            et.setText(value);
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkPermission(Manifest.permission.READ_PHONE_STATE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS_REQUEST, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "软件退出，运行权限被禁止", Toast.LENGTH_SHORT).show();
                    Log.i("=======================", "权限" + permissions[i] + "申请失败");
                    System.exit(0);
                }
            }
            String sdkPath = Environment.getExternalStorageDirectory()
                    .getPath() + "/01SfNaviSdk";
            File file = new File(sdkPath);
            if (!file.exists()) {
                // 创建文件夹
                file.mkdirs();
            }
        }
    }

    @OnClick(R.id.btn_go_navi)
    public void onViewClicked() {
        getLatlon();
        NaviLatLng mStartLatlng = new NaviLatLng(startlat, startlon);
        NaviLatLng mEndLatlng = new NaviLatLng(endlat, endlon);
        String startLatlng = new Gson().toJson(mStartLatlng);
        String endLatlng = new Gson().toJson(mEndLatlng);
//        String truckInfo = new Gson().toJson(getTruckInfo());
        Intent intent = new Intent();
        intent.putExtra("startLatlng", startLatlng);
        intent.putExtra("endLatlng", endLatlng);

        String taskId = editTaskId.getText().toString().trim();
        String dirverId = editDirverId.getText().toString().trim();
        String carPlate = editCarPlate.getText().toString().trim();
        String destDeptCode = editEndNum.getText().toString().trim();
        String srcDeptCode = editStartNum.getText().toString().trim();

        intent.putExtra("carPlate", carPlate);
        intent.putExtra("taskId", taskId);
        intent.putExtra("driverId", dirverId);
        intent.putExtra("destDeptCode", destDeptCode);
        intent.putExtra("srcDeptCode", srcDeptCode);
//        intent.putExtra("planMode", getMode());
//        intent.putExtra("routeType", getType());
//        intent.putExtra("truckInfo", truckInfo);
        intent.setClass(getApplicationContext(), RouteActivity.class);
        startActivity(intent);


        String startPointNum = editStartNum.getText().toString();
        String endPointNum = editEndNum.getText().toString();
        //配置数据存入SP
        putConfigInfo2SP(startlat+"", startlon+"",
                endlat+"",endlon+"", taskId, dirverId,
                carPlate, startPointNum, endPointNum);
    }

    private void putConfigInfo2SP(String startlat, String startlon, String endLat,
                                  String endLng, String taskId, String dirverId,
                                  String carPlate, String startPointNum, String endPointNum) {
        SPUtils.put(context, KeyConst.SP_START_LAT, startlat);
        SPUtils.put(context, KeyConst.SP_START_LNG, startlon);
        SPUtils.put(context, KeyConst.SP_END_LAT, endLat);
        SPUtils.put(context, KeyConst.SP_END_LNG, endLng);

        SPUtils.put(context, KeyConst.SP_TASK_ID, taskId);
        SPUtils.put(context, KeyConst.SP_DIRVER_ID, dirverId);
        SPUtils.put(context, KeyConst.SP_CAR_PLATE, carPlate);
        SPUtils.put(context, KeyConst.SP_START_POINT_NUM, startPointNum);
        SPUtils.put(context, KeyConst.SP_END_POINT_NUM, endPointNum);
    }

    private void getLatlon() {
        if (TextUtils.isEmpty(editStartLat.getText().toString()) || TextUtils.isEmpty(editStartLon.getText().toString())
                || TextUtils.isEmpty(editEndLat.getText().toString()) || TextUtils.isEmpty(editEndLon.getText().toString())) {
            return;
        }
        startlat = Double.parseDouble(editStartLat.getText().toString());
        startlon = Double.parseDouble(editStartLon.getText().toString());
        endlat = Double.parseDouble(editEndLat.getText().toString());
        endlon = Double.parseDouble(editEndLon.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

