package com.navidroid.model.positioning;

import java.util.List;

import com.navidroid.Defaults;
import com.navidroid.model.LatLng;

public class GpsOptions {
	
	public enum GpsType {
		REAL,
		SIMULATED
	}
	
	private GpsType gpsType = GpsType.REAL;
	private boolean debugMode = false;
	private int updateIntervalMilliseconds = 500;
	private LatLng simulatedLocation = Defaults.LOCATION;
	private List<LatLng> simulatedPath;
	
	public GpsOptions gpsType(GpsType gpsType) {
		this.gpsType = gpsType;
		return this;
	}
	
	public GpsType gpsType() {
		return gpsType;
	}
	
	public GpsOptions debugMode(boolean debugMode) {
		this.debugMode = debugMode;
		return this;
	}
	
	public boolean debugMode() {
		return debugMode;
	}
	
	public GpsOptions updateIntervalMilliseconds(int updateIntervalMilliseconds) {
		this.updateIntervalMilliseconds = updateIntervalMilliseconds;
		return this;
	}
	
	public int updateIntervalMilliseconds() {
		return updateIntervalMilliseconds;
	}
	
	public GpsOptions simulatedLocation(LatLng simulatedLocation) {
		this.simulatedLocation = simulatedLocation;
		return this;
	}
	
	public LatLng simulatedLocation() {
		return simulatedLocation;
	}
	
	public GpsOptions simulatedPath(List<LatLng> simulatedPath) {
		this.simulatedPath = simulatedPath;
		return this;
	}
	
	public List<LatLng> simulatedPath() {
		return simulatedPath;
	}
}
