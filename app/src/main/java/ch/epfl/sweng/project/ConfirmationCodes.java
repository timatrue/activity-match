package ch.epfl.sweng.project;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

public final class ConfirmationCodes extends AppCompatActivity {


    private static String success;
    public static String get_success(Context context) {
        if (success == null) {
            success = context.getResources().getString(R.string.create_activity_confirmation_message);
        }
        return success;
    }

    private static String missing_field_error;
    public static String get_missing_field_error(Context context) {
        if (missing_field_error == null) {
            missing_field_error = context.getResources().getString(R.string.create_activity_missing_field_error_message);
        }
        return missing_field_error;
    }

    private static String date_error;
    public static String get_date_error(Context context) {
        if (date_error == null) {
            date_error = context.getResources().getString(R.string.create_activity_date_error_message);
        }
        return date_error;
    }

    private static String missing_location_error;
    public static String get_missing_location_error(Context context) {
        if (missing_location_error == null) {
            missing_location_error = context.getResources().getString(R.string.create_activity_location_error_message);
        }
        return missing_location_error;
    }

    private static String missing_category;
    public static String get_missing_category_error(Context context) {
        if (missing_category == null) {
            missing_category = context.getResources().getString(R.string.create_activity_category_error_message);
        }
        return missing_category;
    }

    private static String invalid_max_nb_participants;
    public static String get_invalid_max_nb_participants(Context context) {
        if (invalid_max_nb_participants == null) {
            invalid_max_nb_participants = context.getResources().getString(R.string.create_activity_invalid_max_number_participants);
        }
        return invalid_max_nb_participants;
    }

    private static String unknown_error;
    public static String get_unknown_error(Context context) {
        if (unknown_error == null) {
            unknown_error = context.getResources().getString(R.string.create_activity_unknown_error_message);
        }
        return unknown_error;
    }

    private static String no_connection;
    public static String get_no_connection(Context context) {
        if (no_connection == null) {
            no_connection = context.getResources().getString(R.string.create_activity_no_connection);
        }
        return no_connection;
    }
}
