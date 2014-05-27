package com.navidroid.model;

import java.util.ArrayList;
import java.util.List;

public abstract class WhenReadyWrapper<T> {
	
	public interface WhenReady<T> {
		void invoke(T object);
	}
	
	public interface WhenReadyReturn<T, R> {
		R invoke(T object); 
	}
	
	private List<WhenReady<T>> whenReadyCallbacks = new ArrayList<WhenReady<T>>(); 
	private T innerObject;

	public void setInnerObject(T object) {
		innerObject = object;
		for (int i = 0; i < whenReadyCallbacks.size(); i++) {
			whenReadyCallbacks.get(i).invoke(object);
		}
	}
	
	protected void whenReady(WhenReady<T> whenReady) {
		if (innerObject == null) {
			whenReadyCallbacks.add(whenReady);
		} else {
			whenReady.invoke(innerObject);
		}
	}
	
	protected <R> R whenReadyReturn(WhenReadyReturn<T, R> whenReadyReturn, R notReadyReturnValue) {
		return innerObject == null ? notReadyReturnValue : whenReadyReturn.invoke(innerObject);		
	}
}
