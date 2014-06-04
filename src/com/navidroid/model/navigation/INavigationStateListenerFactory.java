package com.navidroid.model.navigation;

import com.navidroid.NavigationFragment;

public interface INavigationStateListenerFactory {
	
	INavigationStateListener createNavigationStateListener(NavigationFragment navigationFragment);
}
