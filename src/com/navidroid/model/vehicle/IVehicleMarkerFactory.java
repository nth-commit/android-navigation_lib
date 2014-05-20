package com.navidroid.model.vehicle;

import com.navidroid.model.map.NavigationMap;

public interface IVehicleMarkerFactory {
	
	IVehicleMarker createVehicleMarker(Vehicle vehicle, NavigationMap navigationMap);

}
