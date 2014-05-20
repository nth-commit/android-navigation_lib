package com.navidroid.model;

public class PointD {
	
	public double x;
	public double y;
	
	public PointD(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void rotate(PointD origin, float angleDeg) {
		double rotationRadians = Math.toRadians(angleDeg);
		this.x -= origin.x;
		this.y -= origin.y;
		double rotatedX = this.x * Math.cos(rotationRadians) - this.y * Math.sin(rotationRadians);
		double rotatedY = this.x * Math.sin(rotationRadians) + this.y * Math.cos(rotationRadians);
		this.x = rotatedX;
		this.y = rotatedY;
		this.x += origin.x;
		this.y += origin.y;
	}
}