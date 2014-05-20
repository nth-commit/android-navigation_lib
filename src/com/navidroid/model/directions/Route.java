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
		void onFailure(String message, LatLng origin, LatLng destination);
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
		} catch (Exception ex) {
			String error = "Failed to retrieve directions with exception: " + ex.getMessage();
			Log.e("DefinedError", error);
			directionsRetrieved.onFailure(error, origin, destination);
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String response) {
		try {
			Directions directions = directionsFactory.createDirections(origin, destination, response);
			directionsRetrieved.onSuccess(directions, origin, destination);
		} catch (Exception e) {
			Log.e("DefinedError", e.getMessage());
			directionsRetrieved.onFailure(e.getMessage(), origin, destination);
		}
	}
}
