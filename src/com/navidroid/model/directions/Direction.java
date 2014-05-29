package com.navidroid.model.directions;

import java.util.ArrayList;
import java.util.List;

import com.navidroid.model.LatLng;
import com.navidroid.model.util.LatLngUtil;
import com.navidroid.model.util.StringUtil;

public class Direction {
	
	private final static int MIN_DIST_FOR_HEADING_METERS = 5;
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
	
	private List<LatLng> path;
	private int timeSeconds;
	private int distanceMeters;
	private String description;
	private String shortDescription;
	private String movementDescription;
	private String current;
	private String target;
	private Movement movement;
	
	public Direction(List<LatLng> path, int timeSeconds, int distanceMeters, String description, String current, String target, Direction nextDirection) {
		this(path, timeSeconds, distanceMeters, description, current, target, calculateMovement(path, nextDirection.getPath()));
	}
	
	public Direction(List<LatLng> path, int timeSeconds, int distanceMeters, String description, String current, String target, Movement movement) {
		this.path = path;
		this.timeSeconds = timeSeconds;
		this.distanceMeters = distanceMeters;
		this.description = description;
		this.current = current;
		this.target = target;
		this.movement = movement;
		createMovementDescription();
		createShortDescription();
	}
	
	private static Movement calculateMovement(List<LatLng> currentPath, List<LatLng> nextPath) {
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
	
	private static double calculateAngleAtMovement(List<LatLng> currentPath, List<LatLng> nextPath) {
		double currentPathFinalHeading = calculateHeadingAtPathEnd(currentPath);
		double nextPathInitialHeading = calculateHeadingAtPathStart(currentPath);
		return nextPathInitialHeading - currentPathFinalHeading;
	}
	
	private static double calculateHeadingAtPathEnd(List<LatLng> path) {
		return calculateHeadingOfPath(getRelevantPoints(path, false));
	}
	
	private static double calculateHeadingAtPathStart(List<LatLng> path) {
		return calculateHeadingOfPath(getRelevantPoints(path, true));
	}
	
	private static List<LatLng> getRelevantPoints(List<LatLng> path, boolean fromStart) {
		ArrayList<LatLng> points = new ArrayList<LatLng>();
		int pathSize = path.size();
		
		if (pathSize > 0) {
			int i = fromStart ? 0 : path.size();
			int iterationDirection = fromStart ? 1 : -1;
			int travelledDistance = 0;
			LatLng currentLocation = path.get(i);
			while (i >= 0 && i < path.size() && travelledDistance < MIN_DIST_FOR_HEADING_METERS) {
				LatLng nextLocation = path.get(i);
				travelledDistance += LatLngUtil.distanceInMeters(currentLocation, nextLocation);
				currentLocation = nextLocation;
				i += iterationDirection;
			}
		}
		
		return points;
	}
	
	private static double calculateHeadingOfPath(List<LatLng> path) {
		return LatLngUtil.initialBearing(path.get(0), path.get(path.size() - 1));
	}
	
	public List<LatLng> getPath() {
		return path;
	}
	
	public int getTimeInSeconds() {
		return timeSeconds;
	}
	
	public int getDistanceInMeters() {
		return distanceMeters;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getShortDescription() {
		return shortDescription;
	}
	
	public String getMovementDescription() {
		return movementDescription;
	}

	public String getTarget() {
		return target;
	}
	
	public String getCurrent() {
		return current;
	}
	
	public Movement getMovement() {
		return movement;
	}
	
	private void createMovementDescription() {
		switch (movement) {
			case TURN_RIGHT:
				movementDescription = "turn right";
				break;
			case TURN_LEFT:
				movementDescription = "turn left";
				break;
			case CONTINUE:
				movementDescription = description;
				break;
			default:
				movementDescription = "";
				break;
		}
	}
	
	private void createShortDescription() {
		if (!StringUtil.isNullOrEmpty(target) && !StringUtil.isNullOrEmpty(movementDescription)) {
			shortDescription = movementDescription + " onto " + target;
		} else {
			shortDescription = "UNABLE TO GENERATE SHORT DESCRIPTION";
		}
	}
}
