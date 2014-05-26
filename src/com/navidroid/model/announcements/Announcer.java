package com.navidroid.model.announcements;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import com.navidroid.NavigationFragment;
import com.navidroid.model.directions.Direction;
import com.navidroid.model.directions.Directions;
import com.navidroid.model.directions.DistanceFormatter;
import com.navidroid.model.directions.Movement;
import com.navidroid.model.navigation.NavigationState;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class Announcer {
	
	private final static int MAX_TIME_WINDOW_TO_ANNOUNCE_S = 3;
	private final static int NEXT_DIRECTION_CLOSE_TIME_S = 5;
	private final static int TIME_ANNOUNCMENTS_MIN_WAIT_TIME_MS = 10000;
	
	private Hashtable<Direction, AnnouncementGroup> announcementGroups;
	private int[] preAnnouncementTimes;
	private TextToSpeech tts;
	private long lastAnnouncementTime;
	
	public Announcer(NavigationFragment fragment, AnnouncementOptions options) {
		preAnnouncementTimes = options.times();
		tts = new TextToSpeech(fragment.getActivity(), new OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					int result = tts.setLanguage(Locale.US);
					if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
						onInitialisationFailed();
					}
				} else {
					onInitialisationFailed();
				}				
			}
		});
	}
	
	private void onInitialisationFailed() {
		Log.e("TTS", "TextToSpeech service failed to initialise.");
	}
	
	public void startNavigation(Directions directions) {
		setupAnnouncementGroups(directions);
		announce(directions.getDepartureDirection().getDescription());
	}
	
	private void setupAnnouncementGroups(Directions directions) {
		announcementGroups = new Hashtable<Direction, AnnouncementGroup>();
		List<Direction> directionsList = directions.getDirectionsList(); 
		for (int i = 0; i < directionsList.size(); i++) {
			announcementGroups.put(directionsList.get(i), new AnnouncementGroup(preAnnouncementTimes));
		}
	}
	
	public void announceDirectionAfterDeparture(Direction directionAfterDeparture) {
		announce(directionAfterDeparture.getDescription());
	}
	
	public void announceArrival() {
		announce("You have reached your destination");
	}
	
	public void announceDirectionChanged(Direction currentDirection, Direction nextDirection) {
		AnnouncementGroup announcementGroup = getAnnouncementGroup(currentDirection);
		if (!announcementGroup.hasAnnouncedOnDirection()) {
			announcementGroup.signalAnnouncedOnDirection();
			String announcement = currentDirection.getMovementDescription() + " then ";
			if (nextDirection.getMovement() == Movement.ARRIVAL) {
				announcement += getDestinationInString(nextDirection.getDistanceInMeters());
			} else {
				announcement += nextDirection.getDescription();
			}
			announce(announcement);
		}
	}
	
	public void checkAnnounceUpcomingDirection(NavigationState navigationState) {
		if (lastAnnouncementTime + TIME_ANNOUNCMENTS_MIN_WAIT_TIME_MS > System.currentTimeMillis()) {
			return;
		}
		
		double timeToDirection = navigationState.getTimeToCurrentDirection();
		int announcementTime = -1;
		for (int i = 0; i < preAnnouncementTimes.length; i++) {
			int candidateTime = preAnnouncementTimes[i];
			if (timeToDirection > candidateTime - MAX_TIME_WINDOW_TO_ANNOUNCE_S &&
					timeToDirection < candidateTime + MAX_TIME_WINDOW_TO_ANNOUNCE_S) {
				announcementTime = candidateTime;
				break;
			}
		}
		
		if (announcementTime != -1) {
			AnnouncementGroup announcementGroup = getAnnouncementGroup(navigationState);
			if (!announcementGroup.hasAnnouncedAtTimeBeforeDirection(announcementTime)) {
				announcementGroup.signalAnnouncedAtTimeBeforeDirection(announcementTime);
				announceUpcomingDirection(navigationState);
			}
		}
	}
	
	private AnnouncementGroup getAnnouncementGroup(Direction direction) {
		return announcementGroups.get(direction);
	}
	
	private AnnouncementGroup getAnnouncementGroup(NavigationState navigationState) {
		return getAnnouncementGroup(navigationState.getCurrentDirection());
	}

	private void announceUpcomingDirection(NavigationState navigationState) {
		Direction direction = navigationState.getCurrentDirection();
		String announcement;
		if (direction.getMovement() == Movement.ARRIVAL) {
			announcement = getDestinationInString((int)navigationState.getDistanceToCurrentDirection());
		} else {
			announcement = "In ";
			announcement += DistanceFormatter.formatMeters(navigationState.getDistanceToCurrentDirection(), true);
			announcement += " " + navigationState.getCurrentPoint().direction.getShortDescription();
			if (navigationState.getTimeToNextDirection() < NEXT_DIRECTION_CLOSE_TIME_S) {
				String movementString = getMovementString(navigationState.getNextDirection().getMovement());
				if (!movementString.equals("")) {
					announcement += " then " + movementString;
				}
			}
		}
		announce(announcement);
	}
	
	private void announce(String text) {
		Log.i("com.navidroid", "Announcer: " + text);
		lastAnnouncementTime = System.currentTimeMillis();
		tts.speak(text, TextToSpeech.QUEUE_ADD, null);
	}

	private String getMovementString(Movement movement) {
		switch (movement) {
			case TURN_RIGHT:
				return "turn right";
			case TURN_LEFT:
				return "turn left";
			default:
				return "";
		}
	}
	
	private String getDestinationInString(int meters) {
		return "your destination is in " + DistanceFormatter.formatMeters(meters, true);
	}
}
