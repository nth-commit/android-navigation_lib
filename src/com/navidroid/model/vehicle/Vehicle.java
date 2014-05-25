package com.navidroid.model.vehicle;

import android.graphics.Bitmap;

import com.navidroid.NavigationFragment;
import com.navidroid.model.LatLng;
import com.navidroid.model.PointD;
import com.navidroid.model.map.NavigationMap;
public class Vehicle {
	
	private Bitmap image;
	private PointD screenAnchor;
	private NavigationMap navigationMap;
	private LatLng location;
	private double bearing;
	
	private IVehicleMarker latLngMarker;
	private StaticVehicleMarker overlayMarker;
		
	public Vehicle(NavigationFragment navigationFragment, IVehicleMarkerFactory factory, NavigationMap navigationMap, VehicleOptions options) {
		this.navigationMap = navigationMap;
		location = options.location();
		image = options.image();
		setScreenAnchor(options.screenAnchor());
		
		latLngMarker = factory.createVehicleMarker(this, navigationMap);
		overlayMarker = new StaticVehicleMarker(navigationFragment, this, navigationMap);
		navigationMap.setVehicle(this);
	}
	
	public void signalFollowing() {
		latLngMarker.hide();
		overlayMarker.show();
	}
	
	public void signalNotFollowing() {
		overlayMarker.hide();
		latLngMarker.show();
	}
	
	public void setVehiclePosition(LatLng location, double bearing) {
		this.location = location;
		this.bearing = bearing;
		latLngMarker.setLocation(location);
		latLngMarker.setBearing(bearing);
		navigationMap.setVehiclePosition(location, bearing);
	}
	
	public LatLng getLocation() {
		return location;
	}
	
	public double getBearing() {
		return bearing;
	}
	
	public Bitmap getImage() {
		return image;
	}
	
	public void setScreenAnchor(PointD screenAnchor) {
		this.screenAnchor = screenAnchor;
		navigationMap.getMap().setAnchor(screenAnchor);
	}
	
	public PointD getScreenAnchor() {
		return screenAnchor;
	}
}