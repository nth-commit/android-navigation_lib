package com.navidroid.model.navigation;

import android.util.Log;

import com.navidroid.NavigationFragment;
import com.navidroid.model.LatLng;
import com.navidroid.model.announcements.Announcer;
import com.navidroid.model.directions.Direction;
import com.navidroid.model.directions.Directions;
import com.navidroid.model.directions.IDirectionsFactory;
import com.navidroid.model.directions.Movement;
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
	
	private final int DIRECTION_ANNOUNCEMENT_TIME = 3;
	private final int MIN_ARRIVAL_DIST_METERS = 10;
	private final int OFF_PATH_TOLERANCE_METERS = 10;
	private final int OFF_PATH_TOLERANCE_BEARING = 45;
	private final int MAX_TIME_OFF_PATH_MS = 3000;
	
	private NavigationMap map;
	private VehicleSmoother vehicleSmoother;
	private IGps gps;
	private Announcer announcer;
	private IDirectionsFactory directionsFactory;
	private INavigationStateListener navigationStateListener;
	private Position position;
	private MutableNavigationState navigationState;
	private NavigationState navigationStateSnapshot;
	private NavigationState lastNavigationStateSnapshot;
	private LatLng destination;
	private boolean hasGpsTicked;
	
	private final Object navigatingLock = new Object();
	
	public InternalNavigator(NavigationFragment navigationFragment, IGps gps, NavigationMap map, Vehicle vehicle, IDirectionsFactory directionsFactory, Announcer announcer) {
		this.gps = gps;
		this.map = map;
		this.directionsFactory = directionsFactory;
		this.announcer = announcer;
		
		hasGpsTicked = false;
		vehicleSmoother = new VehicleSmoother(vehicle);
		navigationState = new MutableNavigationState();
		navigationStateSnapshot = navigationState.getSnapshot();
		
		listenToGps();
	}
	
	private void listenToGps() {
		gps.setOnTickHandler(new OnTickHandler() {
			@Override
			public void invoke(Position position) {
				hasGpsTicked = true;
				onGpsTick(position);
			}
		});
		gps.enableTracking();
		gps.forceTick();
	}
	
	public void setNavigationStateListener(INavigationStateListener stateListener) {
		navigationStateListener = stateListener;
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
				navigationStateListener.OnNewPathFound(directions, origin, destination);
				if (finalWasNavigating) {
					redirectNavigation(directions, location);
				} else {
					beginNavigation(directions, location);
				}
			}
			
			@Override
			public void onFailure(Exception e, LatLng origin, LatLng destination) {
				navigationStateListener.OnNewPathFoundFailed(e, origin, destination);
			}
		});
	}
	
	public void stop() {
		synchronized (navigatingLock) {
			destination = null;
			navigationState.endNavigation();
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
	
	public LatLng getLocation() {
		synchronized (navigatingLock) {
			return navigationStateSnapshot.getLocation();
		}
	}
	
	public boolean hasGpsTicked() {
		return hasGpsTicked;
	}
	
	private void beginNavigation(Directions directions, LatLng location) {
		synchronized (navigatingLock) {
			destination = location;
			map.addPathPolyline(directions.getLatLngPath());
			map.followVehicle();
			navigationState.startNavigation(directions);
			announcer.startNavigation(directions);
			navigationStateListener.OnNavigationStarted(navigationState);
			assert navigationState.isNavigating();
			if (gps instanceof AbstractSimulatedGps) {
				((AbstractSimulatedGps)gps).followPath(directions.getLatLngPath());
			}
		}
	}
	
	private void redirectNavigation(Directions directions, LatLng location) {
		synchronized (navigatingLock) {
			navigationState.redirectNavigation(directions);
			announcer.startNavigation(directions);
			destination = location;
			map.addPathPolyline(directions.getLatLngPath());
		}
	}
	
	private void onGpsTick(Position position) {
		synchronized (navigatingLock) {
			this.position = position;
			navigationState.update(position);
			lastNavigationStateSnapshot = navigationStateSnapshot;
			navigationStateSnapshot = navigationState.getSnapshot();
			if (isNavigating()) {
				checkDeparted();
				checkArrival();
				checkDirectionAnnouncements();
				checkDirectionChanged();
				checkOffPath();
			}
			
			if (navigationStateListener != null) {
				navigationStateListener.OnNavigatorTick(navigationState);
			}
		}
		vehicleSmoother.update(navigationStateSnapshot);
	}
	
	private void checkDeparted() {
		if (navigationStateSnapshot.isNavigating()) {
			Direction currentDirection = navigationStateSnapshot.getCurrentDirection();
			if (currentDirection.getMovement() != Movement.DEPARTURE) {
				
				if (!navigationStateSnapshot.hasDeparted()) {
					announcer.announceDirectionAfterDeparture(currentDirection);
					navigationState.signalHasDeparted();
					navigationStateListener.OnDeparture(navigationStateSnapshot);
				}
				
				if (!navigationStateSnapshot.hasStartedFollowingDirections()) {
					navigationState.signalHasStartedFollowingDirections();
				}
			}
		}
	}
	
	private void checkArrival() {
		if (LatLngUtil.distanceInMeters(navigationState.getLocation(), destination) <= MIN_ARRIVAL_DIST_METERS) {	
			navigationStateListener.OnArrival(navigationState);
			announcer.announceArrival();
			stop();
		}
	}
	
	private void checkDirectionAnnouncements() {
		if (navigationStateSnapshot.isNavigating()) {
			if (navigationStateSnapshot.getTimeToCurrentDirection() < DIRECTION_ANNOUNCEMENT_TIME &&
					navigationStateSnapshot.getCurrentDirection().getMovement() != Movement.ARRIVAL) {
				announcer.announceDirectionChanged(navigationStateSnapshot.getCurrentDirection(), navigationState.getNextDirection());
			}
			announcer.checkAnnounceUpcomingDirection(navigationStateSnapshot);
		}
	}
	
	private void checkDirectionChanged() {
		if (navigationStateSnapshot.isNavigating() &&
				(lastNavigationStateSnapshot == null ||
				!lastNavigationStateSnapshot.isNavigating() ||
				navigationStateSnapshot.getCurrentDirection() != lastNavigationStateSnapshot.getCurrentDirection())) {
			navigationStateListener.OnNewDirection(navigationStateSnapshot);	
		}
	}
	
	private void checkOffPath() {
		if (navigationStateSnapshot.isNavigating() && navigationState.hasDeparted()) {
			if (navigationStateSnapshot.getDistanceOffPath() > OFF_PATH_TOLERANCE_METERS ||
				navigationStateSnapshot.getBearingDifferenceFromPath() > OFF_PATH_TOLERANCE_BEARING) {
				
				if (!lastNavigationStateSnapshot.isNavigating() || !lastNavigationStateSnapshot.isHeadingOffPath()) {
					// Last time we checked, we werern't navigating or we weren't heading off path.
					navigationState.signalHeadingOffPath();
				} else if (navigationStateSnapshot.isOnPath() &&
					navigationStateSnapshot.getTime() - navigationStateSnapshot.getHeadingOffPathStartTime() > MAX_TIME_OFF_PATH_MS) {
					// We have been off path for the tolerance time and not yet signalled so.
					navigationState.signalOffPath();
					navigationStateListener.OnVehicleOffPath(navigationState);
				}
				
			} else if (lastNavigationStateSnapshot != null && lastNavigationStateSnapshot.isHeadingOffPath()) { // We are back on path.
				navigationState.signalOnPath();
			}
		}
	}
}
