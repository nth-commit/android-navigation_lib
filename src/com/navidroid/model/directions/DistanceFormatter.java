package com.navidroid.model.directions;

public class DistanceFormatter {
	
	public enum UnitSystem {
		METRIC,
		IMPERIAL
	}
	
	public static String formatMeters(double meters) {
		if (meters > 1000) {
			return String.format("%.1f", meters / 1000) + "km";
		} else {
			int formattedMeters = formatNumber(meters);
			return formattedMeters + "m";
		}
	}
	
	private static int formatNumber(double value) {
		if (value <= 10) {
			return (int)value;
		} else {
			return ((int)(value / 10)) * 10;
		}
	}

}
