package com.navidroid.model.directions;

import java.util.ArrayList;
import java.util.List;

import com.navidroid.model.LatLng;
import com.navidroid.model.util.LatLngUtil;

public class Directions {
	
	private List<Direction> directions;
	private ArrayList<Point> path;
	private ArrayList<LatLng> latLngPath;
	private String originAddress;
	private String destinationAddress;
	private LatLng origin;
	private LatLng destination;
	
	public Directions(LatLng origin, LatLng destination, List<Direction> directions) throws Exception {
		this(origin, destination, null, null, directions);
	}
	
	public Directions(LatLng origin, LatLng destination, String originAddress, String destinationAddress, List<Direction> directions) throws Exception {
		this.origin = origin;
		this.destination = destination;
		this.originAddress = originAddress;
		this.destinationAddress = destinationAddress;
		this.directions = directions;
		validateDirections();
		createPath();
		indexPath();
		createLatLngPath();
	}
	
	private void validateDirections() throws Exception {
		int numberOfDirections = directions.size();
		if (directions.get(0).getMovement() != Movement.DEPARTURE) {
			throw new Exception("First Direction object must be Movement.DEPATURE");
		}
		if (directions.get(numberOfDirections - 1).getMovement() != Movement.ARRIVAL) {
			throw new Exception("Last Direction object must be Movement.ARRIVAL");
		}
		for (int i = 1; i < numberOfDirections - 2; i++) {
			Movement movement = directions.get(i).getMovement();
			if (movement == Movement.ARRIVAL || movement == Movement.DEPARTURE) {
				throw new Exception("Intermediate Direction objects must not be Movement.ARRIVAL or Movement.DEPARTUE");
			}
		}
	}
	
	private void createPath() {
		Direction currentDirection;
		Point currentPoint;
		Point prevPoint = createLastPoint();
		
		path = new ArrayList<Point>();
		path.add(prevPoint);
		
		for (int i = directions.size() - 1; i >= 0; i--) {
			currentDirection = directions.get(i);
			List<LatLng> currentDirectionPoints = currentDirection.getPath();
			for (int j = currentDirectionPoints.size() - 1; j >= 0; j--) {
				currentPoint = createPoint(currentDirectionPoints.get(j), currentDirection, prevPoint);
				path.add(0, currentPoint);
				prevPoint = currentPoint;
			}
		}
	}
	
	private Point createLastPoint() {
		return new Point() {{
			location = destination;
			distanceToNextPointMeters = 0;
			timeToNextPointSeconds = 0;
			distanceToCurrentDirectionMeters = 0;
			timeToCurrentDirectionSeconds = 0;
			distanceToNextDirectionMeters = 0;
			timeToNextDirectionSeconds = 0;
			distanceToArrivalMeters = 0;
			timeToArrivalSeconds = 0;
			direction = directions.get(directions.size() - 1);
			nextDirection = null;
			next = null;
		}};
	}
	
	private Point createPoint(final LatLng loc, final Direction dir, final Point nextPoint) {
		final boolean isNewDirection = nextPoint.direction != dir;
		final double distanceToNext = LatLngUtil.distanceInMeters(loc, nextPoint.location);
		double directionDistance = dir.getDistanceInMeters();
		double ratioOfTotalDistance = directionDistance == 0 ? 0 : distanceToNext / directionDistance;
		final double timeToNext = ratioOfTotalDistance == 0 ? 0 : dir.getTimeInSeconds() * ratioOfTotalDistance;
		
		return new Point() {{
			location = loc;
			distanceToNextPointMeters = distanceToNext;
			timeToNextPointSeconds = timeToNext;
			distanceToCurrentDirectionMeters = isNewDirection ? 0 : nextPoint.distanceToCurrentDirectionMeters + distanceToNext;
			timeToCurrentDirectionSeconds = isNewDirection ? 0 : nextPoint.timeToCurrentDirectionSeconds + timeToNext;
			distanceToNextDirectionMeters = isNewDirection ? nextPoint.distanceToCurrentDirectionMeters + distanceToNext : nextPoint.distanceToNextDirectionMeters + distanceToNext;
			timeToNextDirectionSeconds = isNewDirection ? nextPoint.timeToCurrentDirectionSeconds + timeToNext : nextPoint.timeToNextDirectionSeconds + timeToNext;
			distanceToArrivalMeters = nextPoint.distanceToArrivalMeters + distanceToNext;
			timeToArrivalSeconds = nextPoint.timeToArrivalSeconds + timeToNext;
			direction = dir;
			nextDirection = isNewDirection ? nextPoint.direction : nextPoint.nextDirection;
			next = nextPoint;
		}};
	}
	
	private void indexPath() {
		Point currentPoint = path.get(0);
		Point lastPoint = null;
		int currentIndex = 0;
		do {
			currentPoint.pathIndex = currentIndex;
			currentPoint.prev = lastPoint;
			currentIndex++;
			lastPoint = currentPoint;
		} while ((currentPoint = currentPoint.next) != null);
	}
	
	private void createLatLngPath() {
		latLngPath = new ArrayList<LatLng>();
		LatLng lastLocation = path.get(0).location;
		for (int i = 1; i < path.size(); i++) {
			LatLng currentLocation = path.get(i).location;
			if (!lastLocation.equals(currentLocation)) {
				latLngPath.add(currentLocation);
			}
			lastLocation = currentLocation;
		}
	}
	
	public List<Direction> getDirectionsList() {
		return directions;
	}
	
	public List<Point> getPath() {
		return path;
	}
	
	public List<LatLng> getLatLngPath() {
		return latLngPath;
	}
	
	public LatLng getOrigin() {
		return origin;
	}
	
	public LatLng getDestination() {
		return destination;
	}
	
	public String getOriginAddress() {
		return originAddress;
	}
	
	public String getDestinationAddress() {
		return destinationAddress;
	}
}
