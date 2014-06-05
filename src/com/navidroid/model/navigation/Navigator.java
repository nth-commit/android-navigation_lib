package com.navidroid.model.navigation;

import com.navidroid.model.LatLng;
import com.navidroid.model.WhenReadyWrapper;
import com.navidroid.model.events.OnArrivalListener;
import com.navidroid.model.events.OnDepartureListener;
import com.navidroid.model.events.OnNavigationStartedListener;
import com.navidroid.model.events.OnNavigatorTickListener;
import com.navidroid.model.events.OnNewDirectionListener;
import com.navidroid.model.events.OnNewPathFoundFailedListener;
import com.navidroid.model.events.OnNewPathFoundListener;
import com.navidroid.model.events.OnVehicleOffPathListener;

public class Navigator extends WhenReadyWrapper<INavigator> implements INavigator {
	
	@Override
	public void go(final LatLng location) {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.go(location);
			}
		});
	}

	@Override
	public void stop() {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.stop();
			}
		});
	}

	@Override
	public void reroute() {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.reroute();
			}
		});
	}

	@Override
	public boolean isNavigating() {
		return whenReadyReturn(new WhenReadyReturn<INavigator, Boolean>() {
			@Override
			public Boolean invoke(INavigator object) {
				return object.isNavigating();
			}
		}, false);
	}

	@Override
	public LatLng getDestination() {
		return whenReadyReturn(new WhenReadyReturn<INavigator, LatLng>() {
			@Override
			public LatLng invoke(INavigator object) {
				return object.getDestination();
			}
		}, null);
	}

	@Override
	public LatLng getLocation() {
		return whenReadyReturn(new WhenReadyReturn<INavigator, LatLng>() {
			@Override
			public LatLng invoke(INavigator object) {
				return object.getLocation();
			}
		}, null);
	}

	@Override
	public boolean hasGpsTicked() {
		return whenReadyReturn(new WhenReadyReturn<INavigator, Boolean>() {
			@Override
			public Boolean invoke(INavigator object) {
				return object.hasGpsTicked();
			}
		}, false);
	}

	@Override
	public void setOnNewPathFoundFailedListener(final OnNewPathFoundFailedListener listener) {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.setOnNewPathFoundFailedListener(listener);
			}
		});
	}

	@Override
	public void setOnNewPathFoundListener(final OnNewPathFoundListener listener) {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.setOnNewPathFoundListener(listener);
			}
		});
	}

	@Override
	public void setOnNavigationStartedListener(final OnNavigationStartedListener listener) {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.setOnNavigationStartedListener(listener);
			}
		});
	}

	@Override
	public void setOnDepartureListener(final OnDepartureListener listener) {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.setOnDepartureListener(listener);
			}
		});
	}

	@Override
	public void setOnArrivalListener(final OnArrivalListener listener) {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.setOnArrivalListener(listener);
			}
		});
	}

	@Override
	public void setOnVehicleOffPathListener(final OnVehicleOffPathListener listener) {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.setOnVehicleOffPathListener(listener);
			}
		});
	}

	@Override
	public void setOnNewDirectionListener(final OnNewDirectionListener listener) {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.setOnNewDirectionListener(listener);
			}
		});
	}

	@Override
	public void setOnNavigatorTickListener(final OnNavigatorTickListener listener) {
		whenReady(new WhenReady<INavigator>() {
			@Override
			public void invoke(INavigator object) {
				object.setOnNavigatorTickListener(listener);
			}
		});
	}
}
