package com.sfmap.tbt;

public class OfflineDataElem {
    int nID;				//!< 离线数据ID
    int nDSize;;				//!< 数据大小
    int nISize;				//!< 附加信息长度
    String[] wName;		//!< 数据名称，Unicode编码方式
    String[] wVers;		//!< 版本信息，Unicode编码方式
    String[] wInfo;		//!< 附加信息，Unicode编码方式
}
