package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.uiobjects.UserProfileExpandableListAdapter;

import android.util.Log;
import android.widget.TextView;


/**
 * Created by olga on 07.11.16.
 * Displays the profile of the currently logged in user
 */

public class UserProfile extends AppCompatActivity {

    TextView emailTextView;
    User current_user;

    List<String> interestedIds = new ArrayList<>();
    List<String> organizedIds = new ArrayList<>();

    ArrayList<String> intTitles = new ArrayList<>();
    ArrayList<String> orgTitles = new ArrayList<>();
    ArrayList<String> partTitles = new ArrayList<>();

    ArrayList<DeboxActivity> intEvents = new ArrayList<>();
    ArrayList<DeboxActivity> orgEvents = new ArrayList<>();
    ArrayList<DeboxActivity> partEvents = new ArrayList<>();

    private DataProvider dp;
    private DataProvider dpData;

    private String interestedEvents;
    private String participatedEvents;
    private String organizedEvents;

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> activityCollection;
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

        setupUserToolBar();
        createGroupList();
        createCollection();
        expListView = (ExpandableListView) findViewById(R.id.userProfileActivityList);
        final UserProfileExpandableListAdapter eventsExpListAdapter =
                new UserProfileExpandableListAdapter(this, activityCollection, groupList);
        expListView.setAdapter(eventsExpListAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent,
                                        View v, int groupPosition, int childPosition, long id) {
                final String selected = (String) eventsExpListAdapter.getChild(groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                        .show();
                return true;
            }
        });
    }
    private void createGroupList() {
        groupList = new ArrayList<>();
        groupList.add(organizedEvents);
        groupList.add(participatedEvents);
        groupList.add(interestedEvents);
    }
    private void createCollection() {
        dp = new DataProvider();
        dp.userProfile(new DataProvider.DataProviderListenerUserInfo(){

            @Override
            public void getUserInfo(User user) {
                current_user = user.copy();
                interestedIds = new ArrayList<String>(user.getInterestedEventIds());
                organizedIds = new ArrayList<String>(user.getOrganizedEventIds());

                dpData = new DataProvider();
                dpData.getSpecifiedActivities(new DataProvider.DataProviderListenerUserEvents (){

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
                        String[] interestedEventsArray = new String[intTitles.size()];
                        if (intTitles.size() != 0) {
                            loadChild(intTitles.toArray(interestedEventsArray));
                        } else {
                            loadChild(emptyEventList);
                        }
                        activityCollection.put(interestedEvents, childList);

                        String[] participatedEventsArray = new String[partTitles.size()];
                        if (partTitles.size() != 0) {
                            loadChild(partTitles.toArray(participatedEventsArray));
                        } else {
                            loadChild(emptyEventList);
                        }
                        activityCollection.put(participatedEvents, childList);

                        for (DeboxActivity event : orgList) {
                            orgTitles.add(event.getTitle());
                            orgEvents.add(event);
                        }
                        String[] organizedEventsArray = new String[orgTitles.size()];
                        if (orgTitles.size() != 0) {
                            loadChild(orgTitles.toArray(organizedEventsArray));
                        } else {
                            loadChild(emptyEventList);
                        }
                        activityCollection.put(organizedEvents, childList);

                    }
                }, interestedIds, organizedIds);
                emailTextView = (TextView) findViewById(R.id.userEmail);
                emailTextView.setText(user.getEmail());
            }
        });

        activityCollection = new LinkedHashMap<String, List<String>>();

    }
    private void loadChild(String[] events) {
        childList = new ArrayList<String>();
        for (String event : events)
            childList.add(event);
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
}