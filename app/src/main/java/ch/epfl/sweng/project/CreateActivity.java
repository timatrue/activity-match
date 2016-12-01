package ch.epfl.sweng.project;

import android.app.Activity;
import android.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import ch.epfl.sweng.project.fragments.CreateValidationFragment;
import ch.epfl.sweng.project.fragments.FilterFragment;

import static android.R.attr.category;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

import static com.google.android.gms.internal.zzs.TAG;

import static java.text.DateFormat.getDateInstance;


public class CreateActivity extends AppCompatActivity implements CalendarPickerListener {


    protected static boolean TEST_MODE = false;
    public final int PLACE_PICKER_REQUEST = 1;
    public final int PICK_IMAGE_REQUEST = 2;

    final static public String CREATE_ACTIVITY_TEST_KEY = "ch.epfl.sweng.project.CreateActivity.CREATE_ACTIVITY_TEST_KEY";
    final static public String CREATE_ACTIVITY_NO_TEST = "ch.epfl.sweng.project.CreateActivity.CREATE_ACTIVITY_NO_TEST";
    final static public String CREATE_ACTIVITY_TEST = "ch.epfl.sweng.project.CreateActivity.CREATE_ACTIVITY_TEST";

    boolean creation = true;


    final static public String CREATE_ACTIVITY_DEFAULT_ID = "ch.epfl.sweng.project.CreateActivity.CREATE_ACTIVITY_DEFAULT_ID";


    TextView startDateTextView;
    TextView endDateTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;

    DatePickerFragment startDateFragment;
    DatePickerFragment endDateFragment;
    TimePickerFragment startTimeFragment;
    TimePickerFragment endTimeFragment;


    private AutoCompleteTextView proSpinner;
    private List<String> stringList;
    private String emptyString;

    String activityId = CREATE_ACTIVITY_DEFAULT_ID;
    String activityOrganizer = "default_organizer";
    String activityTitle = "";
    String activityDescription = "";
    Calendar activityStartCalendar = Calendar.getInstance();
    Calendar activityEndCalendar = Calendar.getInstance();
    double activityLatitude = 0;
    double activityLongitude = 0;
    String activityCategory = null;
    private int minuteDivisor;
    private int minuteDelayStartTime;
    private int minuteDelayEndTime;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DataProvider mDataProvider;
    private ImageProvider mImageProvider;

    private List<Uri> imagesUriList = new ArrayList<>();
    protected List<String> imagesNameList = new ArrayList<>();

    private CreateValidationFragment validationFragment;

    LinearLayout imagesLayout;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_activity);

        minuteDivisor = getResources().getInteger(R.integer.minute_divisor);
        minuteDelayStartTime = getResources().getInteger(R.integer.start_minutes_delay);
        minuteDelayEndTime = getResources().getInteger(R.integer.end_minutes_delay);
        roundTime();
        setupUserToolBar();

        startDateTextView = (TextView) findViewById(R.id.createActivityStartDate);
        endDateTextView = (TextView) findViewById(R.id.createActivityEndDate);
        startDateTextView.setText(makeDateString(activityStartCalendar));
        endDateTextView.setText(makeDateString(activityEndCalendar));

        startTimeTextView = (TextView) findViewById(R.id.createActivityStartTime);
        endTimeTextView = (TextView) findViewById(R.id.createActivityEndTime);
        startTimeTextView.setText(makeTimeString(activityStartCalendar));
        endTimeTextView.setText(makeTimeString(activityEndCalendar));


        imagesLayout = (LinearLayout) findViewById(R.id.imagesLayout);


        if(user != null) {
            activityOrganizer = user.getUid();
        }
        else {
            activityOrganizer = getString(R.string.unlogged_user);
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //Check if the activity runs in test mode, if not, initialize with real Dataprovider and
        //Get categories on the DB and display them in the dropdown
        Bundle bundle = getIntent().getExtras();
        String test = bundle.getString(CREATE_ACTIVITY_TEST_KEY);
        if(test != null) {
            if(test.equals(CREATE_ACTIVITY_NO_TEST)) {
                setDataProvider(new DataProvider());
                setImageProvider(new ImageProvider());
                getAndDisplayCategories();
            }
            else {
                TEST_MODE = true;
            }
        }
        else {
            Log.d(TAG, "Dataprovider is not initialized: Bundle is null");
        }

    }

    //Set the DataProvider (allows test to insert a Mock DataProvider)
    public void setDataProvider(DataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    public void setImageProvider(ImageProvider imageProvider) {
        mImageProvider = imageProvider;
    }

    public CreateValidationFragment getValidationFragment() {
        return validationFragment;
    }

    //Get Categories available on the DB and display them in the dropdown
    public void getAndDisplayCategories() {
        mDataProvider.getAllCategories(new DataProvider.DataProviderListenerCategories(){
            @Override
            public void getCategories(List<DataProvider.CategoryName> items) {
                stringList = new ArrayList<>();
                for (DataProvider.CategoryName cat : items) {
                    stringList.add(cat.getCategory());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateActivity.this, android.R.layout.simple_dropdown_item_1line, stringList);
                proSpinner = (AutoCompleteTextView)
                        findViewById(R.id.proSpinner);
                proSpinner.setAdapter(adapter);
                proSpinner.setThreshold(1);
                proSpinner.setValidator(new Validator());
                proSpinner.setOnFocusChangeListener(new FocusListener());
                proSpinner.setOnItemClickListener(selectedItemListener);
                proSpinner.setOnTouchListener(new View.OnTouchListener(){
                    @Override
                    public boolean onTouch(View v, MotionEvent event){
                        proSpinner.showDropDown();
                        proSpinner.setError(null);
                        return false;
                    }
                });
            }
        });
    }
    class Validator implements AutoCompleteTextView.Validator{
        @Override
        public boolean isValid(CharSequence userInput){
         //Log.v("Test", "Checking if valid: "+ userInput);
            Collections.sort(stringList);
            if( Collections.binarySearch(stringList, userInput.toString()) > 0 ){
                return true;
            }
            return false;
        }
        @Override
        public CharSequence fixText(CharSequence invalidUserInput){
            String invalidCategory = getResources().getString(R.string.create_activity_invalid_category);
            emptyString = getResources().getString(R.string.emptyString);
            proSpinner.setError(invalidCategory);
            activityCategory = null;
            return emptyString;
        }
    }
    class FocusListener implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v,boolean hasFocus){
            if(v.getId() == R.id.proSpinner && !hasFocus){
                ((AutoCompleteTextView)v).performValidation();
            }
        }
    }

    //When user chose a category on the dropdown, saves it
    AdapterView.OnItemClickListener selectedItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            activityCategory = parent.getItemAtPosition(position).toString();
        }

    };



    //When click on the choose a location button, start the PLacePicker activity
    public void chooseLocation(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            Log.d(TAG, "PlacePicker: GooglePlayServicesRepairableException");
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Log.d(TAG, "PlacePicker: GooglePlayServicesNotAvailableException");
        }
    }

    //Start an intent to let the user chose the image he/she want to upload on the server
    public void pickImage(View v) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //When user has choosen a location, saves it
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                activityLatitude = place.getLatLng().latitude;
                activityLongitude = place.getLatLng().longitude;

                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //Add image URI in the list
                imagesUriList.add(data.getData());
            }
        }
    }

    /* Action of the activity creation confirmation button */
    public void createActivity(View v) {

        EditText TitleEditText = (EditText) findViewById(R.id.createActivityTitleEditText);
        activityTitle = TitleEditText.getText().toString();

        EditText DescriptionEditText = (EditText) findViewById(R.id.createActivityDescriptionEditText);
        activityDescription = DescriptionEditText.getText().toString();


        String validation = validateActivity();


        final DeboxActivity newDeboxActivity = createActivityMethod(validation);

        String valid = ConfirmationCodes.get_success(this);
        if(validation.equals(valid)) {
            if(/*!TEST_MODE*/true) {
                FragmentManager fm = getFragmentManager();
                validationFragment = new CreateValidationFragment();
                validationFragment.setDataProvider(mDataProvider);
                validationFragment.setImageProvider(mImageProvider);
                validationFragment.show(fm, "Validating your event");
                //Add all images name in the debox activity

                for (Uri uri : imagesUriList) {
                    imagesNameList.add(uri.getLastPathSegment());
                }

                for (String name : imagesNameList) {
                    newDeboxActivity.addImage(name);
                }


                validationFragment.setImagesUriList(imagesUriList);


                validationFragment.uploadActivity(newDeboxActivity, creation);
            }
        }
        else {
            setErrorTextView(validation);
        }
    }

    /* Checks the parameters entered by the user an returns a String with the corresponding error
    or success */
    public String validateActivity() {
        if (!activityTitle.equals("") && !activityDescription.equals("")) {

            if (activityLongitude == 0 || activityLatitude == 0) {
                return ConfirmationCodes.get_missing_location_error(this);
            }
            if (activityCategory == null) {
                return ConfirmationCodes.get_missing_category_error(this);
            }
            if (activityEndCalendar.after(activityStartCalendar)
                    && activityEndCalendar.after(Calendar.getInstance())) {
                return ConfirmationCodes.get_success(this);
            } else {
                return ConfirmationCodes.get_date_error(this);
            }
        } else {
            return ConfirmationCodes.get_missing_field_error(this);
        }
    }

    /* Returns a DeboxActivity instance with the parameters entered in the by the user or null if
    the parameters are incorrect */
    public DeboxActivity createActivityMethod(String validation) {

        DeboxActivity newDeboxActivity = null;

        if (validation.equals(ConfirmationCodes.get_success(this))) {
            if (activityStartCalendar.before(Calendar.getInstance())) {
                    /* sets the starting time of the activity to the current time if the starting time
                    is before the current time */
                activityStartCalendar = Calendar.getInstance();
            }

            newDeboxActivity = new DeboxActivity(
                    activityId,
                    activityOrganizer,
                    activityTitle,
                    activityDescription,
                    activityStartCalendar,
                    activityEndCalendar,
                    activityLatitude,
                    activityLongitude,
                    activityCategory
                    );
        }
        return newDeboxActivity;
    }

    /* Adds an error message in a TextView depending on the String returned by validateActivity().
    If there is no error, it stores the message in an Intent, creates a new CreateActivity instance
    and displays the validation message on a TextView in that new CreateActivity instance */
    public void setErrorTextView(String error) {

        TextView confirmation = (TextView) findViewById(R.id.createActivityError);
        confirmation.setTextColor(Color.RED);

        if(error.equals(ConfirmationCodes.get_missing_field_error(this))) {
            confirmation.setText(error);
        }

        else if(error.equals(ConfirmationCodes.get_date_error(this))) {
            confirmation.setText(error);
        }

        else if(error.equals(ConfirmationCodes.get_missing_location_error(this))) {
            confirmation.setText(error);
        }

        else {
            confirmation.setText(ConfirmationCodes.get_unknown_error(this));
        }
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
        startTimeFragment.roundTime = activityStartCalendar;
        startTimeFragment.show(getSupportFragmentManager(), "timePicker");
        startTimeFragment.setPickerListener(this);
    }

    public void showEndTimePickerDialog(View v) {
        endTimeFragment = new TimePickerFragment();
        endTimeFragment.roundTime = activityEndCalendar;
        endTimeFragment.show(getSupportFragmentManager(), "timePicker");
        endTimeFragment.setPickerListener(this);
    }

    @Override
    public void updateDate(DialogFragment fragment, int year, int month, int day) {
        if (fragment == startDateFragment) {
            activityStartCalendar.set(Calendar.YEAR, year);
            activityStartCalendar.set(Calendar.MONTH, month);
            activityStartCalendar.set(Calendar.DAY_OF_MONTH, day);
            startDateTextView.setText(makeDateString(activityStartCalendar));
        } else if (fragment == endDateFragment) {
            activityEndCalendar.set(Calendar.YEAR, year);
            activityEndCalendar.set(Calendar.MONTH, month);
            activityEndCalendar.set(Calendar.DAY_OF_MONTH, day);
            endDateTextView.setText(makeDateString(activityEndCalendar));
        }
    }

    @Override
    public void updateTime(DialogFragment fragment, int hour, int minute) {
        if (fragment == startTimeFragment) {
            activityStartCalendar.set(Calendar.HOUR_OF_DAY, hour);
            activityStartCalendar.set(Calendar.MINUTE, minute);
            startTimeTextView.setText(makeTimeString(activityStartCalendar));
        } else if (fragment == endTimeFragment) {
            activityEndCalendar.set(Calendar.HOUR_OF_DAY, hour);
            activityEndCalendar.set(Calendar.MINUTE, minute);
            endTimeTextView.setText(makeTimeString(activityEndCalendar));
        }
    }
    public void roundTime(){
        int minutes = activityStartCalendar.get(Calendar.MINUTE);
        int remainder = minutes % minuteDivisor;
        activityStartCalendar.add(Calendar.MINUTE, -remainder + minuteDelayStartTime);
        activityEndCalendar.add(Calendar.MINUTE, -remainder + minuteDelayEndTime + minuteDelayStartTime);
    }
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Create Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    private void setupUserToolBar(){
        Toolbar mUserToolBar = (Toolbar) findViewById(R.id.create_activity_toolbar);
        mUserToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public void setImageList(ArrayList<Uri> imageList) {
        this.imagesUriList = imageList;
    }
}
