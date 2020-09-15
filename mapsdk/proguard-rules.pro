

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontusemixedcaseclassnames

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends android.view.ContextThemeWrapper
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends sfmap.plugin.core.ctx.IMPlugin


# 保留Activity中的方法参数是view的方法，
# 从而我们在layout里面编写onClick就不会影响
-keepclassmembers class * extends android.app.Activity {
public void * (android.view.View);
}

# 保留自定义控件(继承自View)不能被混淆
-keep public class * extends android.view.View {
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
public void set*(***);
*** get* ();
}


-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# 保留Serializable 序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

-keepattributes *Annotation*
#keep所有的注解
-keep class * extends java.lang.annotation.Annotation { *; }


-keep class org.apache.commons.**{*;}
-keep class dalvik.system.**{*;}
-keep class com.google.a.**{*;}
-keep class com.mato.**{*;}
-keep class com.ta.utdid2.**{*;}
-keep class com.ut.device.**{*;}
-keep class android.support.v4.**{*;}
-keep class sfmap.plugin.core.ctx.IMPlugin{*;}
-keep class com.sfmap.library.**{*;}
-keep class com.sfmap.plugin.**{*;}



#el表达式
-keep class org.xidea.el.**{*;}
#adcode
-keep class sfmap.adcode.**{*;}
#animation
-keep class sfmap.library.animation.AnimationFactory{*;}
#注解
-keep class sfmap.library.container.action.Action{*;}
-keep class sfmap.library.container.action.ViewInject{*;}
-keep class sfmap.library.container.annotation.ViewInjector{*;}
#container
-keep class sfmap.library.container.DialogNodeFragment{*;}
-keep class sfmap.library.container.FragmentContainer{*;}
-keep class sfmap.library.container.FragmentContainerDelegater{*;}
-keep class sfmap.library.container.FragmentContainer.IntentControlerDelegater{*;}
-keep class sfmap.library.container.FragmentContainer$*
-keep class sfmap.library.container.LaunchMode{*;}
-keep class sfmap.library.container.LaunchMode$*
-keep class sfmap.library.container.NodeFragment{*;}
-keep class sfmap.library.container.PerformanceAnalyzer{*;}
-keep class sfmap.library.container.NodeFragmentBundle{*;}
#hardware
-keep class sfmap.library.hardware.RotationListener{*;}
-keep class sfmap.library.hardware.SensorHelper{*;}
-keep public class sfmap.library.hardware.SensorHelper$*
#http
-keep class sfmap.library.http.params.ParamEntity{*;}
-keep class sfmap.library.http.url.URLBuilder{*;}
-keep class sfmap.library.http.HttpAsyncTask{*;}
-keep class sfmap.library.http.Network{*;}
-keep class sfmap.library.http.NetworkImpl{*;}
-keep class sfmap.library.http.cache.HttpCacheEntry{*;}
#image
-keep class sfmap.library.image.ImageLoader{*;}
-keep class sfmap.library.image.ImageImpl{*;}
-keep class sfmap.library.image.factory.DrawableFactory{*;}
#io
-keep class sfmap.library.io.KeyValueStorage{*;}
-keep class sfmap.library.io.SQLiteMapper{*;}
-keep class sfmap.library.io.StorageFactory{*;}
-keep class sfmap.library.io.WebStorage{*;}
#model
-keep class sfmap.library.model.GeoPoint{*;}
-keep class sfmap.library.model.PointD{*;}
-keep public class sfmap.library.model.GeoPoint$*
#task
-keep class sfmap.library.task.Priority{*;}
-keep class sfmap.library.task.PriorityAsyncTask{*;}
#util
-keep class sfmap.library.util.AppUtil{*;}
-keep class sfmap.library.util.CalculateUtil{*;}
-keep class sfmap.library.util.DateTimeUtil{*;}
-keep class sfmap.library.util.DeviceUtil{*;}
-keep class sfmap.library.util.FileUtil{*;}
-keep class sfmap.library.util.FontSizeUtils{*;}
-keep class sfmap.library.util.ImageUtil{*;}
-keep class sfmap.library.util.JsonHelper{*;}
-keep class sfmap.library.util.MD5Util{*;}
-keep class sfmap.library.util.NetworkUtil{*;}
-keep class sfmap.library.util.Projection{*;}
-keep class sfmap.library.util.ResUtil{*;}
-keep class sfmap.library.util.ToastHelper{*;}
-keep class sfmap.library.util.ZipUtils{*;}
-keep class sfmap.library.http.utils.OtherUtils{*;}
#lib
-keep class sfmap.library.Callback{*;}
-keep public class sfmap.library.Callback$*
-keep class sfmap.library.Page{*;}

-keepnames class org.xidea.el.json.JSONDecoder$* {
    public <fields>;
    public <methods>;
}

-keep public class * implements java.io.Serializable{
    public protected private *;
}

-keep interface sfmap.http.app.builder.ParamEntity { *; }
-keep class * implements sfmap.http.app.builder.ParamEntity{
    public protected private *;
}
-keepclassmembers class * extends java.lang.annotation.Annotation{ *; }

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepnames public class sfmap.library.model.GeoPoint,sfmap.library.model.GeoPoint$* {
    public <fields>;
    public <methods>;
}

-keepnames public class sfmap.library.Callback, sfmap.library.Callback$* {
    public <fields>;
    public <methods>;
}

-keepnames public class sfmap.library.container.LaunchMode,sfmap.library.container.LaunchMode$*{
    public <fields>;
    public <methods>;
}

-keepnames public  class sfmap.library.hardware.SensorHelper,sfmap.library.hardware.SensorHelper$*{
    public <fields>;
    public <methods>;
}

-keepnames public class sfmap.library.container.FragmentContainer,sfmap.library.container.FragmentContainer$*{
    public <fields>;
    public <methods>;
}
# 对R文件下的所有类及其方法，都不能被混淆
-keep public class com.foresee.R$*{
public static final int *;
}

# 对于带有回调函数onXXEvent的，不能混淆
-keepclassmembers class * {
void *(**On*Event);
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

 #maps
-keep public class com.sfmap.api.maps.MapController{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.MapController$OnCacheRemoveListener{
     public  <methods>;
}
-keep public class com.sfmap.api.maps.MapController$OnMapLevelChangeListener{
     public  <methods>;
}
-keep public abstract interface com.sfmap.api.maps.MapController$OnPOIClickListener,com.sfmap.api.maps.MapController$OnMapLoadedListener,com.sfmap.api.maps.MapController$GridUrlListener{
     public  <methods>;
}
-keep public abstract interface com.sfmap.api.maps.MapController$OnMapTouchListener,com.sfmap.api.maps.MapController$OnMapClickListener,com.sfmap.api.maps.MapController$OnMapScreenShotListener{
     public  <methods>;
}
-keep public abstract interface com.sfmap.api.maps.MapController$OnMapLongClickListener,com.sfmap.api.maps.MapController$OnCameraChangeListener,com.sfmap.api.maps.MapController$OnMarkerClickListener{
     public  <methods>;
}
-keep public abstract interface com.sfmap.api.maps.MapController$OnPolylineClickListener,com.sfmap.api.maps.MapController$OnMarkerDragListener,com.sfmap.api.maps.MapController$OnInfoWindowClickListener{
     public  <methods>;
}
-keep public class com.sfmap.api.maps.MapController$CancelableCallback,com.sfmap.api.maps.MapController$OnMyLocationChangeListener,com.sfmap.api.maps.MapController$InfoWindowAdapter{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.MapException{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.MapOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.MapOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.MapUtils{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.MapView{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.CameraUpdate{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.CameraUpdateFactory{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.CustomRenderer{
     public protected <methods>;
}
-keep public interface com.sfmap.api.maps.LocationSource,com.sfmap.api.maps.LocationSource$OnLocationChangedListener{
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.MapsInitializer{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.Projection{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.SupportMapFragment{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.TextureMapView{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.TextureSupportMapFragment{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.UiSettings{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.TextureMapFragment{
     public protected <fields>;
     public protected <methods>;
}
# maps

# overlay
-keep public class com.sfmap.api.maps.cluster.**{*;}

-keep public class com.sfmap.api.maps.overlay.BusLineOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.overlay.BusRouteOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.overlay.DrivingRouteOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.overlay.PoiOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.overlay.WalkRouteOverlay{
     public protected <fields>;
     public protected <methods>;
}
# overlay


# offlinemap
-keep public class com.sfmap.api.maps.offlinemap.City{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.offlinemap.OfflineMapCity{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.offlinemap.OfflineMapManager{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.offlinemap.OfflineMapManager$OfflineMapDownloadListener{
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.offlinemap.OfflineMapProvince{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.offlinemap.Province{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.offlinemap.OfflineMapStatus{
     public protected <fields>;
     public protected <methods>;
}
# offlinemap


# model
-keep public class com.sfmap.api.maps.model.MapGLOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Arc{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.ArcOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.ArcOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.BitmapDescriptor{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.BitmapDescriptorCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.BitmapDescriptorFactory{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.CameraPosition,com.sfmap.api.maps.model.CameraPosition$Builder{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.CameraPositionCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Circle{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.CircleOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.CircleOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Gradient{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.GroundOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.GroundOverlayOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.GroundOverlayOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.HeatmapTileProvider,com.sfmap.api.maps.model.HeatmapTileProvider$Builder{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.LatLng{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.LatLngBounds,com.sfmap.api.maps.model.LatLngBounds$Builder{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.LatLngBoundsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Marker{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.LatLngCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.MarkerOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.MarkerOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.MyLocationStyle{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.MyLocationStyleCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.MyTrafficStyle{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.NavigateArrow{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.NavigateArrowOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.NavigateArrowOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.Poi{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.PoiCreator{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.Polygon{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.PolygonOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.PolygonOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Polyline{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.PolylineOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.PolylineOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.maps.model.RuntimeRemoteException{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Text{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TextOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TextOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.Tile{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileOverlay{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileOverlayOptions{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileOverlayOptionsCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileProjection{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileProjectionCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.TileProvider{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.UrlTileProvider{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.VisibleRegion{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.VisibleRegionCreator{
     public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.maps.model.WeightedLatLng{
     public protected <fields>;
     public protected <methods>;
}

# model

-keep public class com.sfmap.mapcore.FPoint{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.mapcore.Tile{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.mapcore.NativeLineRenderer{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.mapcore.DPoint{
     public protected <fields>;
     public protected <methods>;
}


-keep public class com.sfmap.mapcore.IPoint{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.mapcore.MapPoi{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.mapcore.MapCore{
     public protected private <fields>;
     public protected private <methods>;
}
-keep public class com.sfmap.mapcore.MapProjection{
     public protected private <fields>;
     public protected private <methods>;
}
#extral
-keep public class com.sfmap.api.maps.ExtralBaseDraw{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawBitmap{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawPolygon{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawPolyline{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawText{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawArc{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.ExtralDrawCircle{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.mapcore.util.AppInfo{
     public   <fields>;
     public   <methods>;
}
-keep public class com.sfmap.api.maps.DesUtil{
     public   <fields>;
     public   <methods>;
}


#extral 搜素混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep public class com.sfmap.api.services.core.SearchException, com.sfmap.api.services.core.SearchException$* {
      public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.services.core.LatLonPoint {

     public protected <methods>;
      public protected <fields>;
}

# geocodesearch
-keep public class com.sfmap.api.services.geocoder.GeocodeSearch {
     public protected <fields>;
     public protected <methods>;
}
# geocode
-keep public class com.sfmap.api.services.geocoder.GeocodeAddress, com.sfmap.api.services.geocoder.GeocodeQuery, com.sfmap.api.services.geocoder.GeocodeResult{
     public protected <methods>;
       public protected <fields>;
}
-keep public interface com.sfmap.api.services.geocoder.GeocodeSearch,com.sfmap.api.services.geocoder.GeocodeSearch$OnGeocodeSearchListener{
     public protected <methods>;
      public protected <fields>;
}

# regeocode
-keep public class com.sfmap.api.services.geocoder.RegeocodeAddress, com.sfmap.api.services.geocoder.RegeocodeQuery, com.sfmap.api.services.geocoder.RegeocodeResult{
     public protected <methods>;
       public protected <fields>;
}

-keep public class com.sfmap.api.services.geocoder.RegeocodeAddress{
     public protected <fields>;
     public protected <methods>;
}

#district begin
-keep public class com.sfmap.api.services.district.DistrictItem, com.sfmap.api.services.district.DistrictResult, com.sfmap.api.services.district.DistrictSearchQuery{
     public protected <methods>;
       public protected <fields>;
}

-keep public class com.sfmap.api.services.district.DistrictSearch, com.sfmap.api.services.district.DistrictSearch$* {
     public protected <fields>;
     public protected <methods>;
}
#district end


#busline begin
-keep public class com.sfmap.api.services.busline.BusLineItem, com.sfmap.api.services.busline.BusLineResult, com.sfmap.api.services.busline.BusLineQuery{
     public protected <methods>;
       public protected <fields>;
}

-keep public class com.sfmap.api.services.busline.BusLineSearch, com.sfmap.api.services.busline.BusLineSearch$* {
     public protected <fields>;
     public protected <methods>;
}


-keep public class com.sfmap.api.services.busline.BusLineQuery$SearchType {
     public protected <fields>;
     public protected <methods>;
}


#busline end

#busstation begin
-keep public class com.sfmap.api.services.busline.BusStationItem, com.sfmap.api.services.busline.BusStationResult, com.sfmap.api.services.busline.BusStationQuery{
    public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.services.busline.BusStationSearch, com.sfmap.api.services.busline.BusStationSearch$* {
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.services.busline.BusStationQuery$StopSearchType {
     public protected <fields>;
     public protected <methods>;
}
#busstation end


#inputtips begin
-keep public class com.sfmap.api.services.help.Tip,com.sfmap.api.services.help.InputtipsQuery{
     public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.services.help.Inputtips, com.sfmap.api.services.help.Inputtips$* {
     public protected <fields>;
     public protected <methods>;
}


#route begin
-keep public class com.sfmap.api.services.route.RouteSearch, com.sfmap.api.services.route.Path,com.sfmap.api.services.route.WalkPath{
     public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.route.DrivePath, com.sfmap.api.services.route.BusPath,com.sfmap.api.services.route.Step,com.sfmap.api.services.route.DriveStep{
     public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.route.WalkStep,com.sfmap.api.services.route.BusStep,com.sfmap.api.services.route.RoutePoint{
     public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.route.WalkRouteResult, com.sfmap.api.services.route.BusRouteResult,com.sfmap.api.services.route.DriveRouteResult,com.sfmap.api.services.route.RouteResult{
     public protected <methods>;
     public protected <fields>;
}

-keep public class com.sfmap.api.services.route.Doorway,com.sfmap.api.services.route.RouteBusLineItem,com.sfmap.api.services.route.RouteBusWalkItem{
     public protected <methods>;
     public protected <fields>;
}

-keep public class com.sfmap.api.services.route.RouteSearch$BusRouteQuery {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.route.RouteSearch$DriveRouteQuery {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.route.RouteSearch$FromAndTo {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.route.RouteSearch$WalkRouteQuery {
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.sfmap.api.services.route.Route$FromAndTo {
      public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.services.route.Route {
      public protected <fields>;
     public protected <methods>;
}


-keep public class com.sfmap.api.services.route.Segment, com.sfmap.api.services.route.Segment$* {
      public protected <fields>;
     public protected <methods>;
}
-keep public interface com.sfmap.api.services.route.RouteSearch$OnRouteSearchListener{
     public protected <methods>;
     public protected <fields>;
}

#route end

#poisearch
-keep public class com.sfmap.api.services.poisearch.PoiPagedResult, com.sfmap.api.services.poisearch.PoiPagedResult$*{
      public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.services.poisearch.PoiResult, com.sfmap.api.services.poisearch.PoiResult$*{
     public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.poisearch.PoiItem, com.sfmap.api.services.poisearch.PoiItem$*{
      public protected <fields>;
     public protected <methods>;
}
-keep public class com.sfmap.api.services.poisearch.PoiSearch$Query {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.poisearch.PoiSearch$SearchBound  {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.poisearch.PoiSearch$OnPoiSearchListener  {
      public protected <methods>;
      public protected <fields>;
}
-keep public class com.sfmap.api.services.poisearch.PoiSearch {
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.sfmap.api.services.poisearch.PoiTypeDef, com.sfmap.api.services.poisearch.PoiTypeDef$* {
      public protected <fields>;
     public protected <methods>;
}

-keep public class com.sfmap.api.services.poisearch.ComplexSearch {
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.sfmap.api.services.poisearch.ComplexSearch$Query {
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.sfmap.api.services.poisearch.ComplexSearch$OnComplexSearchListener {
      public protected <methods>;
      public protected <fields>;
}

-keep public class com.sfmap.api.services.poisearch.ComplexSearchResult {
      public protected <methods>;
      public protected <fields>;
}

# poisearch end


#cloud

-keep public class com.sfmap.api.services.cloud.CloudStorage,com.sfmap.api.services.cloud.CloudStorage$OnCloudStorageListener{
    public protected <methods>;
     public protected <fields>;
}

-keep public class com.sfmap.api.services.cloud.CloudStorageResult,com.sfmap.api.services.cloud.DBFieldInfo,com.sfmap.api.services.cloud.DBFieldInfo$FieldType{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudItem,com.sfmap.api.services.cloud.CloudDatasetItem{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudSearch,com.sfmap.api.services.cloud.CloudSearch$OnCloudSearchListener{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudSearch$SearchType,com.sfmap.api.services.cloud.CloudSearch$Query{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudDatasetSearch,com.sfmap.api.services.cloud.CloudDatasetSearch$OnCloudDatasetSearchListener{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudDatasetSearch$SearchType,com.sfmap.api.services.cloud.CloudDatasetSearch$Query{
    public protected <methods>;
     public protected <fields>;
}
-keep public class com.sfmap.api.services.cloud.CloudDatasetSearchResult,com.sfmap.api.services.cloud.CloudSearchResult{
    public protected <methods>;
     public protected <fields>;
}
#cloud end


-keep public class com.sfmap.api.services.core.ServerUrlSetting, com.sfmap.api.services.core.ServerUrlSetting$* {
      public protected <fields>;
      public protected <methods>;
}

# localpoisearh
-keep public class com.sfmap.api.services.localsearch.ADCodeLevel {
      public protected <fields>;
      public protected <methods>;
}
-keep public class com.sfmap.api.services.localsearch.LocalPoiResult {
      public protected <fields>;
      public protected <methods>;
}
-keep public class com.sfmap.api.services.localsearch.LocalPoiSearch, com.sfmap.api.services.localsearch.LocalPoiSearch$*{
      public protected <fields>;
      public protected <methods>;
}
-keep public class com.sfmap.api.services.localsearch.SearchType {
      public protected <fields>;
      public protected <methods>;
}
-keep public class com.sfmap.api.services.localsearch.model.SearchResultInfo {
      public protected <fields>;
      public protected <methods>;
}
-keep  class com.sfmap.api.services.localsearch.SearchCore {
      public protected <fields>;
      public protected <methods>;
}



-keepattributes *Annotation*
#保留和点击事件相关的Action注解

-keepclassmembernames @com.sfmap.basemodule.SQLiteMapper$SQLiteEntry class * {
    *;
}



-keep interface com.sfmap.basemodule.URLBuilder { *; }
-keep class * implements com.sfmap.basemodule.URLBuilder{*; }


#
# This ProGuard configuration file specifies how annotations can be used
# to configure the processing of other code.
# Usage:

#
# Note that the other input/output options still have to be specified.
# If you specify them in a separate file, you can simply include this file:
#     -include annotations.pro
#
# You can add any other options that are required. For instance, if you are
# processing a library, you can still include the options from library.pro.


# The annotations are defined in the accompanying jar. For now, we'll start
# with these. You can always define your own annotations, if necessary.



# The following annotations can be specified with classes and with class
# members.

# @Keep specifies not to shrink, optimize, or obfuscate the annotated class
# or class member as an entry point.

-keep @proguard.annotation.Keep class *

-keepclassmembers class * {
    @proguard.annotation.Keep *;
}


# @KeepName specifies not to optimize or obfuscate the annotated class or
# class member as an entry point.

-keepnames @proguard.annotation.KeepName class *

-keepclassmembernames class * {
    @proguard.annotation.KeepName *;
}


# The following annotations can only be specified with classes.

# @KeepImplementations and @KeepPublicImplementations specify to keep all,
# resp. all public, implementations or extensions of the annotated class as
# entry points. Note the extension of the java-like syntax, adding annotations
# before the (wild-carded) interface name.

-keep        class * implements @proguard.annotation.KeepImplementations       *
-keep public class * implements @proguard.annotation.KeepPublicImplementations *

# @KeepApplication specifies to keep the annotated class as an application,
# together with its main method.

-keepclasseswithmembers @proguard.annotation.KeepApplication public class * {
    public static void main(java.lang.String[]);
}

# @KeepClassMembers, @KeepPublicClassMembers, and
# @KeepPublicProtectedClassMembers specify to keep all, all public, resp.
# all public or protected, class members of the annotated class from being
# shrunk, optimized, or obfuscated as entry points.

-keepclassmembers @proguard.annotation.KeepClassMembers class * {
    *;
}

-keepclassmembers @proguard.annotation.KeepPublicClassMembers class * {
    public *;
}

-keepclassmembers @proguard.annotation.KeepPublicProtectedClassMembers class * {
    public protected *;
}

# @KeepClassMemberNames, @KeepPublicClassMemberNames, and
# @KeepPublicProtectedClassMemberNames specify to keep all, all public, resp.
# all public or protected, class members of the annotated class from being
# optimized or obfuscated as entry points.

-keepclassmembernames @proguard.annotation.KeepClassMemberNames class * {
    *;
}

-keepclassmembernames @proguard.annotation.KeepPublicClassMemberNames class * {
    public *;
}

-keepclassmembernames @proguard.annotation.KeepPublicProtectedClassMemberNames class * {
    public protected *;
}

# @KeepGettersSetters and @KeepPublicGettersSetters specify to keep all, resp.
# all public, getters and setters of the annotated class from being shrunk,
# optimized, or obfuscated as entry points.

-keepclassmembers @proguard.annotation.KeepGettersSetters class * {
    void set*(***);
    void set*(int, ***);

    boolean is*();
    boolean is*(int);

    *** get*();
    *** get*(int);
}

-keepclassmembers @proguard.annotation.KeepPublicGettersSetters class * {
    public void set*(***);
    public void set*(int, ***);

    public boolean is*();
    public boolean is*(int);

    public *** get*();
    public *** get*(int);
}




