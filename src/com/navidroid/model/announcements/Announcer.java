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
	
	private final static int MAX_TIME_WINDOW_TO_ANNOUNCE_S = 5;
	private final static int NEXT_DIRECTION_CLOSE_TIME_S = 5;
	
	private Hashtable<Direction, boolean[]> hasAnnounced;
	private int[] preAnnouncementTimes;
	private TextToSpeech tts;
	private boolean isReady = false;
	
	public Announcer(NavigationFragment fragment, AnnouncementOptions options) {
		preAnnouncementTimes = options.times();
		tts = new TextToSpeech(fragment.getActivity(), new OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					int result = tts.setLanguage(Locale.US);
					if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
						onInitialisationFailed();
					} else {
						isReady = true;
						tts.speak("Text to speech initialised!", TextToSpeech.QUEUE_FLUSH, null);
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
		hasAnnounced = new Hashtable<Direction, boolean[]>();
		List<Direction> directionsList = directions.getDirectionsList(); 
		for (int i = 0; i < directionsList.size(); i++) {
			boolean[] hasAnnouncedSingle = new boolean[preAnnouncementTimes.length];
			for (int j = 0; j < hasAnnouncedSingle.length; j++) {
				hasAnnouncedSingle[j] = false;
			}
			hasAnnounced.put(directionsList.get(i), hasAnnouncedSingle);
		}
	}
	
	public void announceDeparture() {
		
	}

	public void announceMovement(Direction direction) {
		tts.speak(getMovementString(direction.getMovement()), TextToSpeech.QUEUE_FLUSH, null);
	}
	
	public void announceDirection(Direction direction) {
		tts.speak(direction.getText(), TextToSpeech.QUEUE_FLUSH, null);
	}
	
	public void checkAnnounceUpcomingDirection(NavigationState navigationState) {
		double timeToDirection = navigationState.getTimeToCurrentDirection();
		int timeIndex = -1;
		for (int i = 0; i < preAnnouncementTimes.length; i++) {
			int candidateTime = preAnnouncementTimes[i];
			if (timeToDirection > candidateTime - MAX_TIME_WINDOW_TO_ANNOUNCE_S &&
					timeToDirection < candidateTime + MAX_TIME_WINDOW_TO_ANNOUNCE_S) {
				timeIndex = i;
				break;
			}
		}
		
		if (timeIndex != -1) {
			Direction currentDirection = navigationState.getCurrentPoint().direction;
			if (!hasAnnounced.get(currentDirection)[timeIndex]) {
				announceUpcomingDirection(navigationState);
				hasAnnounced.get(currentDirection)[timeIndex] = true;
			}
		}
	}
	
	private void announceUpcomingDirection(NavigationState navigationState) {
		String announcement = "In ";
		announcement += DistanceFormatter.formatMeters(navigationState.getDistanceToCurrentDirection(), true);
		announcement += " " + getMovementString(navigationState.getCurrentPoint().direction.getMovement());
		if (navigationState.getTimeToNextDirection() < NEXT_DIRECTION_CLOSE_TIME_S) {
			announcement += " then " + getMovementString(navigationState.getCurrentPoint().nextDirection.getMovement());
		}		
		tts.speak(announcement, TextToSpeech.QUEUE_FLUSH, null);
	}

	private String getMovementString(Movement movement) {
		return "Turn right";
	}
	
	
	
}
