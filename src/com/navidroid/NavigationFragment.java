package com.navidroid;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.navidroid.R;
import com.navidroid.model.announcements.AnnouncementOptions;
import com.navidroid.model.announcements.Announcer;
import com.navidroid.model.directions.IDirectionsFactory;
import com.navidroid.model.map.IMap;
import com.navidroid.model.map.IMapFactory;
import com.navidroid.model.map.Map;
import com.navidroid.model.map.NavigationMap;
import com.navidroid.model.navigation.DefaultNavigatorStateListener;
import com.navidroid.model.navigation.INavigatorStateListener;
import com.navidroid.model.navigation.InternalNavigator;
import com.navidroid.model.navigation.NavigationOptions;
import com.navidroid.model.navigation.Navigator;
import com.navidroid.model.positioning.GpsFactory;
import com.navidroid.model.positioning.IGps;
import com.navidroid.model.positioning.GpsOptions.GpsType;
import com.navidroid.model.vehicle.IVehicleMarkerFactory;
import com.navidroid.model.vehicle.Vehicle;

import android.app.Activity;
import android.app.Fragment;
import android.content.IntentSender.SendIntentException;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NavigationFragment extends Fragment implements
	ConnectionCallbacks,
	OnConnectionFailedListener  {
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	private static List<IDirectionsFactory> directionsFactorysById = new ArrayList<IDirectionsFactory>();
	private static List<IMapFactory> mapFactorysById = new ArrayList<IMapFactory>();
	private static List<IVehicleMarkerFactory> vehicleMarkerFactorysById = new ArrayList<IVehicleMarkerFactory>();
	private static List<NavigationOptions> optionsById = new ArrayList<NavigationOptions>();
	
	public static final NavigationFragment newInstance(
			IDirectionsFactory directionsFactory,
			IMapFactory mapFactory,
			IVehicleMarkerFactory vehicleMarkerFactory) {
		return newInstance(directionsFactory, mapFactory, vehicleMarkerFactory, null);
	}
	
	public static final NavigationFragment newInstance(
			IDirectionsFactory directionsFactory,
			IMapFactory mapFactory,
			IVehicleMarkerFactory vehicleMarkerFactory,
			NavigationOptions options) {
		
		NavigationFragment fragment = new NavigationFragment();
		int id = directionsFactorysById.size();
		
		directionsFactorysById.add(id, directionsFactory);
		mapFactorysById.add(id, mapFactory);
		vehicleMarkerFactorysById.add(id, vehicleMarkerFactory);
		optionsById.add(id, options);
		
		Bundle args = new Bundle();
		args.putInt("index", id);
		fragment.setArguments(args);
		return fragment;
	}
	
	private Navigator navigator = new Navigator();
	private Map map = new Map();
	
	private IDirectionsFactory directionsFactory;
	private IMapFactory mapFactory;
	private IVehicleMarkerFactory vehicleMarkerFactory;
	private NavigationOptions options;
	
	private InternalNavigator internalNavigator;
	private Activity parent;
	private LocationClient locationClient;
	private NavigationMap navigationMap;
	private Vehicle vehicle;
	private Announcer announcer;
	private IGps gps;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int id = getArguments().getInt("index");
		directionsFactory = directionsFactorysById.get(id);
		mapFactory = mapFactorysById.get(id);
		vehicleMarkerFactory = vehicleMarkerFactorysById.get(id);
		options = optionsById.get(id);
		
		parent = getActivity();
		parent.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.navigation_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		map.setInnerObject(mapFactory.createMap(this));
		navigationMap = new NavigationMap(map, options.mapOptions());
		announcer = new Announcer(this, options.announcementOptions());
		
		if (options.gpsOptions().gpsType() == GpsType.REAL) {
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
	
	private void createNavigator() {
		vehicle = new Vehicle(this, vehicleMarkerFactory, navigationMap, options.vehicleOptions().location(gps.getLastLocation()));
		internalNavigator = new InternalNavigator(this, gps, navigationMap, vehicle, directionsFactory, announcer);
		INavigatorStateListener stateListener = options.navigationStateListenerFactory().createNavigatorStateListener(this);
		internalNavigator.setNavigatorStateListener(stateListener);
		navigator.setInnerObject(internalNavigator);
	}
	
	public Navigator getNavigator() {
		return navigator;
	}
	
	public Map getMap() {
		return map;
	}
}

