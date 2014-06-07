package com.navidroid.model.map;

import android.graphics.Point;

import com.navidroid.model.LatLng;
import com.navidroid.model.PointD;

public interface IMap {
	
	public interface OnTouchListener {
		void invoke();
	}
	
	public interface OnUpdateHandler {
		void invoke();
	}
	
	public interface OnInvalidationAnimationFinishedCallback {
		void invoke();
	}
	
	public void setLocation(LatLng location);
	
	public void setBearing(double bearing);
	
	public void setTilt(double tilt);
	
	public void setZoom(double zoom);
	
	public void setAnchor(PointD anchor);

	public void setOnTouchListener(OnTouchListener handler);
	
	public void setOnUpdateHandler(OnUpdateHandler handler);
	
	public LatLng getLocation();
	
	public double getBearing();
	
	public double getTilt();
	
	public double getZoom();
	
	public PointD getAnchor();
	
	public Point getSize();
	
	public void invalidate();
	
	public void invalidate(int animationTime);
	
	public void invalidate(int animationTime, OnInvalidationAnimationFinishedCallback callback);
	
	public void addPolyline(PolylineOptions options);
	
	public void removePolyline();

}
