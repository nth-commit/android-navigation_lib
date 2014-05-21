package com.navidroid.model.navigation;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.AsyncTask;

import com.navidroid.DirectionFragment;
import com.navidroid.DirectionsOverlayFragment;
import com.navidroid.NavigationFragment;
import com.navidroid.R;
import com.navidroid.model.LatLng;
import com.navidroid.model.directions.Direction;
import com.navidroid.model.directions.Directions;
import com.navidroid.model.util.AsyncTaskExecutor;

public class DefaultNavigatorStateListener implements INavigatorStateListener {
	
	private static final int DIRECTIONS_REREQUEST_BACKOFF_MS = 5000;
	
	private DirectionsOverlayFragment directionsOverlayFragment;
	private DirectionFragment currentDirectionFragment;
	private Activity parentActivity;
	private Navigator navigator;
	
	public DefaultNavigatorStateListener(NavigationFragment fragment) {
		parentActivity = fragment.getActivity();
		navigator = fragment.getNavigator();
		directionsOverlayFragment = new DirectionsOverlayFragment();
	}
	
	@Override
	public void OnNewPathFoundFailed(String message, LatLng origin, LatLng destination) {
		AsyncTaskExecutor.execute(new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(DIRECTIONS_REREQUEST_BACKOFF_MS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				navigator.go(navigator.getDestination());
				return null;
			}
			
		});
	}
	
	@Override
	public void OnNewPathFound(Directions directions, LatLng origin, LatLng destination) {
	}

	@Override
	public void OnDeparture(NavigationState state) {
		addDirectionsOverlay();
	}

	@Override
	public void OnArrival(NavigationState state) {
		removeDirectionsOverlay();
	}

	@Override
	public void OnVehicleOffPath(NavigationState state) {
		navigator.go(navigator.getDestination());
	}
	
	@Override
	public void OnNewDirection(NavigationState state) {
		Direction direction = state.getCurrentPoint().direction;
		FragmentTransaction ft = parentActivity.getFragmentManager().beginTransaction();
		if (currentDirectionFragment != null) {
			ft.remove(currentDirectionFragment);
		}
		currentDirectionFragment = DirectionFragment.newInstance(direction);
		ft.add(R.id.direction_fragment_container, currentDirectionFragment);
		ft.commit();
	}
	
	public void OnNavigatorTick(NavigationState state) {
		if (currentDirectionFragment != null) {
			currentDirectionFragment.setDirectionDistance(state.getDistanceToCurrentDirection());
		}
	}
	
	private void addDirectionsOverlay() {
		FragmentTransaction ft = parentActivity.getFragmentManager().beginTransaction();
		ft.add(R.id.directions_overlay_container, directionsOverlayFragment);
		ft.commit();
	}
	
	private void removeDirectionsOverlay() {
		FragmentTransaction ft = parentActivity.getFragmentManager().beginTransaction();
		ft.remove(directionsOverlayFragment);
		ft.commit();
	}

}
