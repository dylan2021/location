package com.sfmap.library.task;


import com.sfmap.library.Callback;

/**
 * (功能说明)
 * <p/>
 */
public interface TaskHandler extends Callback.Cancelable {

    boolean supportPause();

    boolean supportResume();

    boolean supportCancel();

    void pause();

    void resume();

    boolean isPaused();

    boolean isStopped();
}
