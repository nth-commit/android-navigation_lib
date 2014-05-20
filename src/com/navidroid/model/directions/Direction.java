package com.navidroid.model.directions;

import java.util.List;

import com.navidroid.model.LatLng;

public class Direction {
	
	private List<LatLng> path;
	private int timeSeconds;
	private int distanceMeters;
	private String text;
	private String current;
	private String target;
	private Movement movement;
	
	public Direction(List<LatLng> path, int timeSeconds, int distanceMeters, String text, String current, String target, Movement movement) {
		this.path = path;
		this.timeSeconds = timeSeconds;
		this.distanceMeters = distanceMeters;
		this.text = text;
		this.current = current;
		this.target = target;
		this.movement = movement;
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
	
	public String getText() {
		return text;
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
	
	@Override
	public String toString() {
		return text;
	}
}
