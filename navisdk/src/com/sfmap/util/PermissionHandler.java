package com.sfmap.util;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.sfmap.activity.PermissionDialogActivity;
import com.sfmap.navi.BuildConfig;

public class PermissionHandler {
    private static final String TAG = "PermissionHandler";
    private final Application mApp;

    public PermissionHandler(Application application) {
        mApp = application;
    }

    public void check() {
        boolean hasPhonePermission = ContextCompat.checkSelfPermission(mApp, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
        boolean hasLocationPermission = ContextCompat.checkSelfPermission(mApp, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean hasWriteExternalPermission = ContextCompat.checkSelfPermission(mApp, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        if(hasPhonePermission && hasLocationPermission && hasWriteExternalPermission) {
            if(BuildConfig.DEBUG) {
                CommUtil.d(mApp, TAG, "All of 3 required permissions has granted");
            }
        } else {
            startPermissionActivity();
        }
    }

    private void startPermissionActivity() {
        Intent intent = new Intent(mApp, PermissionDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mApp.startActivity(intent);
    }
}
