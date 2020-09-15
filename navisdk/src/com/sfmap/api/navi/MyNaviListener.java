package com.sfmap.api.navi;

import com.sfmap.api.navi.model.CarLocation;

abstract interface MyNaviListener extends NaviListener {
	public abstract void carProjectionChange(CarLocation paramCarLocation);
}