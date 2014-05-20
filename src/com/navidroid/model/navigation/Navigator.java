package com.navidroid.model.navigation;

import java.util.ArrayList;
import java.util.List;

import com.navidroid.model.LatLng;

/**
 * A lazy wrapper for the InternalNavigator class.
 * Stacks up callbacks and calls them, in-order,
 * so a user can do operations on a Navigator,
 * via NavigationFragment.getNavigator(), as soon
 * as the NavigationFragment is instantiated.
 */
public class Navigator {
	
	private interface WhenNavigatorReady {
		void invoke(InternalNavigator navigator);
	}
	
	private List<WhenNavigatorReady> callbacks;
	private InternalNavigator navigator;
	
	public Navigator() {
		callbacks = new ArrayList<WhenNavigatorReady>();
	}
	
	public void setInternalNavigator(InternalNavigator navigator) {
		this.navigator = navigator;
		for (int i = 0; i < callbacks.size(); i++) {
			callbacks.get(i).invoke(navigator);
		}
	}
	
	public void go(final LatLng location) {
		if (navigator == null) {
			callbacks.add(new WhenNavigatorReady() {
				@Override
				public void invoke(InternalNavigator navigator) {
					navigator.go(location);
				}
			});
		} else {
			navigator.go(location);
		}
	}
	
	public void stop() {
		if (navigator == null) {
			callbacks.add(new WhenNavigatorReady() {
				@Override
				public void invoke(InternalNavigator navigator) {
					navigator.stop();
				}
			});
		} else {
			navigator.stop();
		}
	}
	
	public boolean isNavigating() {
		return navigator == null ? false : navigator.isNavigating();
	}
	
	public LatLng getDestination() {
		return navigator == null ? null : navigator.getDestination();
	}
}
