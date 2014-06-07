package com.navidroid.model.events;

import com.navidroid.model.LatLng;
import com.navidroid.model.directions.Directions;

public interface OnNewPathFoundListener {
	public void invoke(Directions directions);
}
