package ch.epfl.sweng.project;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.project.fragments.FilterFragment;
import ch.epfl.sweng.project.uiobjects.ActivityPreview;
import ch.epfl.sweng.project.uiobjects.NoResultsPreview;

import static com.google.android.gms.internal.zzs.TAG;
import static java.text.DateFormat.getDateInstance;


public class WelcomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, CalendarPickerListener {

    //radius of Earth in km
    final static double EARTH_RADIUS = 6371;

    final static public String WELCOME_ACTIVITY_TEST_KEY = "ch.epfl.sweng.project.CreateActivity.WELCOME_ACTIVITY_TEST_KEY";
    final static public String WELCOME_ACTIVITY_NO_TEST = "ch.epfl.sweng.project.CreateActivity.WELCOME_ACTIVITY_NO_TEST";
    final static public String WELCOME_ACTIVITY_TEST = "ch.epfl.sweng.project.CreateActivity.WELCOME_ACTIVITY_TEST";

    public final static HashMap<String, Integer> maxDistanceMap;
    static
    {
        maxDistanceMap = new HashMap<>();
        maxDistanceMap.put("1 km", 1);
        maxDistanceMap.put("5 km", 5);
        maxDistanceMap.put("10 km", 10);
        maxDistanceMap.put("20 km", 20);
        maxDistanceMap.put("50 km", 50);
        maxDistanceMap.put("All", 21000);
    }

    Button displayActivities;
    LinearLayout activityPreviewsLayout;

    FilterFragment dialogFragment;

    DatePickerFragment startDateFragment;
    DatePickerFragment endDateFragment;
    TimePickerFragment startTimeFragment;
    TimePickerFragment endTimeFragment;

    public Calendar filterStartCalendar;
    public Calendar filterEndCalendar;

    // private DatabaseReference mDatabase;
    private DataProvider mDataProvider;
    final public List<String> categories = new ArrayList<>();
    private final static int PLACE_PICKER_REQUEST = 1;
    public double centerLatitude = 0;
    public double centerLongitude = 0;
    public String filterCategory = "All";
    public String maxDistanceString = "All";
    private final static int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    boolean permission_granted;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button addActivityButton = (Button) findViewById(R.id.addActivity);
        addActivityButton.setOnClickListener(newActivityListener);

        Button filterButton = (Button) findViewById(R.id.filterActivity);
        filterButton.setOnClickListener(filterEventsListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        activityPreviewsLayout = (LinearLayout) findViewById(R.id.activityPreviewsLayout);

        displayActivities = (Button) findViewById(R.id.displayActivities);
        displayActivities.setOnClickListener(activitiesClickListener);

        filterStartCalendar = Calendar.getInstance();
        filterEndCalendar = Calendar.getInstance();
        filterEndCalendar.add(Calendar.DATE, 7);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String test = bundle.getString(WELCOME_ACTIVITY_TEST_KEY);
            if (test != null) {
                if (test.equals(WELCOME_ACTIVITY_NO_TEST)) {
                    setDataProvider(new DataProvider());
                    getAllCategoriesAndLocation();
                }
            }
        }
    }

    public void setDataProvider(DataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    public void getAllCategoriesAndLocation() {
        getAllCategories();

        setCurrentLocalisation();
        createLocationRequest();


        //The permissions need to be asked to the user at runtime for newer APIs
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        } else {
            permission_granted = true;
            initializeGoogleApiClient();
        }
    }

    public void getAllCategories() {
        mDataProvider.getAllCategories(new DataProvider.DataProviderListenerCategories() {
            @Override
            public void getCategories(List<DataProvider.CategoryName> items) {
                for (DataProvider.CategoryName cat : items) {
                    categories.add(cat.getCategory());
                }
            }
        });
    }

    protected synchronized void initializeGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    //Options for the user localisation refresh rate
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Error with the permission granting process");
        }
        else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLastLocation != null) {
            setCurrentLocalisation();
            stopLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission_granted = true;
                    initializeGoogleApiClient();

                } else {
                    permission_granted = false;
                    Log.d(TAG, "Localisation permission denied");
                }
            }
        }
    }

    //Updates the latitude and longitude variables for the filter
    private void setCurrentLocalisation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Error with the permission granting process");
        } else {
            Log.d(TAG, "Localisation permission granted");
            if (mGoogleApiClient != null) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    centerLatitude = mLastLocation.getLatitude();
                    centerLongitude = mLastLocation.getLongitude();
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        if (permission_granted) {
            startLocationUpdates();
        }
    }

    View.OnClickListener filterEventsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CategoryFragment(v);
        }
    };

    protected void CategoryFragment(View v){
        FragmentManager fm = getFragmentManager();
        dialogFragment = new FilterFragment ();
        dialogFragment.categoryList = categories;
        dialogFragment.show(fm, "filterFragment");
    }

    View.OnClickListener previewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof ActivityPreview) {
                String eventId = ((ActivityPreview) v).getEventId();
                Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                intent.putExtra(DisplayActivity.DISPLAY_ACTIVITY_TEST_KEY, DisplayActivity.DISPLAY_ACTIVITY_NO_TEST);
                intent.putExtra(DisplayActivity.DISPLAY_EVENT_ID, eventId);
                startActivity(intent);
            }
        }
    };

    View.OnClickListener newActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
            intent.putExtra(CreateActivity.CREATE_ACTIVITY_TEST_KEY, CreateActivity.CREATE_ACTIVITY_NO_TEST);
            startActivity(intent);
        }
    };

    View.OnClickListener activitiesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivitiesAndDisplay();
        }
    };

    public void chooseLocation(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                centerLatitude = place.getLatLng().latitude;
                centerLongitude = place.getLatLng().longitude;

                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    //Gets the activities that should be displayed given the filters and displays them
    public void displaySpecifiedActivities() {

        final int maxDistance;
        if(maxDistanceMap.get(maxDistanceString) != null) {
            maxDistance = maxDistanceMap.get(maxDistanceString);
        }
        else {
            maxDistance = 21000;
            Log.d(TAG, "Invalid maxDistance string detected");
        }

        Calendar currentTime = Calendar.getInstance();
        if(filterStartCalendar.before(currentTime)) {
            filterStartCalendar = currentTime;
        }

        if(filterCategory.equals("All")){
            mDataProvider.getAllActivities(new DataProvider.DataProviderListenerActivities() {

                @Override
                public void getActivities(List<DeboxActivity> activitiesList) {

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(30, 20, 30, 0);

                    cleanLinearLayout(activityPreviewsLayout);
                    if (activitiesList.isEmpty()) {
                        NoResultsPreview result = new NoResultsPreview(getApplicationContext());
                        activityPreviewsLayout.addView(result, layoutParams);

                    } else {
                        for(DeboxActivity elem: activitiesList) {
                            if(distanceFromCenter(elem) <= maxDistance && elem.getTimeEnd().after(filterStartCalendar) && elem.getTimeStart().before(filterEndCalendar)) {
                                ActivityPreview ap = new ActivityPreview(getApplicationContext(), elem);
                                activityPreviewsLayout.addView(ap, layoutParams);
                                ap.setOnClickListener(previewClickListener);
                            }
                        }
                    }
                    mDataProvider = new DataProvider();
                }
            });
        }

        else  {
            mDataProvider.getSpecifiedCategory(new DataProvider.DataProviderListenerCategory() {


                @Override
                public void getCategory(List<DeboxActivity> activitiesList) {

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(30, 20, 30, 0);

                    cleanLinearLayout(activityPreviewsLayout);
                    if (activitiesList.isEmpty()) {
                        NoResultsPreview result = new NoResultsPreview(getApplicationContext());
                        activityPreviewsLayout.addView(result, layoutParams);

                    } else {
                        for(DeboxActivity elem: activitiesList) {
                            if(distanceFromCenter(elem) <= maxDistance && elem.getTimeEnd().after(filterStartCalendar) && elem.getTimeStart().before(filterEndCalendar)) {
                                ActivityPreview ap = new ActivityPreview(getApplicationContext(), elem);
                                activityPreviewsLayout.addView(ap, layoutParams);
                                ap.setOnClickListener(previewClickListener);
                            }
                        }
                    }
                    mDataProvider = new DataProvider();
                }
            }, filterCategory);
        }
    }

    //Computes distance in km from lagitudes and longitudes with the equirectangular approximation
    public double distanceFromCenter(DeboxActivity elem) {
        double centerLatitudeRad = Math.toRadians(centerLatitude);
        double latitudeElemRad = Math.toRadians(elem.getLocation()[0]);
        double latitudeDiff = centerLatitudeRad - latitudeElemRad;
        double longitudeDiff = Math.toRadians(centerLongitude - elem.getLocation()[1]);
        double correction = Math.cos((centerLatitudeRad + latitudeElemRad)/2);
        return EARTH_RADIUS * Math.sqrt(Math.pow(latitudeDiff,2) + Math.pow(longitudeDiff * correction, 2));
    }

    private void getActivitiesAndDisplay() {
        cleanLinearLayout(activityPreviewsLayout);
        mDataProvider.getAllActivities(new DataProvider.DataProviderListenerActivities() {

            @Override
            public void getActivities(List<DeboxActivity> activitiesList) {

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 20, 30, 0);

                activityPreviewsLayout.removeAllViews();
                for(DeboxActivity elem: activitiesList) {
                    ActivityPreview ap = new ActivityPreview(getApplicationContext(), elem);
                    activityPreviewsLayout.addView(ap, layoutParams);
                    ap.setOnClickListener(previewClickListener);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Close the drawer if opened
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //Quit the APP without logout on backpressed
        else {
            setResult(Login.RE_QUIT);
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_log_out) {
            //Return to Login Activity and logout
            setResult(Login.RE_LOG_OUT);
            finish();

        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_contact) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",getResources().getString(R.string.company_mail), null));
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.contact_message)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void cleanLinearLayout(LinearLayout linearLayout){
        if((linearLayout).getChildCount() > 0)
            (linearLayout).removeAllViews();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void updateDate(android.support.v4.app.DialogFragment fragment, int year, int month, int day) {
        if (fragment == startDateFragment) {
            filterStartCalendar.set(Calendar.YEAR, year);
            filterStartCalendar.set(Calendar.MONTH, month);
            filterStartCalendar.set(Calendar.DAY_OF_MONTH, day);
        } else if (fragment == endDateFragment) {
            filterEndCalendar.set(Calendar.YEAR, year);
            filterEndCalendar.set(Calendar.MONTH, month);
            filterEndCalendar.set(Calendar.DAY_OF_MONTH, day);
        }
        dialogFragment.updateDateTextViews();
    }

    @Override
    public void updateTime(android.support.v4.app.DialogFragment fragment, int hour, int minute) {
        if (fragment == startTimeFragment) {
            filterStartCalendar.set(Calendar.HOUR_OF_DAY, hour);
            filterStartCalendar.set(Calendar.MINUTE, minute);
        } else if (fragment == endTimeFragment) {
            filterEndCalendar.set(Calendar.HOUR_OF_DAY, hour);
            filterEndCalendar.set(Calendar.MINUTE, minute);
        }
        dialogFragment.updateTimeTextViews();
    }

    public String makeDateString(Calendar calendar) {
        DateFormat dateFormat = getDateInstance();
        return dateFormat.format(calendar.getTime());
    }

    public String makeTimeString(Calendar calendar) {
        //DateFormat timeFormat = getTimeInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(calendar.getTime());
    }

    public void showStartDatePickerDialog(View v) {
        startDateFragment = new DatePickerFragment();
        startDateFragment.show(getSupportFragmentManager(), "datePicker");
        startDateFragment.setPickerListener(this);
    }

    public void showEndDatePickerDialog(View v) {
        endDateFragment = new DatePickerFragment();
        endDateFragment.show(getSupportFragmentManager(), "datePicker");
        endDateFragment.setPickerListener(this);
    }

    public void showStartTimePickerDialog(View v) {
        startTimeFragment = new TimePickerFragment();
        startTimeFragment.show(getSupportFragmentManager(), "timePicker");
        startTimeFragment.setPickerListener(this);
    }

    public void showEndTimePickerDialog(View v) {
        endTimeFragment = new TimePickerFragment();
        endTimeFragment.show(getSupportFragmentManager(), "timePicker");
        endTimeFragment.setPickerListener(this);
    }
}
