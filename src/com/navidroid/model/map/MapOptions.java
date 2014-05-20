package com.navidroid.model.map;

import com.navidroid.Defaults;
import com.navidroid.model.LatLng;

public class MapOptions {
	
	private LatLng location = Defaults.LOCATION;
	private IMapFactory mapFactory;
	
	public LatLng location() {
		return location;
	}
	
	public MapOptions location(LatLng location) {
		this.location = location;
		return this;
	}
	
	public IMapFactory mapFactory() {
		return mapFactory;
	}
	
	public MapOptions mapFactory(IMapFactory mapFactory) {
		this.mapFactory = mapFactory;
		return this;
	}
}
