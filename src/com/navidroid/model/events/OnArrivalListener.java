package com.navidroid.model.events;

import com.navidroid.model.navigation.NavigationState;

public interface OnArrivalListener {
	public void invoke(NavigationState state);
}
