package com.sfmap.library.container.annotation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.sfmap.plugin.IMPluginManager;

/**
 *
 */
public class ViewFinder {

	private View view;
	private Activity activity;
	private Dialog dialog;

	public ViewFinder(View view) {
		this.view = view;
	}

	public ViewFinder(Activity activity) {
		this.activity = activity;
	}

	public ViewFinder(Dialog dialog) {
		this.dialog = dialog;
	}

	public View findViewById(int id) {
		if (view != null) return view.findViewById(id);
		if (activity != null) return activity.findViewById(id);
		if (dialog != null) return dialog.findViewById(id);
		return null;
	}

	public View findViewByInfo(ViewInjectInfo info) {
		return findViewById(info.value, info.parentId);
	}

	public View findViewById(int id, int pid) {
		View pView = null;
		if (pid > 0) {
			pView = this.findViewById(pid);
		}

		View view = null;
		if (pView != null) {
			view = pView.findViewById(id);
		} else {
			view = this.findViewById(id);
		}
		return view;
	}

	public Context getContext() {
		if (view != null) return view.getContext();
		if (activity != null) return activity;
		if (dialog != null) return dialog.getContext();
		return IMPluginManager.getApplication();
	}
}
