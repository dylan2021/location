package com.sfmap.library.container;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;


/**
 * 序列存储映射值
 */
public final class NodeFragmentBundle {
    private static final String TAG = "Bundle";
    static final boolean DEBUG = false;
    protected static final NodeFragmentBundle EMPTY;

    static final int BUNDLE_MAGIC = 0x4C444E42; // 'B' 'N' 'D' 'L'

    static {
        EMPTY = new NodeFragmentBundle();
        EMPTY.mMap = new ArrayMap<String, Object>();
    }

    // Invariant - exactly one of mMap / mParcelledData will be null
    // (except inside a call to unparcel)

    /* package */ ArrayMap<String, Object> mMap = null;

    private boolean mHasFds = false;
    private boolean mFdsKnown = true;
    private boolean mAllowFds = true;

    /**
     * The ClassLoader used when unparcelling data from mParcelledData.
     */
    private ClassLoader mClassLoader;


    /**
     * 构造器
     */
    public NodeFragmentBundle() {
        mMap = new ArrayMap<String, Object>();
        mClassLoader = getClass().getClassLoader();
    }


    /**
     * 构造器
     *
     * @param loader 类加载器
     */
    public NodeFragmentBundle(ClassLoader loader) {
        mMap = new ArrayMap<String, Object>();
        mClassLoader = loader;
    }

    /**
     * 构造器
     *
     * @param capacity NodeFragmentBundle存储容量大小
     */
    public NodeFragmentBundle(int capacity) {
        mMap = new ArrayMap<String, Object>(capacity);
        mClassLoader = getClass().getClassLoader();
    }


    /**
     * 构造器
     *
     * @param b 一个NodeFragmentBundle数据模型
     */
    public NodeFragmentBundle(NodeFragmentBundle b) {
        if (b.mMap != null) {
            mMap = new ArrayMap<String, Object>(b.mMap);
        } else {
            mMap = null;
        }

        mHasFds = b.mHasFds;
        mFdsKnown = b.mFdsKnown;
        mClassLoader = b.mClassLoader;
    }

    private Intent intent;

    /**
     * 构造器
     *
     * @param intent 一个意图类型
     */
    public NodeFragmentBundle(Intent intent) {
        mMap = new ArrayMap<String, Object>();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Set<String> keySet = bundle.keySet();
            if (keySet != null) {
                for (String key : keySet) {
                    mMap.put(key, bundle.get(key));
                }
            }
        }
    }

    /**
     * 创造一个简单的key-value bundle
     * @param key   一个字符串key
     * @param value 一个字符串value
     * @return  一个NodeFragmentBundle对象
     */
    public static NodeFragmentBundle forPair(String key, String value) {
        NodeFragmentBundle b = new NodeFragmentBundle(1);
        b.putString(key, value);
        return b;
    }

    /**
     * 返回一个简单的single-pair
     *
     * @return single-pair bundle值,可能为空
     */
    public String getPairValue() {

        int size = mMap.size();
        if (size > 1) {
            Log.w(TAG, "getPairValue() used on Bundle with multiple pairs.");
        }
        if (size == 0) {
            return null;
        }
        Object o = mMap.valueAt(0);
        try {
            return (String) o;
        } catch (ClassCastException e) {
            typeWarning("getPairValue()", o, "String", e);
            return null;
        }
    }

    /**
     * 设置类加载
     * @param loader    类加载
     */
    public void setClassLoader(ClassLoader loader) {
        mClassLoader = loader;
    }

    /**
     *
     * 返回bundle关联的ClassLoader
     */
    public ClassLoader getClassLoader() {
        return mClassLoader;
    }


    /**
     * 克隆当前的Bundle
     * @return 一个Object对象
     */
    @Override
    public Object clone() {
        return new NodeFragmentBundle(this);
    }

    /**
     * 返回Bundle映射数量
     * @return  映射数据
     */
    public int size() {
        return mMap.size();
    }

    /**
     * 判断bundle映射是否为空
     * @return true为空否者反之
     */
    public boolean isEmpty() {
        return mMap.isEmpty();
    }


    /**
     * 清除,重置
     */
    public void clear() {
        mMap.clear();
        mHasFds = false;
        mFdsKnown = true;
    }

    /**
     * 检查是否存在相应的Bundle
     * @param key   字符串key
     * @return      true存在映射 false不存在映射
     */
    public boolean containsKey(String key) {
        return mMap.containsKey(key);
    }

    /**
     * 根据key返回object
     * @param key   字符串key
     * @return      Object,可能为空
     */
    public Object get(String key) {
        return mMap.get(key);
    }


    /**
     * 移除一个Bundle映射
     * @param key
     */
    public void remove(String key) {
        mMap.remove(key);
    }


    /**
     * 返回正在使用的Bundle
     * @return 集合Bundle
     */
    public Set<String> keySet() {

        return mMap.keySet();
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value 一个boolean值
     */
    public void putBoolean(String key, boolean value) {

        mMap.put(key, value);
    }


    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value 一个数组
     */
    public void putByte(String key, byte value) {
        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value 一个字符
     */
    public void putChar(String key, char value) {
        mMap.put(key, value);
    }


    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value 一个short
     */
    public void putShort(String key, short value) {
        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key     字符串key
     * @param value   一个short
     */
    public void putInt(String key, int value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value 一个long
     */
    public void putLong(String key, long value) {

        mMap.put(key, value);
    }


    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value 一个float
     */
    public void putFloat(String key, float value) {

        mMap.put(key, value);
    }


    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value 一个doublet
     */
    public void putDouble(String key, double value) {

        mMap.put(key, value);
    }


    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value 一个String    可能为空
     */
    public void putString(String key, String value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value 一个CharSequence 可能为空
     */
    public void putCharSequence(String key, CharSequence value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value ArrayList<Integer> 可能为空
     */
    public void putIntegerArrayList(String key, ArrayList<Integer> value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value ArrayList<String> 可能为空
     */
    public void putStringArrayList(String key, ArrayList<String> value) {

        mMap.put(key, value);
    }


    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value ArrayList<CharSequence> 可能为空
     */
    public void putCharSequenceArrayList(String key,
                                         ArrayList<CharSequence> value) {

        mMap.put(key, value);
    }


    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value Serializable object 可能为空
     */
    public void putSerializable(String key, Serializable value) {
        mMap.put(key, value);
    }


    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value  boolean数组 可能为空
     */
    public void putBooleanArray(String key, boolean[] value) {
        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value  byte数组 可能为空
     */
    public void putByteArray(String key, byte[] value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value short数组 可能为空
     */
    public void putShortArray(String key, short[] value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value char数组 可能为空
     */
    public void putCharArray(String key, char[] value) {

        mMap.put(key, value);
    }


    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value int数组 可能为空
     */
    public void putIntArray(String key, int[] value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value long数组 可能为空
     */
    public void putLongArray(String key, long[] value) {

        mMap.put(key, value);
    }


    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value float数组 可能为空
     */
    public void putFloatArray(String key, float[] value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value double数组 可能为空
     */
    public void putDoubleArray(String key, double[] value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value String数组 可能为空
     */
    public void putStringArray(String key, String[] value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value  CharSequence数组 可能为空
     */
    public void putCharSequenceArray(String key, CharSequence[] value) {

        mMap.put(key, value);
    }

    /**
     * 插入Bundle映射,覆盖已存在的key
     * @param key   字符串key
     * @param value  一个NodeFragmentBundle对象 可能为空
     */
    public void putBundle(String key, NodeFragmentBundle value) {

        mMap.put(key, value);
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      boolean值
     */
    public boolean getBoolean(String key) {

        if (DEBUG)
            Log.d(TAG,
                    "Getting boolean in "
                            + Integer.toHexString(System.identityHashCode(this)));
        return getBoolean(key, false);
    }

    // Log a message if the value was non-null but not of the expected type
    private void typeWarning(String key, Object value, String className,
                             Object defaultValue, ClassCastException e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Key ");
        sb.append(key);
        sb.append(" expected ");
        sb.append(className);
        sb.append(" but value was a ");
        sb.append(value.getClass().getName());
        sb.append(".  The default value ");
        sb.append(defaultValue);
        sb.append(" was returned.");
        Log.w(TAG, sb.toString());
        Log.w(TAG, "Attempt to cast generated internal exception:", e);
    }

    private void typeWarning(String key, Object value, String className,
                             ClassCastException e) {
        typeWarning(key, value, className, "<null>", e);
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @param defaultValue  value不存在默认的value
     * @return              boolean值
     */
    public boolean getBoolean(String key, boolean defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Boolean) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Boolean", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      byte值
     */
    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }


    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @param defaultValue value不存在默认的value
     * @return      byte值
     */
    public Byte getByte(String key, byte defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Byte) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Byte", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     *返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      char值
     */
    public char getChar(String key) {
        return getChar(key, (char) 0);
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @param defaultValue value不存在默认的value
     * @return      char值默认defaultValue
     */
    public char getChar(String key, char defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Character) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Character", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      short值
     */
    public short getShort(String key) {

        return getShort(key, (short) 0);
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @param defaultValue value不存在默认的value
     * @return      short值
     */
    public short getShort(String key, short defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Short) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Short", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      int值
     */
    public int getInt(String key) {

        return getInt(key, 0);
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @param defaultValue value不存在默认的value
     * @return      int值
     */
    public int getInt(String key, int defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Integer) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Integer", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      long值
     */
    public long getLong(String key) {

        return getLong(key, 0L);
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @param defaultValue value不存在默认的value
     * @return      long值
     */
    public long getLong(String key, long defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Long) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Long", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      float值
     */
    public float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @param defaultValue value不存在默认的value
     * @return      float值
     */
    public float getFloat(String key, float defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Float) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Float", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      double值
     */
    public double getDouble(String key) {

        return getDouble(key, 0.0);
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @param defaultValue value不存在默认的value
     * @return      double值
     */
    public double getDouble(String key, double defaultValue) {

        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Double) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Double", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      String值 可能为空
     */
    public String getString(String key) {

        final Object o = mMap.get(key);
        try {
            return (String) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "String", e);
            return null;
        }
    }


    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @param defaultValue value不存在默认的value
     * @return     String值 可能为空
     */
    public String getString(String key, String defaultValue) {
        final String s = getString(key);
        return (s == null) ? defaultValue : s;
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     CharSequence值 可能为空
     */
    public CharSequence getCharSequence(String key) {

        final Object o = mMap.get(key);
        try {
            return (CharSequence) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "CharSequence", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @param defaultValue value不存在默认的value
     * @return     CharSequence值 可能为空
     */
    public CharSequence getCharSequence(String key, CharSequence defaultValue) {
        final CharSequence cs = getCharSequence(key);
        return (cs == null) ? defaultValue : cs;
    }


    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     Bundle值 可能为空
     */
    public NodeFragmentBundle getBundle(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (NodeFragmentBundle) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Bundle", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     Serializable 可能为空
     */
    public Serializable getSerializable(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (Serializable) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Serializable", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     ArrayList<Integer> 可能为空
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Integer> getIntegerArrayList(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList<Integer>) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "ArrayList<Integer>", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     ArrayList<String> 可能为空
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> getStringArrayList(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList<String>) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "ArrayList<String>", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     ArrayList<CharSequence> 可能为空
     */
    @SuppressWarnings("unchecked")
    public ArrayList<CharSequence> getCharSequenceArrayList(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (ArrayList<CharSequence>) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "ArrayList<CharSequence>", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     boolean[] 可能为空
     */
    public boolean[] getBooleanArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (boolean[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "byte[]", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      byte[] 可能为空
     */
    public byte[] getByteArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (byte[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "byte[]", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      short[] 可能为空
     */
    public short[] getShortArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (short[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "short[]", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      char[] 可能为空
     */
    public char[] getCharArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (char[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "char[]", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     int[] 可能为空
     */
    public int[] getIntArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (int[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "int[]", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     long[] 可能为空
     */
    public long[] getLongArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (long[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "long[]", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     float[] 可能为空
     */
    public float[] getFloatArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (float[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "float[]", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     double[]  可能为空
     */
    public double[] getDoubleArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (double[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "double[]", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return     String[]  可能为空
     */
    public String[] getStringArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (String[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "String[]", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return    CharSequence[]  可能为空
     */
    public CharSequence[] getCharSequenceArray(String key) {

        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (CharSequence[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "CharSequence[]", e);
            return null;
        }
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return    Object值  可能为空
     */
    public void putObject(String key, Object value) {
        mMap.put(key, value);
    }

    /**
     * 返回插入Bundle映射的值,根据key查询
     * @param key   字符串key
     * @return      Object值 可能为空
     */
    public Object getObject(String key) {
        return mMap.get(key);
    }
}
