package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import ch.epfl.sweng.project.fragments.PublicUserImageFragment;
import ch.epfl.sweng.project.uiobjects.UserProfileExpandableListAdapter;


/*
 * Displays the profile of the currently logged in user
 */

public class PublicUserProfile extends UserProfile {

    public final static String PUBLIC_USER_PROFILE_UID_KEY = "ch.epfl.sweng.project.PublicUserProfile.PUBLIC_USER_PROFILE_UID_KEY";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        userId = intent.getStringExtra(PUBLIC_USER_PROFILE_UID_KEY);

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
        };

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
                interestedIds = new ArrayList<>(user.getInterestedEventIds());
                organizedIds = new ArrayList<>(user.getOrganizedEventIds());
                rankedIds = new ArrayList<>(user.getOrganizedEventIds());
                comments = new ArrayList<>(user.getCommentField());
                addUsersComments(comments);

                mDataProvider.getSpecifiedActivities(new DataProvider.DataProviderListenerUserEvents (){

                    @Override
                    public void getUserActivities(List<DeboxActivity> intList, List<DeboxActivity> orgList, List<DeboxActivity> rankedList) {
                        for (DeboxActivity event : orgList) {
                            if (event.getTimeEnd().after(Calendar.getInstance())) {
                                orgTitles.add(event.getTitle());
                                orgEvents.add(event);
                            }

                        }
                        activityCollection.put(organizedEvents, orgEvents);

                    }
                }, interestedIds, organizedIds, rankedIds);
                nameTextView = (TextView) findViewById(R.id.userName);
                String userName =  user.getUsername();
                if(userName == null) {
                    userName = user.getEmail();
                }
                if(userName != null) {
                    nameTextView.setText(user.getUsername());
                }

                setExpListView();

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