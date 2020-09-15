package com.sfmap.map.core;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.MeasureSpec;

import com.sfmap.navi.R;


/**
 * Marker图标纹理加载。
 */
public class OverlayMarker {

    public static final int MARKER_NAVI_END = 11;

    public static final int MARKER_START = 12;
    public static final int MARKER_END = 13;
    public static final int MARKER_NAVIDST = 15;

    public static final int MARKER_MID = 16;

    public static final int MARKER_POI_1 = 130;
    public static final int MARKER_POI_2 = 131;
    public static final int MARKER_POI_3 = 132;
    public static final int MARKER_POI_4 = 133;
    public static final int MARKER_POI_5 = 134;
    public static final int MARKER_POI_6 = 135;
    public static final int MARKER_POI_7 = 136;
    public static final int MARKER_POI_8 = 137;
    public static final int MARKER_POI_9 = 138;
    public static final int MARKER_POI_10 = 139;
    public static final int MARKER_POI_11 = 140;

    public static final int MARKER_POI_1_hl = 150;
    public static final int MARKER_POI_2_hl = 151;
    public static final int MARKER_POI_3_hl = 152;
    public static final int MARKER_POI_4_hl = 153;
    public static final int MARKER_POI_5_hl = 154;
    public static final int MARKER_POI_6_hl = 155;
    public static final int MARKER_POI_7_hl = 156;
    public static final int MARKER_POI_8_hl = 157;
    public static final int MARKER_POI_9_hl = 158;
    public static final int MARKER_POI_10_hl = 159;

    public static final int MARKER_POI_BUBBLE = 400;

    public static final int MARKER_BUS_START = 50;
    public static final int MARKER_BUS_END = 51;

    public static final int MARKER_BUBBLE_WRONGCHECK = 70;
    public static final int MARKER_BUBBLE_WRONGPOI = 71;

    public static final int MARKER_VACANT = 72;
    // 公交特型一次聚类标识
    public static final int MARKER_CHILD_POINT_SELECT = 80;

    public static final int MARKER_POI_MARK = 520;
    public static final int MARKER_POI_MARK_HL = 521;

    public static final int MARKER_TMC_BUBBLE = 361;

    public static final int MARKER_BUSSTATION = 1004;
    public static final int MARKER_SAVE = 1006;
    public static final int MARKER_GPSTRACKER = 1007;
    public static final int MARKER_NAVI_CAR = 1009;
    public static final int MARKER_GPS_SHINE = 1010;
    public static final int MARKER_GPS_NO_SENSOR = 1011;
    public static final int MARKER_GPS_VALID = 1012;
    public static final int MARKER_GPS_3D = 1013;
    public static final int MARKER_ARC = 1015;

    public static final int MARKER_CAMERA = 1016;

    public static final int MARKER_TURNPOINT_BUS = 1017;
    public static final int MARKER_TURNPOINT_SUBWAY = 1018;
    public static final int MARKER_TURNPOINT_FOOT = 1019;
    // 索道
    public static final int MARKER_TURNPOINT_CABLEWAY = 1026;
    // 人行横道
    public static final int MARKER_TURNPOINT_CROSSWALK = 1027;
    // 游船
    public static final int MARKER_TURNPOINT_CRUISES = 1028;
    // 过街天桥
    public static final int MARKER_TURNPOINT_FLYOVER = 1029;
    // 地下通道\地铁通道
    public static final int MARKER_TURNPOINT_PASSAGE = 1030;
    // 乘观光车
    public static final int MARKER_TURNPOINT_SIGHTSEEINGBUS = 1031;
    // 乘滑道
    public static final int MARKER_TURNPOINT_SLIP = 1032;

    public static final int MARKER_BUBBLE_LAYER_HL = 1050;

    public static final int MARKER_POINT_BLUE = 1061;
    public static final int MARKER_POINT_RED = 1062;

    public static final int MARKER_TLINE_WALK = 2000;
    public static final int MARKER_TLINE_BUS = 2000 + 1;
    public static final int MARKER_TLINE_ROUTE_OTHER = 2000 + 2;
    //严重拥堵
    public static final int MARKER_TLINE_TRAFFIC_BAD = 2000 + 3;
    public static final int MARKER_TLINE_TRAFFIC_SLOW = 2000 + 4;
    public static final int MARKER_TLINE_TRAFFIC_GREEN = 2000 + 5;
    public static final int MARKER_TLINE_TRAFFIC_NO_DATA = 2000 + 6;
    //超级拥堵
    public static final int MARKER_TLINE_TRAFFIC_BADER = 2000 + 7;
    // m_arrowlineTextureID
    public static final int MARKER_TLINE_ARROW = 3001;
    // m_arrowlineTextureID
    public static final int MARKER_TLINE_ARROW_NIGHT = 3002;
    // m_arrowonlylineTextureID
    public static final int MARKER_TLINE_ARROW_ONLY = 3003;
    // m_linkdottTextureID
    public static final int MARKER_TLINE_LINK_DOTT = 3010;
    // m_lineroundTextureID
    public static final int MARKER_TLINE_ROUND = 3000;

    public static final int MARKER_TEST_CAR_ICON = 3032;

    public static final int MARKER_NOT_SHOW = -999;

    /**
     * 导航过程中的方向指示
     */
    public static final int MARKER_NAVI_DIRECTION = 1008;

    public static void clearCache() {
    }


    public static Bitmap convertViewToBitmap(View view) {
        if (view == null) {
            return null;
        }
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public static int createIconMarker(int iconID) {
        if (iconID == MARKER_NOT_SHOW)
            return -1;
        int resId = -1;
        if (iconID < 1000 && iconID > 0) {
            switch (iconID) {

                case MARKER_POI_1:
                    resId = R.drawable.b_poi_1;
                    break;
                case MARKER_POI_2:
                    resId = R.drawable.b_poi_2;
                    break;
                case MARKER_POI_3:
                    resId = R.drawable.b_poi_3;
                    break;
                case MARKER_POI_4:
                    resId = R.drawable.b_poi_4;
                    break;
                case MARKER_POI_5:
                    resId = R.drawable.b_poi_5;
                    break;
                case MARKER_POI_6:
                    resId = R.drawable.b_poi_6;
                    break;
                case MARKER_POI_7:
                    resId = R.drawable.b_poi_7;
                    break;
                case MARKER_POI_8:
                    resId = R.drawable.b_poi_8;
                    break;
                case MARKER_POI_9:
                    resId = R.drawable.b_poi_9;
                    break;
                case MARKER_POI_10:
                    resId = R.drawable.b_poi_10;
                    break;

                case MARKER_POI_1_hl:
                    resId = R.drawable.b_poi_1_hl;
                    break;
                case MARKER_POI_2_hl:
                    resId = R.drawable.b_poi_2_hl;
                    break;
                case MARKER_POI_3_hl:
                    resId = R.drawable.b_poi_3_hl;
                    break;
                case MARKER_POI_4_hl:
                    resId = R.drawable.b_poi_4_hl;
                    break;
                case MARKER_POI_5_hl:
                    resId = R.drawable.b_poi_5_hl;
                    break;
                case MARKER_POI_6_hl:
                    resId = R.drawable.b_poi_6_hl;
                    break;
                case MARKER_POI_7_hl:
                    resId = R.drawable.b_poi_7_hl;
                    break;
                case MARKER_POI_8_hl:
                    resId = R.drawable.b_poi_8_hl;
                    break;
                case MARKER_POI_9_hl:
                    resId = R.drawable.b_poi_9_hl;
                    break;
                case MARKER_POI_10_hl:
                    resId = R.drawable.b_poi_10_hl;
                    break;
                case MARKER_NAVI_END:
                    resId = R.drawable.navi_set_end_point;
                    break;
                case MARKER_START:
                    resId = R.drawable.bubble_start;
                    break;
                case MARKER_MID:
                    resId = R.drawable.bubble_midd;
                    break;
                case MARKER_END:
                    resId = R.drawable.bubble_end;
                    break;
                case MARKER_NAVIDST:
                    resId = R.drawable.restdistance;
                    break;

                case MARKER_TMC_BUBBLE:
                    resId = R.drawable.tmc_select;
                    break;
                case MARKER_POI_BUBBLE:
                    resId = R.drawable.b_poi_hl;
                    break;
                case MARKER_BUS_START:
                    resId = R.drawable.bubble_start;
                    break;
                case MARKER_BUS_END:
                    resId = R.drawable.bubble_end;
                    break;
                case MARKER_BUBBLE_WRONGCHECK:
                    resId = R.drawable.bubble_wrongcheck;
                    break;
                case MARKER_BUBBLE_WRONGPOI:
                    resId = R.drawable.bubble_wrongpoi;
                    break;

                case MARKER_CHILD_POINT_SELECT:
                    resId = R.drawable.poi_bus_hl;
                    break;

                case MARKER_VACANT:
                    resId = R.drawable.marker_taxi;
                    break;
                case MARKER_POI_MARK:
                    resId = R.drawable.marker_other;
                    break;
                case MARKER_POI_MARK_HL:
                    resId = R.drawable.marker_other_highlight;
                    break;
            }
        } else {
            switch (iconID) {
                case MARKER_BUSSTATION:
                    resId = R.drawable.bubble_turnpoint;
                    break;
                case MARKER_SAVE:
                    resId = R.drawable.favorite_layer;
                    break;
                case MARKER_GPSTRACKER:
                    resId = R.drawable.navi_map_gps_locked;
                    break;
                case MARKER_NAVI_CAR:
                    resId = R.drawable.navi_car_locked;
                    break;
                case MARKER_NAVI_DIRECTION:
                    resId = R.drawable.navi_direction;
                    break;
                case MARKER_GPS_SHINE:
                    resId = R.drawable.navi_map_flash;
                    break;
                case MARKER_GPS_NO_SENSOR:
                    resId = R.drawable.marker_gps_no_sensor;
                    break;
                case MARKER_GPS_VALID:
                    resId = R.drawable.navi_map_gps_locked;
                    break;
                case MARKER_GPS_3D:
                    resId = R.drawable.navi_map_gps_3d;
                    break;
                case MARKER_ARC:
                    resId = R.drawable.marker_arc;
                    break;
                case MARKER_CAMERA:
                    resId = R.drawable.electronic_eye_line;
                    break;
                case MARKER_TURNPOINT_BUS:
                    resId = R.drawable.bus_turnpoint;
                    break;
                case MARKER_TURNPOINT_FOOT:
                    resId = R.drawable.foot_turnpoint;
                    break;
                case MARKER_TURNPOINT_SUBWAY:
                    resId = R.drawable.sub_turnpoint;
                    break;
                case MARKER_TURNPOINT_CABLEWAY:
                    resId = R.drawable.cableway_turnpoint;
                    break;
                case MARKER_TURNPOINT_CROSSWALK:
                    resId = R.drawable.crosswalk_turnpoint;
                    break;
                case MARKER_TURNPOINT_CRUISES:
                    resId = R.drawable.cruises_turnpoint;
                    break;
                case MARKER_TURNPOINT_FLYOVER:
                case MARKER_TURNPOINT_PASSAGE:
                    resId = R.drawable.flyover_turnpoint;
                    break;
                case MARKER_TURNPOINT_SIGHTSEEINGBUS:
                    resId = R.drawable.sightseeingbus_turnpoint;
                    break;
                case MARKER_TURNPOINT_SLIP:
                    resId = R.drawable.slip_turnpoint;
                    break;
                case MARKER_BUBBLE_LAYER_HL:
                    break;
                case MARKER_POINT_BLUE:
                    resId = R.drawable.measure_point;
                    break;
                case MARKER_POINT_RED:
                    resId = R.drawable.measure_point_red;
                    break;
                case MARKER_TLINE_ROUND:
                    resId = R.drawable.map_lr;
                    break;
                case MARKER_TLINE_ARROW://白天导航线路
                    resId = R.drawable.map_alr;
                    break;
                case MARKER_TLINE_ARROW_NIGHT://夜晚导航线
                    resId = R.drawable.map_alr_night;
                    break;
                case MARKER_TLINE_ARROW_ONLY:
                    resId = R.drawable.map_aolr;
                    break;
                case MARKER_TLINE_LINK_DOTT:
                    resId = R.drawable.map_link_dott;
                    break;
                case MARKER_TEST_CAR_ICON:
                    resId = R.drawable.bubble_point_red_big;
                    break;
                case MARKER_TLINE_WALK:
                    resId = R.drawable.map_lr_walk;
                    break;
                case MARKER_TLINE_BUS:
                    resId = R.drawable.map_lr_bus;
                    break;
                case MARKER_TLINE_ROUTE_OTHER:
                    resId = R.drawable.map_lr_other;
                    break;
                case MARKER_TLINE_TRAFFIC_BAD:
                    resId = R.drawable.map_lr_bad;
                    break;
                case MARKER_TLINE_TRAFFIC_BADER:
                    resId = R.drawable.map_lr_bader;
                    break;
                case MARKER_TLINE_TRAFFIC_SLOW:
                    resId = R.drawable.map_lr_slow;
                    break;
                case MARKER_TLINE_TRAFFIC_GREEN:
                    resId = R.drawable.map_lr_green;
                    break;
                case MARKER_TLINE_TRAFFIC_NO_DATA:
                    resId = R.drawable.map_lr_nodata;
                    break;

            }
        }
        return resId;
    }
}
