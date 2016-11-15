package ch.epfl.sweng.project;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.Toast;
import java.util.ArrayList;
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

    ArrayList<String> titles = new ArrayList<String>();
    List<String> interestedIds = new ArrayList<>();
    private DataProvider dp;
    private DataProvider dpData;

    private String interestedEvents;
    private String participatedEvents;
    private String organizedEvents;

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> activityCollection;
    ExpandableListView expListView;
    private static Context mContext;

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
        groupList = new ArrayList<String>();
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
                Log.d("current_user email: ", current_user.getEmail());
                interestedIds = new ArrayList<String>(user.getInterestedEvents());

                dpData = new DataProvider();
                dpData.getSpecifiedActivities(new DataProvider.DataProviderListenerUserEvents (){

                    @Override
                    public void getUserActivities(List<DeboxActivity> activitiesList) {
                        for (DeboxActivity event : activitiesList) {
                            titles.add(event.getTitle());
                        }
                        String[] interestedEventsArray = new String[titles.size()];
                        loadChild(titles.toArray(interestedEventsArray));
                        activityCollection.put(interestedEvents, childList);
                    }
                }, interestedIds);
                emailTextView = (TextView) findViewById(R.id.userEmail);
                emailTextView.setText(user.getEmail());
            }
        });
        String[] organisedEventsArray= { "No Events"};
        String[] participateEventsArray = { "No Events"};

        activityCollection = new LinkedHashMap<String, List<String>>();

        for (String group : groupList) {
            if (group.equals(organizedEvents)) {
                loadChild(organisedEventsArray);
            } else if (group.equals(participatedEvents)){
                loadChild(participateEventsArray);
            }
            activityCollection.put(group, childList);
        }
    }
    private void loadChild(String[] events) {
        childList = new ArrayList<String>();
        for (String event : events)
            childList.add(event);
    }
    private static Context getContext() {
        return mContext;
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