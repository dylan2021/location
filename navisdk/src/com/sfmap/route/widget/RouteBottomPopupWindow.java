//package com.sfmap.route.widget;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.drawable.ColorDrawable;
//import android.support.constraint.Group;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import com.sfmap.api.navi.Navi;
//import com.sfmap.library.util.DateTimeUtil;
//import com.sfmap.navi.R;
//import com.sfmap.map.model.IRouteResultData;
//import com.sfmap.map.model.RouteType;
//import com.sfmap.map.util.RoutePathHelper;
//import com.sfmap.route.model.ICarRouteResult;
//import com.sfmap.route.model.IFootRouteResult;
//import com.sfmap.route.model.NavigationPath;
//import com.sfmap.route.model.NavigationResult;
//
//
//public class RouteBottomPopupWindow extends PopupWindow implements View.OnClickListener {
//
//    private View mMenuView;
//    private Group groupRecommend;
//    private Group groupFree;
//    private Group groupRedGreen;
//    private Group groupShortest;
//
//    private RouteType type;
//
//    private IRouteResultData data;
//
//    private Activity activity;
//
//    //当前线路  1：推荐道路 2：避免收费 3：距离最短
//    private int currentLine = RECOMMEND_LINE;
//
//    public static final int RECOMMEND_LINE = 1;
//    public static final int FREE_LINE = 3;
//    public static final int SHORTEST_LINE = 2;
//
//    private TextView tv_navi,tvRedGreenLight, recommendTitle, recommendTime, recommendLength, tvFreeTitle, tvFreeTime, tvFreeLength, tvShortestTitle, tvShortestTime, tvShortestLength;
//
//
//    public interface RouteBottomListener {
//        void switchLine(int line);
//
//        void startNavi();
//
//    }
//
//    public RouteBottomListener routeBottomListener;
//
//
//    public void setRouteBottomListener(RouteBottomListener listener) {
//        routeBottomListener = listener;
//    }
//
//    public void updateContent(IRouteResultData resultData, RouteType type) {
//        currentLine = RECOMMEND_LINE;
//        this.type = type;
//        data = resultData;
////        switchLine();
//        groupFree.setVisibility(View.GONE);
//        groupShortest.setVisibility(View.GONE);
//        groupRecommend.setVisibility(View.GONE);
//        setContent();
//    }
//
//    public RouteBottomPopupWindow(final Activity context, IRouteResultData routeResultData, RouteType type) {
//        super(context);
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMenuView = inflater.inflate(R.layout.route_bottom_layout, null);
////        ButterKnife.bind(this,mMenuView);
//        activity = context;
//        recommendTitle = (TextView) mMenuView.findViewById(R.id.tc_recommend_line);
//        recommendTime = (TextView) mMenuView.findViewById(R.id.tv_recommend_time);
//        recommendLength = (TextView) mMenuView.findViewById(R.id.tv_recommend_length);
//
//        tvFreeTitle = (TextView) mMenuView.findViewById(R.id.tc_free_line);
//        tvFreeTime = (TextView) mMenuView.findViewById(R.id.tv_free_time);
//        tvFreeLength = (TextView) mMenuView.findViewById(R.id.tv_free_length);
//
//        tvShortestTitle = (TextView) mMenuView.findViewById(R.id.tc_shortest_line);
//        tvShortestTime = (TextView) mMenuView.findViewById(R.id.tv_shortest_time);
//        tvShortestLength = (TextView) mMenuView.findViewById(R.id.tv_shortest_length);
//
//        tvRedGreenLight = (TextView) mMenuView.findViewById(R.id.tv_red_green_light);
//
//        groupRecommend = (Group) mMenuView.findViewById(R.id.group_recommend);
//        groupFree = (Group) mMenuView.findViewById(R.id.group_free);
//        groupShortest = (Group) mMenuView.findViewById(R.id.group_shortest);
//        groupRedGreen = (Group) mMenuView.findViewById(R.id.group_red_green_navi);
//
//        tv_navi = mMenuView.findViewById(R.id.tv_route_start_navi);
//        tv_navi.setOnClickListener(this);
//
////        groupRecommend.setOnClickListener(this);
//        int refIds[] = groupRecommend.getReferencedIds();
//        for (int id : refIds) {
//            mMenuView.findViewById(id).setOnClickListener(this);
//        }
////        groupFree.setOnClickListener(this);
//        int freeRefIds[] = groupFree.getReferencedIds();
//        for (int id : freeRefIds) {
//            mMenuView.findViewById(id).setOnClickListener(this);
//        }
////        groupShortest.setOnClickListener(this);
//        int shortestRefIds[] = groupShortest.getReferencedIds();
//        for (int id : shortestRefIds) {
//            mMenuView.findViewById(id).setOnClickListener(this);
//        }
//
//
//        this.type = type;
//        data = routeResultData;
//
//
//        //设置SelectPicPopupWindow的View
//        this.setContentView(mMenuView);
//        //设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        //设置SelectPicPopupWindow弹出窗体可点击
//        this.setFocusable(false);
//        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimBottom);
//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R2.color.transparent));
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        this.setInputMethodMode(INPUT_METHOD_NEEDED);
//
//        this.setOutsideTouchable(false);
//        setContent();
//    }
//
//    private void setContent() {
//        switch (type) {
//            case CAR:
//            case TRUCK:
//                groupRedGreen.setVisibility(View.VISIBLE);
//                NavigationResult result = ((ICarRouteResult) data).getNaviResultData();
////                NavigationPath path = ((ICarRouteResult) data).getFocusNavigationPath();
//                int length = result.pathNum;
//                NavigationPath[] navi_paths = result.paths;
//                switch (length) {
//                    case 1:
//                        groupRecommend.setVisibility(View.VISIBLE);
//                        groupFree.setVisibility(View.GONE);
//                        groupShortest.setVisibility(View.GONE);
//                        int route_length = ((ICarRouteResult) data).getFocusNavigationPath().pathlength;
//                        String res_map_sub_des = RoutePathHelper.createCarSubDesNoPrice(route_length);
//                        int routeTime = Navi.getInstance(activity).getRouteTime();
//                        String timeStr = DateTimeUtil.getTimeStr(routeTime);
//                        recommendTime.setText(timeStr);
//                        recommendLength.setText(res_map_sub_des);
//                        tvRedGreenLight.setText(navi_paths[0].getSubDesSP(), TextView.BufferType.SPANNABLE);
//                        break;
//                    case 2:
//                        groupRecommend.setVisibility(View.VISIBLE);
//                        groupShortest.setVisibility(View.VISIBLE);
//                        groupFree.setVisibility(View.GONE);
//                        recommendTime.setText(DateTimeUtil.getTimeStr(navi_paths[0].costTime));
//                        recommendLength.setText(com.sfmap.library.util.CalculateUtil.getLengDesc(navi_paths[0].pathlength));
//                        tvRedGreenLight.setText(navi_paths[0].getSubDesSP(), TextView.BufferType.SPANNABLE);
//                        tvShortestLength.setText(com.sfmap.library.util.CalculateUtil.getLengDesc(navi_paths[1].pathlength));
//                        tvShortestTime.setText(DateTimeUtil.getTimeStr(navi_paths[1].costTime));
//                        tvRedGreenLight.setText(navi_paths[1].getSubDesSP(), TextView.BufferType.SPANNABLE);
//                        break;
//                    case 3:
//                        groupRecommend.setVisibility(View.VISIBLE);
//                        groupShortest.setVisibility(View.VISIBLE);
//                        groupFree.setVisibility(View.VISIBLE);
//                        recommendTime.setText(DateTimeUtil.getTimeStr(navi_paths[0].costTime));
//                        recommendLength.setText(com.sfmap.library.util.CalculateUtil.getLengDesc(navi_paths[0].pathlength));
//                        tvRedGreenLight.setText(navi_paths[0].getSubDesSP(), TextView.BufferType.SPANNABLE);
//                        tvFreeLength.setText(com.sfmap.library.util.CalculateUtil.getLengDesc(navi_paths[2].pathlength));
//                        tvFreeTime.setText(DateTimeUtil.getTimeStr(navi_paths[2].costTime));
//                        tvRedGreenLight.setText(navi_paths[2].getSubDesSP(), TextView.BufferType.SPANNABLE);
//                        tvShortestLength.setText(com.sfmap.library.util.CalculateUtil.getLengDesc(navi_paths[1].pathlength));
//                        tvShortestTime.setText(DateTimeUtil.getTimeStr(navi_paths[1].costTime));
//                        tvRedGreenLight.setText(navi_paths[1].getSubDesSP(), TextView.BufferType.SPANNABLE);
//                        break;
//                    default:
//
//                        break;
//                }
//                break;
//            case ONFOOT:
//            case ONFOOTBIKE:
//                groupRecommend.setVisibility(View.VISIBLE);
//                groupFree.setVisibility(View.GONE);
//                groupShortest.setVisibility(View.GONE);
//                groupRedGreen.setVisibility(View.GONE);
//                int foot_length = ((IFootRouteResult) data).getOnFootPlanResult().onFootNaviPaths[0].pathlength;
//                String res_map_sub_des = RoutePathHelper.createCarSubDesNoPrice(foot_length);
//                int routeTime = Navi.getInstance(activity).getRouteTime();
//                String timeStr = DateTimeUtil.getTimeStr(routeTime);
//                recommendTime.setText(timeStr);
//                recommendLength.setText(res_map_sub_des);
////                tvRedGreenLight.setText("11", TextView.BufferType.SPANNABLE);
//                break;
//            default:
//                break;
//        }
//    }
//
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R2.id.tv_route_start_navi:
//                routeBottomListener.startNavi();
//                break;
//            case R2.id.tc_recommend_line:
//            case R2.id.tv_recommend_time:
//            case R2.id.tv_recommend_length:
//                if (currentLine == RECOMMEND_LINE) {
//                    break;
//                }
//                currentLine = RECOMMEND_LINE;
//                switchLine();
//                break;
//            case R2.id.tc_shortest_line:
//            case R2.id.tv_shortest_time:
//            case R2.id.tv_shortest_length:
//                if (currentLine == SHORTEST_LINE) {
//                    break;
//                }
//                currentLine = SHORTEST_LINE;
//                switchLine();
//                break;
//            case R2.id.tc_free_line:
//            case R2.id.tv_free_length:
//            case R2.id.tv_free_time:
//                if (currentLine == FREE_LINE) {
//                    break;
//                }
//                currentLine = FREE_LINE;
//                switchLine();
//                break;
//        }
//    }
//
//    /**
//     * 切换了路线
//     */
//    private void switchLine() {
//        if (data instanceof IFootRouteResult) {
//            recommendTime.setTextColor(activity.getResources().getColor(R2.color.box_red));
//            recommendLength.setTextColor(activity.getResources().getColor(R2.color.box_red));
//            recommendTitle.setTextColor(activity.getResources().getColor(R2.color.box_red));
//            tvShortestTitle.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//            tvShortestTime.setTextColor(activity.getResources().getColor(R2.color.red_sf));
//            tvShortestLength.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//            tvFreeTitle.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//            tvFreeTime.setTextColor(activity.getResources().getColor(R2.color.red_sf));
//            tvFreeLength.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//            routeBottomListener.switchLine(currentLine);
//            groupRedGreen.setVisibility(View.GONE);
//            return;
//        }
//        groupRedGreen.setVisibility(View.VISIBLE);
//        NavigationResult result = ((ICarRouteResult) data).getNaviResultData();
//        NavigationPath[] navi_paths = result.paths;
//        switch (currentLine) {
//            case RECOMMEND_LINE:
//                recommendTime.setTextColor(activity.getResources().getColor(R2.color.box_red));
//                recommendLength.setTextColor(activity.getResources().getColor(R2.color.box_red));
//                recommendTitle.setTextColor(activity.getResources().getColor(R2.color.box_red));
//                tvShortestTitle.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                tvShortestTime.setTextColor(activity.getResources().getColor(R2.color.red_sf));
//                tvShortestLength.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                tvFreeTitle.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                tvFreeTime.setTextColor(activity.getResources().getColor(R2.color.red_sf));
//                tvFreeLength.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                tvRedGreenLight.setText(navi_paths[0].getSubDesSP(), TextView.BufferType.SPANNABLE);
//                break;
//            case FREE_LINE:
//                tvFreeTime.setTextColor(activity.getResources().getColor(R2.color.box_red));
//                tvFreeLength.setTextColor(activity.getResources().getColor(R2.color.box_red));
//                tvFreeTitle.setTextColor(activity.getResources().getColor(R2.color.box_red));
//                recommendTitle.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                recommendTime.setTextColor(activity.getResources().getColor(R2.color.red_sf));
//                recommendLength.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                tvShortestTitle.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                tvShortestTime.setTextColor(activity.getResources().getColor(R2.color.red_sf));
//                tvShortestLength.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                tvRedGreenLight.setText(navi_paths[1].getSubDesSP(), TextView.BufferType.SPANNABLE);
//                break;
//            case SHORTEST_LINE:
//                tvShortestTime.setTextColor(activity.getResources().getColor(R2.color.box_red));
//                tvShortestLength.setTextColor(activity.getResources().getColor(R2.color.box_red));
//                tvShortestTitle.setTextColor(activity.getResources().getColor(R2.color.box_red));
//                tvFreeTitle.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                tvFreeTime.setTextColor(activity.getResources().getColor(R2.color.red_sf));
//                tvFreeLength.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                recommendTitle.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                recommendTime.setTextColor(activity.getResources().getColor(R2.color.red_sf));
//                recommendLength.setTextColor(activity.getResources().getColor(R2.color.light_gray));
//                tvRedGreenLight.setText(result.pathNum == 3 ? (navi_paths[2].getSubDesSP()) : navi_paths[1].getSubDesSP(), TextView.BufferType.SPANNABLE);
//                break;
//            default:
//                break;
//
//        }
//        routeBottomListener.switchLine(currentLine);
//    }
//}
