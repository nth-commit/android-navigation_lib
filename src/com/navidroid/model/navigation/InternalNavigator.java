package com.navidroid.model.navigation;

import android.util.Log;

import com.navidroid.NavigationFragment;
import com.navidroid.model.LatLng;
import com.navidroid.model.announcements.Announcer;
import com.navidroid.model.directions.Direction;
import com.navidroid.model.directions.Directions;
import com.navidroid.model.directions.IDirectionsFactory;
import com.navidroid.model.directions.Movement;
import com.navidroid.model.directions.Point;
import com.navidroid.model.directions.Route;
import com.navidroid.model.directions.Route.DirectionsRetrieved;
import com.navidroid.model.events.OnArrivalListener;
import com.navidroid.model.events.OnDepartureListener;
import com.navidroid.model.events.OnNavigationStartedListener;
import com.navidroid.model.events.OnNavigatorTickListener;
import com.navidroid.model.events.OnNewDirectionListener;
import com.navidroid.model.events.OnNewPathFoundFailedListener;
import com.navidroid.model.events.OnNewPathFoundListener;
import com.navidroid.model.events.OnVehicleOffPathListener;
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
	private final int GRACE_DISTANCE_TO_REROUTE_M = 50;
	
	private NavigationMap map;
	private VehicleSmoother vehicleSmoother;
	private IGps gps;
	private Announcer announcer;
	private IDirectionsFactory directionsFactory;
	private Position position;
	private MutableNavigationState navigationState;
	private NavigationState navigationStateSnapshot;
	private NavigationState lastNavigationStateSnapshot;
	private boolean hasGpsTicked;
	
	private OnNewPathFoundFailedListener onNewPathFoundFailedListener;
	private OnNewPathFoundListener onNewPathFoundListener;
	private OnNavigationStartedListener onNavigationStartedListener;
	private OnDepartureListener onDepartureListener;
	private OnArrivalListener onArrivalListener;
	private OnVehicleOffPathListener onVehicleOffPathListener;
	private OnNewDirectionListener onNewDirectionListener;	
	private OnNavigatorTickListener onNavigatorTickListener;
	
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
	
	public void go(final LatLng destination) {
		go(new GetRoute() {
			@Override
			public Route get(LatLng rerouteWaypoint) {
				return new Route(position.location, destination, rerouteWaypoint, directionsFactory);
			}
		});
	}
	
	public void go(final String destinationAddress) {
		go(new GetRoute() {
			@Override
			public Route get(LatLng rerouteWaypoint) {
				return new Route(position.location, destinationAddress, rerouteWaypoint, directionsFactory);
			}
		});
	}
	
	public void go(GetRoute getRoute) {
		boolean wasNavigating = false;
		LatLng rerouteWaypoint = null;
				
		synchronized (navigatingLock) {
			wasNavigating = isNavigating();
			if (wasNavigating) {
				rerouteWaypoint = getRerouteWaypoint();
				stop();
			};
		}
		
		final boolean finalWasNavigating = wasNavigating;
		Route route = getRoute.get(rerouteWaypoint);
		route.getDirections(new DirectionsRetrieved() {
			@Override
			public void onSuccess(Directions directions) {
				if (onNewPathFoundListener != null) {
					onNewPathFoundListener.invoke(directions);
				}
				
				LatLng location = directions.getDestination();
				if (finalWasNavigating) {
					redirectNavigation(directions, location);
				} else {
					beginNavigation(directions, location);
				}
			}
			
			@Override
			public void onFailure(Exception e, LatLng destination, String destinationAddress) {
				boolean shouldRetry = false;
				
				if (onNewPathFoundFailedListener != null) {
					shouldRetry = destination == null ?
						onNewPathFoundFailedListener.invoke(e, destinationAddress) :
						onNewPathFoundFailedListener.invoke(e, destination);
				}
				
				if (shouldRetry) {
					if (destination == null) {
						go(destinationAddress);
					} else {
						go(destination);
					}
				}
			}
		});
	}
	
	private interface GetRoute {
		Route get(LatLng rerouteWaypoint);
	}
	
	public void stop() {
		synchronized (navigatingLock) {
			navigationState.endNavigation();
			map.removePolylinePath();
		}
	}
	
	public void reroute() {
		synchronized (navigatingLock) {
			LatLng destination = navigationStateSnapshot.getDestination();
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
			return navigationStateSnapshot.getDestination();
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
	
	private LatLng getRerouteWaypoint() {
		Point waypoint = navigationStateSnapshot.getCurrentPoint();
		Point currentPoint = waypoint;
		int distanceAhead = 0;
		while (distanceAhead < GRACE_DISTANCE_TO_REROUTE_M &&
				(currentPoint = currentPoint.next) != null &&
				currentPoint.direction.getMovement() != Movement.ARRIVAL) {
			distanceAhead += currentPoint.prev.distanceToNextPointMeters;
			waypoint = currentPoint;
		}
		return waypoint.location;
	}
	
	private void beginNavigation(Directions directions, LatLng location) {
		synchronized (navigatingLock) {
			map.addPathPolyline(directions.getLatLngPath());
			map.followVehicle();
			navigationState.startNavigation(directions);
			updateNavigationStateSnapshot();
			announcer.startNavigation(directions);
			
			if (onNavigationStartedListener != null) {
				onNavigationStartedListener.invoke(navigationStateSnapshot);
			}
			
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
			map.addPathPolyline(directions.getLatLngPath());
		}
	}
	
	private void updateNavigationStateSnapshot() {
		synchronized (navigatingLock) {
			lastNavigationStateSnapshot = navigationStateSnapshot;
			navigationStateSnapshot = navigationState.getSnapshot();
		}
	}
	
	private void onGpsTick(Position position) {
		synchronized (navigatingLock) {
			this.position = position;
			navigationState.update(position);
			updateNavigationStateSnapshot();
			
			if (isNavigating()) {
				checkDeparted();
				checkArrival();
				checkDirectionAnnouncements();
				checkDirectionChanged();
				checkOffPath();
			}
			
			if (onNavigatorTickListener != null) {
				onNavigatorTickListener.invoke(navigationStateSnapshot);
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
					
					if (onDepartureListener != null) {
						onDepartureListener.invoke(navigationStateSnapshot);
					}
				}
				
				if (!navigationStateSnapshot.hasStartedFollowingDirections()) {
					navigationState.signalHasStartedFollowingDirections();
				}
			}
		}
	}
	
	private void checkArrival() {
		if (LatLngUtil.distanceInMeters(navigationStateSnapshot.getLocation(), navigationStateSnapshot.getDestination()) <= MIN_ARRIVAL_DIST_METERS) {	
			if (onArrivalListener != null) {
				onArrivalListener.invoke(navigationStateSnapshot);
			}
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
				navigationStateSnapshot.getCurrentDirection() != lastNavigationStateSnapshot.getCurrentDirection()) &&
				onNewDirectionListener != null) {
			onNewDirectionListener.invoke(navigationStateSnapshot);
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
					
					boolean shouldFindNewRoute = true;
					
					if (onVehicleOffPathListener != null) {
						shouldFindNewRoute = onVehicleOffPathListener.invoke(navigationStateSnapshot);
					}
					
					if (shouldFindNewRoute) {
						reroute();
					}
				}
				
			} else if (lastNavigationStateSnapshot != null && lastNavigationStateSnapshot.isHeadingOffPath()) { // We are back on path.
				navigationState.signalOnPath();
			}
		}
	}

	@Override
	public void setOnNewPathFoundFailedListener(OnNewPathFoundFailedListener listener) {
		onNewPathFoundFailedListener = listener;
	}

	@Override
	public void setOnNewPathFoundListener(OnNewPathFoundListener listener) {
		onNewPathFoundListener = listener;
	}

	@Override
	public void setOnNavigationStartedListener(OnNavigationStartedListener listener) {
		onNavigationStartedListener = listener;		
	}

	@Override
	public void setOnDepartureListener(OnDepartureListener listener) {
		onDepartureListener = listener;
	}

	@Override
	public void setOnArrivalListener(OnArrivalListener listener) {
		onArrivalListener = listener;
	}

	@Override
	public void setOnVehicleOffPathListener(OnVehicleOffPathListener listener) {
		onVehicleOffPathListener = listener;
	}

	@Override
	public void setOnNewDirectionListener(OnNewDirectionListener listener) {
		onNewDirectionListener = listener;
	}

	@Override
	public void setOnNavigatorTickListener(OnNavigatorTickListener listener) {
		onNavigatorTickListener = listener;
	}
}
