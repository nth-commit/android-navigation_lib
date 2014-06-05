package com.navidroid.model.events;

import com.navidroid.model.LatLng;

public interface OnNewPathFoundFailedListener {
	public boolean invoke(Exception e, LatLng origin, LatLng destination);
}
