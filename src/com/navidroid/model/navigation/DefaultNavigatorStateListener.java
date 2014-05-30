package com.navidroid.model.navigation;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

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
	
	private NavigationFragment fragment;
	private DirectionsOverlayFragment directionsOverlayFragment;
	private DirectionFragment currentDirectionFragment;
	private Activity parentActivity;
	private Navigator navigator;
	private Handler handler = new Handler();
	
	public DefaultNavigatorStateListener(NavigationFragment fragment) {
		this.fragment = fragment;
		parentActivity = fragment.getActivity();
		navigator = fragment.getNavigator();
		directionsOverlayFragment = new DirectionsOverlayFragment();
	}
	
	@Override
	public void OnNewPathFoundFailed(Exception e, LatLng origin, final LatLng destination) {
		final Context context = fragment.getView().getContext();
		parentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, "Directions request failed", Toast.LENGTH_SHORT).show();
			}
		});
		
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				fragment.getNavigator().go(destination);
			}
		}, DIRECTIONS_REREQUEST_BACKOFF_MS);
	}
	
	@Override
	public void OnNewPathFound(Directions directions, LatLng origin, LatLng destination) {
	}
	
	@Override
	public void OnNavigationStarted(NavigationState state) {
		addDirectionsOverlay();
	}

	@Override
	public void OnDeparture(NavigationState state) {
	}

	@Override
	public void OnArrival(NavigationState state) {
		removeDirectionsOverlay();
	}

	@Override
	public void OnVehicleOffPath(NavigationState state) {
		navigator.reroute();
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
		ft.commit(); // TODO: Can throw exception - Activity has been destroyed
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
