package com.navidroid.model.directions;

import com.navidroid.R;

import android.content.Context;

public class ImageFactory {
	
	private static final String RESOURCE_TYPE = "drawable";
	private static final String PACKAGE_NAME = "com.droidnav";
	
	// TODO: Resolve images dynamically for color
	public static int getImageResource(Context context, Direction direction, String color) {
		switch (direction.getMovement()) {
			case DEPARTURE:
				return R.drawable.depart_87ceeb;
			case CONTINUE:
			case VEER_LEFT:
			case VEER_RIGHT:
				return R.drawable.continue_87ceeb;
			case TURN_RIGHT:
			case TURN_RIGHT_SHARP:
				return R.drawable.turn_right_87ceeb;
			case TURN_LEFT:
			case TURN_LEFT_SHARP:
				return R.drawable.turn_left_87ceeb;
			default:
				return 0;
		}
	}

}
