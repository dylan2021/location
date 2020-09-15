package com.sfmap.activity;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.sfmap.navi.BuildConfig;
import com.sfmap.navi.R;
import com.sfmap.util.CommUtil;

public class PermissionDialogActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final String TAG = "PermissionDialogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doPermissionsCheck();
    }

    private void doPermissionsCheck() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,

        };

        checkPermission(permissions);
    }

    private void checkPermission(String[] permissions) {
        List<String> notGrantedPermissions = new ArrayList<>();
        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permission);
            }
        }

        if(notGrantedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    notGrantedPermissions.toArray(new String[0]),
                    REQUEST_CODE_PERMISSION);
        } else {
            if(BuildConfig.DEBUG) {
                CommUtil.d(this, TAG, "All permission is granted. Finish PermissionDialogActivity");
            }
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean hasDeny = false;
        for(int result : grantResults) {
            if(result != PackageManager.PERMISSION_GRANTED) {
                hasDeny = true;
                break;
            }
        }
        if(hasDeny) {
            showExplainDialog();
            return;
        }
        finish();
    }

    private void showExplainDialog() {
        PermissionExplainDialogFragment fragment = new PermissionExplainDialogFragment();
        fragment.show(getSupportFragmentManager(), "PermissionExplainDialogFragment");
    }

    public static class PermissionExplainDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity host = getActivity();
            if(host == null) {
                throw new NullPointerException("Host activity of permission explain dialog fragment is null");
            }
            return new AlertDialog.Builder(host)
                    .setTitle(R.string.permission_request_dialog_title)
                    .setMessage(R.string.permission_request_dialog_message)
                    .setPositiveButton(android.R.string.ok,
                            (dialog, whichButton) -> {
                                Intent myAppSettings = new Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:" + host.getPackageName())
                                );
                                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(myAppSettings);
                                host.finish();
                            }
                    )
                    .setOnDismissListener(dialog -> host.finish())
                    .create();
        }

        @Override
        public void show(FragmentManager manager, String tag) {
            try {
                super.show(manager, tag);
                //Can not perform this action after onSaveInstanceState
                //If doing so, IllegalStateException will be thrown.
            } catch (IllegalStateException e) {
                FragmentTransaction ft = manager.beginTransaction();
                ft.add(this, tag);
                ft.commitAllowingStateLoss();
                e.printStackTrace();
            }
        }
    }


}

