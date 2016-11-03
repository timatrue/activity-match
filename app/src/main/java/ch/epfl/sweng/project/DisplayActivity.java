package ch.epfl.sweng.project;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static java.text.DateFormat.getDateInstance;

/**
 * Created by olga on 10.10.16.
 * This class displays the details of a certain event, that comes from the list of events shown in WelcomeActivity class.
 */

public class DisplayActivity extends AppCompatActivity implements OnMapReadyCallback {

    final static public String DISPLAY_EVENT_ID = "ch.epfl.sweng.project.DisplayActivity.DISPLAY_EVENT_ID";
    final static public String DISPLAY_ACTIVITY_TEST_KEY = "ch.epfl.sweng.project.DisplayActivity.DISPLAY_ACTIVITY_TEST_KEY";
    final static public String DISPLAY_ACTIVITY_NO_TEST = "ch.epfl.sweng.project.DisplayActivity.DISPLAY_ACTIVITY_NO_TEST";
    final static public String DISPLAY_ACTIVITY_TEST = "ch.epfl.sweng.project.DisplayActivity.DISPLAY_ACTIVITY_TEST";

    TextView title;
    TextView category;
    TextView description;
    TextView schedule;

    DeboxActivity activityToDisplay = null;
    GoogleMap map = null;

    private DataProvider mDataProvider;
    private String eventId;
    private DeboxActivity currentActivity;
    private Button joinActivityButton;
    private TextView enrolledInfoTextView;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        eventId = intent.getStringExtra(DISPLAY_EVENT_ID);

        joinActivityButton = (Button) findViewById(R.id.joinActivity);
        enrolledInfoTextView = (TextView) findViewById(R.id.enrolledInfo);


        mDataProvider = new DataProvider();

        String test = intent.getStringExtra(DISPLAY_ACTIVITY_TEST_KEY);
        if(test.equals(DISPLAY_ACTIVITY_NO_TEST)) {
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            initDisplay();
        }

    }

    public void setTestDBObjects(DataProvider testDataProvider, FirebaseUser testFirebaseUser) {
        mDataProvider = testDataProvider;
        mFirebaseUser = testFirebaseUser;
    }


    public void initDisplay() {
        //TODO MODIFY THIS AND MAKE IT PROPER
        if(mFirebaseUser!=null || true) {

            mDataProvider.getActivityFromUid(new DataProvider.DataProviderListener() {
                @Override
                public void getActivity(DeboxActivity activity) {

                    currentActivity = activity;
                    title = (TextView) findViewById(R.id.eventTitle);
                    title.setText(activity.getTitle()); //selectedEvent.getTitle()

                    description = (TextView) findViewById(R.id.eventDescription);
                    description.setText(activity.getDescription());

                    activityToDisplay = activity;
                    title = (TextView) findViewById(R.id.eventTitle);
                    title.setText(activity.getTitle()); //selectedEvent.getTitle()

                    category = (TextView) findViewById(R.id.eventCategory);
                    category.setText(getResources().getString(R.string.create_activity_category_text) + " " + activity.getCategory());

                    description = (TextView) findViewById(R.id.eventDescription);
                    description.setText(activity.getDescription());

                    schedule = (TextView) findViewById(R.id.eventSchedule);
                    DateFormat dateFormat = getDateInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    Calendar timeStart = activity.getTimeStart();
                    Calendar timeEnd = activity.getTimeEnd();
                    String stringSchedule = dateFormat.format(timeStart.getTime()) +
                            " at " + timeFormat.format(timeStart.getTime()) + " to " +
                            dateFormat.format(timeEnd.getTime()) +
                            " at " + timeFormat.format(timeEnd.getTime())  ;
                    schedule.setText(stringSchedule);

                    if (map != null) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(activityToDisplay.getLocation()[0], activityToDisplay.getLocation()[1]), 15));
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(activityToDisplay.getLocation()[0], activityToDisplay.getLocation()[1]))
                                .title(activity.getTitle()));
                    }
                }

                @Override
                public void getActivities(List<DeboxActivity> activitiesList) {

                }

                @Override
                public void getIfEnrolled(boolean result) {

                }
            }, eventId);

            // Set listener to check if user is already register in this activity or not.
            mDataProvider.userEnrolledInActivity(new DataProvider.DataProviderListener() {

                @Override
                public void getActivity(DeboxActivity activity) {

                }

                @Override
                public void getActivities(List<DeboxActivity> activitiesList) {

                }

                @Override
                public void getIfEnrolled(boolean result) {

                    if (result) {
                        enrolledInfoTextView.setVisibility(View.VISIBLE);
                    } else {
                        joinActivityButton.setVisibility(View.VISIBLE);
                    }

                }
            }, eventId);


            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }


    /**
     * Method call by button joinActivity. Fill a new relation between user and current
     * activity in database.
     *
     * @param v
     */
    public void joinActivity(View v) {
        if(currentActivity!= null){

            mDataProvider.joinActivity(currentActivity);
            enrolledInfoTextView.setVisibility(View.VISIBLE);
            joinActivityButton.setVisibility(View.INVISIBLE);

            String toastMsg = getString(R.string.toast_success_join);
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

        } else {

            String toastMsg = getString(R.string.toas_fail_join);
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;


        if (activityToDisplay != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(activityToDisplay.getLocation()[0], activityToDisplay.getLocation()[1]), 15));
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(activityToDisplay.getLocation()[0], activityToDisplay.getLocation()[1]))
                    .title(activityToDisplay.getTitle()));
        }
    }
}
