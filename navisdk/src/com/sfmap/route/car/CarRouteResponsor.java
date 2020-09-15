package com.sfmap.route.car;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sfmap.map.util.CatchExceptionUtil;
import com.sfmap.map.util.AbstractResponser;
import com.sfmap.route.model.ICarRouteResult;

/**
 * 驾车结果解析
 */
public class CarRouteResponsor extends AbstractResponser {
    // 起点错误（坐标不在中国范围内）
    private final int START_POINT_FALSE = 3;
    //解码错误
    private final int END_POINT_FALSE = 6;
    // 请求协议非法
    private final int ILLEGAL_REQUEST = 4;
    //无可通行路径
    public final static int ROUTE_FAIL = 13;
    //编码错误
    public final static int ERROR_CODE_ENCODE = 5;
    //参数错误
    public final static int ERROR_CODE_PARAMETER = 7;
    //策略不支持
    public final static int ERROR_CODE_STRATEGY = 8;
    //起点附近没有道路
    public final static int ERROR_CODE_FROMPOINT =14;
    //终点附近没有道路
    public final static int  ERROR_CODE_TOPOINT =15;
    //起点在双向禁行路上
    public final static int  ERROR_CODE_FROMFORBIDDING=18;
    //终点在双向禁行路上
    public final static int  ERROR_CODE_TOFORBIDDING=19;
    //途经点坐标错误，请重新选择途经点
    public final static int  ERROR_CODE_VIAPOINT=20;
    //途经点在双向禁行路上
    public final static int  ERROR_CODE_VIAFORBIDDING=22;

    private ICarRouteResult carPathResult;
    private String TAG = CarRouteResponsor.class.getSimpleName();
    public CarRouteResponsor(ICarRouteResult carPathSearchResult) {
        this.carPathResult = carPathSearchResult;
    }

    @Override
    public void parser(byte[] data) {
        try {
            Log.d(TAG,new String(data));
            if (data == null) {
                this.errorCode = ROUTE_FAIL;
                this.errorMessage = getErrorCodeDes(errorCode);
            }
            if (carPathResult != null) {
                if (data != null && data[5] == 0) { // 第六个字节0表示请求成功
                    int len = ((data[0] & 0xFF))
                            + ((data[1] & 0xFF) << 8)
                            + ((data[2] & 0xFF) << 16)
                            + ((data[3] & 0xFF) << 24);//01380761 zhangxuewen 服务端数据可能比较长，大于3位字节表示的范围，因此修改为前4位字节表示长度。第5位是协议版本号
                    this.errorCode = 0;
                    carPathResult.parseData(data, 0, len);

                }else {
                    this.errorCode = data[5] & 0xFF;
                    this.errorMessage = getErrorCodeDes(errorCode);
                }
            }
        } catch (Exception e) {
            carPathResult = null;
            CatchExceptionUtil.normalPrintStackTrace(e);
        }

    }

    public void parser() {
        try {
            if (carPathResult != null) {
                this.errorCode = 0;
                carPathResult.parseData(null, 0, 0);
            }
        } catch (Exception e) {
            carPathResult = null;
            CatchExceptionUtil.normalPrintStackTrace(e);
        }

    }

    public ICarRouteResult getCarPathSearchResult() {
        return carPathResult;
    }

    @Override
    public String getErrorDesc(int errorCode) {
        return errorMessage;
    }

    /**
     * 解析数据包头 并提取数据内容
     *0
     * @param data 提取的字节内容
     * @return
     */
    public byte[] parseHeader_(byte data[]) {
        byte[] tmpData = null;
        if (data != null && data.length >= 8) {
            int idx = 0;
            int type = ((data[idx] & 0xFF)) + ((data[idx + 1] & 0xFF) << 8);
            idx += 2;
            if (type == 200) {
                idx += 8;
                tmpData = new byte[data.length - idx];
                System.arraycopy(data, idx, tmpData, 0, tmpData.length);
                // 获得错误代码 1B
                this.errorCode = tmpData[4] & 0xFF;
                this.errorMessage = getErrorCodeDes(errorCode);
            }
        }
        return tmpData;
    }

    /**
     * 根据错误码索引返回错误码描述
     *
     * @param errorCodeIndex
     * @return
     */
    public String getErrorCodeDes(int errorCodeIndex) {
        String errorCodeDes = "请求路线失败，请稍后重试";
        switch (errorCodeIndex) {
            case START_POINT_FALSE:
                errorCodeDes = "起点不在规划区域内";
                break;
            case ILLEGAL_REQUEST:
                errorCodeDes = "请求协议非法";
                break;
            case ERROR_CODE_PARAMETER:
                errorCodeDes = "参数错误";
                break;
            case END_POINT_FALSE:
                errorCodeDes = "请求路线失败，请稍后重试";
                break;
            case ERROR_CODE_ENCODE:
                errorCodeDes = "编码错误";
                break;
            case ERROR_CODE_STRATEGY:
                errorCodeDes = "策略不支持，请重新选择策略";
                break;
            case ERROR_CODE_FROMPOINT:
                errorCodeDes = "起点附近没有道路，请重新选择起点";
                break;
            case ERROR_CODE_TOPOINT:
                errorCodeDes = "终点附近没有道路，请重新选择终点";
                break;
            case ERROR_CODE_FROMFORBIDDING:
                errorCodeDes = "起点在双向禁行路上，请重新选择起点";
                break;
            case ERROR_CODE_TOFORBIDDING:
                errorCodeDes = "终点在双向禁行路上，请重新选择终点";
                break;
            case ROUTE_FAIL:
                errorCodeDes = "无可通行路径，请重新选择起终点";
                break;
            case ERROR_CODE_VIAPOINT:
                errorCodeDes = "途经点坐标错误，请重新选择途经点";
                break;
            case ERROR_CODE_VIAFORBIDDING:
                errorCodeDes = "途经点在双向禁行路上，请重新选择途经点";
                break;
            default:
                break;
        }
        return errorCodeDes;
    }

    public String getErrorMsg() {
        if (TextUtils.isEmpty(errorMessage)) {
            return "请求路线失败，请稍后重试";
        }
        return errorMessage;
    }
}
