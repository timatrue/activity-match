package ch.epfl.sweng.project;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import ch.epfl.sweng.project.fragments.PublicUserImageFragment;
import ch.epfl.sweng.project.fragments.UserImageFragment;
import ch.epfl.sweng.project.uiobjects.UserProfileExpandableListAdapter;


/**
 * Created by olga on 07.11.16.
 * Displays the profile of the currently logged in user
 */

public class PublicUserProfile extends UserProfile {

    public final static String PUBLIC_USER_PROFILE_UID_KEY = "ch.epfl.sweng.project.PublicUserProfile.PUBLIC_USER_PROFILE_UID_KEY";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        userId = intent.getStringExtra(PUBLIC_USER_PROFILE_UID_KEY);
        /*
        imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicUserImageFragment publicImageFragment = new PublicUserImageFragment();
                publicImageFragment.setUser(current_user);
                publicImageFragment.setDataProvider(mDataProvider);
                publicImageFragment.setImageProvider(mImageProvider);
                publicImageFragment.setImage(((GlideBitmapDrawable)userImage.getDrawable()).getBitmap());
                publicImageFragment.show(fm, "Validating your event");
            }
        };*/

        super.onCreate(savedInstanceState);
    }



    @Override
    public void createGroupList() {
        groupList = new ArrayList<>();
        groupList.add(organizedEvents);
    }

    @Override
    public void setAdapter() {
        eventsExpListAdapter = new UserProfileExpandableListAdapter(this, activityCollection, groupList, null, null);
        expListView.setAdapter(eventsExpListAdapter);
    }


    @Override
    public void createCollection() {

        mDataProvider.publicUserProfile(userId, new DataProvider.DataProviderListenerUserInfo(){

            @Override
            public void getUserInfo(User user) {
                current_user = user.copy();
                displayUserImage();
                displayUserRanking();
                interestedIds = new ArrayList<String>(user.getInterestedEventIds());
                organizedIds = new ArrayList<String>(user.getOrganizedEventIds());

                mDataProvider.getSpecifiedActivities(new DataProvider.DataProviderListenerUserEvents (){

                    @Override
                    public void getUserActivities(List<DeboxActivity> intList, List<DeboxActivity> orgList) {
                        for (DeboxActivity event : orgList) {
                            orgTitles.add(event.getTitle());
                            orgEvents.add(event);
                        }
                        activityCollection.put(organizedEvents, orgEvents);

                    }
                }, interestedIds, organizedIds);
                nameTextView = (TextView) findViewById(R.id.userName);
                String userName =  user.getUsername();
                if(userName == null) {
                    userName = user.getEmail();
                }
                if(userName != null) {
                    nameTextView.setText(user.getUsername());
                }

                if(expListView != null) {
                    expListView.setVisibility(View.VISIBLE);
                }

                (findViewById(R.id.loadingProgressBar)).setVisibility(View.GONE);

            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String test = bundle.getString(USER_PROFILE_TEST_KEY);
            if (test != null) {
                if (test.equals(USER_PROFILE_NO_TEST)) {
                    activityCollection = new LinkedHashMap<>();
                }
            }
        }

    }

}