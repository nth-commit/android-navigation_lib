package com.navidroid.model.util;

import java.util.Random;

public class MathUtil {
	
	private static Random random = new Random();
	
	public static double clamp(double value, double min, double max) {
		return Math.min(max, Math.max(min, value));
	}
	
	public static int randomInt(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}
}
