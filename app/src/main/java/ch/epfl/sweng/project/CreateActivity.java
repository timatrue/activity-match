package ch.epfl.sweng.project;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Locale;


public class CreateActivity extends AppCompatActivity implements CalendarPickerListener {

    TextView startDateTextView;
    TextView endDateTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;

    DatePickerFragment startDateFragment;
    DatePickerFragment endDateFragment;
    TimePickerFragment startTimeFragment;
    TimePickerFragment endTimeFragment;

    Calendar startCalendar;
    Calendar endCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_activity);

        startDateTextView = (TextView) findViewById(R.id.startDateTextView);
        endDateTextView = (TextView) findViewById(R.id.endDateTextView);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startDateTextView.setText(makeDateString(startCalendar));
        endDateTextView.setText(makeDateString(endCalendar));

        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        endTimeTextView = (TextView) findViewById(R.id.endTimeTextView);
        startTimeTextView.setText(makeTimeString(startCalendar));
        endTimeTextView.setText(makeTimeString(endCalendar));
    }


    public void showStartTimePickerDialog(View v) {
        startTimeFragment = new TimePickerFragment();
        startTimeFragment.show(getSupportFragmentManager(), "timePicker");
        startTimeFragment.setPickerListener(this);
    }

    public void showStartDatePickerDialog(View v) {
        startDateFragment = new DatePickerFragment();
        startDateFragment.show(getSupportFragmentManager(), "datePicker");
        startDateFragment.setPickerListener(this);
    }

    public void showEndTimePickerDialog(View v) {
        endTimeFragment = new TimePickerFragment();
        endTimeFragment.show(getSupportFragmentManager(), "timePicker");
        endTimeFragment.setPickerListener(this);
    }

    public void showEndDatePickerDialog(View v) {
        endDateFragment = new DatePickerFragment();
        endDateFragment.show(getSupportFragmentManager(), "datePicker");
        endDateFragment.setPickerListener(this);
    }

    private String makeDateString(Calendar calendar) {
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + " " +
               calendar.get(Calendar.DAY_OF_MONTH) + "." +
               (calendar.get(Calendar.MONTH) + 1) + "." +
               calendar.get(Calendar.YEAR);
    }

    private String makeTimeString(Calendar calendar) {
        return calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
    }

    @Override
    public void updateDate(DialogFragment fragment, int year, int month, int day) {
        if(fragment == startDateFragment) {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, day);
            startDateTextView.setText(makeDateString(startCalendar));
        }
        else if (fragment == endDateFragment) {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, month);
            endCalendar.set(Calendar.DAY_OF_MONTH, day);
            endDateTextView.setText(makeDateString(endCalendar));
        }
    }

    @Override
    public void updateTime(DialogFragment fragment, int hour, int minute) {
        if(fragment == startTimeFragment) {
            startCalendar.set(Calendar.HOUR, hour);
            startCalendar.set(Calendar.MINUTE, minute);
            startTimeTextView.setText(makeTimeString(startCalendar));
        }
        else if (fragment == endTimeFragment) {
            endCalendar.set(Calendar.HOUR, hour);
            endCalendar.set(Calendar.MINUTE, minute);
            endTimeTextView.setText(makeTimeString(endCalendar));
        }
    }
}
