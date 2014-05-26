package com.navidroid.model.positioning;

import java.util.List;

import com.navidroid.Defaults;
import com.navidroid.model.LatLng;

public class SimulatedGpsOptions {
	
	private boolean debugMode = false;
	private LatLng simulatedLocation = Defaults.LOCATION;
	private List<LatLng> simulatedPath;
	private boolean simulateError = false;
	private int delayBeforePathFollow = 0;
	
	public SimulatedGpsOptions simulatedLocation(LatLng simulatedLocation) {
		this.simulatedLocation = simulatedLocation;
		return this;
	}
	
	public LatLng simulatedLocation() {
		return simulatedLocation;
	}
	
	public SimulatedGpsOptions simulatedPath(List<LatLng> simulatedPath) {
		this.simulatedPath = simulatedPath;
		return this;
	}
	
	public List<LatLng> simulatedPath() {
		return simulatedPath;
	}
	
	public SimulatedGpsOptions debugMode(boolean debugMode) {
		this.debugMode = debugMode;
		return this;
	}
	
	public boolean debugMode() {
		return debugMode;
	}
	
	public SimulatedGpsOptions simulateError(boolean simulateError) {
		this.simulateError = simulateError;
		return this;
	}
	
	public boolean simulateError() {
		return simulateError;
	}
	
	public SimulatedGpsOptions delayBeforePathFollow(int delayBeforePathFollow) {
		this.delayBeforePathFollow = delayBeforePathFollow;
		return this;
	}
	
	public int delayBeforePathFollow() {
		return delayBeforePathFollow;
	}
}
