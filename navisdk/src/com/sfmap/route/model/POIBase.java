package com.sfmap.route.model;

import com.sfmap.library.model.GeoPoint;

import org.xidea.el.impl.ReflectUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class POIBase implements POI, Cloneable {

    private String id = "";
    // POI类型
    private String type = "";
    private String industry = "";
    // 永遠不爲空
    // 坐标
    private GeoPoint point = new GeoPoint();
    // 名称
    private String name = "";
    // 电话
    private String phone = "";
    // 城市名称
    private String cityName = "";
    // 城市区号
    private String cityCode = "";

    private String areaCode = "";
    // 地址
    private String addr = "";
    // 城市代码
    private String adCode = "";
    private String iconURL = "";
    private int markerType = 0;
    // 与我的位置之间的距离
    private int distance = -100;
    protected HashMap<String, Serializable> poiExtra = new HashMap<String, Serializable>();
    //transient 反系列化需要特殊处理！！
    transient HashMap<Class<?>, POI> typeMap = new HashMap<Class<?>, POI>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getIndustry() {
        return industry;
    }

    @Override
    public void setIndustry(String type) {
        this.industry = type;
    }

    public GeoPoint getPoint() {
        return point;
    }

    public void setPoint(GeoPoint point) {
        if (point == null) {
            this.point = new GeoPoint();
        } else {
            this.point = point;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public int getIconId() {
        return markerType;
    }

    public void setIconId(int markerType) {
        this.markerType = markerType;
    }

    public int getDistance() {
        return this.distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    private static HashMap<Class<?>, Field[]> deepCopyMap = new HashMap<Class<?>, Field[]>();

    private static Method clone;

    static {
        try {
            clone = Object.class.getDeclaredMethod("clone");
            clone.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    static void deepClone(Cloneable thiz) {
        Field[] deepField = getDeepField(thiz.getClass());
        for (Field f : deepField) {
            try {
                Object v = f.get(thiz);
                // HashMap,ArrayList，LinkedList 都是 cloneable
                if (v instanceof Object[]) {
                    Object[] vs = ((Object[]) v).clone();
                    for (int i = 0; i < vs.length; i++) {
                        Object item = vs[i];
                        if (v instanceof Cloneable) {
                            vs[i] = clone(item);
                        }
                    }
                    f.set(thiz, vs);
                } else if (v instanceof Cloneable) {
                    f.set(thiz, clone(v));
                }
            } catch (Exception e) {
//                DebugLog.warn(e);
            }

        }
    }

    private static Object clone(Object object) {
        try {
            return clone.invoke(object);
        } catch (Exception e) {
//            DebugLog.warn(e);
            return object;
        }
    }

    private static Field[] getDeepField(Class<?> type) {
        Field[] fields = deepCopyMap.get(type);
        if (fields == null) {
            Class<?> superType = type.getSuperclass();
            HashMap<String, Field> list = new HashMap<String, Field>();
            if (superType != null && superType != Object.class) {
                for (Field f : getDeepField(superType)) {
                    list.put(f.getName(), f);
                }
            }
            Field[] fs = type.getDeclaredFields();
            for (Field f : fs) {
                Class<?> t = f.getType();
                if (!(t.isPrimitive() || String.class == t
                        || Number.class.isAssignableFrom(t) || t == Boolean.class)) {
                    f.setAccessible(true);
                    list.put(f.getName(), f);
                }
            }
            fields = list.values().toArray(new Field[list.size()]);

            deepCopyMap.put(type, fields);

        }
        return fields;
    }

    @SuppressWarnings("unchecked")
    static <T extends POI> T to(Class<T> type, POIBase base,
                                HashMap<Class<?>, POI> cacheMap) {
        if (type.isInstance(base)) {
            return (T) base;
        }
        POI cache = cacheMap.get(type);
        if (cache == null) {
            try {
                cache = createProxyInstance(type, base);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            cacheMap.put(type, cache);
        }
        return (T) cache;
    }

    private static POI createProxyInstance(final Class<?> type,
                                           final POIBase base) {
        try {
            InvocationHandler h = new SubPOIHandler(base);
            return (POI) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, h);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<GeoPoint> getEntranceList() {
        return toGeoList("entranceList");
    }

    @Override
    public void setEntranceList(ArrayList<GeoPoint> inList) {
        this.poiExtra.put("entranceList", inList);
    }

    @Override
    public void setExitList(ArrayList<GeoPoint> exitList) {
        this.poiExtra.put("exitList", exitList);
    }

    @Override
    public ArrayList<GeoPoint> getExitList() {
        return toGeoList("exitList");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ArrayList<GeoPoint> toGeoList(String key) {
        ArrayList list = (ArrayList) this.poiExtra.get(key);
        if (list != null) {
            int i = list.size();
            while (i-- > 0) {
                Object item = list.get(i);
                if (item instanceof Map) {
                    Map map = (Map) item;
                    list.set(i, new GeoPoint(((Number) map.get("x")).intValue(), ((Number) map.get("y")).intValue()));
                }
            }

        }
        return list;
    }

    @Override
    public HashMap<String, Serializable> getPoiExtra() {
        return poiExtra;
    }

    public synchronized <T extends POI> T as(Class<T> type) {
        T poi = to(type, this, typeMap);
        return poi;
    }

    public POI clone() {
        try {
            POIBase n = (POIBase) super.clone();
            deepClone(n);
            n.typeMap = new HashMap<Class<?>, POI>();
            for (Map.Entry<String, Serializable> e : n.poiExtra.entrySet()) {
                Serializable value = e.getValue();
                if (value instanceof Cloneable) {
                    e.setValue((Serializable) clone(value));
                }
            }
            return n;
        } catch (CloneNotSupportedException e) {// 不可能发生的事情。
            throw new RuntimeException(e);
        }
    }

    static class SubPOIHandler implements InvocationHandler, Serializable {
        private static final long serialVersionUID = 1L;
        private final Object[] EMPTY_OBJECTS = new Object[0];
        private POIBase base;

        public SubPOIHandler(POIBase base) {
            this.base = base;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            if (args == null) {
                args = EMPTY_OBJECTS;
            }
            Class<?> clazz = method.getDeclaringClass();
            String methodName = method.getName();
            if (!clazz.isAssignableFrom(POI.class) && clazz != POI.class) {
                String className = clazz.getSimpleName();
                String key = methodName.replaceAll("^(?:get|set|is)([A-Z])", "$1");
                if (!key.equals(methodName)) {
                    // 在key值上加上类名（防止名称冲突）
                    key = className + "." + key;
                    switch (methodName.charAt(0)) {
                        case 'g':
                        case 'i':
                            Object result = base.poiExtra.get(key);
                            if (result == null) {
                                Class<?> returnType = method.getReturnType();
                                if (returnType.isPrimitive()) {
                                    if (returnType == Boolean.TYPE) {
                                        result = false;
                                    } else if (returnType == Character.TYPE) {
                                        result = (char) 0;
                                    } else if (Number.class
                                            .isAssignableFrom(ReflectUtil.toWrapper(returnType))) {
                                        result = ReflectUtil.toValue(0, returnType);
                                    }
                                }
                            }
                            return result;
                        case 's':
                            return base.poiExtra.put(key, (Serializable) args[0]);
                    }
                }
            }
            if ("clone".equals(methodName)) {
                POIBase cloneBase = (POIBase) base.clone();
                return cloneBase.as((Class<? extends POI>) clazz
                        .getInterfaces()[0]);
            }
            return method.invoke(base, args);
        }

    }
}
