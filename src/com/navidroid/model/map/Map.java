package com.navidroid.model.map;

import android.graphics.Point;

import com.navidroid.model.LatLng;
import com.navidroid.model.PointD;
import com.navidroid.model.WhenReadyWrapper;

public class Map extends WhenReadyWrapper<IMap> implements IMap {
	
	private LatLng location;
	private double bearing;
	private double tilt;
	private double zoom;
	private PointD anchor;

	@Override
	public void setLocation(final LatLng location) {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.setLocation(location);
			}
		});
		this.location = location;
	}

	@Override
	public void setBearing(final double bearing) {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.setBearing(bearing);
			}
		});
		this.bearing = bearing;
	}

	@Override
	public void setTilt(final double tilt) {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.setTilt(tilt);
			}
		});
		this.tilt = tilt;
	}

	@Override
	public void setZoom(final double zoom) {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.setZoom(zoom);
			}
		});
		this.zoom = zoom;
	}

	@Override
	public void setAnchor(final PointD anchor) {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.setAnchor(anchor);
			}
		});
		this.anchor = anchor;
	}

	@Override
	public void setOnTouchEventHandler(final OnTouchEventHandler handler) {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.setOnTouchEventHandler(handler);
			}
		});
	}

	@Override
	public void setOnUpdateEventHandler(final OnUpdate handler) {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.setOnUpdateEventHandler(handler);
			}
		});
	}

	@Override
	public LatLng getLocation() {
		return whenReadyReturn(new WhenReadyReturn<IMap, LatLng>() {
			@Override
			public LatLng invoke(IMap object) {
				return object.getLocation();
			}
		}, location);
	}

	@Override
	public double getBearing() {
		return whenReadyReturn(new WhenReadyReturn<IMap, Double>() {
			@Override
			public Double invoke(IMap object) {
				return object.getBearing();
			}
		}, bearing);
	}

	@Override
	public double getTilt() {
		return whenReadyReturn(new WhenReadyReturn<IMap, Double>() {
			@Override
			public Double invoke(IMap object) {
				return object.getTilt();
			}
		}, tilt);
	}

	@Override
	public double getZoom() {
		return whenReadyReturn(new WhenReadyReturn<IMap, Double>() {
			@Override
			public Double invoke(IMap object) {
				return object.getZoom();
			}
		}, zoom);
	}

	@Override
	public PointD getAnchor() {
		return whenReadyReturn(new WhenReadyReturn<IMap, PointD>() {
			@Override
			public PointD invoke(IMap object) {
				return object.getAnchor();
			}
		}, anchor);
	}

	@Override
	public Point getSize() {
		return whenReadyReturn(new WhenReadyReturn<IMap, Point>() {
			@Override
			public Point invoke(IMap object) {
				return object.getSize();
			}
		}, new Point(0, 0));
	}

	@Override
	public void invalidate() {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.invalidate();
			}
		});
	}

	@Override
	public void invalidate(final int animationTime) {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.invalidate(animationTime);
			}
		});
	}

	@Override
	public void invalidate(final int animationTime, final OnInvalidationAnimationFinished invalidationAnimationFinished) {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.invalidate(animationTime, invalidationAnimationFinished);
			}
		});
	}

	@Override
	public void addPolyline(final PolylineOptions options) {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.addPolyline(options);
			}
		});
	}

	@Override
	public void removePolyline() {
		whenReady(new WhenReady<IMap>() {
			@Override
			public void invoke(IMap object) {
				object.removePolyline();
			}
		});
	}
}
