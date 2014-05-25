package com.navidroid.model.navigation;

import android.util.Log;

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
		hasDeparted = false;
		redirectNavigation(directions);
	}
	
	public void redirectNavigation(Directions directions) {
		path = directions.getPath();
		currentPoint = path.get(0);
		isHeadingOffPath = false;
		isOnPath = true;
		hasStartedFollowingDirections = false;
	}
	
	public void endNavigation() {
		path = null;
		currentPoint = null;
		locationOnPath = null;
		distanceOffPath = 0;
		bearingOnPath = 0;
		bearingDifferenceFromPath = 0;
	}

	public void signalOnPath() {
		isOnPath = true;
		isHeadingOffPath = false;
	}
	
	public void signalHeadingOffPath() {
		headingOffPathStartTime = position.timestamp;
		isHeadingOffPath = true;
	}
	
	public void signalOffPath() {
		isOnPath = false;
	}
	
	public void signalHasDeparted() {
		hasDeparted = true;
		assert isNavigating();
	}
	
	public void signalHasStartedFollowingDirections() {
		hasStartedFollowingDirections = true;
		assert hasDeparted();
	}
	
	public NavigationState getSnapshot() {
		return new NavigationState(this);
	}	
	
	private void calculateLocationOnPath() {
		int currentIndex = currentPoint == null ? 0 : currentPoint.pathIndex;
		double bestDistanceOffPath = Double.MAX_VALUE;
		LatLng bestLocation = null;
		Point bestPoint = null;
		
		for (int i = Math.max(0, currentIndex - LOOK_BEHIND_POINTS);
				i <= Math.min(path.size() - 1, currentIndex + LOOK_AHEAD_POINTS);
				i++) {
			
			Point currentPoint = path.get(i);
			LatLng currentLocationOnPath = currentPoint.next == null ? currentPoint.location :
				LatLngUtil.closestLocationOnLine(currentPoint.location, currentPoint.next.location, position.location);
			double currentDistance = LatLngUtil.distanceInMeters(position.location, currentLocationOnPath);

			if (currentDistance < bestDistanceOffPath) {
				bestDistanceOffPath = currentDistance;
				bestPoint = currentPoint;
				bestLocation = currentLocationOnPath;
			}
		}
		
		distanceOffPath = bestDistanceOffPath;
		currentPoint = bestPoint;
		locationOnPath = bestLocation;
	}
	
	private void calculateBearingOnPath() {
		bearingOnPath = LatLngUtil.initialBearing(currentPoint.location, currentPoint.next.location); // TODO: Throws NPE
		bearingDifferenceFromPath = LatLngUtil.bearingDiff(bearingOnPath, position.bearing);
	}
	
	private void calculateDistanceToNextPoint() {
		Point nextPoint = currentPoint.next;
		if (nextPoint != null) {
			distanceToNextPoint = LatLngUtil.distanceInMeters(locationOnPath, nextPoint.location);
		}
	}
	
	private void calculateProgressAlongSegment() {
		Point nextPoint = currentPoint.next;
		if (nextPoint != null) {
			double segmentDistance = currentPoint.distanceToNextPointMeters;
			progressAlongSegment = segmentDistance == 0 ? 0 : distanceToNextPoint / segmentDistance; 
		}
	}
}