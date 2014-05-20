package com.navidroid;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.navidroid.R;
import com.navidroid.model.map.IMap;
import com.navidroid.model.map.NavigationMap;
import com.navidroid.model.navigation.DefaultNavigatorStateListener;
import com.navidroid.model.navigation.INavigatorStateListener;
import com.navidroid.model.navigation.InternalNavigator;
import com.navidroid.model.navigation.NavigationOptions;
import com.navidroid.model.navigation.Navigator;
import com.navidroid.model.positioning.GpsFactory;
import com.navidroid.model.positioning.IGps;
import com.navidroid.model.positioning.GpsOptions.GpsType;
import com.navidroid.model.vehicle.Vehicle;

import android.app.Activity;
import android.app.Fragment;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NavigationFragment extends Fragment implements
	ConnectionCallbacks,
	OnConnectionFailedListener  {
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	private Navigator navigator = new Navigator();

	private NavigationOptions options;
	private InternalNavigator internalNavigator;
	private Activity parent;
	private LocationClient locationClient;
	private NavigationMap navigationMap;
	private Vehicle vehicle;
	private IGps gps;
	
	private static List<NavigationOptions> optionsById = new ArrayList<NavigationOptions>();
	
	public static final NavigationFragment newInstance(NavigationOptions options) {
		NavigationFragment fragment = new NavigationFragment();
		int id = optionsById.size();
		optionsById.add(id, options);
		Bundle args = new Bundle();
		args.putInt("index", id);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int id = getArguments().getInt("index");
		options = optionsById.get(id);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.navigation_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		IMap map = options.mapOptions().mapFactory().createMap(this);
		navigationMap = new NavigationMap(map, options.mapOptions());
		
		if (options.gpsOptions().gpsType() == GpsType.REAL) {
			parent = getActivity();
			locationClient = new LocationClient(parent, this, this);
			locationClient.connect();
		} else {
			gps = GpsFactory.create(options.gpsOptions());
			createNavigator();
		}
		
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(parent, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException ex) {
				ex.printStackTrace();
			}
		} else {
			// TODO: Handler errors with dialog
		}
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		gps = GpsFactory.create(options.gpsOptions(), locationClient);
		createNavigator();			
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}
	
	public Navigator getNavigator() {
		return navigator;
	}
	
	private void createNavigator() {
		vehicle = new Vehicle(this, navigationMap, options.vehicleOptions().location(gps.getLastLocation()));
		internalNavigator = new InternalNavigator(this, gps, navigationMap, vehicle, options.directionsFactory());
		INavigatorStateListener stateListener = new DefaultNavigatorStateListener(this);
		internalNavigator.setNavigatorStateListener(stateListener);
		navigator.setInternalNavigator(internalNavigator);
	}
}

