package com.navidroid.model.navigation;

import com.navidroid.NavigationFragment;

public class DefaultNavigatorStateListenerFactory implements INavigatorStateListenerFactory {

	@Override
	public INavigatorStateListener createNavigatorStateListener(NavigationFragment navigationFragment) {
		return new DefaultNavigatorStateListener(navigationFragment);
	}

}
