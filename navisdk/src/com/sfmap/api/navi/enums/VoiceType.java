package com.sfmap.api.navi.enums;

/**
 * 导航语音中的声音类型
 */
public class VoiceType {

    /**
     * 0 - 无类型或类型未知。
     */
    public final static int VOICETYPE_NULL                   = 0;

    /**
     * 1001 - 基本导航播报--结束导航。
     */
    public final static int VOICETYPE_BASEACTION_END         = 1001;

    /**
     * 1002 - 基本导航播报--开始导航。
     */
    public final static int VOICETYPE_BASEACTION_START       = 1002;

    /**
     * 1003 - 基本导航播报--到达途经点。
     */
    public final static int VOICETYPE_BASEACTION_MID         = 1003;

    /**
     * 1004 - 基本导航播报--偏航。
     */
    public final static int VOICETYPE_BASEACTION_LEEWAY      = 1004;

    /**
     * 2001 - 基本导航播报--隧道。
     */
    public final static int VOICETYPE_BASEACTION_TUNNEL      = 2001;

    /**
     * 2002 - 基本导航播报--环岛。
     */
    public final static int VOICETYPE_BASEACTION_ROUNDABOUT  = 2002;

    /**
     * 2003 - 基本导航播报--服务区。
     */
    public final static int VOICETYPE_BASEACTION_SERVICEAREA = 2003;

    /**
     * 2004 - 基本导航播报--收费站。
     */
    public final static int VOICETYPE_BASEACTION_TOLLGATE    = 2004;

    /**
     * 2005 - 基本导航播报--请您调头行驶。
     */
    public final static int VOICETYPE_BASEACTION_TURNAROUND  = 2005;

    /**
     * 2006 - 基本导航播报--在听到下次语音播报前,请保持直行。
     */
    public final static int VOICETYPE_BASEACTION_LONGPLAY    = 2006;
    /**
     * 2007 - 基本导航播报--普通的左转右转。
     */
    public final static int VOICETYPE_BASEACTION_NOMARL      = 2007;

    /**
     * 3001 - 电子眼播报--电子眼。
     */
    public final static int VOICETYPE_CAMERA_ARRIVE          = 3001;

    /**
     * 3002 - 电子眼播报--电子眼+超速提醒。
     */
    public final static int VOICETYPE_CAMERA_ARRIVE_SPEED    = 3002;

    /**
     * 3003 - 电子眼播报--离开电子眼。
     */
    public final static int VOICETYPE_CAMERA_LEAVE           = 3003;

    /**
     * 3004 - 电子眼播报--离开电子眼+超速提醒。
     */
    public final static int VOICETYPE_CAMERA_LEAVE_SPEED     = 3004;

    /**
     * 4001 - 信息播报--交通事件。
     */
    public final static int VOICETYPE_INFOMATION_TRAFFICINFO    = 4001;

    /**
     * 4005 - 信息播报--请减速。
     */
    public final static int VOICETYPE_INFOMATION_SWERVESLOWDOWN = 4005;

    /**
     * 4006 - 信息播报--车道线。
     */
    public final static int VOICETYPE_CAMERA_INFOMATION_LANE    = 4006;

    /**
     * 5000 - 信息播报--道路附属信息。
     */
    public final static int VOICETYPE_PLAYSTYLE_FURNITURE       = 5000;

    /**
     * 5001 - 信息播报--整点报时。
     */
    public final static int VOICETYPE_PLAYSTYLE_TIME            = 5001;
}
