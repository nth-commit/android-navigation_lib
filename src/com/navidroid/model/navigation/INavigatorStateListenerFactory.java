package com.navidroid.model.navigation;

import com.navidroid.NavigationFragment;

public interface INavigatorStateListenerFactory {
	
	INavigatorStateListener createNavigatorStateListener(NavigationFragment navigationFragment);
	
}
