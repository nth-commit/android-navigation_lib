package com.navidroid.model.navigation;

import com.navidroid.model.LatLng;
import com.navidroid.model.directions.Directions;

public interface INavigatorStateListener {
	
	void OnNewPathFoundFailed(String message, LatLng origin, LatLng destination);
	
	void OnNewPathFound(Directions directions, LatLng origin, LatLng destination);
	
	void OnDeparture(NavigationState state);
	
	void OnArrival(NavigationState state);
	
	void OnVehicleOffPath(NavigationState state);
	
	void OnNewDirection(NavigationState state);
	
	void OnNavigatorTick(NavigationState state);
}
