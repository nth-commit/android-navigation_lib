package com.navidroid.model.events;

import com.navidroid.model.navigation.NavigationState;

public interface OnNavigationStartedListener {
	public void invoke(NavigationState state);
}
