package ch.epfl.sweng.project.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.DataProvider;
import ch.epfl.sweng.project.ImagePicker;
import ch.epfl.sweng.project.ImageProvider;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.User;
import ch.epfl.sweng.project.UserProfile;


public class PublicUserImageFragment extends UserImageFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        ((LinearLayout)rootView.findViewById(R.id.userProfileButtonsLayout)).removeView(rootView.findViewById(R.id.userImageEdit));
        return rootView;
    }
}