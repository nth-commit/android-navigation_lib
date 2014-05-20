package com.navidroid.model.positioning;

import com.navidroid.model.LatLng;

public interface IGps {
	
	public interface OnTickHandler {
		void invoke(Position position);
	}
	
	void enableTracking();
	
	void disableTracking();
	
	void forceTick();
	
	void setOnTickHandler(OnTickHandler handler);
	
	LatLng getLastLocation();
}
