package com.navidroid.model.directions;

import com.navidroid.model.LatLng;

public class Point {
	public LatLng location;
	public double distanceToNextPointMeters;
	public double timeToNextPointSeconds;
	public double distanceToCurrentDirectionMeters;
	public double timeToCurrentDirectionSeconds;
	public double distanceToNextDirectionMeters;
	public double timeToNextDirectionSeconds;
	public double distanceToArrivalMeters;
	public double timeToArrivalSeconds;
	public Direction direction;
	public Direction nextDirection;
	public Point nextPoint;
}
