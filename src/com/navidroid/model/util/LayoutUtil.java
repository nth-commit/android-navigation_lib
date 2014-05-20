package com.navidroid.model.util;

import android.view.View;
import android.view.ViewGroup;

public class LayoutUtil {
	
	public static View getChildViewById(ViewGroup view, int id) {
		for (int i = 0; i < view.getChildCount(); i++) {
			View currentChild = view.getChildAt(i);
			if (currentChild.getId() == id) {
				return currentChild;
			}
		}
		return null;
	}

}
