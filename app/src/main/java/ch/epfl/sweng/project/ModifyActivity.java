package ch.epfl.sweng.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static java.text.DateFormat.getDateInstance;


public class ModifyActivity extends CreateActivity {

    final static public String MODIFY_ACTIVITY_EVENT_ID = "ch.epfl.sweng.project.ModifyActivity.MODIFY_ACTIVITY_EVENT_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        creation = false;

        TextView modifyActivityTitle = (TextView) findViewById(R.id.createActivityTitle);
        modifyActivityTitle.setText(R.string.modify_activity_title);
        Button modifyActivityLocation = (Button) findViewById(R.id.createActivityLocation);
        modifyActivityLocation.setText(R.string.modify_activity_set_location);
        Button modifyActivityButton = (Button) findViewById(R.id.createActivityValidateButton);
        modifyActivityButton.setText(R.string.modify_activity_modify_activity_button);

        Bundle bundle = getIntent().getExtras();
        String test = bundle.getString(CREATE_ACTIVITY_TEST_KEY);
        if(test != null) {
            if (test.equals(CREATE_ACTIVITY_NO_TEST)) {
                setDataProvider(new DataProvider());
                setImageProvider(new ImageProvider());
                getDeboxActivityAndDisplay();
            }
        }
    }

    void getDeboxActivityAndDisplay(){

        final String eventId = getIntent().getStringExtra(MODIFY_ACTIVITY_EVENT_ID);

        mDataProvider.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
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
                initialNumberParticipants = activity.getNbOfParticipants();
                activityMaxNbParticipants = activity.getNbMaxOfParticipants();

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

                EditText maxNbParticipantsEditText = (EditText) findViewById(R.id.createActivityMaxNbParticipantsEditText);
                maxNbParticipantsEditText.setText(String.format(Locale.US, "%1$d", activityMaxNbParticipants));

                Button modifyButton = (Button) findViewById(R.id.createActivityValidateButton);
                modifyButton.setText(R.string.modifyButtonText);

                imagesNameList = activity.getImageList();

                if(imagesNameList != null) {
                    ((Button) findViewById(R.id.createActivityUploadImage)).setText(R.string.modify_add_images);
                    mImageProvider.downloadImage(getApplicationContext(), eventId, imagesLayout, imagesNameList);
                }
            }
        }, eventId);
    }

}
