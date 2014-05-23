package com.navidroid.model.navigation;

import android.util.Log;

import com.navidroid.NavigationFragment;
import com.navidroid.model.LatLng;
import com.navidroid.model.directions.Directions;
import com.navidroid.model.directions.IDirectionsFactory;
import com.navidroid.model.directions.Route;
import com.navidroid.model.directions.Route.DirectionsRetrieved;
import com.navidroid.model.map.NavigationMap;
import com.navidroid.model.positioning.AbstractSimulatedGps;
import com.navidroid.model.positioning.IGps;
import com.navidroid.model.positioning.Position;
import com.navidroid.model.positioning.IGps.OnTickHandler;
import com.navidroid.model.util.LatLngUtil;
import com.navidroid.model.vehicle.Vehicle;
import com.navidroid.model.vehicle.VehicleSmoother;

public class InternalNavigator implements INavigator {
	
	private final int MIN_ARRIVAL_DIST_METERS = 10;
	private final int OFF_PATH_TOLERANCE_METERS = 10;
	private final int OFF_PATH_TOLERANCE_BEARING = 45;
	private final int MAX_TIME_OFF_PATH_MS = 3000;
	
	private NavigationMap map;
	private Vehicle vehicle;
	private VehicleSmoother vehicleSmoother;
	private IGps gps;
	private IDirectionsFactory directionsFactory;
	private INavigatorStateListener navigatorStateListener;
	private Position position;
	private MutableNavigationState navigationState;
	private NavigationState navigationStateSnapshot;
	private NavigationState lastNavigationStateSnapshot;
	private LatLng destination;
	
	private final Object navigatingLock = new Object();
	
	public InternalNavigator(NavigationFragment navigationFragment, IGps gps, NavigationMap map, Vehicle vehicle, IDirectionsFactory directionsFactory) {
		this.gps = gps;
		this.map = map;
		this.vehicle = vehicle;
		this.directionsFactory = directionsFactory;
		
		vehicleSmoother = new VehicleSmoother(vehicle);
		navigationState = new MutableNavigationState();
		navigationStateSnapshot = navigationState.getSnapshot();
		
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
			public void onFailure(Exception e, LatLng origin, LatLng destination) {
				navigatorStateListener.OnNewPathFoundFailed(e, origin, destination);
			}
		});
	}
	
	public void stop() {
		synchronized (navigatingLock) {
			destination = null;
			navigationState.endNavigation();
			map.unfollowVehicle();
			map.removePolylinePath();
		}
	}
	
	public void reroute() {
		synchronized (navigatingLock) {
			if (destination != null) {
				go(destination);
			}
		}
	}
	
	public boolean isNavigating() {
		synchronized (navigatingLock) {
			return navigationState.isNavigating();
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
			navigatorStateListener.OnDeparture(navigationState);
			assert navigationState.isNavigating();
			if (gps instanceof AbstractSimulatedGps) {
				((AbstractSimulatedGps)gps).followPath(directions.getLatLngPath());
			}
		}
	}
	
	private void redirectNavigation(Directions directions, LatLng location) {
		synchronized (navigatingLock) {
			navigationState.startNavigation(directions);
			destination = location;
			map.addPathPolyline(directions.getLatLngPath());
			map.followVehicle();
		}
	}
	
	private void onGpsTick(Position position) {
		synchronized (navigatingLock) {
			this.position = position;
			navigationState.update(position);
			lastNavigationStateSnapshot = navigationStateSnapshot;
			navigationStateSnapshot = navigationState.getSnapshot();
			if (isNavigating()) {
				checkArrival();
				checkDirectionChanged();
				checkOffPath();
			}
			
			if (navigatorStateListener != null) {
				navigatorStateListener.OnNavigatorTick(navigationState);
			}
		}
		vehicleSmoother.update(navigationStateSnapshot);
	}
	
	private void checkArrival() {
		if (LatLngUtil.distanceInMeters(navigationState.getLocation(), destination) <= MIN_ARRIVAL_DIST_METERS) {	
			navigatorStateListener.OnArrival(navigationState);
			stop();
		}
	}
	
	private void checkDirectionChanged() {
		if (navigationStateSnapshot.isNavigating()) {
			if (!lastNavigationStateSnapshot.isNavigating()) {
				navigatorStateListener.OnNewDirection(navigationStateSnapshot);
			} else if (navigationStateSnapshot.getCurrentPoint().direction != lastNavigationStateSnapshot.getCurrentPoint().direction) {
				navigatorStateListener.OnNewDirection(navigationStateSnapshot);
			}
		}
	}
	
	private void checkOffPath() {
		if (navigationStateSnapshot.isNavigating()) {
			if (navigationStateSnapshot.getDistanceOffPath() > OFF_PATH_TOLERANCE_METERS ||
				navigationStateSnapshot.getBearingDifferenceFromPath() > OFF_PATH_TOLERANCE_BEARING) {
				
				if (!lastNavigationStateSnapshot.isNavigating() || !lastNavigationStateSnapshot.isHeadingOffPath()) {
					// Last time we checked, we werern't navigating or we weren't heading off path.
					navigationState.signalHeadingOffPath();
				} else if (navigationStateSnapshot.isOnPath() &&
					navigationStateSnapshot.getTime() - navigationStateSnapshot.getHeadingOffPathStartTime() > MAX_TIME_OFF_PATH_MS) {
					// We have been off path for the tolerance time and not yet signalled so.
					navigationState.signalOffPath();
					navigatorStateListener.OnVehicleOffPath(navigationState);
				}
				
			} else { // We are back on path.
				navigationState.signalOnPath();
			}
			
		}
	}
}
