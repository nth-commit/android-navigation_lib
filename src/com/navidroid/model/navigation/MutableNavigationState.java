package com.navidroid.model.navigation;

import com.navidroid.model.LatLng;
import com.navidroid.model.directions.Directions;
import com.navidroid.model.directions.Point;
import com.navidroid.model.positioning.Position;
import com.navidroid.model.util.LatLngUtil;

public class MutableNavigationState extends NavigationState {
	
	private final int LOOK_AHEAD_POINTS = 5;
	private final int LOOK_BEHIND_POINTS = 2;
	
	public void update(Position position) {
		this.position = position;
		if (isNavigating()) {
			calculateLocationOnPath();
			calculateBearingOnPath();
			calculateDistanceToNextPoint();
			calculateProgressAlongSegment();
		}
	}
	
	public void startNavigation(Directions directions) {
		path = directions.getPath();
		currentIndex = 0;
		currentPoint = path.get(0);
		isHeadingOffPath = false;
		isOnPath = true;
	}
	
	public void endNavigation() {
		path = null;
		currentPoint = null;
		currentIndex = 0;
		locationOnPath = null;
		distanceOffPath = 0;
		bearingOnPath = 0;
		bearingDifferenceFromPath = 0;
	}

	public void signalOnPath() {
		isOnPath = true;
	}
	
	public void signalHeadingOffPath() {
		headingOffPathStartTime = position.timestamp;
		isHeadingOffPath = true;
	}
	
	public void signalOffPath() {
		isOnPath = false;
	}
	
	public NavigationState getSnapshot() {
		return new NavigationState(this);
	}	
	
	private void calculateLocationOnPath() {
		double bestDistanceOffPath = Double.MAX_VALUE;
		int bestIndex = 0;
		LatLng bestLocation = null;
		Point bestPoint = null;
		
		for (int i = Math.max(0, currentIndex - LOOK_BEHIND_POINTS);
				i <= Math.min(path.size() - 1, currentIndex + LOOK_AHEAD_POINTS);
				i++) {
			
			Point currentPoint = path.get(i);
			LatLng currentLocationOnPath = currentPoint.nextPoint == null ? currentPoint.location :
				LatLngUtil.closestLocationOnLine(currentPoint.location, currentPoint.nextPoint.location, position.location);
			double currentDistance = LatLngUtil.distanceInMeters(position.location, currentLocationOnPath);

			if (currentDistance < bestDistanceOffPath) {
				bestDistanceOffPath = currentDistance;
				bestIndex = i;
				bestPoint = currentPoint;
				bestLocation = currentLocationOnPath;
			}
		}
		
		distanceOffPath = bestDistanceOffPath;
		currentIndex = bestIndex;
		currentPoint = bestPoint;
		locationOnPath = bestLocation;
	}
	
	private void calculateBearingOnPath() {
		bearingOnPath = LatLngUtil.initialBearing(currentPoint.location, currentPoint.nextPoint.location);
		bearingDifferenceFromPath = Math.max(bearingOnPath, position.bearing) - Math.min(bearingOnPath, position.bearing);
	}
	
	private void calculateDistanceToNextPoint() {
		Point nextPoint = currentPoint.nextPoint;
		if (nextPoint != null) {
			distanceToNextPoint = LatLngUtil.distanceInMeters(locationOnPath, nextPoint.location);
		}
	}
	
	private void calculateProgressAlongSegment() {
		Point nextPoint = currentPoint.nextPoint;
		if (nextPoint != null) {
			double segmentDistance = currentPoint.distanceToNextPointMeters;
			progressAlongSegment = segmentDistance == 0 ? 0 : distanceToNextPoint / segmentDistance; 
		}
	}
}