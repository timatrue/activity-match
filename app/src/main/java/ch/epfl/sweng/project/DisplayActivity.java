package ch.epfl.sweng.project;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    private Button joinActivityButton;
    private TextView enrolledInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        eventId = intent.getStringExtra(DISPLAY_EVENT_ID);

        joinActivityButton = (Button) findViewById(R.id.joinActivity);
        enrolledInfoTextView = (TextView) findViewById(R.id.enrolledInfo);


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

            @Override
            public void getIfEnrolled(boolean result) {

            }
        }, eventId);

        dp.userEnrolledInActivity(new DataProvider.DataProviderListener() {
            @Override
            public void getActivity(DeboxActivity activity) {

            }

            @Override
            public void getActivities(List<DeboxActivity> activitiesList) {

            }

            @Override
            public void getIfEnrolled(boolean result) {

                if (result){
                    enrolledInfoTextView.setVisibility(View.VISIBLE);
                }else{
                    Log.e("resutl : ","not enrolled");
                    joinActivityButton.setVisibility(View.VISIBLE);
                }

            }
        }, eventId);
    }

    //joinActivity
    public void joinActivity(View v) {
        Log.d("tag",".joinActivity");
        if(currentActivity!= null){

            dp.joinActivity(currentActivity);
            enrolledInfoTextView.setVisibility(View.VISIBLE);
            joinActivityButton.setVisibility(View.INVISIBLE);

        } else {
            Log.d("debug","current activity is null");
        }
    }


}
