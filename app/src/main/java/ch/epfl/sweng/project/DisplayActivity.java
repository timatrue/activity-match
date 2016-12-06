package ch.epfl.sweng.project;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.project.DataProvider.UserStatus;
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
    TextView scheduleStarts;
    TextView scheduleEnds;
    TextView location;
    String timeStartFull;
    String timeEndFull;
    String commaSpace;
    Resources res;

    LinearLayout imagesLayout;

    DeboxActivity activityToDisplay = null;
    GoogleMap map = null;
    Geocoder geocoder;
    List<Address> addresses;


    private DataProvider mDataProvider;
    private String eventId;
    private DeboxActivity currentActivity;
    private Button joinActivityButton;
    private Button leaveActivityButton;
    private RatingBar rankWidgetRatingBar;
    private TextView statusInfoTextView;
    public TextView occupancyTextView;
    private FirebaseUser mFirebaseUser;
    private LinearLayout ratingLayout;
    private LinearLayout textBlockLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        eventId = intent.getStringExtra(DISPLAY_EVENT_ID);
        geocoder = new Geocoder(this, Locale.getDefault());

        joinActivityButton = (Button) findViewById(R.id.joinActivity);
        leaveActivityButton = (Button) findViewById(R.id.leaveActivity);
        rankWidgetRatingBar = (RatingBar) findViewById(R.id.rankWidget);
        statusInfoTextView = (TextView) findViewById(R.id.StatusInfo);
        occupancyTextView = (TextView) findViewById(R.id.eventOccupancy);
        location = (TextView) findViewById(R.id.location);
        ratingLayout = (LinearLayout) findViewById(R.id.rankLayout);
        imagesLayout = (LinearLayout) findViewById(R.id.imagesLayout);
        textBlockLayout = (LinearLayout) findViewById(R.id.textBlockLayout);
        res = getResources();



        String test = intent.getStringExtra(DISPLAY_ACTIVITY_TEST_KEY);
        if(test.equals(DISPLAY_ACTIVITY_NO_TEST)) {
            mDataProvider = new DataProvider();
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            initDisplay(false);


        }
        setupUserToolBar();

        //
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 20, 10, 20);
        textBlockLayout.setLayoutParams(layoutParams);



        //

    }

    public void setTestDBObjects(DataProvider testDataProvider, FirebaseUser testFirebaseUser) {
        mDataProvider = testDataProvider;
        mFirebaseUser = testFirebaseUser;
    }


    public void initDisplay(boolean test) {

        if(mFirebaseUser!=null || test) {

            mDataProvider.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
                @Override
                public void getActivity(DeboxActivity activity) {

                    currentActivity = activity;
                    title = (TextView) findViewById(R.id.titleEvent);
                    title.setText(activity.getTitle());


                    description = (TextView) findViewById(R.id.eventDescription);
                    description.setText(activity.getDescription());

                    activityToDisplay = activity;

                    category = (TextView) findViewById(R.id.eventCategory);
                    category.setText(activity.getCategory() + " " + getResources().getString(R.string.create_activity_category_text));

                    description = (TextView) findViewById(R.id.eventDescription);
                    description.setText(activity.getDescription());

                    scheduleStarts = (TextView) findViewById(R.id.eventScheduleStarts);
                    scheduleEnds = (TextView) findViewById(R.id.eventScheduleEnds);
                    DateFormat dateFormat = getDateInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                    Calendar timeStart = activity.getTimeStart();
                    Calendar timeEnd = activity.getTimeEnd();
                    String stringScheduleStarts = dateFormat.format(timeStart.getTime()) +
                            " at " + timeFormat.format(timeStart.getTime());
                    String stringScheduleEnds = dateFormat.format(timeEnd.getTime()) +
                            " at " + timeFormat.format(timeEnd.getTime());


                    timeStartFull = String.format(res.getString(R.string.timeStart), stringScheduleStarts);
                    timeEndFull = String.format(res.getString(R.string.timeEnd), stringScheduleEnds);
                    scheduleStarts.setText(timeStartFull);
                    scheduleEnds.setText(timeEndFull);

                    // TODO for the moment, not all activities are correct entry for occupancy
                    if(!(activity.getNbMaxOfParticipants()==-1 && activity.getNbOfParticipants() == -1)) {
                        if (activity.getNbMaxOfParticipants() >= 0) {
                            occupancyTextView.setText("Occupancy : " + activity.getNbOfParticipants() + " / " + activity.getNbMaxOfParticipants());
                        } else {
                            occupancyTextView.setText("Occupancy : " + activity.getNbOfParticipants());
                        }
                    } else {
                        occupancyTextView.setText("Invalid information about occupancy");
                    }

                    if (map != null) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(activityToDisplay.getLocation()[0], activityToDisplay.getLocation()[1]), 15));
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(activityToDisplay.getLocation()[0], activityToDisplay.getLocation()[1]))
                                .title(activity.getTitle()));
                        try {
                            addresses  = geocoder.getFromLocation(
                                    activityToDisplay.getLocation()[0],activityToDisplay.getLocation()[1],1);
                            if (addresses != null && addresses.size() > 0) {
                                String address = addresses.get(0).getAddressLine(0);
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName();
                                commaSpace = res.getString(R.string.commaSpace);

                                location.setText(address + commaSpace + city);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                    List<String> imagesList = activity.getImageList();

                    if(imagesList != null) {
                        if(imagesList.size() != 0) {
                            new ImageProvider().downloadImage(getApplicationContext(), eventId, imagesLayout, imagesList);

                        }
                        else {
                            ((LinearLayout) imagesLayout.getParent().getParent()).removeView((View) imagesLayout.getParent());
                        }
                    }
                    else {
                        ((LinearLayout) imagesLayout.getParent().getParent()).removeView((View) imagesLayout.getParent());
                    }
                }
            }, eventId);


            mDataProvider.getCurrentUserStatus(eventId, new DataProvider.DataProviderListenerUserState() {
                @Override
                public void getUserState(UserStatus status) {

                    switch(status){
                        case ENROLLED:
                            leaveActivityButton.setVisibility(View.VISIBLE);
                            //statusInfoTextView.setText("You are enrolled in this Activity");
                            statusInfoTextView.setVisibility(View.GONE);

                            break;
                        case NOT_ENROLLED_NOT_FULL:
                            joinActivityButton.setVisibility(View.VISIBLE);
                            //statusInfoTextView.setText("You can joins this activity");
                            statusInfoTextView.setVisibility(View.GONE);

                            break;
                        case NOT_ENROLLED_FULL:
                            statusInfoTextView.setText("This activity is full sorry for you ");

                            break;
                        case MUST_BE_RANKED:
                            ratingLayout.setVisibility(View.VISIBLE);
                            statusInfoTextView.setText("Please Rank this activity");

                            break;
                        case ALREADY_RANKED:
                            statusInfoTextView.setText("You have already rank this activity");
                            ratingLayout.setVisibility(View.VISIBLE);

                            break;
                        case ACTIVITY_PAST:
                            statusInfoTextView.setText("This activty has past you cannot join it");

                            break;
                        default:

                            break;

                    }

                    Log.e("Status  : ",status.toString());
                }
            });


            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }


    /**
     * Method call by button joinActivity. Fill a new relation between user and current
     * activity in database.
     */
    public void joinActivity(View v) {
        if(currentActivity!= null){

            mDataProvider.joinActivity(currentActivity);
            joinActivityButton.setVisibility(View.INVISIBLE);
            leaveActivityButton.setVisibility(View.VISIBLE);

            String toastMsg = getString(R.string.toast_success_join);
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

        } else {

            String toastMsg = getString(R.string.toas_fail_join);
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

        }
    }

    public void leaveActivity(View v){
        if(currentActivity!= null){

            mDataProvider.leaveActivity(currentActivity);
            leaveActivityButton.setVisibility(View.INVISIBLE);
            joinActivityButton.setVisibility(View.VISIBLE);

        } else {

            String toastMsg = getString(R.string.toas_fail_join);
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
        }


    }

    public void rateButtonPressed(View v){

        if(currentActivity!= null){

            ratingLayout.setVisibility(View.INVISIBLE);
            int rank = Math.round(rankWidgetRatingBar.getRating());
            mDataProvider.rankUser(currentActivity.getId(),rank);

        } else {

            // TODO change message error
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
    private void setupUserToolBar(){
        Toolbar mUserToolBar = (Toolbar) findViewById(R.id.display_activity_toolbar);
        mUserToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
