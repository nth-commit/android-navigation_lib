package com.navidroid.model.positioning;

import android.os.AsyncTask;

import com.navidroid.model.LatLng;
import com.navidroid.model.util.AsyncTaskExecutor;

public class SimulatedGps extends AbstractSimulatedGps {
	
	public SimulatedGps(GpsOptions options, LatLng location) {
		super(options, location);
	}
	
	@Override
	public void doFollowPath() {
		AsyncTask<Void, Void, Void> tickLoopTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				currentPosition = new Position(currentPosition.location, 0, System.currentTimeMillis());
				publishProgress();
				whileHasCurrentPath(new WhileHasCurrentPathAction() {
					@Override
					public void invoke() {
						advancePosition(currentPath);
						publishProgress();
						try {
							Thread.sleep(updateIntervalMs);
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}	
					}
				});
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Void... progress) {
				onTickHandler.invoke(currentPosition);
			}
		};
		AsyncTaskExecutor.execute(tickLoopTask);
	}
}
