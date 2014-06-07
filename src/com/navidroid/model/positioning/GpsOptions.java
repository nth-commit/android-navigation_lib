package com.navidroid.model.positioning;

import java.util.List;

import com.navidroid.model.Defaults;
import com.navidroid.model.LatLng;

public class GpsOptions {
	
	public enum GpsType {
		REAL,
		SIMULATED
	}
	
	private GpsType gpsType = GpsType.REAL;
	private SimulatedGpsOptions simulatedGpsOptions = new SimulatedGpsOptions();
	private int updateIntervalMilliseconds = 500;
	
	public GpsOptions gpsType(GpsType gpsType) {
		this.gpsType = gpsType;
		return this;
	}
	
	public GpsType gpsType() {
		return gpsType;
	}
	
	public GpsOptions updateIntervalMilliseconds(int updateIntervalMilliseconds) {
		this.updateIntervalMilliseconds = updateIntervalMilliseconds;
		return this;
	}
	
	public int updateIntervalMilliseconds() {
		return updateIntervalMilliseconds;
	}
	
	public GpsOptions simulatedGpsOptions(SimulatedGpsOptions options) {
		simulatedGpsOptions = options;
		return this;
	}
	
	public SimulatedGpsOptions simulatedGpsOptions() {
		return simulatedGpsOptions;
	}
}
