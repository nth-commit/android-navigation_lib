package com.navidroid;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.navidroid.model.directions.Direction;

public class DirectionDescriptionFragment extends Fragment {
	
	private static List<Direction> directionsById = new ArrayList<Direction>();
	
	public static DirectionDescriptionFragment newInstance(Direction direction) {
		DirectionDescriptionFragment fragment = new DirectionDescriptionFragment();
		int id = directionsById.size();
		directionsById.add(direction);
		Bundle args = new Bundle();
		args.putInt("index", id);
		fragment.setArguments(args);
		return fragment;
	}
	
	private static final double PADDING_RATIO = 0.1;
	
	private LinearLayout view;
	private Direction direction;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int id = getArguments().getInt("index");
		direction = directionsById.get(id);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int workingWidth = container.getMeasuredWidth();
		int workingHeight = container.getMeasuredHeight();
		
		LinearLayout view = new LinearLayout(getActivity());
		view.setOrientation(LinearLayout.VERTICAL);
		LayoutParams viewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		viewLayoutParams.gravity = Gravity.CENTER;
		view.setLayoutParams(viewLayoutParams);
		
		int horizontalPadding = (int)(workingWidth * PADDING_RATIO);
		workingWidth -= horizontalPadding;
		int verticalPadding = (int)(workingHeight * PADDING_RATIO);
		workingHeight -= verticalPadding;
		
		String[] descriptionLines = getDescriptionLines();
		for (int i = 0; i < descriptionLines.length; i++) {
			FontFitTextView tv = new FontFitTextView(getActivity());
			tv.setText(descriptionLines[i]);
			tv.setTextColor(Color.parseColor("#FFFFFFFF"));
			tv.setTypeface(null, Typeface.BOLD);
			tv.setGravity(Gravity.CENTER);
			tv.setWidth(workingWidth);
			tv.setHeight(100);
			view.addView(tv);
		}

		return view;
	}
	
	private String[] getDescriptionLines() {
		switch (direction.getMovement()) {
		case DEPARTURE:
			return new String[] {
				"continue on " + direction.getCurrent(),
				"toward " + direction.getTarget()
			};
		case TURN_LEFT:
		case TURN_RIGHT:
		case CONTINUE:
			return new String[] { direction.getTarget() };
		case ARRIVAL:
			String[] splitAddress = direction.getTarget().split(",");
			int numberOfLines = Math.min(splitAddress.length, 3);
			String[] descriptionLines = new String[numberOfLines];
			for (int i = 0; i < numberOfLines; i++) {
				descriptionLines[i] = splitAddress[i].trim();
			}
			return descriptionLines;
		default:
			return new String[] { "UNKNOWN" };
		}
	}
}
