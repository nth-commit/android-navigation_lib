package com.navidroid.model.navigation;

import com.navidroid.model.LatLng;
import com.navidroid.model.directions.Directions;

public interface INavigationStateListener {
	
	void OnNewPathFoundFailed(Exception e, LatLng origin, LatLng destination);
	
	void OnNewPathFound(Directions directions, LatLng origin, LatLng destination);
	
	void OnNavigationStarted(NavigationState state);
	
	void OnDeparture(NavigationState state);
	
	void OnArrival(NavigationState state);
	
	void OnVehicleOffPath(NavigationState state);
	
	void OnNewDirection(NavigationState state);
	
	void OnNavigatorTick(NavigationState state);
}
