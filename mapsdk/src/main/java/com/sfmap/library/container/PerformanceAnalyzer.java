package com.sfmap.library.container;


import android.os.SystemClock;


import com.sfmap.map.api.BuildConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 页面统计类
 */
public class PerformanceAnalyzer {
    public static enum Category {
        Activity, Fragment, Logic
    }


    private static ConcurrentHashMap<Category, ConcurrentHashMap<String, ConcurrentHashMap<String, ExecutorInfo>>> sTimeRecorderConcurrentHashMap = new ConcurrentHashMap<Category, ConcurrentHashMap<String, ConcurrentHashMap<String, ExecutorInfo>>>();

    /**
     * 开始统计Category类型出现的次数
     *
     * @param executor  引用对象(一般为context)
     * @param category  Category类型
     * @param type      类型名称
     */
    public static void startRecordPerformance(Object executor,
                                              Category category, String type) {
        if (BuildConfig.DEBUG) {
            getExecutorInfo(executor, category, type).startRecord();
        }
    }

    /**
     * 结束统计Category页面次数
     *
     * @param executor  引用对象(一般为context)
     * @param category  Category类型
     * @param type      类型名称
     */
    public static void stopRecordPerformance(Object executor, Category category, String type) {
        if (BuildConfig.DEBUG) {
            getExecutorInfo(executor, category, type).stopRecord();
        }
    }

    private static ExecutorInfo getExecutorInfo(Object executor,
                                                Category category, String type) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ExecutorInfo>> classMap = sTimeRecorderConcurrentHashMap
                .get(category);
        final String className = executor.getClass().getSimpleName();
        if (classMap == null) {
            ExecutorInfo executorInfo = new ExecutorInfo(className, category, type);
            ConcurrentHashMap<String, ExecutorInfo> typeMap = new ConcurrentHashMap<String, ExecutorInfo>();
            typeMap.put(type, executorInfo);
            classMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, ExecutorInfo>>();
            classMap.put(className, typeMap);
            sTimeRecorderConcurrentHashMap.put(category, classMap);
            return executorInfo;
        } else {
            ConcurrentHashMap<String, ExecutorInfo> typeMap = classMap.get(className);
            if (typeMap == null) {
                ExecutorInfo executorInfo = new ExecutorInfo(className, category, type);
                typeMap = new ConcurrentHashMap<String, ExecutorInfo>();
                typeMap.put(type, executorInfo);
                classMap.put(className, typeMap);
                return executorInfo;
            } else {
                ExecutorInfo executorInfo = typeMap.get(type);
                if (executorInfo == null) {
                    executorInfo = new ExecutorInfo(className, category, type);
                    typeMap.put(type, executorInfo);
                    return executorInfo;
                } else {
                    return executorInfo;
                }
            }
        }
    }

    /**
     * 返回分析结果
     * @return  引用数组
     */
    public static ArrayList<ExecutorInfo> printAnalysis() {
        ArrayList<ExecutorInfo> executorInfoList = new ArrayList<ExecutorInfo>();
        Enumeration<ConcurrentHashMap<String, ConcurrentHashMap<String, ExecutorInfo>>> categoryIterator = sTimeRecorderConcurrentHashMap
                .elements();
        while (categoryIterator.hasMoreElements()) {
            ConcurrentHashMap<String, ConcurrentHashMap<String, ExecutorInfo>> hashMap = (ConcurrentHashMap<String, ConcurrentHashMap<String, ExecutorInfo>>) categoryIterator
                    .nextElement();
            Enumeration<ConcurrentHashMap<String, ExecutorInfo>> classEnumeration = hashMap
                    .elements();
            while (classEnumeration.hasMoreElements()) {
                ConcurrentHashMap<String, ExecutorInfo> concurrentHashMap = (ConcurrentHashMap<String, ExecutorInfo>) classEnumeration
                        .nextElement();
                Enumeration<ExecutorInfo> enumeration = concurrentHashMap
                        .elements();
                while (enumeration.hasMoreElements()) {
                    ExecutorInfo executorInfo = (ExecutorInfo) enumeration
                            .nextElement();
                    executorInfoList.add(executorInfo);
                    // System.out.println("Category: "+executorInfo.mCategory +
                    // " Class: "+executorInfo.mClassName +
                    // " Type: "+executorInfo.mType);
                    // System.out.println("MinExecuteTime: "+executorInfo.mMinExecuteTime+" MaxExecuteTime"+executorInfo.mMaxExecuteTime);
                }
            }
        }
        return executorInfoList;
    }

    public static ArrayList<ExecutorInfo> analysisSlowestElementOfType(
            Category category) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ExecutorInfo>> classMap = sTimeRecorderConcurrentHashMap
                .get(category);
        ArrayList<ExecutorInfo> executorInfoList = printAnalysis();
        ArrayList<ExecutorInfo> resultArrayList = new ArrayList<ExecutorInfo>();
        HashSet<String> typeList = new HashSet<String>();
        for (ExecutorInfo executorInfo : executorInfoList) {
            typeList.add(executorInfo.mType);
        }
        Iterator<String> iterator = typeList.iterator();
        while (iterator.hasNext()) {
            final String type = (String) iterator.next();
            Collections.sort(executorInfoList, new MaxExecuteTimeComparator(
                    type));
            resultArrayList.add(executorInfoList.get(0));
        }
        Collections.sort(resultArrayList, new MaxExecuteTimeComparator(null));
        // for(ExecutorInfo executorInfo : resultArrayList){
        // System.out.println("Type: "+executorInfo.mType+"  MaxExecuteTime: "+executorInfo.mMaxExecuteTime);
        // }
        return resultArrayList;
    }

    private static class MaxExecuteTimeComparator implements
            Comparator<ExecutorInfo> {
        private String mType;

        MaxExecuteTimeComparator(String type) {
            mType = type;
        }

        @Override
        public int compare(ExecutorInfo lhs, ExecutorInfo rhs) {
            if (lhs.mType.equals(mType)) {
                if (rhs.mType.equals(mType)) {
                    return (int) -(lhs.mMaxExecuteTime - rhs.mMaxExecuteTime);
                } else {
                    return -1;
                }
            } else {
                if (rhs.mType.equals(mType)) {
                    return 1;
                } else {
                    return -(int) (lhs.mMaxExecuteTime - rhs.mMaxExecuteTime);
                }
            }
        }

    }

    /**
     * 页面统计类模型
     */
    public static class ExecutorInfo {
        String mClassName;
        Category mCategory;
        String mType;
        int mExecuteCount;
        long mMinExecuteTime = -1;
        long mMaxExecuteTime = -1;
        // long mAverageExecuteTime;
        long mTotalExecuteTime;
        private long mStartTime;

        ExecutorInfo(String className, Category category, String type) {
            mClassName = className;
            mCategory = category;
            mType = type;
        }

        private void startRecord() {
            mExecuteCount++;
            mStartTime = SystemClock.uptimeMillis();
        }

        private void stopRecord() {
            long executeTime = SystemClock.uptimeMillis() - mStartTime;
            mTotalExecuteTime += executeTime;
            if (mMinExecuteTime == -1 || mMaxExecuteTime == -1) {
                mMinExecuteTime = mMaxExecuteTime = executeTime;
            }
            if (executeTime < mMinExecuteTime) {
                mMinExecuteTime = executeTime;
            }
            if (executeTime > mMaxExecuteTime) {
                mMaxExecuteTime = executeTime;
            }
        }

        /**
         * 返回统计的次数
         * @return 统计次数
         */
        public int getExecuteCount() {
            return mExecuteCount;
        }

        /**
         * 返回类型
         * @return  类型
         */
        public String getType() {
            return mType;
        }

        /**
         * 返回统计名称
         * @return  统计名称
         */
        public String getName() {
            return mClassName;
        }

        /**
         * 返回统计类型
         * @return    统计Category类型
         */
        public String getCategory() {
            switch (mCategory) {
                case Activity:
                    return "Activity";
                case Fragment:
                    return "Fragment";
                case Logic:
                    return "Logic";
            }
            return "Unknow Category!";
        }

        /**
         * 返回最小执行时间
         * @return  时间
         */
        public long getMinExecuteTime() {
            return mMinExecuteTime;
        }

        /**
         * 返回最大执行时间
         * @return  时间
         */
        public long getMaxExecuteTime() {
            return mMaxExecuteTime;
        }

        /**
         * 返回平均执行时间
         * @return  时间
         */
        public long getAverageExecuteTime() {
            return mTotalExecuteTime / mExecuteCount;
        }

    }
}
