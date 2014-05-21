package com.navidroid.model.map;

import java.util.List;

import com.navidroid.model.LatLng;
import com.navidroid.model.map.IMap.OnInvalidationAnimationFinished;
import com.navidroid.model.map.IMap.OnTouchEventHandler;
import com.navidroid.model.vehicle.Vehicle;

public class NavigationMap {

	private static final int FOLLOW_VEHICLE_ANIMATION_TIME = 500;
	private static final float NAVIGATING_TILT = 60;
	private static final float NAVIGATING_ZOOM = 19;

	private IMap map;
	private Vehicle vehicle;
	private LatLng vehicleLocation;
	private double vehicleBearing;
	private boolean trackLocation = true;
	
	public NavigationMap(IMap map, MapOptions options) {
		vehicleLocation = options.location();
		this.map = map;
		
		map.setOnTouchEventHandler(new OnTouchEventHandler() {
			@Override
			public void invoke() {
				unfollowVehicle();
			}
		});
	}
	
	public IMap getMap() {
		return map;
	}
	
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	public void addPathPolyline(List<LatLng> path) {
		map.addPolyline(new PolylineOptions().path(path).color(0xff3073F0));
	}
	
	public void removePolylinePath() {
		map.removePolyline();
	}
	
	public void unfollowVehicle() {
		trackLocation = false;
		vehicle.signalNotFollowing();
	}
	
	public void followVehicle() {
		trackLocation = true;
		map.setZoom(NAVIGATING_ZOOM);
		map.setTilt(NAVIGATING_TILT);
		map.setBearing(vehicleBearing);
		map.invalidate(FOLLOW_VEHICLE_ANIMATION_TIME, new OnInvalidationAnimationFinished() {
			@Override
			public void invoke() {
				vehicle.signalFollowing();
			}
		});
	}
	
	public void setVehiclePosition(LatLng location, double bearing) {
		vehicleLocation = location;
		vehicleBearing = bearing;
		
		if (trackLocation) {
			map.setBearing(bearing);
			map.setLocation(location);
			map.invalidate();
		}
	}
}
