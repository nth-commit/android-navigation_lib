package com.navidroid.model.util;

public class MathUtil {
	
	public static double clamp(double value, double min, double max) {
		return Math.min(max, Math.max(min, value));
	}
}
