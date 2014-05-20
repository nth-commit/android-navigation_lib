package com.navidroid.model.vehicle;

import android.graphics.Bitmap;

import com.navidroid.Defaults;
import com.navidroid.model.LatLng;
import com.navidroid.model.PointD;

public class VehicleOptions {
	
	private LatLng location = Defaults.LOCATION;
	private Bitmap image;
	private PointD imageAnchor = new PointD(0.5, 0.5); // TODO: Implement me.
	private PointD screenAnchor = new PointD(0.5d, 0.75d);
	private ILatLngVehicleMarkerFactory latLngVehicleMarkerFactory;
	
	public VehicleOptions location(LatLng location) {
		this.location = location;
		return this;
	}
	
	public LatLng location() {
		return location;
	}
	
	public VehicleOptions image(Bitmap image) {
		this.image = image;
		return this;
	}
	
	public Bitmap image() {
		return image;
	}
	
	public VehicleOptions screenAnchor(PointD screenAnchor) {
		this.screenAnchor = screenAnchor;
		return this;
	}
	
	public PointD screenAnchor() {
		return screenAnchor;
	}
	
	public VehicleOptions latLngVehicleMarkerFactory(ILatLngVehicleMarkerFactory latLngVehicleMarkerFactory) {
		this.latLngVehicleMarkerFactory = latLngVehicleMarkerFactory;
		return this;
	}
	
	public ILatLngVehicleMarkerFactory latLngVehicleMarkerFactory() {
		return latLngVehicleMarkerFactory;
	}
}