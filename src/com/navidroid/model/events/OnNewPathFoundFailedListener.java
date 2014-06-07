package com.navidroid.model.events;

import com.navidroid.model.LatLng;

public interface OnNewPathFoundFailedListener {
	public boolean invoke(Exception e, LatLng destination);
	public boolean invoke(Exception e, String destinationAddress);
}
