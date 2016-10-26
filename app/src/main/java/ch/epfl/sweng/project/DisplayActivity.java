package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.List;

/**
 * Created by olga on 10.10.16.
 * This class displays the details of a certain event, that comes from the list of events shown in WelcomeActivity class.
 */

public class DisplayActivity extends AppCompatActivity implements OnMapReadyCallback {

    final static public String DISPLAY_EVENT_ID = "ch.epfl.sweng.project.DisplayActivity.DISPLAY_EVENT_ID";

    TextView title;
    TextView description;

    DeboxActivity activityToDisplay = null;
    GoogleMap map = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String eventId = intent.getStringExtra(DISPLAY_EVENT_ID);


        DataProvider dp = new DataProvider();
        dp.getActivityFromUid(new DataProvider.DataProviderListener() {
            @Override
            public void getActivity(DeboxActivity activity) {

                activityToDisplay = activity;
                title = (TextView) findViewById(R.id.eventTitle);
                title.setText(activity.getTitle()); //selectedEvent.getTitle()

                description = (TextView) findViewById(R.id.eventDescription);
                description.setText(activity.getDescription());

                if(map != null) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(activityToDisplay.getLocation()[0], activityToDisplay.getLocation()[1]))
                            .title("Marker"));
                }
            }

            @Override
            public void getActivities(List<DeboxActivity> activitiesList) {

            }
        }, eventId);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        if(activityToDisplay != null) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(activityToDisplay.getLocation()[0], activityToDisplay.getLocation()[1]))
                    .title("Marker"));
        }
    }
}
