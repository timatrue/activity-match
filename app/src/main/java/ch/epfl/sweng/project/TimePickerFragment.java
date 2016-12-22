package ch.epfl.sweng.project;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    public Calendar roundTime;
    CalendarPickerListener pickerListener;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time or rounded time (if exists) as the default values for the picker
        Calendar currentTime;
        if(roundTime != null) {
            currentTime = roundTime;
        } else {
            currentTime = Calendar.getInstance();
        }
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hour, int minute) {
        pickerListener.updateTime(this, hour, minute);
    }

    public void setPickerListener(CalendarPickerListener calendarPickerListener) {
        pickerListener = calendarPickerListener;
    }
}
