package com.navidroid.model.navigation;

import com.navidroid.model.LatLng;

public interface INavigator {
	
	void go(LatLng location);
	
	void stop();
	
	void reroute();
	
	boolean isNavigating();
	
	LatLng getDestination();
	
}
