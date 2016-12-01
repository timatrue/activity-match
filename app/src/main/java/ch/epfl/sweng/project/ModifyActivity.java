package ch.epfl.sweng.project;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.epfl.sweng.project.fragments.CreateValidationFragment;

import static com.google.android.gms.internal.zzs.TAG;
import static java.text.DateFormat.getDateInstance;


public class ModifyActivity extends CreateActivity {

    public final static String MODIFY_ACTIVITY_EVENT_ID = "ch.epfl.sweng.project.ModifyActivity.MODIFY_ACTIVITY_EVENT_ID";
    private DataProvider dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String eventId = getIntent().getStringExtra(MODIFY_ACTIVITY_EVENT_ID);
        creation = false;

        TextView modifyActivityTitle = (TextView) findViewById(R.id.createActivityTitle);
        modifyActivityTitle.setText(R.string.modify_activity_title);
        Button modifyActivityLocation = (Button) findViewById(R.id.createActivityLocation);
        modifyActivityLocation.setText(R.string.modify_activity_set_location);
        Button modifyActivityButton = (Button) findViewById(R.id.createActivityValidateButton);
        modifyActivityButton.setText(R.string.modify_activity_modify_activity_button);

        dp = new DataProvider();
        dp.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
            @Override
            public void getActivity(DeboxActivity activity) {
                activityId = eventId;
                activityOrganizer = activity.getOrganizer();
                activityTitle = activity.getTitle();
                activityDescription = activity.getDescription();
                activityStartCalendar = activity.getTimeStart();
                activityEndCalendar = activity.getTimeEnd();
                activityLatitude = activity.getLocation()[0];
                activityLongitude = activity.getLocation()[1];
                activityCategory = activity.getCategory();

                EditText titleEditText = (EditText) findViewById(R.id.createActivityTitleEditText);
                titleEditText.setText(activityTitle);

                EditText descriptionEditText = (EditText) findViewById(R.id.createActivityDescriptionEditText);
                descriptionEditText.setText(activityDescription);

                startDateTextView = (TextView) findViewById(R.id.createActivityStartDate);
                endDateTextView = (TextView) findViewById(R.id.createActivityEndDate);
                startTimeTextView = (TextView) findViewById(R.id.createActivityStartTime);
                endTimeTextView = (TextView) findViewById(R.id.createActivityEndTime);

                DateFormat dateFormat = getDateInstance();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                startDateTextView.setText(dateFormat.format(activityStartCalendar.getTime()));
                endDateTextView.setText(dateFormat.format(activityEndCalendar.getTime()));
                startTimeTextView.setText(timeFormat.format(activityStartCalendar.getTime()));
                endTimeTextView.setText(timeFormat.format(activityEndCalendar.getTime()));

                Button modifyButton = (Button) findViewById(R.id.createActivityValidateButton);
                modifyButton.setText(R.string.modifyButtonText);

                imagesNameList = activity.getImageList();

                if(imagesNameList != null) {
                    new ImageProvider().downloadImage(getApplicationContext(), eventId, imagesLayout, imagesNameList);
                }

            }
        }, eventId);
    }

}
