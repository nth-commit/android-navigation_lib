package com.navidroid.model.map;

import java.util.List;

import com.navidroid.model.LatLng;
import com.navidroid.model.map.IMap.OnTouchEventHandler;

public class NavigationMap {
	
	public interface OnMapModeChangedListener {
		void invoke(MapMode mode);
	}
	
	public enum MapMode {
		FOLLOW,
		FREE
	}
	
	private static final int FOLLOW_VEHICLE_ANIMATION_TIME = 1000;
	private static final float NAVIGATING_TILT = 60;
	private static final float NAVIGATING_ZOOM = 19;

	private IMap map;
	private LatLng vehicleLocation;
	private double vehicleBearing;
	private MapMode mapMode;
	
	private OnMapModeChangedListener mapModeChangedListener;
	
	private boolean trackLocation = true;
	
	public NavigationMap(IMap map, MapOptions options) {
		vehicleLocation = options.location();
		this.map = map;
		
		map.setOnTouchEventHandler(new OnTouchEventHandler() {
			@Override
			public void invoke() {
				trackLocation = false;
				if (mapMode != MapMode.FREE) {
					setMapMode(MapMode.FREE);
				}
			}
		});
	}
	
	public IMap getMap() {
		return map;
	}
	
	public void setOnMapModeChangedListener(OnMapModeChangedListener listener){
		mapModeChangedListener = listener;
	}
	
	public void addPathPolyline(List<LatLng> path) {
		map.addPolyline(new PolylineOptions().path(path).color(0xff3073F0));
	}
	
	public void removePolylinePath() {
		map.removePolyline();
	}
	
	public MapMode getMapMode() {
		return mapMode;
	}
	
	public void setMapMode(MapMode mode) {
		mapMode = mode;
		boolean isFollowing = mode == MapMode.FOLLOW;
		if (isFollowing) {
			setCameraPositionNavigatingDefault();
		}
		
		if (mapModeChangedListener != null) {
			mapModeChangedListener.invoke(mode);
		}
	}
	
	private void setCameraPositionNavigatingDefault() {
		map.setZoom(NAVIGATING_ZOOM);
		map.setTilt(NAVIGATING_TILT);
		map.setBearing(vehicleBearing);
		map.invalidate(FOLLOW_VEHICLE_ANIMATION_TIME);
	}
	
	public void setVehiclePosition(LatLng location, double bearing) {
		vehicleLocation = location;
		vehicleBearing = bearing;
		
		if (trackLocation) {
			map.setLocation(location);
			map.setBearing(bearing);
			map.invalidate();
		}
	}
}
