package com.navidroid.model.directions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DirectionImageView extends ImageView {

	public DirectionImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		setMeasuredDimension(size, size);
	}
}
