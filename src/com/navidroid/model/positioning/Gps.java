package com.navidroid.model.positioning;

import android.location.Location;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.navidroid.model.Defaults;
import com.navidroid.model.LatLng;
import com.navidroid.model.util.LatLngUtil;

public class Gps extends AbstractGps implements LocationListener {

	private LocationClient locationClient;
	private boolean isTrackingEnabled = false;

	public Gps(GpsOptions options, LocationClient locationClient) {
		super(options);
		this.locationClient = locationClient;
	}

	@Override
	public void enableTracking() {
		if (!isTrackingEnabled) {
			LocationRequest request = LocationRequest.create();
			request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			request.setInterval(updateIntervalMs);
			this.locationClient.requestLocationUpdates(request, this);
			isTrackingEnabled = true;
		}
	}

	@Override
	public void disableTracking() {
		if (isTrackingEnabled) {
			this.locationClient.removeLocationUpdates(this);
			isTrackingEnabled = false;
		}
	}

	@Override
	public void forceTick() {
		onLocationChanged(locationClient.getLastLocation());
	}

	@Override
	public void onLocationChanged(final Location loc) {
		onTickHandler.invoke(new Position(LatLngUtil.toLatLng(loc), loc
				.hasBearing() ? loc.getBearing() : 0, System
				.currentTimeMillis()));
	}

	@Override
	public LatLng getLastLocation() {
		Location loc = locationClient.getLastLocation();
		return loc == null ? Defaults.LOCATION : LatLngUtil.toLatLng(loc);
	}
}
