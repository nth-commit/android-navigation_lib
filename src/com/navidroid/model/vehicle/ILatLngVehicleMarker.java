package com.navidroid.model.vehicle;

import com.navidroid.model.LatLng;

public interface ILatLngVehicleMarker extends IVehicleMarker {
	void setLocation(LatLng location);
	void setBearing(double bearing);
}
