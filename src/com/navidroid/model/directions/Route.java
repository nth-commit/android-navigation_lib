package com.navidroid.model.directions;

import java.net.URLEncoder;

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
		void onSuccess(Directions directions);
		void onFailure(Exception e, LatLng destination, String destinationAddress);
	}
	
	private LatLng origin;
	private LatLng destination;
	private String destinationAddress;
	private LatLng rerouteWaypoint;
	private DirectionsRetrieved directionsRetrieved;
	private IDirectionsFactory directionsFactory;
	
	public Route(LatLng origin, String destinationAddress, LatLng rerouteWaypoint, IDirectionsFactory directionsFactory) {
		this(origin, rerouteWaypoint, directionsFactory);
		this.destinationAddress = destinationAddress;
	}

	public Route(LatLng origin, LatLng destination, LatLng rerouteWaypoint, IDirectionsFactory directionsFactory) {
		this(origin, rerouteWaypoint, directionsFactory);
		this.destination = destination;
	}
	
	private Route(LatLng origin, LatLng rerouteWaypoint, IDirectionsFactory directionsFactory) {
		this.directionsFactory = directionsFactory;
		this.origin = origin;
		this.rerouteWaypoint = rerouteWaypoint;
	}
	
	public void getDirections(DirectionsRetrieved directionsRetrieved) {
		this.directionsRetrieved = directionsRetrieved;
		AsyncTaskExecutor.execute(this);
	}

	@Override
	protected String doInBackground(Void... arg0) {
		try {
			HttpClient http = new DefaultHttpClient();
			String url;
			if (destination == null) {
				url = directionsFactory.createRequestUrl(origin, destinationAddress, rerouteWaypoint);
			} else {
				assert destinationAddress == null;
				url = directionsFactory.createRequestUrl(origin, destination, rerouteWaypoint);
			}
			HttpResponse response = http.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (Exception e) {
			String error = "Failed to retrieve directions with exception: " + e.getMessage();
			Log.e("com.navidroid", error);
			directionsRetrieved.onFailure(e, destination, destinationAddress);
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String response) {
		try {
			Directions directions = directionsFactory.createDirections(origin, destination, response);
			directionsRetrieved.onSuccess(directions);
		} catch (Exception e) {
			Log.e("com.navidroid", "Error parsing response from directions service.");
			directionsRetrieved.onFailure(e, destination, destinationAddress);
		}
	}
}
