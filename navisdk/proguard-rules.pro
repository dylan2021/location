

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,LineNumberTable,*Annotation*,EnclosingMethod

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontusemixedcaseclassnames
-dontwarn
-ignorewarnings #这1句是屏蔽警告



-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService




-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#-keep public class com.sfmap.mapcore.*,com.sfmap.api.maps.*,com.sfmap.api.maps.model.*,com.sfmap.api.maps.offlinemap.*,com.sfmap.api.maps.overlay.*,com.sfmap.api.mapcore.*{
#public protected <fields>;
#     public protected <methods>;
#}
-keep public class com.iflytek.*{
public protected <fields>;
     public protected <methods>;
}

# navi
-keep public class com.sfmap.api.navi.HudView{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.HudViewListener{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.Navi
-keep public class com.sfmap.api.navi.Navi{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.NaviSettingDecode{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.NaviException{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.NaviListener{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.NaviView{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.NaviViewListener{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.NaviViewOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.OfflineCrossManager{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.OfflineCrossManager$OfflineCrossDownloadListener{
     public protected <fields>;
     public protected <methods>;
}
#enums
-keep public class com.sfmap.api.navi.enums.AimLessMode{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.enums.CameraType{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.enums.IconType{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.enums.NaviTTSType{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.enums.PathPlanningErrCode{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.enums.PathPlanningStrategy{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.enums.RoadClass{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.enums.RoadType{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.enums.VoiceType{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.enums.OfflineCrossDownStatus{
     public protected <fields>;
     public protected <methods>;
}

#model
-keep public class com.sfmap.api.navi.model.NaviLaneInfo{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviCamera{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviCross{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviGuide{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviLink{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviLocation{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviPath{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviStep{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviTrafficFacilityInfo{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviTrafficStatus{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviInfo{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviLatLng{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviInfo{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviServiceFacilityInfo{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.OfflineCrossInfo{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.model.NaviAvoidBound{
     public protected <fields>;
     public protected <methods>;
}

#view
-keep public class com.sfmap.api.navi.view.HudMirrorImage{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.view.CustomTmcView{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.view.DirectionView{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.view.DriveWayView{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.view.NaviRoadEnlargeView{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.navi.view.RouteOverLay{
     public protected <fields>;
     public protected <methods>;
}

#tbt
-keep public class com.sfmap.tbt.AvoidJamArea{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.Camera{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.CarLocation{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.DGNaviInfo{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.tbt.RouteLink{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.RoutePath{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.RouteSegment{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.RouteIncident{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.ServiceFacilityInfo{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.TBT{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.NaviUtilDecode{
     public protected <fields>;
     public protected <methods>;
}

-keep public interface com.sfmap.tbt.IFrameForTBT{
    public protected <fields>;
    public protected <methods>;
}

-keep public class com.sfmap.tbt.FrameForTBT{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tbt.TmcBarItem{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.tbt.MPolygon{
      public protected <fields>;
      public protected <methods>;
}
-keep public class com.sfmap.tbt.LinkStatus{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.GroupSegment{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tbt.NaviGuideItem{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tbt.RestAreaInfo{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tbt.RestrictionArea{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.tbt.NaviStaticInfo{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tbt.GPSDataInfo{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tbt.GeoPoint{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tbt.NaviAction{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tbt.JamInfo{
    public protected <fields>;
    public protected <methods>;
}

-keep public class com.sfmap.tbt.TrafficFacilityInfo{
     public protected <fields>;
     public protected <methods>;
}

#wtbt
-keep public class com.sfmap.api.navi.model.CarLocation{
     public protected <fields>;
     public protected <methods>;
}
# AndroidEventBus
-keep class org.simple.** { *;}
-keep interface org.simple.** { *;}
-keepclassmembers class * {
    @org.simple.eventbus.Subscriber <methods>;
}

#-------------------Navisdk混淆-------------------
-keep public class com.sfmap.api.navi.model.** { *; }
-keep public class com.sfmap.tbt.util.AppInfo{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tbt.loc.GPS_FGService

-keep public class com.sfmap.tbt.loc.GPS_FGService$addLocationListener{ *; }
-keep public class com.sfmap.tbt.loc.GPS_FGService$LocalBinder{ *; }
-keep public class com.sfmap.tbt.loc.GPS_FGService{
    public protected <fields>;
    public protected <methods>;
}

-keep public class com.sfmap.api.navi.model.NaviLatLng
-keep public class com.sfmap.api.navi.model.NaviLatLng{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.map.navi.LogcatFileManager
-keep public class com.sfmap.map.navi.LogcatFileManager{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.map.navi.TruckInfo
-keep public class com.sfmap.map.navi.TruckInfo{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.SfNaviSDK
-keep public class com.sfmap.SfNaviSDK{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.util.KeyConst
-keep public class com.sfmap.util.KeyConst{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.util.AppInfo
-keep public class com.sfmap.util.AppInfo{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.route.RouteActivity
-keep public class com.sfmap.route.RouteActivity{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.map.navi.NaviActivity
-keep public class com.sfmap.map.navi.NaviActivity{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.util.SPUtils
-keep public class com.sfmap.util.SPUtils{
    public protected <fields>;
    public protected <methods>;
}

-keep public interface com.sfmap.api.location.SfMapLocationListener
-keep public interface com.sfmap.api.location.SfMapLocationListener{
    public protected <fields>;
    public protected <methods>;
}

-keep public class com.sfmap.tbt.DeviceIdManager
-keep public class com.sfmap.tbt.DeviceIdManager{
    public protected <fields>;
    public protected <methods>;
}

-keep public class com.sfmap.tts.SpeechSynthesizer
-keep public class com.sfmap.tts.SpeechSynthesizer{
    public protected <fields>;
    public protected <methods>;
}

-keep public class com.sfmap.tts.LYSpeechSynthesizer
-keep public class com.sfmap.tts.LYSpeechSynthesizer{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tts.HciCloudSysHelper
-keep public class com.sfmap.tts.HciCloudSysHelper{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sfmap.tts.** { *; }
-keep public class com.sfmap.tts.TtsManager
-keep public class com.sfmap.tts.TtsManager{
    public protected <fields>;
    public protected <methods>;
}
-keep public class com.sinovoice.hcicloudsdk.common.** { *; }
-keepclassmembers class ** {
    public void *(**);
}
-keepclassmembers class ** {
    public void onEvent*(**);
}

-keep public class com.sfmap.tbt.util.LogUtil
-keep public class com.sfmap.tbt.util.LogUtil{
    public protected <fields>;
    public protected <methods>;
}