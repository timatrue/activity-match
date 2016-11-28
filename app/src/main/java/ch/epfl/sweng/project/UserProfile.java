package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.uiobjects.ActivityPreview;
import ch.epfl.sweng.project.uiobjects.UserProfileExpandableListAdapter;

import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by olga on 07.11.16.
 * Displays the profile of the currently logged in user
 */

public class UserProfile extends AppCompatActivity {

    final static public String USER_PROFILE_TEST_KEY = "ch.epfl.sweng.project.UserProfile.USER_PROFILE_TEST_KEY";
    final static public String USER_PROFILE_NO_TEST = "ch.epfl.sweng.project.UserProfile.USER_PROFILE_NO_TEST";
    final static public String USER_PROFILE_TEST = "ch.epfl.sweng.project.UserProfile.USER_PROFILE_TEST";


    private RatingBar userRank;

    private UserProfileExpandableListAdapter eventsExpListAdapter;

    private DataProvider mDataProvider;
    private FirebaseUser user ;

    TextView nameTextView;
    User current_user;

    List<String> interestedIds = new ArrayList<>();
    List<String> organizedIds = new ArrayList<>();

    ArrayList<String> intTitles = new ArrayList<>();
    ArrayList<String> orgTitles = new ArrayList<>();
    ArrayList<String> partTitles = new ArrayList<>();

    ArrayList<DeboxActivity> intEvents = new ArrayList<>();
    ArrayList<DeboxActivity> orgEvents = new ArrayList<>();
    ArrayList<DeboxActivity> partEvents = new ArrayList<>();

    private DataProvider dpData;

    public String interestedEvents;
    public String participatedEvents;
    public String organizedEvents;

    List<String> groupList;
    List<DeboxActivity> childList;
    Map<String, List<DeboxActivity>> activityCollection;
    ExpandableListView expListView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mContext = getApplicationContext();

        interestedEvents = getResources().getString(R.string.interested_events);
        participatedEvents = getResources().getString(R.string.participated_events);
        organizedEvents = getResources().getString(R.string.organised_events);


        createGroupList();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String test = bundle.getString(USER_PROFILE_TEST_KEY);
            if (test != null) {
                if (test.equals(USER_PROFILE_NO_TEST)) {
                    setDataProvider(new DataProvider());
                    displayUserImage();
                    createCollection();
                    setExpListView();

                }
            }
        }

        setupUserToolBar();

    }



    public void setDataProvider(DataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpsURLConnection connection = (HttpsURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 240, 240, false);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void displayUserImage() {
        final ImageView userImage = (ImageView) findViewById(R.id.userImage);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            final Uri photoUrl = user.getPhotoUrl();

            new Thread(new Runnable() {
                public void run() {
                    final Bitmap bitmap = getBitmapFromURL(photoUrl.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userImage.setImageBitmap(bitmap);
                        }
                    });
                }
            }).start();
        }
    }

    private void displayUserRanking() {
        userRank = (RatingBar) findViewById(R.id.userRank);
        if(userRank == null) {
            return;
        }

        double rank = current_user.getRating();
        if(rank != -1) {
            userRank.setProgress((int) (rank/5 * userRank.getMax()));
        }
        else {
            ((RelativeLayout) userRank.getParent()).removeAllViews();
        }
        userRank.setVisibility(View.VISIBLE);
    }


    public void createGroupList() {
        groupList = new ArrayList<>();
        groupList.add(organizedEvents);
        groupList.add(participatedEvents);
        groupList.add(interestedEvents);
    }


    public void createCollection() {

        mDataProvider.userProfile(new DataProvider.DataProviderListenerUserInfo(){

            @Override
            public void getUserInfo(User user) {
                current_user = user.copy();
                displayUserRanking();
                interestedIds = new ArrayList<String>(user.getInterestedEventIds());
                organizedIds = new ArrayList<String>(user.getOrganizedEventIds());

                mDataProvider.getSpecifiedActivities(new DataProvider.DataProviderListenerUserEvents (){

                    @Override
                    public void getUserActivities(List<DeboxActivity> intList, List<DeboxActivity> orgList) {
                        String [] emptyEventList = { "No Events" };

                        for (DeboxActivity event : intList) {
                            if (event.getTimeEnd().after(Calendar.getInstance())) {
                                intTitles.add(event.getTitle());
                                intEvents.add(event);
                            } else {
                                partTitles.add(event.getTitle());
                                partEvents.add(event);
                            }
                        }
                        activityCollection.put(interestedEvents, intEvents);
                        activityCollection.put(participatedEvents, partEvents);

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

                expListView.setVisibility(View.VISIBLE);

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


    private void setupUserToolBar(){
        Toolbar mUserToolBar = (Toolbar) findViewById(R.id.user_toolbar);
        mUserToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setExpListView() {

        expListView = (ExpandableListView) findViewById(R.id.userProfileActivityList);
        eventsExpListAdapter = new UserProfileExpandableListAdapter(this, activityCollection, groupList, organizedEvents, eventsModifyDeleteListener);
        expListView.setAdapter(eventsExpListAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent,
                                        View v, int groupPosition, int childPosition, long id) {
                final DeboxActivity selected = (DeboxActivity) eventsExpListAdapter.getChild(groupPosition, childPosition);
                if(selected != null) {
                    Toast.makeText(getBaseContext(), selected.getTitle(), Toast.LENGTH_SHORT)
                            .show();

                    if (selected != null) {
                        String eventId = selected.getId();
                        launchDisplayActivity(eventId);
                    }
                }
                return true;
            }
        });
    }

    UserProfileExpandableListAdapter.modifyDeleteListener eventsModifyDeleteListener = new UserProfileExpandableListAdapter.modifyDeleteListener() {

        @Override
        public void onItemModified(int groupPosition, int childPosition) {
            final DeboxActivity selected = (DeboxActivity) eventsExpListAdapter.getChild(groupPosition, childPosition);
            Toast.makeText(getBaseContext(), selected.getTitle(), Toast.LENGTH_SHORT)
                    .show();

            if (selected != null) {
                String eventId = selected.getId();
                launchModifyActivity(eventId);
            }
        }

        @Override
        public void onItemDeleted(int groupPosition, int childPosition) {
            final DeboxActivity selected = (DeboxActivity) eventsExpListAdapter.getChild(groupPosition, childPosition);
            Toast.makeText(getBaseContext(), selected.getTitle(), Toast.LENGTH_SHORT)
                    .show();

            if (selected != null) {
                String eventId = selected.getId();
                launchDisplayActivity(eventId);
            }
        }

        @Override
        public void onItemClicked(int groupPosition, int childPosition) {

            final DeboxActivity selected = (DeboxActivity) eventsExpListAdapter.getChild(groupPosition, childPosition);
            Toast.makeText(getBaseContext(), selected.getTitle(), Toast.LENGTH_SHORT)
                    .show();

            if (selected != null) {
                String eventId = selected.getId();
                launchDisplayActivity(eventId);
            }
        }

    };

    private void launchDisplayActivity(String eventId) {
        Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
        intent.putExtra(DisplayActivity.DISPLAY_ACTIVITY_TEST_KEY, DisplayActivity.DISPLAY_ACTIVITY_NO_TEST);
        intent.putExtra(DisplayActivity.DISPLAY_EVENT_ID, eventId);
        startActivity(intent);
    }

    private void launchModifyActivity(String eventId) {
        Intent intent = new Intent(getApplicationContext(), ModifyActivity.class);
        intent.putExtra(ModifyActivity.CREATE_ACTIVITY_TEST_KEY, ModifyActivity.CREATE_ACTIVITY_NO_TEST);
        intent.putExtra(ModifyActivity.MODIFY_ACTIVITY_EVENT_ID, eventId);
        startActivity(intent);
    }

}