package com.sf.collect.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.map.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sf.collect.myapplication.ShunLuDemoActivity.EXTRA_KEY_ADD_VIEW_ID;
import static com.sf.collect.myapplication.ShunLuDemoActivity.EXTRA_KEY_LATITUDE;
import static com.sf.collect.myapplication.ShunLuDemoActivity.EXTRA_KEY_LONGITUDE;

public class LocationMapPickerActivity extends AppCompatActivity implements MapController.OnCameraChangeListener {
    @BindView(R.id.mapView)
    MapView mapView;
    private MapController mapController;
    private LatLng pickedPointed;

    @OnClick(R.id.buttonPick)
    public void onPickButtonClick() {
        if(pickedPointed != null) {
            Intent startIntent = getIntent();
            Intent data = new Intent();
            data.putExtra(EXTRA_KEY_ADD_VIEW_ID, startIntent.getIntExtra(EXTRA_KEY_ADD_VIEW_ID, 0));
            data.putExtra(EXTRA_KEY_LATITUDE, pickedPointed.latitude);
            data.putExtra(EXTRA_KEY_LONGITUDE, pickedPointed.longitude);
            setResult(RESULT_OK, data);
            finish();
        } else {
            Toast.makeText(this, "先移动缩放地图选点", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map_picker);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        initMap();
    }

    private void initMap() {

        mapController = mapView.getMap();
        //设置比例尺绝对位置， 第一个参数表示横坐标，第二个表示纵坐标
        mapController.getUiSettings().setScaleControlsEnabled(true);
        mapController.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(22.524644,113.93761),
                        18)
        );
        mapController.setOnCameraChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        pickedPointed = cameraPosition.target;
    }
}