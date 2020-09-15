package com.sfmap.library.http;

/**
 * 进度控制接口, updateProgress方式中ProgressCallback#onLoading.
 * 默认速率每秒调用一次.
 */
public interface ProgressCallbackHandler {
    /**
     * @param total
     * @param current
     * @param forceUpdateUI
     * @return continue
     */
    boolean updateProgress(long total, long current, boolean forceUpdateUI);
}
