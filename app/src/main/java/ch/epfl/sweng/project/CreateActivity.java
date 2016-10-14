package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

    Calendar activityStartCalendar;
    Calendar activityEndCalendar;

    private DataProvider mDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_activity);

        startDateTextView = (TextView) findViewById(R.id.startDateTextView);
        endDateTextView = (TextView) findViewById(R.id.endDateTextView);
        activityStartCalendar = Calendar.getInstance();
        activityEndCalendar = Calendar.getInstance();
        startDateTextView.setText(makeDateString(activityStartCalendar));
        endDateTextView.setText(makeDateString(activityEndCalendar));

        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        endTimeTextView = (TextView) findViewById(R.id.endTimeTextView);
        startTimeTextView.setText(makeTimeString(activityStartCalendar));
        endTimeTextView.setText(makeTimeString(activityEndCalendar));

        mDataProvider = new DataProvider();

        //Retrieves and displays the confirmation message after a successful activity creation
        Bundle confirmationMessage = getIntent().getExtras();
        if (confirmationMessage != null) {
            String confirmationMessageString = confirmationMessage.getString("CONFIRMATION_MESSAGE");
            if (confirmationMessageString.equals("success")) {
                TextView confirmationPreviousActivity = (TextView) findViewById(R.id.createActivityConfirmation);
                confirmationPreviousActivity.setText(R.string.create_activity_confirmation_message);
                confirmationPreviousActivity.setTextColor(getResources().getColor(R.color.green));
            }
        }
    }

    public void createActivity(View v) {

        String activityId = "delfault_id";
        String activityOrganizer = "default_organizer";

        EditText TitleEditText = (EditText) findViewById(R.id.createActivityTitleEditText);
        String activityTitle = TitleEditText.getText().toString();

        EditText DescriptionEditText = (EditText) findViewById(R.id.createActivityDescriptionEditText);
        String activityDescription = DescriptionEditText.getText().toString();

        double activityLatitude = 0.00;
        double activityLongitude = 0.00;
        String activityCategory = "default_category";

        if (!activityTitle.equals("") && !activityDescription.equals("") && !activityCategory.equals("")) {

            if (activityEndCalendar.after(activityStartCalendar)
                    && activityEndCalendar.after(Calendar.getInstance())) {

                if (activityStartCalendar.before(Calendar.getInstance())) {
                    /* sets the starting time of the activity to the current time if the starting time
                    is before the current time */
                    activityStartCalendar = Calendar.getInstance();
                }

                DeboxActivity newDeboxActivity = new DeboxActivity(activityId,
                                                                   activityOrganizer,
                                                                   activityTitle,
                                                                   activityDescription,
                                                                   activityStartCalendar,
                                                                   activityEndCalendar,
                                                                   activityLatitude,
                                                                   activityLongitude,
                                                                   activityCategory);

                mDataProvider.pushActivity(newDeboxActivity);

                Intent intent = new Intent(this, CreateActivity.class);
                String message = "success";
                intent.putExtra("CONFIRMATION_MESSAGE", message);
                startActivity(intent);
            }

            else {
                TextView confirmation = (TextView) findViewById(R.id.createActivityConfirmation);
                confirmation.setText(R.string.create_activity_date_error_message);
                confirmation.setTextColor(getResources().getColor(R.color.red));
            }
        }

        else {
            TextView confirmation = (TextView) findViewById(R.id.createActivityConfirmation);
            confirmation.setText(R.string.create_activity_missing_field_error_message);
            confirmation.setTextColor(getResources().getColor(R.color.red));
        }
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

    private String makeDateString(Calendar calendar) {
        DateFormat dateFormat = getDateInstance();
        return dateFormat.format(calendar.getTime());
    }

    private String makeTimeString(Calendar calendar) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(calendar.getTime());
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
            activityStartCalendar.set(Calendar.HOUR, hour);
            activityStartCalendar.set(Calendar.MINUTE, minute);
            startTimeTextView.setText(makeTimeString(activityStartCalendar));
        }
        else if (fragment == endTimeFragment) {
            activityEndCalendar.set(Calendar.HOUR, hour);
            activityEndCalendar.set(Calendar.MINUTE, minute);
            endTimeTextView.setText(makeTimeString(activityEndCalendar));
        }
    }
}
