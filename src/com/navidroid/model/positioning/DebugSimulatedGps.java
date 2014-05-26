package com.navidroid.model.positioning;

import com.navidroid.model.LatLng;

public class DebugSimulatedGps extends AbstractSimulatedGps {
	
	public DebugSimulatedGps(GpsOptions options) {
		super(options);
	}

	@Override
	public void doFollowPath() {
		currentPosition = new Position(currentPosition.location, 0, System.currentTimeMillis());
		onTickHandler.invoke(currentPosition);
		whileHasCurrentPath(new WhileHasCurrentPathAction() {
			@Override
			public void invoke() {
				advancePosition(currentPath, currentPosition.timestamp + updateIntervalMs);
				onTickHandler.invoke(currentPosition);	
			}
		});
	}
}
