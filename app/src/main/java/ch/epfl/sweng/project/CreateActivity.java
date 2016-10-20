package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.text.DateFormat.getDateInstance;


public class CreateActivity extends AppCompatActivity implements CalendarPickerListener {

    TextView startDateTextView;
    TextView endDateTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;

    DatePickerFragment startDateFragment;
    DatePickerFragment endDateFragment;
    TimePickerFragment startTimeFragment;
    TimePickerFragment endTimeFragment;

    String activityId =  "";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String activityOrganizer = user.getUid();
    String activityTitle = "";
    String activityDescription = "";
    Calendar activityStartCalendar = Calendar.getInstance();
    Calendar activityEndCalendar = Calendar.getInstance();
    double activityLatitude = 0;
    double activityLongitude = 0;
    String activityCategory = "default_category";
    String validation = "default_validation";

    private DataProvider mDataProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_activity);

        startDateTextView = (TextView) findViewById(R.id.createActivityStartDate);
        endDateTextView = (TextView) findViewById(R.id.createActivityEndDate);
        startDateTextView.setText(makeDateString(activityStartCalendar));
        endDateTextView.setText(makeDateString(activityEndCalendar));

        startTimeTextView = (TextView) findViewById(R.id.createActivityStartTime);
        endTimeTextView = (TextView) findViewById(R.id.createActivityEndTime);
        startTimeTextView.setText(makeTimeString(activityStartCalendar));
        endTimeTextView.setText(makeTimeString(activityEndCalendar));

        mDataProvider = new DataProvider();

        //Retrieves and displays the confirmation message after a successful activity creation
        Bundle confirmationMessage = getIntent().getExtras();
        if (confirmationMessage != null) {
            String confirmationMessageString = confirmationMessage.getString("CONFIRMATION_MESSAGE");
            if (confirmationMessageString != null && confirmationMessageString.equals("success")) {

                TextView confirmationPreviousActivity = (TextView) findViewById(R.id.createActivityConfirmation);
                confirmationPreviousActivity.setText(R.string.create_activity_confirmation_message);
                confirmationPreviousActivity.setTextColor(getResources().getColor(R.color.green));
            }
        }
    }

    /* Action of the activity creation confirmation button */
    public void createActivity(View v) {

        EditText TitleEditText = (EditText) findViewById(R.id.createActivityTitleEditText);
        activityTitle = TitleEditText.getText().toString();

        EditText DescriptionEditText = (EditText) findViewById(R.id.createActivityDescriptionEditText);
        activityDescription = DescriptionEditText.getText().toString();

        validation = validateActivity();

        DeboxActivity newDeboxActivity = createActivityMethod();

        if(validation.equals("success")) {
            mDataProvider.pushActivity(newDeboxActivity);
        }

        setConfirmationTextView(validation);
    }

    /* Checks the parameters entered by the user an returns a String with the corresponding error
    or success */
    public String validateActivity() {
        if (!activityTitle.equals("") && !activityDescription.equals("") && !activityCategory.equals("")) {

            if (activityEndCalendar.after(activityStartCalendar)
                    && activityEndCalendar.after(Calendar.getInstance())) {
                return "success";
            }

            else {
                return "date_error";
            }
        }

        else {
            return "missing_field_error";
        }
    }

    /* Returns a DeboxActivity instance with the parameters entered in the by the user or null if
    the parameters are incorrect */
    public DeboxActivity createActivityMethod() {

        DeboxActivity newDeboxActivity = null;

        if(validation.equals("success")) {
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
                    activityCategory);
        }
        return newDeboxActivity;
    }

    /* Adds an error message in a TextView depending on the String returned by validateActivity().
    If there is no error, it stores the message in an Intent, creates a new CreateActivity instance
    and displays the validation message on a TextView in that new CreateActivity instance */
    public void setConfirmationTextView (String validation) {

        TextView confirmation = (TextView) findViewById(R.id.createActivityConfirmation);

        switch (validation) {
            case "success":
                Intent intent = new Intent(this, CreateActivity.class);
                intent.putExtra("CONFIRMATION_MESSAGE", validation);
                startActivity(intent);
                break;

            case "missing_field_error":
                confirmation.setText(R.string.create_activity_missing_field_error_message);
                confirmation.setTextColor(getResources().getColor(R.color.red));
                break;

            case "date_error":
                confirmation.setText(R.string.create_activity_date_error_message);
                confirmation.setTextColor(getResources().getColor(R.color.red));
                break;

            default:
                confirmation.setText(R.string.create_activity_unknown_error_message);
                confirmation.setTextColor(getResources().getColor(R.color.red));
                break;
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
        startTimeFragment.show(getSupportFragmentManager(), "timePicker");
        startTimeFragment.setPickerListener(this);
    }

    public void showEndTimePickerDialog(View v) {
        endTimeFragment = new TimePickerFragment();
        endTimeFragment.show(getSupportFragmentManager(), "timePicker");
        endTimeFragment.setPickerListener(this);
    }

    @Override
    public void updateDate(DialogFragment fragment, int year, int month, int day) {
        if(fragment == startDateFragment) {
            activityStartCalendar.set(Calendar.YEAR, year);
            activityStartCalendar.set(Calendar.MONTH, month);
            activityStartCalendar.set(Calendar.DAY_OF_MONTH, day);
            startDateTextView.setText(makeDateString(activityStartCalendar));
        }
        else if (fragment == endDateFragment) {
            activityEndCalendar.set(Calendar.YEAR, year);
            activityEndCalendar.set(Calendar.MONTH, month);
            activityEndCalendar.set(Calendar.DAY_OF_MONTH, day);
            endDateTextView.setText(makeDateString(activityEndCalendar));
        }
    }

    @Override
    public void updateTime(DialogFragment fragment, int hour, int minute) {
        if(fragment == startTimeFragment) {
            activityStartCalendar.set(Calendar.HOUR_OF_DAY, hour);
            activityStartCalendar.set(Calendar.MINUTE, minute);
            startTimeTextView.setText(makeTimeString(activityStartCalendar));
        }
        else if (fragment == endTimeFragment) {
            activityEndCalendar.set(Calendar.HOUR_OF_DAY, hour);
            activityEndCalendar.set(Calendar.MINUTE, minute);
            endTimeTextView.setText(makeTimeString(activityEndCalendar));
        }
    }
}
