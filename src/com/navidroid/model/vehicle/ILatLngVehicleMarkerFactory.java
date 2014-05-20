package com.navidroid.model.vehicle;

import com.navidroid.model.map.NavigationMap;

public interface ILatLngVehicleMarkerFactory {
	
	ILatLngVehicleMarker createLatLngVehicleMarker(Vehicle vehicle, NavigationMap navigationMap);

}
