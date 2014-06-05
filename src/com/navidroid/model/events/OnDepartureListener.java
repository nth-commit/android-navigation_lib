package com.navidroid.model.events;

import com.navidroid.model.navigation.NavigationState;

public interface OnDepartureListener {
	public void invoke(NavigationState state);
}
