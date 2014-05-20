package com.navidroid.model.navigation;

import java.io.InvalidObjectException;
import java.util.List;

import com.navidroid.model.LatLng;
import com.navidroid.model.directions.Directions;
import com.navidroid.model.directions.Point;
import com.navidroid.model.positioning.Position;
import com.navidroid.model.util.LatLngUtil;

public class NavigationState {
	
	private final int LOOK_AHEAD_POINTS = 5;
	private final int LOOK_BEHIND_POINTS = 2;
	
	private List<Point> path;
	private Position position;
	private LatLng locationOnPath;
	private double bearingOnPath;
	private double distanceOffPath;
	private double bearingDifferenceFromPath;
	private int currentIndex;
	private Point currentPoint;
	private double distanceToNextPoint;
	private double progressAlongSegment;
	private boolean isHeadingOffPath;
	private boolean isOnPath;
	private long headingOffPathStartTime;
	private boolean isSnapshot;
	
	public NavigationState(Directions directions) {
		path = directions.getPath();
		currentIndex = 0;
		currentPoint = path.get(0);
		isHeadingOffPath = false;
		isOnPath = true;
		isSnapshot = false;
	}
	
	private NavigationState(NavigationState navigationStateSnapshot) {
		position = navigationStateSnapshot.getPosition();
		locationOnPath = navigationStateSnapshot.getLocationOnPath();
		bearingOnPath = navigationStateSnapshot.getBearingOnPath();
		distanceOffPath = navigationStateSnapshot.getDistanceOffPath();
		bearingDifferenceFromPath = navigationStateSnapshot.getBearingDifferenceFromPath();
		currentPoint = navigationStateSnapshot.getCurrentPoint();
		isHeadingOffPath = navigationStateSnapshot.isHeadingOffPath();
		isOnPath = navigationStateSnapshot.isOnPath();
		headingOffPathStartTime = navigationStateSnapshot.getHeadingOffPathStartTime();
		isSnapshot = true;
	}
	
	NavigationState snapshot() {
		return new NavigationState(this);
	}
	
	void update(Position position) throws InvalidObjectException {
		if (isSnapshot) {
			throw new InvalidObjectException("A NavigationState snapshot cannot be updated");
		} else {
			this.position = position;
			calculateLocationOnPath();
			calculateBearingOnPath();
			calculateDistanceToNextPoint();
			calculateProgressAlongSegment();
		}
	}
	
	public Point getCurrentPoint() {
		return currentPoint;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public LatLng getLocation() {
		return position.location;
	}
	
	public double getBearing() {
		return position.bearing;
	}
	
	public long getTime() {
		return position.timestamp;
	}
	
	public LatLng getLocationOnPath() {
		return locationOnPath;
	}
	
	public double getBearingOnPath() {
		return bearingOnPath;
	}
	
	public double getDistanceOffPath() {
		return distanceOffPath;
	}
	
	public double getBearingDifferenceFromPath() {
		return bearingDifferenceFromPath;
	}
	
	public boolean isHeadingOffPath() {
		return isHeadingOffPath;
	}
	
	public boolean isOnPath() {
		return isOnPath;
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
	
	public long getHeadingOffPathStartTime() {
		return headingOffPathStartTime;
	}
	
	public double getDistanceToCurrentDirection() {
		Point nextPoint = currentPoint.nextPoint;
		return nextPoint == null ? 0 : distanceToNextPoint + nextPoint.distanceToCurrentDirectionMeters;
	}
	
	public double getDistanceToArrival() {
		Point nextPoint = currentPoint.nextPoint;
		return nextPoint == null ? 0 : distanceToNextPoint + nextPoint.distanceToArrivalMeters;
	}
	
	public double getTimeToCurrentDirection() {
		Point nextPoint = currentPoint.nextPoint;
		double timeFromNextPoint = nextPoint == null ? 0 : nextPoint.timeToCurrentDirectionSeconds;
		return progressAlongSegment * currentPoint.timeToCurrentDirectionSeconds + timeFromNextPoint;
	}
	
	public double getTimeToArrival() {
		Point nextPoint = currentPoint.nextPoint;
		double timeToNextPoint = progressAlongSegment * currentPoint.timeToCurrentDirectionSeconds;
		return nextPoint == null || currentPoint.direction != nextPoint.direction ? timeToNextPoint :
			timeToNextPoint + nextPoint.timeToArrivalSeconds;
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
