package ch.epfl.sweng.project;


import android.support.v4.app.DialogFragment;


/**
 * Created by benoit on 12.10.16.
 */

public interface CalendarPickerListener {

    public void updateDate(DialogFragment fragment, int year, int month, int day);
    public void updateTime(DialogFragment fragment, int hour, int minute);
}
