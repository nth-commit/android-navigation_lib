package com.navidroid.model.navigation;

public class NavigationStateListenerOptions {
	
	public interface OnNavigationStateListenerCreation {
		public void invoke(INavigationStateListener navigatorStateListener);
	}
	
	private INavigationStateListenerFactory navigationStateListenerFactory = new DefaultNavigationStateListenerFactory();
	private OnNavigationStateListenerCreation onNavigationStateListenerCreation;
	
	public NavigationStateListenerOptions navigationStateListenerFactory(INavigationStateListenerFactory navigationStateListenerFactory) {
		this.navigationStateListenerFactory = navigationStateListenerFactory;
		return this;
	}
	
	public INavigationStateListenerFactory navigationStateListenerFactory() {
		return navigationStateListenerFactory;
	}
	
	public NavigationStateListenerOptions onNavigationStateListenerCreation(OnNavigationStateListenerCreation onNavigationStateListenerCreation) {
		this.onNavigationStateListenerCreation = onNavigationStateListenerCreation;
		return this;
	}
	
	public OnNavigationStateListenerCreation onNavigationStateListenerCreation() {
		return onNavigationStateListenerCreation;
	}

}
