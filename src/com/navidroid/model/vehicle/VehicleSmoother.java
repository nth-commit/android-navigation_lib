package com.navidroid.model.vehicle;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.PSource;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.drive.internal.GetMetadataRequest;
import com.navidroid.model.LatLng;
import com.navidroid.model.directions.Point;
import com.navidroid.model.navigation.NavigationState;
import com.navidroid.model.positioning.Position;
import com.navidroid.model.util.AsyncTaskExecutor;
import com.navidroid.model.util.LatLngUtil;
import com.navidroid.model.util.ListUtil;
import com.navidroid.model.util.MathUtil;
import com.navidroid.model.util.ListUtil.Predicate;

public class VehicleSmoother {
	
	private static final int TARGET_FRAMES_PER_S = 20;
	private static final int MS_PER_FRAME = 1000 / TARGET_FRAMES_PER_S;
	private static final int GPS_DELAY_MS = 500;
	
	private Vehicle vehicle;
	private ArrayList<NavigationState> stateHistory;
	private LatLng location;
	private double bearing;
	
	private Object navigationStateHistoryLock = new Object();
	
	public VehicleSmoother(Vehicle vehicle) {
		this.vehicle = vehicle;
		stateHistory = new ArrayList<NavigationState>();
		startUpdateTask();
	}
	
	public void update(NavigationState navigationState) {
		synchronized (navigationStateHistoryLock) {
			stateHistory.add(navigationState);
		}
	}
	
	private void startUpdateTask() {
		AsyncTask<Void, Void, Void> vehicleUpdateTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				while (true) {
					long timeDelayed;
					NavigationState[] navigationStates = null;
					
					synchronized (navigationStateHistoryLock) {
						timeDelayed = System.currentTimeMillis() - GPS_DELAY_MS;
						navigationStates = getNavigationStatesAroundTime(timeDelayed);					
					}
					
					calculatePosition(timeDelayed, navigationStates[0], navigationStates[1]);
					publishProgress();
					
					try {
						Thread.sleep(MS_PER_FRAME); // TODO: adjust sleep time based on real processing time.
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			protected void onProgressUpdate(Void... values) {
				vehicle.setVehiclePosition(location, bearing);
			}
			
		};
		AsyncTaskExecutor.execute(vehicleUpdateTask);
	}
	
	private NavigationState[] getNavigationStatesAroundTime(final long time) {
		NavigationState[] currentStates = new NavigationState[2];
		synchronized (navigationStateHistoryLock) {
			int startIndex = ListUtil.lastIndexOf(stateHistory, new Predicate<NavigationState>() {
				@Override
				public boolean check(NavigationState item, int index) {
					return item.getTime() <= time;
				}
			});
			
			if (stateHistory.size() > startIndex && startIndex > -1) {
				currentStates[0] = stateHistory.get(startIndex);
				currentStates[1] = ListUtil.find(stateHistory, new Predicate<NavigationState>() {
					@Override
					public boolean check(NavigationState item, int index) {
						return item.getTime() > time;
					}
				});
			}
			
			for (int i = 0; i < startIndex && i < stateHistory.size(); i++) {
				stateHistory.remove(i);
			}
			stateHistory.trimToSize();
		}
		return currentStates;
	}
	
	private void calculatePosition(long time, NavigationState a, NavigationState b) {
		if (a == null) {
			return;
		}
		
		if (b == null) {
			location = a.getLocation();
			bearing = a.getBearing();
			return;
		}
		
		if (a.isNavigating() && b.isNavigating() && b.isOnPath()) {
			calculatePositionOnPath(time, a, b);
		} else {
			calculatePositionOffPath(time, a, b);
		}
	}
	
	private void calculatePositionOnPath(long time, NavigationState a, NavigationState b) {
		ArrayList<LatLng> path = getPathBetweenNavigationStates(a, b);
		double distanceBetweenStates = LatLngUtil.distanceInMeters(path);
		double deltaTime = b.getTime() - a.getTime();
		double timeFromA = time - a.getTime();
		double interpolationFactor = MathUtil.clamp(timeFromA / deltaTime, 0, 1);
		double distanceRemaining = distanceBetweenStates * interpolationFactor;
		LatLng currentLocation = path.remove(0);
		double currentBearing = 0;
		
		while (path.size() > 0 && distanceRemaining > 0) { // TODO: Extract to LatLngUtil.travel(List<LatLng> path, double distance) with SimulatedGps logic
			LatLng nextLocationInPath = path.get(0);
			double distanceToNextPoint = LatLngUtil.distanceInMeters(currentLocation, nextLocationInPath);
			double distanceToTravel = Math.min(distanceToNextPoint, distanceRemaining);
			currentBearing = LatLngUtil.initialBearing(currentLocation, nextLocationInPath);
			currentLocation = LatLngUtil.travel(currentLocation, currentBearing, distanceToTravel);
			
			distanceRemaining -= distanceToTravel;
			if (distanceRemaining > 0) {
				path.remove(0);
			}
		}
		
		location = currentLocation;
		bearing = currentBearing;
	}

	private ArrayList<LatLng> getPathBetweenNavigationStates(NavigationState a, NavigationState b) {
		ArrayList<LatLng> locations = new ArrayList<LatLng>();
		locations.add(a.getLocationOnPath());
		
		if (b != null) {
			Point currentNextPoint = a.getCurrentPoint().nextPoint;
			Point lastNextPoint = b.getCurrentPoint().nextPoint;
			while (currentNextPoint != lastNextPoint) {
				locations.add(currentNextPoint.location);
				currentNextPoint = currentNextPoint.nextPoint;
			}
			locations.add(b.getLocationOnPath());
		}
		
		return locations;
	}
	
	private void calculatePositionOffPath(long time, NavigationState a, NavigationState b) {
		assert !b.isNavigating() || !b.isOnPath();
		bearing = a.getBearing();
		double deltaDist = LatLngUtil.distanceInMeters(a.getLocation(), b.getLocation());
		double deltaTime = b.getTime() - a.getTime();
		double timeFromA = time - a.getTime();
		double interpolationFactor = MathUtil.clamp(timeFromA / deltaTime, 0, 1);
		double distFromA = deltaDist * interpolationFactor;
		location = LatLngUtil.travel(a.getLocation(), bearing, distFromA);
	}
}
