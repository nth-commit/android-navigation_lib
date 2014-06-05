package com.navidroid.model.events;

import com.navidroid.model.navigation.NavigationState;

public interface OnNavigatorTickListener {
	public void invoke(NavigationState state);
}
