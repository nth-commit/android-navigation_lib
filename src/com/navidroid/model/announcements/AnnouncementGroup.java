package com.navidroid.model.announcements;

import com.navidroid.model.directions.Direction;

public class AnnouncementGroup {
	
	private int[] announcementTimes;
	private boolean[] hasAnnounced;
	
	public AnnouncementGroup(int[] announcementTimes) {
		this.announcementTimes = announcementTimes;
		hasAnnounced = new boolean[announcementTimes.length + 1];
		for (int i = 0; i < hasAnnounced.length; i++) {
			hasAnnounced[i] = false;
		}
	}
	
	public void signalAnnouncedOnDirection() {
		hasAnnounced[hasAnnounced.length - 1] = true;
	}
	
	public boolean hasAnnouncedOnDirection() {
		return hasAnnounced[hasAnnounced.length - 1];
	}
	
	public void signalAnnouncedAtTimeBeforeDirection(int time) {
		hasAnnounced[getIndexForTime(time)] = true;
	}
	
	public boolean hasAnnouncedAtTimeBeforeDirection(int time) {
		return hasAnnounced[getIndexForTime(time)];
	}
	
	private int getIndexForTime(int time) {
		for (int i = 0; i < announcementTimes.length; i++) {
			if (announcementTimes[i] == time) {
				return i + 1;
			}
		}
		return -1;
	}
}
