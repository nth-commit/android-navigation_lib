package com.navidroid.model.directions;

import java.util.List;

import com.navidroid.model.LatLng;
import com.navidroid.model.util.StringUtil;

public class Direction {
	
	private List<LatLng> path;
	private int timeSeconds;
	private int distanceMeters;
	private String description;
	private String shortDescription;
	private String movementDescription;
	private String current;
	private String target;
	private Movement movement;
	
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
