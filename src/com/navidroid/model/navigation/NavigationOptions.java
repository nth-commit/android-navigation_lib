package com.navidroid.model.navigation;

import com.navidroid.model.directions.IDirectionsFactory;
import com.navidroid.model.map.MapOptions;
import com.navidroid.model.positioning.GpsOptions;
import com.navidroid.model.vehicle.VehicleOptions;

public class NavigationOptions {
	
	private VehicleOptions vehicleOptions = new VehicleOptions();
	private MapOptions mapOptions = new MapOptions();
	private GpsOptions gpsOptions = new GpsOptions();
	
	public NavigationOptions vehicleOptions(VehicleOptions options) {
		vehicleOptions = options;
		return this;
	}
	
	public VehicleOptions vehicleOptions() {
		return vehicleOptions;
	}
	
	public NavigationOptions mapOptions(MapOptions options) {
		mapOptions = options;
		return this;
	}
	
	public MapOptions mapOptions() {
		return mapOptions;
	}
	
	public NavigationOptions gpsOptions(GpsOptions options) {
		gpsOptions = options;
		return this;
	}
	
	public GpsOptions gpsOptions() {
		return gpsOptions;
	}
}
