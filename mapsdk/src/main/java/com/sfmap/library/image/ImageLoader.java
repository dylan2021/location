package com.sfmap.library.image;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.sfmap.library.Callback;
import com.sfmap.library.image.factory.DrawableFactory;
import com.sfmap.library.task.TaskHandler;

/**
 * 工厂管理类
 */
public interface ImageLoader extends TaskHandler {
    /**
     * 绑定网络图片到ImageView控件去
     * @param view              imageview
     * @param url               远程地址
     * @param factory           图片处理工厂类
     * @param fallbackResource  本地资源id
     * @param callback          图片回调
     */
    void bind(ImageView view, String url,
              DrawableFactory factory, int fallbackResource,
              Callback<Drawable> callback);
}
