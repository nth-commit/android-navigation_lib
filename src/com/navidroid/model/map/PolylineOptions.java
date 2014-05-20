package com.navidroid.model.map;

import java.util.List;

import com.navidroid.model.LatLng;

public class PolylineOptions {
	
	private List<LatLng> path;
	private int color;
	
	public PolylineOptions path(List<LatLng> path) {
		this.path = path;
		return this;
	}
	
	public List<LatLng> path() {
		return path;
	}
	
	public PolylineOptions color(int color) {
		this.color = color;
		return this;
	}
	
	public int color() {
		return color;
	}
}
