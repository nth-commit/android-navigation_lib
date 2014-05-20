package com.navidroid.model.navigation;

import java.io.InvalidObjectException;

import com.navidroid.NavigationFragment;
import com.navidroid.model.LatLng;
import com.navidroid.model.directions.Direction;
import com.navidroid.model.directions.Directions;
import com.navidroid.model.directions.IDirectionsFactory;
import com.navidroid.model.directions.Point;
import com.navidroid.model.directions.Route;
import com.navidroid.model.directions.Route.DirectionsRetrieved;
import com.navidroid.model.map.NavigationMap;
import com.navidroid.model.map.NavigationMap.MapMode;
import com.navidroid.model.positioning.AbstractSimulatedGps;
import com.navidroid.model.positioning.IGps;
import com.navidroid.model.positioning.Position;
import com.navidroid.model.positioning.IGps.OnTickHandler;
import com.navidroid.model.util.LatLngUtil;
import com.navidroid.model.vehicle.Vehicle;

import android.util.Log;

public class InternalNavigator {
	
	private final int MIN_ARRIVAL_DIST_METERS = 10;
	private final int OFF_PATH_TOLERANCE_METERS = 10;
	private final int OFF_PATH_TOLERANCE_BEARING = 45;
	private final int MAX_TIME_OFF_PATH_MS = 3000;
	
	private NavigationMap map;
	private Vehicle vehicle;
	private IGps gps;
	private IDirectionsFactory directionsFactory;
	private INavigatorStateListener navigatorStateListener;
	private Position position;
	private NavigationState navigationState;
	private NavigationState lastNavigationState;
	private LatLng destination;
	
	private final Object navigatingLock = new Object();
	
	public InternalNavigator(NavigationFragment navigationFragment, IGps gps, NavigationMap map, Vehicle vehicle, IDirectionsFactory directionsFactory) {
		this.gps = gps;
		this.map = map;
		this.vehicle = vehicle;
		this.directionsFactory = directionsFactory;
		listenToGps();
	}
	
	private void listenToGps() {
		gps.setOnTickHandler(new OnTickHandler() {
			@Override
			public void invoke(Position position) {
				onGpsTick(position);
			}
		});
		gps.enableTracking();
		gps.forceTick();
	}
	
	public void setNavigatorStateListener(INavigatorStateListener stateListener) {
		navigatorStateListener = stateListener;
	}

	public void go(final LatLng location) {
		boolean wasNavigating = false;
		synchronized (navigatingLock) {
			wasNavigating = isNavigating();
			if (wasNavigating) {
				stop();
				destination = location;
			}
		}
		
		final boolean finalWasNavigating = wasNavigating;
		Route request = new Route(position.location, location, directionsFactory);
		request.getDirections(new DirectionsRetrieved() {
			@Override
			public void onSuccess(Directions directions, LatLng origin, LatLng destination) {
				navigatorStateListener.OnNewPathFound(directions, origin, destination);
				if (finalWasNavigating) {
					redirectNavigation(directions, location);
				} else {
					beginNavigation(directions, location);
				}
			}
			
			@Override
			public void onFailure(String message, LatLng origin, LatLng destination) {
				navigatorStateListener.OnNewPathFoundFailed(message, origin, destination);
			}
		});
	}
	
	public void stop() {
		synchronized (navigatingLock) {
			destination = null;
			navigationState = null;
			lastNavigationState = null;
			map.setMapMode(MapMode.FREE);
			map.removePolylinePath();
		}
	}
	
	public boolean isNavigating() {
		synchronized (navigatingLock) {
			return navigationState != null;
		}
	}
	
	public LatLng getDestination() {
		synchronized (navigatingLock) {
			return destination;
		}
	}
	
	private void beginNavigation(Directions directions, LatLng location) {
		synchronized (navigatingLock) {
			redirectNavigation(directions, location);
			map.setMapMode(MapMode.FOLLOW);
			if (gps instanceof AbstractSimulatedGps) {
				((AbstractSimulatedGps)gps).followPath(directions.getLatLngPath());
			}
			navigatorStateListener.OnDeparture(navigationState);
		}
	}
	
	private void redirectNavigation(Directions directions, LatLng location) {
		synchronized (navigatingLock) {
			navigationState = new NavigationState(directions);
			destination = location;
			map.addPathPolyline(directions.getLatLngPath());
		}
	}
	
	private void onGpsTick(Position position) {
		synchronized (navigatingLock) {
			this.position = position;
			if (isNavigating()) {
				try {
					navigationState.update(position);
				} catch (InvalidObjectException e) {
					e.printStackTrace();
					Log.e("Fatal exception in Navigator", e.getMessage());
				}
				checkArrival();
				checkDirectionChanged();
				checkOffPath();
				tickNavigator();
			}
		}
		updateVehicleMarker();
	}
	
	private void checkArrival() {
		if (LatLngUtil.distanceInMeters(navigationState.getLocation(), destination) <= MIN_ARRIVAL_DIST_METERS) {	
			navigatorStateListener.OnArrival(navigationState);
			stop();
		}
	}
	
	private void checkDirectionChanged() {
		if (!isNavigating()) {
			return;
		}
		
		Point currentPoint = navigationState.getCurrentPoint();
		if (lastNavigationState == null) {
			navigatorStateListener.OnNewDirection(navigationState);
		} else {
			Point lastPoint = lastNavigationState.getCurrentPoint();
			if (currentPoint != lastPoint) {
				Direction currentDirection = currentPoint.nextDirection;
				if (currentDirection != lastPoint.nextDirection) {
					navigatorStateListener.OnNewDirection(navigationState);
				}
			}
		}
	}
	
	private void checkOffPath() {
		if (!isNavigating() || !navigationState.isOnPath()) {
			return;
		}
		
		if (navigationState.getDistanceOffPath() > OFF_PATH_TOLERANCE_METERS ||
				navigationState.getBearingDifferenceFromPath() > OFF_PATH_TOLERANCE_BEARING) {
			
			if (lastNavigationState != null && !lastNavigationState.isHeadingOffPath()) {
				navigationState.signalHeadingOffPath();
			} else if (navigationState.getTime() - navigationState.getHeadingOffPathStartTime() > MAX_TIME_OFF_PATH_MS) {
				navigationState.signalOffPath();
				navigatorStateListener.OnVehicleOffPath(navigationState);
			}
		} else {
			navigationState.signalOnPath();
		}
	}
	
	private void tickNavigator() {
		if (!isNavigating()) {
			return;
		}
		
		lastNavigationState = navigationState.snapshot();
		navigatorStateListener.OnNavigatorTick(navigationState);
	}
	
	private void updateVehicleMarker() {
		if (isNavigating()) {
			long timestamp = navigationState.getTime();
			vehicle.setPosition(navigationState.isOnPath() ?
					new Position(navigationState.getLocationOnPath(), navigationState.getBearingOnPath(), timestamp) :
					new Position(navigationState.getLocation(), navigationState.getBearing(), timestamp));
		} else {
			vehicle.setPosition(position);
		}
	}
}
