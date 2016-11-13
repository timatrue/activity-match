package ch.epfl.sweng.project;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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


    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> activityCollection;
    ExpandableListView expListView;
    private static Context mContext;

    //private String organisedEvents = getContext().getResources().getString(R.string.organised_events);
    //private String participatedEvents = getResources().getString(R.string.participated_events);
    //private String interestedEvents = getResources().getString(R.string.interested_events);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mContext = getApplicationContext();
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
        //groupList.add(organisedEvents);
        //groupList.add(participatedEvents);
        //groupList.add(interestedEvents);
        groupList.add("organisedEvents");
        groupList.add("participatedEvents");
        groupList.add("interestedEvents");

    }
    private void createCollection() {
        String[] organisedEventsArray= { "organisedEvent1", "organisedEvent2"};
        String[] participateEventsArray = { "participatedEvent1", "participatedEvent2"};
        String[] interestedEventsArray = { "interestedEvent1", "interestedEvent2" };

        activityCollection = new LinkedHashMap<String, List<String>>();

        for (String group : groupList) {
            if (group.equals("organisedEvents")) {
                loadChild(organisedEventsArray);
            } else if (group.equals("participatedEvents")){
                loadChild(participateEventsArray);
            } else {
                loadChild(interestedEventsArray);
            }
            activityCollection.put(group, childList);
        }
    }
    private void loadChild(String[] events) {
        childList = new ArrayList<String>();
        for (String event : events)
            childList.add(event);
    }

    public static Context getContext() {
        return mContext;
    }
}
