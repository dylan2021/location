package com.sfmap.route.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import com.sfmap.library.http.HttpAsyncTask;
import com.sfmap.plugin.app.IMPageHelper;

public class DialogHelper {

    public synchronized static Dialog createLoadingDialog(Context context,final HttpAsyncTask<?> task, String msg) {
        final Dialog dialog = new ProgressDlg((Activity) context, msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (task != null) {
                    task.cancel();
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }
}
