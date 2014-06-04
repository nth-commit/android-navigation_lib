package com.navidroid.model.navigation;

import com.navidroid.NavigationFragment;

public class DefaultNavigationStateListenerFactory implements INavigationStateListenerFactory {

	@Override
	public INavigationStateListener createNavigationStateListener(NavigationFragment navigationFragment) {
		return new DefaultNavigationStateListener(navigationFragment);
	}
}
