package com.navidroid.model.positioning;

import com.google.android.gms.location.LocationClient;
import com.navidroid.Defaults;
import com.navidroid.model.positioning.GpsOptions.GpsType;

public class GpsFactory {
	
	public static IGps create(GpsOptions options) {
		return create(options, null);
	}
	
	public static IGps create(GpsOptions options, LocationClient locationClient) {
		if (options.gpsType() == GpsType.SIMULATED) {
			if (options.simulatedGpsOptions().debugMode()) {
				return new DebugSimulatedGps(options);
			} else {
				return new SimulatedGps(options);
			}
		} else {
			return new Gps(options, locationClient);
		}
	}
}
