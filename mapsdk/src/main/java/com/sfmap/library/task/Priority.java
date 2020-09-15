package com.sfmap.library.task;

/**
 * 异步任务类型。
 */
public enum Priority {
    /**
     * 任务工作在ui线程,优先级最高。
     */
    UI_TOP,
    /**
     * 任务工作在ui线程,优先级适中。
     */
    UI_NORMAL,
    /**
     * 任务工作在ui线程,优先级适最低。
     */
    UI_LOW,
    /**
     * 运行在非任务区,默认优先级。
     */
    DEFAULT,
    /**
     * 任务工作在后台,优先级最高。
     */
    BG_TOP,
    /**
     * 任务工作在后台,优先级适中。
     */
    BG_NORMAL,
    /**
     * 任务工作在后台,优先级适最低。
     */
    BG_LOW;
}
