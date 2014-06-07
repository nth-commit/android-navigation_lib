package com.navidroid.model.map;

import com.navidroid.model.Defaults;
import com.navidroid.model.LatLng;

public class MapOptions {
	
	private LatLng location = Defaults.LOCATION;
	
	public LatLng location() {
		return location;
	}
	
	public MapOptions location(LatLng location) {
		this.location = location;
		return this;
	}
}
