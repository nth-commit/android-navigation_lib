package com.navidroid.model.navigation;

import com.navidroid.model.LatLng;
import com.navidroid.model.WhenReadyWrapper;

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
}
