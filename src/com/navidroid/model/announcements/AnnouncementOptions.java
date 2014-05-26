package com.navidroid.model.announcements;

public class AnnouncementOptions {
	
	private int[] times = new int[] { 30, 60, 300 };
	
	public AnnouncementOptions times(int... times) {
		this.times = times;
		return this;
	}
	
	public int[] times() {
		return times;
	}
}
