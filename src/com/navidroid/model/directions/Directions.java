package com.navidroid.model.directions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.navidroid.model.LatLng;
import com.navidroid.model.util.LatLngUtil;

public class Directions {
	
	private final static int MIN_DIST_FOR_Bearing_METERS = 5;
	private final static int MIN_U_TURN_ANGLE = 175;
	private final static int MIN_SHARP_TURN_ANGLE = 150;
	private final static int MIN_SHARP_RIGHT_TURN_ANGLE = MIN_SHARP_TURN_ANGLE;
	private final static int MIN_SHARP_LEFT_TURN_ANGLE = -MIN_SHARP_TURN_ANGLE;
	private final static int MIN_TURN_ANGLE = 45;
	private final static int MIN_RIGHT_TURN_ANGLE = MIN_TURN_ANGLE;
	private final static int MIN_LEFT_TURN_ANGLE = -MIN_TURN_ANGLE;
	private final static int MIN_VEER_MOVEMENT_ANGLE = 10;
	private final static int MIN_RIGHT_VEER_ANGLE = MIN_VEER_MOVEMENT_ANGLE;
	private final static int MIN_LEFT_VEER_ANGLE = -MIN_VEER_MOVEMENT_ANGLE;
	
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
		createPath();
		indexPath();
		calculateMovements();
		createLatLngPath();
		validateDirections();
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
	
	private void calculateMovements() {
		directions.get(0).setMovement(Movement.DEPARTURE);
		int numberOfDirections = directions.size();
		for (int i = 1; i < numberOfDirections - 1; i++) {
			Direction currentDirection = directions.get(i);
			if (currentDirection.getMovement() == null) {
				Movement currentMovement = calculateMovement(currentDirection.getPath(), directions.get(i + 1).getPath());
				currentDirection.setMovement(currentMovement);
			}
		}
		directions.get(numberOfDirections - 1).setMovement(Movement.ARRIVAL);
	}
	
	private Movement calculateMovement(List<LatLng> currentPath, List<LatLng> nextPath) {
		if (currentPath.size() < 2 || nextPath.size() < 2) {
			return Movement.UNKNOWN;
		}
		
		double movementAngle = calculateAngleAtMovement(currentPath, nextPath);
		if (movementAngle > 180) {
			return Movement.UNKNOWN;
		} else if (movementAngle > MIN_U_TURN_ANGLE) {
			return Movement.U_TURN;
		} else if (movementAngle > MIN_SHARP_RIGHT_TURN_ANGLE) {
			return Movement.TURN_RIGHT_SHARP;
		} else if (movementAngle > MIN_RIGHT_TURN_ANGLE) {
			return Movement.TURN_RIGHT;
		} else if (movementAngle > MIN_RIGHT_VEER_ANGLE) {
			return Movement.VEER_RIGHT;
		} else if (movementAngle > MIN_LEFT_VEER_ANGLE) {
			return Movement.CONTINUE;
		} else if (movementAngle > MIN_LEFT_TURN_ANGLE) {
			return Movement.VEER_LEFT;
		} else if (movementAngle > MIN_SHARP_LEFT_TURN_ANGLE) {
			return Movement.TURN_LEFT;
		} else if (movementAngle > -MIN_U_TURN_ANGLE) {
			return Movement.TURN_LEFT_SHARP;
		} else if (movementAngle >= -180) {
			return Movement.U_TURN;
		} else {
			return Movement.UNKNOWN;
		}
	}
	
	private double calculateAngleAtMovement(List<LatLng> currentPath, List<LatLng> nextPath) {
		double currentPathFinalBearing = calculateBearingAtPathEnd(currentPath);
		double nextPathInitialBearing = calculateBearingAtPathStart(nextPath);
		return 180 - LatLngUtil.normalizeBearing(nextPathInitialBearing - currentPathFinalBearing);
	}
	
	private double calculateBearingAtPathEnd(List<LatLng> path) {
		return calculateBearingOfPath(getRelevantPoints(path, false));
	}
	
	private double calculateBearingAtPathStart(List<LatLng> path) {
		return calculateBearingOfPath(getRelevantPoints(path, true));
	}
	
	private List<LatLng> getRelevantPoints(List<LatLng> path, boolean fromStart) {
		ArrayList<LatLng> points = new ArrayList<LatLng>();
		int pathSize = path.size();
		
		if (pathSize > 0) {
			int i = fromStart ? 0 : path.size() - 1;
			int iterationDirection = fromStart ? 1 : -1;
			int travelledDistance = 0;
			LatLng currentLocation = path.get(i);
			while (i >= 0 && i < path.size() && travelledDistance < MIN_DIST_FOR_Bearing_METERS) {
				LatLng nextLocation = path.get(i);
				travelledDistance += LatLngUtil.distanceInMeters(currentLocation, nextLocation);
				currentLocation = nextLocation;
				points.add(currentLocation);
				i += iterationDirection;
			}
		}
		
		if (!fromStart) {
			Collections.reverse(points);
		}
		
		return points;
	}
	
	private double calculateBearingOfPath(List<LatLng> path) {
		return LatLngUtil.initialBearing(path.get(0), path.get(path.size() - 1));
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
	
	public Direction getDepartureDirection() {
		return directions.get(0);
	}
	
	public Direction getArrivalDirection() {
		return directions.get(directions.size() - 1);
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
