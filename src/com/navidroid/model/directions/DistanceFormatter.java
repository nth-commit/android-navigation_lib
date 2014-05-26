package com.navidroid.model.directions;

public class DistanceFormatter {
	
	public enum UnitSystem {
		METRIC,
		IMPERIAL
	}
	
	public static String formatMeters(double meters, boolean fullUnits) {
		if (meters > 1000) {
			return String.format("%.1f", meters / 1000) + (fullUnits ? " kilometers" : "km");
		} else {
			int formattedMeters = formatNumber(meters);
			return formattedMeters + (fullUnits ? " meters" : "m");
		}
	}
	
	public static String formatMeters(double meters) {
		return formatMeters(meters, false);
	}
	
	private static int formatNumber(double value) {
		if (value <= 10) {
			return (int)value;
		} else {
			return ((int)(value / 10)) * 10;
		}
	}

}
