package com.sfmap.api.navi.model;

import android.os.Parcel;
import android.os.Parcelable;

final class NaviLatLngCreatorDecode implements Parcelable.Creator<NaviLatLng> {
	public NaviLatLng a(Parcel paramParcel) {
		double d1 = paramParcel.readDouble();
		double d2 = paramParcel.readDouble();
		return new NaviLatLng(d1, d2);
	}

	public NaviLatLng[] a(int paramInt) {
		return new NaviLatLng[paramInt];
	}

	@Override
	public NaviLatLng createFromParcel(Parcel arg0) {
		return null;
	}

	@Override
	public NaviLatLng[] newArray(int arg0) {
		return null;
	}
}