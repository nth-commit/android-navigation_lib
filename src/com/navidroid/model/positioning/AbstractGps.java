package com.navidroid.model.positioning;

public abstract class AbstractGps implements IGps {
	
	protected OnTickHandler onTickHandler;
	protected int updateIntervalMs;
	
	public AbstractGps(GpsOptions options) {
		updateIntervalMs = options.updateIntervalMilliseconds();
	}
	
	@Override
	public void setOnTickHandler(OnTickHandler onTickHandler) {
		this.onTickHandler = onTickHandler;
	}
}
