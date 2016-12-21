package ch.epfl.sweng.project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ch.epfl.sweng.project.R;


public class PublicUserImageFragment extends UserImageFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        ((LinearLayout)rootView.findViewById(R.id.userProfileButtonsLayout)).removeView(rootView.findViewById(R.id.userImageEdit));
        return rootView;
    }
}