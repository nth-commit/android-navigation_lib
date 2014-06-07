package com.navidroid.model.navigation;

import com.navidroid.model.LatLng;
import com.navidroid.model.events.OnArrivalListener;
import com.navidroid.model.events.OnDepartureListener;
import com.navidroid.model.events.OnNavigationStartedListener;
import com.navidroid.model.events.OnNavigatorTickListener;
import com.navidroid.model.events.OnNewDirectionListener;
import com.navidroid.model.events.OnNewPathFoundFailedListener;
import com.navidroid.model.events.OnNewPathFoundListener;
import com.navidroid.model.events.OnVehicleOffPathListener;

public interface INavigator {
	
	public void go(LatLng location);
	
	public void go(String address);
	
	public void stop();
	
	public void reroute();
	
	public boolean isNavigating();
	
	public LatLng getDestination();
	
	public LatLng getLocation();
	
	public boolean hasGpsTicked();

	public void setOnNewPathFoundFailedListener(OnNewPathFoundFailedListener listener);
	
	public void setOnNewPathFoundListener(OnNewPathFoundListener listener);
	
	public void setOnNavigationStartedListener(OnNavigationStartedListener listener);
	
	public void setOnDepartureListener(OnDepartureListener listener);
	
	public void setOnArrivalListener(OnArrivalListener listener);
	
	public void setOnVehicleOffPathListener(OnVehicleOffPathListener listener);
	
	public void setOnNewDirectionListener(OnNewDirectionListener listener);
	
	public void setOnNavigatorTickListener(OnNavigatorTickListener listener);	
}
