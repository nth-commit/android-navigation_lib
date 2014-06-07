package com.navidroid.model.directions;

import com.navidroid.model.LatLng;

public interface IDirectionsFactory {
	
	String createRequestUrl(LatLng origin, LatLng destination, LatLng rerouteWaypoint);
	
	Directions createDirections(LatLng origin, LatLng destination, String response) throws Exception;

}
