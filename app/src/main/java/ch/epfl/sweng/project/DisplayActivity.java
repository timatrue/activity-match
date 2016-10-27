package ch.epfl.sweng.project;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by olga on 10.10.16.
 * This class displays the details of a certain event, that comes from the list of events shown in WelcomeActivity class.
 */

public class DisplayActivity extends AppCompatActivity {

    final static public String DISPLAY_EVENT_ID = "ch.epfl.sweng.project.DisplayActivity.DISPLAY_EVENT_ID";

    TextView title;
    TextView description;
    private DataProvider dp;
    private String eventId;
    private DeboxActivity currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        eventId = intent.getStringExtra(DISPLAY_EVENT_ID);


        dp = new DataProvider();
        dp.getActivityFromUid(new DataProvider.DataProviderListener() {
            @Override
            public void getActivity(DeboxActivity activity) {

                currentActivity = activity;
                title = (TextView) findViewById(R.id.eventTitle);
                title.setText(activity.getTitle()); //selectedEvent.getTitle()

                description = (TextView) findViewById(R.id.eventDescription);
                description.setText(activity.getDescription());
            }

            @Override
            public void getActivities(List<DeboxActivity> activitiesList) {

            }
        }, eventId);
    }

    //joinActivity
    public void joinActivity(View v) {
        Log.d("tag",".joinActivity");
        if(currentActivity!= null){

            dp.joinActivity(currentActivity);

        } else {
            Log.d("debug","current activity is null");
        }



    }


}
