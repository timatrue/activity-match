package ch.epfl.sweng.project;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;

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
    TextView eventLocation;
    TextView userSignture;
    SpannableStringBuilder userSigntureFull;
    String publishedByString;
    String userName;
    ForegroundColorSpan colorSpan;
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
    private DeboxActivity currentActivity = null;
    private User currentUser = null;
    private Button joinActivityButton;
    private Button leaveActivityButton;
    private Button rateButton;
    private RatingBar rankWidgetRatingBar;
    private TextView statusInfoTextView;
    public TextView occupancyTextView;
    private FirebaseUser mFirebaseUser;
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
        rateButton = (Button) findViewById(R.id.rateButton);
        rankWidgetRatingBar = (RatingBar) findViewById(R.id.rankWidget);
        statusInfoTextView = (TextView) findViewById(R.id.StatusInfo);
        occupancyTextView = (TextView) findViewById(R.id.eventOccupancy);
        eventLocation = (TextView) findViewById(R.id.location);
        imagesLayout = (LinearLayout) findViewById(R.id.imagesLayout);

        setupUserToolBar();

        textBlockLayout = (LinearLayout) findViewById(R.id.textBlockLayout);
        res = getResources();


        String test = intent.getStringExtra(DISPLAY_ACTIVITY_TEST_KEY);
        if(test.equals(DISPLAY_ACTIVITY_NO_TEST)) {
            mDataProvider = new DataProvider();
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            initDisplay(false);

        }
        setupUserToolBar();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 20, 10, 20);
        textBlockLayout.setLayoutParams(layoutParams);

    }

    public void setTestDBObjects(DataProvider testDataProvider, FirebaseUser testFirebaseUser) {
        mDataProvider = testDataProvider;
        mFirebaseUser = testFirebaseUser;
    }

    public void refreshDisplay(boolean test, User user, DeboxActivity activity){
        if(mFirebaseUser!=null || test) {

            UserStatus status = mDataProvider.getUserStatusInActivity(activity,user);

            switch(status){
                case ENROLLED:
                    leaveActivityButton.setVisibility(View.VISIBLE);
                    statusInfoTextView.setText(R.string.already_enrolled);

                    break;
                case NOT_ENROLLED_NOT_FULL:
                    joinActivityButton.setVisibility(View.VISIBLE);
                    statusInfoTextView.setText(R.string.no_enrolled_free_place);


                    break;
                case NOT_ENROLLED_FULL:
                    statusInfoTextView.setText(R.string.no_enrolled_full);

                    break;
                case MUST_BE_RANKED:
                    rateButton.setVisibility(View.VISIBLE);
                    joinActivityButton.setVisibility(View.INVISIBLE);
                    leaveActivityButton.setVisibility(View.INVISIBLE);
                    statusInfoTextView.setText(R.string.must_be_rank);

                    break;
                case ALREADY_RANKED:
                    statusInfoTextView.setText(R.string.already_rank);

                    break;
                case ACTIVITY_PAST:

                    statusInfoTextView.setText(R.string.activity_past);

                    break;
                default:
                    statusInfoTextView.setText(R.string.you_are_organizer);

                    break;
            }

            title = (TextView) findViewById(R.id.titleEvent);
            title.setText(activity.getTitle());

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

            userSignture = (TextView) findViewById(R.id.userSignture);
            final String organizerId = activity.getOrganizer();

            final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

            //Get the public user profile of the organizer
            mDataProvider.publicUserProfile(organizerId, new DataProvider.DataProviderListenerUserInfo() {
                @Override
                public void getUserInfo(final User organizer) {
                    userName = organizer.getUsername();
                    publishedByString = res.getString(R.string.user_signature);
                    userSigntureFull = new SpannableStringBuilder(publishedByString + userName);
                    colorSpan = new ForegroundColorSpan(res.getColor(R.color.niceBlueDebox));

                    userSigntureFull.setSpan(new UnderlineSpan(), publishedByString.length() - 1, userSigntureFull.length(), 0);
                    userSigntureFull.setSpan(colorSpan, publishedByString.length() - 1, userSigntureFull.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    userSignture.setText(userSigntureFull);

                    //Launch organizer Public user profile activity when click on the organizer name
                    //Launch private user profile if the user is the organizer
                    userSignture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent;
                            if (organizerId.equals(user1.getUid())) {
                                intent = new Intent(getApplicationContext(), UserProfile.class);
                            }
                            else {
                                intent = new Intent(getApplicationContext(), PublicUserProfile.class);
                                intent.putExtra(PublicUserProfile.PUBLIC_USER_PROFILE_UID_KEY, organizerId);
                            }
                            intent.putExtra(UserProfile.USER_PROFILE_TEST_KEY, UserProfile.USER_PROFILE_NO_TEST);
                            startActivity(intent);
                        }
                    });
                }
            });



            // TODO for the moment, not all activities are correct entry for occupancy
            final int nbParticipants = activity.getNbOfParticipants();
            final int nbMaxParticipants = activity.getNbMaxOfParticipants();

            if(nbParticipants >= 0) {
                if(nbMaxParticipants > 0) {
                    occupancyTextView.setText(getString(R.string.occupancy_with_max, nbParticipants, nbMaxParticipants));
                } else {
                    occupancyTextView.setText(getString(R.string.occupancy, nbParticipants));
                }
            } else {
                occupancyTextView.setText(R.string.invalid_occupancy);
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

                        SpannableString content = new SpannableString(address + commaSpace + city);
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        eventLocation.setText(content);
                        eventLocation.setOnClickListener(jumpToMapListener);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            List<String> imagesList = activity.getImageList();

            if(!test)

            if(imagesList != null) {
                if(imagesList.size() != 0) {
                    new ImageProvider().downloadImage(getApplicationContext(), eventId, imagesLayout, imagesList);

                }
                else {
                    ((LinearLayout) imagesLayout.getParent().getParent()).removeView((View) imagesLayout.getParent());
                }
            } else {
                if(imagesLayout.getParent().getParent() != null)
                    ((LinearLayout) imagesLayout.getParent().getParent()).removeView((View) imagesLayout.getParent());
            }


        }
    }

    public void initDisplay(final boolean test) {

        if(mFirebaseUser!=null || test) {

            mDataProvider.getActivityAndListenerOnChange(new DataProvider.DataProviderListenerActivity(){
                @Override
                public void getActivity(DeboxActivity activity) {
                    currentActivity = activity;
                    if(currentActivity != null && currentUser != null){
                        refreshDisplay(test,currentUser,currentActivity);
                    }
                }
            },eventId);

            mDataProvider.getUserProfileAndListenerOnChange(new DataProvider.DataProviderListenerUserInfo(){

                @Override
                public void getUserInfo(User user) {
                    currentUser = user;
                    if(currentActivity != null && currentUser != null){
                        refreshDisplay(test,currentUser,currentActivity);
                    }
                }
            });

            if(currentActivity != null && currentUser != null) {
                refreshDisplay(test, currentUser, currentActivity);
            }




            /*mDataProvider.userProfile(new DataProvider.DataProviderListenerUserInfo() {
                @Override
                public void getUserInfo(final User user) {


                    //mDataProvider.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
                    mDataProvider.getActivityAndListenerOnChange(new DataProvider.DataProviderListenerActivity(){
                        @Override
                        public void getActivity(DeboxActivity activity) {
                            currentActivity = activity;

                            UserStatus status = mDataProvider.getUserStatusInActivity(activity,user);

                            switch(status){
                                case ENROLLED:
                                    leaveActivityButton.setVisibility(View.VISIBLE);
                                    statusInfoTextView.setText(R.string.already_enrolled);

                                    break;
                                case NOT_ENROLLED_NOT_FULL:
                                    joinActivityButton.setVisibility(View.VISIBLE);
                                    statusInfoTextView.setText(R.string.no_enrolled_free_place);


                                    break;
                                case NOT_ENROLLED_FULL:
                                    statusInfoTextView.setText(R.string.no_enrolled_full);

                                    break;
                                case MUST_BE_RANKED:
                                    ratingLayout.setVisibility(View.VISIBLE);
                                    statusInfoTextView.setText(R.string.must_be_rank);

                                    break;
                                case ALREADY_RANKED:
                                    statusInfoTextView.setText(R.string.already_rank);

                                    break;
                                case ACTIVITY_PAST:

                                    statusInfoTextView.setText(R.string.activity_past);

                                    break;
                                default:
                                    statusInfoTextView.setText(R.string.you_are_organizer);

                                    break;
                            }

                            title = (TextView) findViewById(R.id.titleEvent);
                            title.setText(activity.getTitle());

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

                            userSignture = (TextView) findViewById(R.id.userSignture);
                            final String organizerId = activity.getOrganizer();

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            //Get the public user profile of the organizer
                            mDataProvider.publicUserProfile(organizerId, new DataProvider.DataProviderListenerUserInfo() {
                                @Override
                                public void getUserInfo(final User organizer) {
                                    userName = organizer.getUsername();
                                    publishedByString = res.getString(R.string.user_signature);
                                    userSigntureFull = new SpannableStringBuilder(publishedByString + userName);
                                    colorSpan = new ForegroundColorSpan(res.getColor(R.color.niceBlueDebox));

                                    userSigntureFull.setSpan(new UnderlineSpan(), publishedByString.length() - 1, userSigntureFull.length(), 0);
                                    userSigntureFull.setSpan(colorSpan, publishedByString.length() - 1, userSigntureFull.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    userSignture.setText(userSigntureFull);

                                    //Launch organizer Public user profile activity when click on the organizer name
                                    //Launch private user profile if the user is the organizer
                                    userSignture.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent;
                                            if (organizerId.equals(user.getUid())) {
                                                intent = new Intent(getApplicationContext(), UserProfile.class);
                                            }
                                            else {
                                                intent = new Intent(getApplicationContext(), PublicUserProfile.class);
                                                intent.putExtra(PublicUserProfile.PUBLIC_USER_PROFILE_UID_KEY, organizerId);
                                            }
                                            intent.putExtra(UserProfile.USER_PROFILE_TEST_KEY, UserProfile.USER_PROFILE_NO_TEST);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });



                            // TODO for the moment, not all activities are correct entry for occupancy
                            final int nbParticipants = activity.getNbOfParticipants();
                            final int nbMaxParticipants = activity.getNbMaxOfParticipants();

                            if(nbParticipants >= 0) {
                                if(nbMaxParticipants > 0) {
                                    occupancyTextView.setText(getString(R.string.occupancy_with_max, nbParticipants, nbMaxParticipants));
                                } else {
                                    occupancyTextView.setText(getString(R.string.occupancy, nbParticipants));
                                }
                            } else {
                                occupancyTextView.setText(R.string.invalid_occupancy);
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

                                        SpannableString content = new SpannableString(address + commaSpace + city);
                                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                                        eventLocation.setText(content);
                                        eventLocation.setOnClickListener(jumpToMapListener);
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
                            } else {
                                ((LinearLayout) imagesLayout.getParent().getParent()).removeView((View) imagesLayout.getParent());
                            }


                        }
                    },eventId);

                }
            });*/

            /*mDataProvider.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
                @Override
                public void getActivity(final DeboxActivity activity) {

                    /*mDataProvider.getCurrentUserStatusSimplified(activity, new DataProvider.DataProviderListenerUserState() {
                        @Override
                        public void getUserState(UserStatus status) {

                            Log.e("Simplified status:",status.toString());

                            switch(status){
                                case ENROLLED:
                                    leaveActivityButton.setVisibility(View.VISIBLE);
                                    statusInfoTextView.setText(R.string.already_enrolled);

                                    break;
                                case NOT_ENROLLED_NOT_FULL:
                                    joinActivityButton.setVisibility(View.VISIBLE);
                                    statusInfoTextView.setText(R.string.no_enrolled_free_place);


                                    break;
                                case NOT_ENROLLED_FULL:
                                    statusInfoTextView.setText(R.string.no_enrolled_full);

                                    break;
                                case MUST_BE_RANKED:
                                    ratingLayout.setVisibility(View.VISIBLE);
                                    statusInfoTextView.setText(R.string.must_be_rank);

                                    break;
                                case ALREADY_RANKED:
                                    statusInfoTextView.setText(R.string.already_rank);

                                    break;
                                case ACTIVITY_PAST:

                                    statusInfoTextView.setText(R.string.activity_past);

                                    break;
                                default:
                                    statusInfoTextView.setText(R.string.you_are_organizer);

                                    break;

                            }

                        }
                    });

                    currentActivity = activity;

                    title = (TextView) findViewById(R.id.titleEvent);
                    title.setText(activity.getTitle());

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

                    userSignture = (TextView) findViewById(R.id.userSignture);
                    final String organizerId = activity.getOrganizer();

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    //Get the public user profile of the organizer
                    mDataProvider.publicUserProfile(organizerId, new DataProvider.DataProviderListenerUserInfo() {
                        @Override
                        public void getUserInfo(final User organizer) {
                            userName = organizer.getUsername();
                            publishedByString = res.getString(R.string.user_signature);
                            userSigntureFull = new SpannableStringBuilder(publishedByString + userName);
                            colorSpan = new ForegroundColorSpan(res.getColor(R.color.niceBlueDebox));

                            userSigntureFull.setSpan(new UnderlineSpan(), publishedByString.length() - 1, userSigntureFull.length(), 0);
                            userSigntureFull.setSpan(colorSpan, publishedByString.length() - 1, userSigntureFull.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            userSignture.setText(userSigntureFull);

                            //Launch organizer Public user profile activity when click on the organizer name
                            //Launch private user profile if the user is the organizer
                            userSignture.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent;
                                    if (organizerId.equals(user.getUid())) {
                                        intent = new Intent(getApplicationContext(), UserProfile.class);
                                    }
                                    else {
                                        intent = new Intent(getApplicationContext(), PublicUserProfile.class);
                                        intent.putExtra(PublicUserProfile.PUBLIC_USER_PROFILE_UID_KEY, organizerId);
                                    }
                                    intent.putExtra(UserProfile.USER_PROFILE_TEST_KEY, UserProfile.USER_PROFILE_NO_TEST);
                                    startActivity(intent);
                                }
                            });
                        }
                    });



                    // TODO for the moment, not all activities are correct entry for occupancy
                    final int nbParticipants = activity.getNbOfParticipants();
                    final int nbMaxParticipants = activity.getNbMaxOfParticipants();

                    if(nbParticipants >= 0) {
                        if(nbMaxParticipants > 0) {
                            occupancyTextView.setText(getString(R.string.occupancy_with_max, nbParticipants, nbMaxParticipants));
                        } else {
                            occupancyTextView.setText(getString(R.string.occupancy, nbParticipants));
                        }
                    } else {
                        occupancyTextView.setText(R.string.invalid_occupancy);
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

                                SpannableString content = new SpannableString(address + commaSpace + city);
                                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                                eventLocation.setText(content);
                                eventLocation.setOnClickListener(jumpToMapListener);
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
            }, eventId);*/



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
            //leaveActivityButton.setVisibility(View.VISIBLE);

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
            //joinActivityButton.setVisibility(View.VISIBLE);

        } else {

            String toastMsg = getString(R.string.toas_fail_join);
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
        }

    }
    public void rateButtonPressed(View v){

        if(currentActivity!= null){

            rateButton.setVisibility(View.INVISIBLE);
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

    View.OnClickListener jumpToMapListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View targetView = findViewById(R.id.googleMapLayout);
            targetView.getParent().requestChildFocus(targetView,targetView);
        }
    };

}
