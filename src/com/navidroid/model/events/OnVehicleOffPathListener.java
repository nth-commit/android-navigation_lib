package com.navidroid.model.events;

import com.navidroid.model.navigation.NavigationState;

public interface OnVehicleOffPathListener {
	public boolean invoke(NavigationState state);
}
