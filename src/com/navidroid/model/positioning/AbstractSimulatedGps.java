package com.navidroid.model.positioning;

import java.util.List;

import android.util.Log;

import com.navidroid.model.LatLng;
import com.navidroid.model.util.LatLngUtil;
import com.navidroid.model.util.MathUtil;

public abstract class AbstractSimulatedGps extends AbstractGps {
	
	protected interface WhileHasCurrentPathAction {
		void invoke();
	}
	
	private final int SPEED_LIMIT_KPH = 50;
	private final double KPH_TO_MPS = 0.277778;
	private final double SPEED_LIMIT_MPS = SPEED_LIMIT_KPH * KPH_TO_MPS;
	private final double S_TO_MS = 1000;
	private final int MAX_ERROR_DIST_METERS = 10;
	private final int MAX_ERROR_HEADING_DEGREES = 5;
	
	protected Position currentPosition;
	protected List<LatLng> currentPath;
	protected List<LatLng> customPath;
	
	private Object currentPathLock = new Object();
	
	private boolean simulateError;
	
	public AbstractSimulatedGps(GpsOptions options, LatLng location) {
		super(options);
		customPath = options.simulatedGpsOptions().simulatedPath();
		simulateError = options.simulatedGpsOptions().simulateError();
		currentPosition = new Position(location, 0, System.currentTimeMillis());
	}

	public void followPath(List<LatLng> path) {
		synchronized (currentPathLock) {
			if (customPath == null) {
				currentPath = path;
			} else {
				currentPath = customPath;
			}
		}
		doFollowPath();
	}
	
	public void clearPath() {
		synchronized (currentPathLock) {
			currentPath = null;
		}
	}
	
	public abstract void doFollowPath();
	
	@Override
	public void enableTracking() {
		forceTick();
	}

	@Override
	public void disableTracking() {
	}
	
	@Override
	public void forceTick() {
		onTickHandler.invoke(currentPosition);
	}
	
	@Override
	public LatLng getLastLocation() {
		return currentPosition.location;
	}
	
	protected void advancePosition(List<LatLng> path, long toTime) {
		long timePassedMillisconds = toTime - currentPosition.timestamp;
		double distanceRemaining = (timePassedMillisconds / S_TO_MS) * SPEED_LIMIT_MPS;
		LatLng currentLocation = currentPosition.location;
		double currentBearing = 0;
		
		while (path.size() > 0 && distanceRemaining > 0) {
			LatLng nextLocationInPath = path.get(0);
			double distanceToNextPoint = LatLngUtil.distanceInMeters(currentLocation, nextLocationInPath);
			double distanceToTravel = Math.min(distanceToNextPoint, distanceRemaining);
			currentBearing = LatLngUtil.initialBearing(currentLocation, nextLocationInPath);
			currentLocation = LatLngUtil.travel(currentLocation, currentBearing, distanceToTravel);
			
			distanceRemaining -= distanceToTravel;
			if (distanceRemaining > 0) {
				path.remove(0);
			}
		}
		
		currentPosition = new Position(currentLocation, currentBearing, toTime);
		if (simulateError) {
			messUpCurrentPosition();
		}
	}
	
	private void messUpCurrentPosition() {
		int distanceErrorMagnitude = MathUtil.randomInt(0, MAX_ERROR_DIST_METERS);
		int distanceErrorVector = MathUtil.randomInt(0, 360);
		LatLng currentLocation = currentPosition.location;
		LatLng errorLocation = LatLngUtil.travel(currentLocation, distanceErrorVector, distanceErrorMagnitude);
		double currentBearing = currentPosition.bearing;
		double errorBearing = MathUtil.randomInt((int)(currentBearing - MAX_ERROR_HEADING_DEGREES), (int)(currentBearing + MAX_ERROR_HEADING_DEGREES));
		errorBearing = LatLngUtil.normalizeBearing(errorBearing);
		currentPosition.location = errorLocation;
		currentPosition.bearing = errorBearing;
	}
	
	protected void whileHasCurrentPath(WhileHasCurrentPathAction action) {
		while (true) {
			synchronized (currentPathLock) {
				if (currentPath != null && currentPath.size() > 0) {
					action.invoke();
				} else {
					break;
				}
			}
		}
	}
}
