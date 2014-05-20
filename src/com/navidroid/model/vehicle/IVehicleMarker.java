package com.navidroid.model.vehicle;

import com.navidroid.model.LatLng;

public interface IVehicleMarker {
	void show();
	void hide();
	void setLocation(LatLng location);
	void setBearing(double bearing);
}
