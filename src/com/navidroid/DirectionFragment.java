package com.navidroid;

import java.util.ArrayList;
import java.util.List;

import com.navidroid.R;
import com.navidroid.R.id;
import com.navidroid.R.layout;
import com.navidroid.model.directions.Direction;
import com.navidroid.model.directions.DistanceFormatter;
import com.navidroid.model.directions.ImageFactory;
import com.navidroid.model.util.LayoutUtil;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.GridLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DirectionFragment extends Fragment {
	
	private final static int LAYOUT_PADDING = 10;
	
	private Direction direction;
	private GridLayout view;
	
	private ImageView directionImage;
	private TextView directionDistance;
	private View directionDivider;
	private LinearLayout directionDescriptionContainer;
	private DirectionDescriptionFragment directionDescription;

	private static List<Direction> directionsById = new ArrayList<Direction>();
	
	public static final DirectionFragment newInstance(Direction direction) {
		DirectionFragment fragment = new DirectionFragment();
		int id = directionsById.size();
		directionsById.add(id, direction);
		Bundle args = new Bundle();
		args.putInt("index", id);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int id = getArguments().getInt("index");
		direction = directionsById.get(id);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = (GridLayout)inflater.inflate(R.layout.direction_fragment, container, false);
		}
		view.setPadding(LAYOUT_PADDING, LAYOUT_PADDING, LAYOUT_PADDING, LAYOUT_PADDING);
		createChildReferences();
		arrangeChildViews(container);
		setDirectionImage();
		return view;
	}
	
	private void createChildReferences() {
		directionImage = (ImageView)LayoutUtil.getChildViewById(view, R.id.direction_image);
		directionDistance = (TextView)LayoutUtil.getChildViewById(view, R.id.distance_to_direction);
		directionDivider = LayoutUtil.getChildViewById(view, R.id.direction_divider);
		directionDescriptionContainer = (LinearLayout)LayoutUtil.getChildViewById(view, R.id.direction_description_fragment_placeholder);
		directionDescription = DirectionDescriptionFragment.newInstance(direction);
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.add(directionDescriptionContainer.getId(), directionDescription);
		ft.commit();
	}
	
	private void arrangeChildViews(ViewGroup container) {
		int paddingSize = 2 * LAYOUT_PADDING;
		int workingWidth = container.getMeasuredWidth() - paddingSize;
		int workingHeight = container.getMeasuredHeight() - paddingSize;
		
		LayoutParams imageLayoutParams = arrangeDirectionImage(workingWidth, workingHeight);
		arrangeDirectionDescription(workingWidth, imageLayoutParams);
	}
	
	private LayoutParams arrangeDirectionImage(int workingWidth, int workingHeight) {
		int remainingHeight = workingHeight; 
		int directionImageMargin = (int)(0.1 * workingHeight);
		remainingHeight -= directionImageMargin;
		int directionImageSize = (int)(0.75 * remainingHeight);
		remainingHeight -= directionImageSize;
		
		LayoutParams imageLayoutParams = (LayoutParams)directionImage.getLayoutParams();
		imageLayoutParams.width = directionImageSize;
		imageLayoutParams.height = directionImageSize;
		imageLayoutParams.setMargins(directionImageMargin, directionImageMargin, directionImageMargin, 0);
		directionImage.setLayoutParams(imageLayoutParams);
		return imageLayoutParams;
	}
	
	private void arrangeDirectionDescription(int workingWidth, LayoutParams imageLayoutParams) {
		int directionImageWidth = imageLayoutParams.width + imageLayoutParams.leftMargin + imageLayoutParams.rightMargin;
		LayoutParams dividerLayoutParams = (LayoutParams)directionDivider.getLayoutParams();
		int directionDescriptionWorkingWidth = workingWidth - directionImageWidth - dividerLayoutParams.width - LAYOUT_PADDING;
		
		int directionDescriptionHorizontalMargin = (int)(0.05 * directionDescriptionWorkingWidth);
		directionDescriptionWorkingWidth -= directionDescriptionHorizontalMargin;
		
		LayoutParams descriptionParams = (LayoutParams)directionDescriptionContainer.getLayoutParams();
		descriptionParams.width = directionDescriptionWorkingWidth;
		descriptionParams.setMargins(directionDescriptionHorizontalMargin, 0, directionDescriptionHorizontalMargin, 0);
		directionDescriptionContainer.setLayoutParams(descriptionParams);
	}
	
	public void setDirectionImage() {
		if (view != null) {
			directionImage = (ImageView)LayoutUtil.getChildViewById(view, R.id.direction_image);
			directionImage.setImageResource(ImageFactory.getImageResource(directionImage.getContext(), direction, "87ceeb"));
		}
	}
	
	public void setDirectionDistance(double distanceMeters) {
		if (view != null) {
			directionDistance = (TextView)LayoutUtil.getChildViewById(view, R.id.distance_to_direction);
			String distance = DistanceFormatter.formatMeters(distanceMeters);
			directionDistance.setText(distance);
		}
	}
}
