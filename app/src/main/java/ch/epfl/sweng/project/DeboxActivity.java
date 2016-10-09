package ch.epfl.sweng.project;

import android.location.Location;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nathan on 09.10.16.
 */

public class DeboxActivity {
    public final int DEBOX_ACTIVITY_SHORT_DESCRIPTION_MAX_LENGTH = 64;

    private String organizer;
    private String title;
    private String description;
    private Calendar timeStart;
    private Calendar timeEnd;
    private double longitude;
    private double latitude;
    private String category;

    /**
     * Allocates a <code>DeboxActivity</code> object and initializes it so that
     * it represents a Debox activity, containing all informations about it.
     *
     * @param   organizer    The name of the organizer of the activity
     * @param   title   the title of the activity
     * @param   description    a description of the activity
     * @param   timeStart    the <code>Calendar</code> representing the scheduled start of the Activity
     * @param   timeEnd    the <code>Calendar</code> representing the scheduled end of the Activity
     * @param   longitude    the longitude of the activity's meeting point location
     * @param   latitude    the latitude of the activity's meeting point location
     * @param   category    the category of the activity
     */
    public DeboxActivity(String organizer, String title, String description, Calendar timeStart,
                         Calendar timeEnd, double latitude, double longitude, String category) {
        this.organizer = organizer;
        this.title = title;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }

    /**
     * @return  the organizer of the Debox Activity
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * @return  the title of the Debox Activity
     */
    public String getTitle() {
        return title;
    }


    /**
     * @return the description of the Debox Activity
     */
    public String getDescription(){
        return description;
    }

    /**
     * @return a short description of the Debox Activity. The short description corresponds to the
     * description, with a maximum length of 64 characters.
     */
    public String getShortDescription() {
        if (description.length() > DEBOX_ACTIVITY_SHORT_DESCRIPTION_MAX_LENGTH) {
            return description.substring(0, DEBOX_ACTIVITY_SHORT_DESCRIPTION_MAX_LENGTH-3) + "...";
        }
        else {
            return description;
        }
    }

    /**
     * @param   maxLength    the maximum number of character of the short description to return
     * @return a short description of the Debox Activity. The short description corresponds to the
     * description, with a maximum length specified by the paramter maxLength
     */
    public String getShortDescription(int maxLength) {
        if(maxLength <= 3) {
            return "...";
        }
        if (description.length() > maxLength) {
            return description.substring(0, maxLength-3) + "...";
        }
        else {
            return description;
        }
    }


    /**
     * @return the start time of the Debox Activity
     */
    public Calendar getTimeStart() {
        return timeStart;
    }

    /**
     * @return the end time of the Debox Activity
     */
    public Calendar getTimeEnd() {
        return timeEnd;
    }

    /**
     * @return an array containing the latitude and longitude of the Debox Activity meeting point
     * location
     */
    public double[] getLocation() {
        double[] location = {latitude, longitude};
        return location;
    }

    /**
     * @return the category of the Debox Activity
     */
    public String getCategory() {
        return category;
    }
}
