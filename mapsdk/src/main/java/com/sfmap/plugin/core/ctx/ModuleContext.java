package com.sfmap.plugin.core.ctx;

import android.annotation.TargetApi;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import com.sfmap.plugin.IMPluginManager;
import com.sfmap.plugin.core.install.Config;
import com.sfmap.plugin.util.ReflectUtil;

import java.io.File;

/**
 */
public final class ModuleContext extends ContextThemeWrapper {

	/*package*/ final ModuleClassLoader classLoader;
	private LayoutInflater layoutInflater;
	private Resources.Theme theme;
	private AssetManager assetManager;
	private Resources resources;
	private Configuration overrideConfiguration;

	public ModuleContext(File pluginFile, Config config) {
		super(IMPluginManager.getApplication(), 0);

		// init classLoader
		classLoader = new ModuleClassLoader(pluginFile, config);

		// init assets
		{
			String apkPath = pluginFile.getAbsolutePath();
			try {
				this.assetManager = AssetManager.class.newInstance();
			} catch (Throwable ex) {
				throw new RuntimeException("IMPlugin init failed", ex);
			}
			if (assetManager != null) {
				int cookie = ReflectUtil.addAssetPath(assetManager, apkPath);
				if (cookie == 0) {
					throw new RuntimeException(
						"IMPlugin init failed: addAssets Failed:" + apkPath + "#" + cookie);
				}
			}
		}
	}

	@Override
	public Object getSystemService(String name) {
		if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
			if (layoutInflater == null) {
				LayoutInflater service = (LayoutInflater) super.getSystemService(name);
				this.layoutInflater = service.cloneInContext(this);
			}
			return layoutInflater;
		}
		return super.getSystemService(name);
	}

	@Override
	public Resources.Theme getTheme() {
		if (this.theme == null) {
			Resources.Theme oldTheme = super.getTheme();
			this.theme = this.getResources().newTheme();
			this.theme.setTo(oldTheme);
		}
		return this.theme;
	}

	@Override
	public AssetManager getAssets() {
		return assetManager;
	}

	@Override
	public Resources getResources() {
		if (resources == null) {
			Resources parent = super.getResources();
			resources =
				new ResourcesProxy(
					assetManager,
					parent.getDisplayMetrics(),
					overrideConfiguration == null ? parent.getConfiguration() : overrideConfiguration);
		}
		return resources;
	}

	@Override
	public void applyOverrideConfiguration(Configuration overrideConfiguration) {
		this.overrideConfiguration = new Configuration(overrideConfiguration);
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void registerComponentCallbacks(ComponentCallbacks callback) {
		IMPluginManager.getApplication().registerComponentCallbacks(callback);
	}

	@Override
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void unregisterComponentCallbacks(ComponentCallbacks callback) {
		IMPluginManager.getApplication().unregisterComponentCallbacks(callback);
	}

	@Override
	public Context getApplicationContext() {
		return this;
	}
}
