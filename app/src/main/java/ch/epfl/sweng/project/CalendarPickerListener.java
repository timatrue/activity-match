package ch.epfl.sweng.project;


import android.support.v4.app.DialogFragment;


/**
 * Created by benoit on 12.10.16.
 */

public interface CalendarPickerListener {

    void updateDate(DialogFragment fragment, int year, int month, int day);
    void updateTime(DialogFragment fragment, int hour, int minute);
}
