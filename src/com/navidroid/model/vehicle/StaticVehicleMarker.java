package com.navidroid.model.vehicle;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.navidroid.NavigationFragment;
import com.navidroid.R;
import com.navidroid.model.PointD;
import com.navidroid.model.map.IMap;
import com.navidroid.model.map.NavigationMap;
import com.navidroid.model.map.IMap.OnUpdateHandler;
import com.navidroid.model.util.LayoutUtil;

public class StaticVehicleMarker {
	
	private Vehicle vehicle;
	private IMap map;
	private ImageView markerImageView;
	private Bitmap image;
	private boolean isVisible;
	private double currentTilt = -1.0f;
	
	public StaticVehicleMarker(NavigationFragment navigationFragment, Vehicle vehicle, NavigationMap navigationMap) {
		this.vehicle = vehicle;
		map = navigationMap.getMap();

		ViewGroup view = (ViewGroup)navigationFragment.getView();
		LinearLayout container = (LinearLayout)LayoutUtil.getChildViewById(view, R.id.static_vehicle_marker_container);
		markerImageView = (ImageView)LayoutUtil.getChildViewById(container, R.id.static_vehicle_marker);
		image = vehicle.getImage();
		isVisible = markerImageView.getVisibility() == View.VISIBLE;
		
		map.setOnUpdateHandler(new OnUpdateHandler() {
			@Override
			public void invoke() {
				setLayoutParams(map.getTilt());
			}
		});
		updateLayoutParams();
	}
	
	private void updateLayoutParams() {
		setLayoutParams(map.getTilt());
	}

	public void show() {
		if (!isVisible) {
			isVisible = true;
			updateLayoutParams();
			markerImageView.setVisibility(View.VISIBLE);
		}
	}

	public void hide() {
		if (isVisible) {
			isVisible = false;
			markerImageView.setVisibility(View.INVISIBLE);
		}
	}

	private void setLayoutParams(double tilt) {
		if (isVisible && tilt != currentTilt) {
			final Point mapSize = map.getSize();
			double viewAngle = 90 - tilt;
			double halfMapHeight = 0.5 * mapSize.y; 
			double cameraPosY = halfMapHeight * Math.cos(Math.toRadians(viewAngle)) + halfMapHeight;
			double cameraPosZ = halfMapHeight * Math.sin(Math.toRadians(viewAngle));
			
			PointD anchor = vehicle.getScreenAnchor();
			double markerCenterX = mapSize.x * anchor.x;
			double markerCenterY = mapSize.y * anchor.y;
			double markerCameraHorizontalDist = cameraPosY - markerCenterY;
			double tanQuotient = cameraPosZ / markerCameraHorizontalDist;
			double angleToMarker = 90 - Math.toDegrees(Math.atan(tanQuotient));
			
			int imageHeight = image.getHeight();
			int scaledImageHeight = (int)(imageHeight * Math.cos(Math.toRadians(angleToMarker)));
			
			double distanceToVehicle = Math.sqrt(Math.pow(markerCameraHorizontalDist, 2) + Math.pow(cameraPosZ, 2));
			int imageWidth = image.getWidth();
			double distanceRatio = halfMapHeight / distanceToVehicle;
			int percievedWidth = (int)(imageWidth * distanceRatio);  
			
			Bitmap flattenedImage = Bitmap.createScaledBitmap(image, percievedWidth, scaledImageHeight, true);
			markerImageView.setImageBitmap(flattenedImage);
			
			final int markerLeftMargin = (int)(markerCenterX - percievedWidth / 2);
			final int markerTopMargin = (int)(markerCenterY - scaledImageHeight / 2);
			LayoutParams layout = new LayoutParams(percievedWidth, scaledImageHeight) {{
				leftMargin = markerLeftMargin;
				topMargin = markerTopMargin;
			}};
			markerImageView.setLayoutParams(layout);
			currentTilt = tilt;
		}
	}
}
