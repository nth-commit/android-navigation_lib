package com.navidroid.model.directions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.navidroid.model.LatLng;
import com.navidroid.model.util.AsyncTaskExecutor;

public class Route extends AsyncTask<Void, Void, String> {
	
	public interface DirectionsRetrieved {
		void onSuccess(Directions directions, LatLng origin, LatLng destination);
		void onFailure(Exception e, LatLng origin, LatLng destination);
	}
	
	private LatLng origin;
	private LatLng destination;
	private DirectionsRetrieved directionsRetrieved;
	private IDirectionsFactory directionsFactory;

	public Route(LatLng origin, LatLng destination, IDirectionsFactory directionsFactory) {
		this.directionsFactory = directionsFactory;
		this.origin = origin;
		this.destination = destination;		
	}
	
	public void getDirections(DirectionsRetrieved directionsRetrieved) {
		this.directionsRetrieved = directionsRetrieved;
		AsyncTaskExecutor.execute(this);
	}

	@Override
	protected String doInBackground(Void... arg0) {
		try {
			HttpClient http = new DefaultHttpClient();
			String url = directionsFactory.createRequestUrl(origin, destination);
			HttpResponse response = http.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (Exception e) {
			String error = "Failed to retrieve directions with exception: " + e.getMessage();
			Log.e("com.navidroid", error);
			directionsRetrieved.onFailure(e, origin, destination);
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String response) {
		try {
			Directions directions = directionsFactory.createDirections(origin, destination, response);
			directionsRetrieved.onSuccess(directions, origin, destination);
		} catch (Exception e) {
			directionsRetrieved.onFailure(e, origin, destination);
		}
	}
}
