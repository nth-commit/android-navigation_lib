package com.navidroid.model;

public class LatLng {
	
	public double latitude;
	public double longitude;
	
	public LatLng(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public boolean equals(LatLng other, double tolerance) {
		double maxLat = Math.max(latitude, other.latitude);
		double minLat = Math.min(latitude, other.latitude);
		double maxLng = Math.max(longitude, other.longitude);
		double minLng = Math.min(longitude, other.longitude);
		return ((maxLat - minLat) <= tolerance) && ((maxLng - minLng <= tolerance));
	}
}
