package com.navidroid.model.events;

import com.navidroid.model.navigation.NavigationState;

public interface OnNewDirectionListener {
	public void invoke(NavigationState state);
}
