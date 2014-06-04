package com.navidroid.model.navigation;

import com.navidroid.model.announcements.AnnouncementOptions;
import com.navidroid.model.map.MapOptions;
import com.navidroid.model.positioning.GpsOptions;
import com.navidroid.model.vehicle.VehicleOptions;

public class NavigationOptions {
	
	private VehicleOptions vehicleOptions = new VehicleOptions();
	private MapOptions mapOptions = new MapOptions();
	private GpsOptions gpsOptions = new GpsOptions();
	private AnnouncementOptions announcementOptions = new AnnouncementOptions();
	private NavigationStateListenerOptions navigationStateListenerOptions = new NavigationStateListenerOptions();
	
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
	
	public NavigationOptions announcementOptions(AnnouncementOptions options) {
		announcementOptions = options;
		return this;
	}
	
	public AnnouncementOptions announcementOptions() {
		return announcementOptions;
	}
	
	public NavigationOptions navigationStateListenerOptions(NavigationStateListenerOptions options) {
		navigationStateListenerOptions = options;
		return this;
	}
	
	public NavigationStateListenerOptions navigationStateListenerOptions() {
		return navigationStateListenerOptions;
	}
}
