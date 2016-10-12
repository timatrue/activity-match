package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by olga on 10.10.16.
 * This class displays the details of a certain event, that comes from the list of events shown in WelcomeActivity class.
 */

public class DisplayActivity extends AppCompatActivity {

    final public String DISPLAY_EVENT_ID = "ch.epfl.sweng.project.DisplayActivity.DISPLAY_EVENT_ID";

    TextView title;
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String eventId = intent.getStringExtra(DISPLAY_EVENT_ID);
        DeboxActivity selectedEvent = new DeboxActivity("1", "dummy", "football", "play", Calendar.getInstance(), Calendar.getInstance(), 20, 30, "Sports" );
        //DeboxActivity selectedEvent = DBProvider.getEventById(eventId);

        //display also schedule
        title = (TextView) findViewById(R.id.eventTitle);
        title.setText(selectedEvent.getTitle());

        description = (TextView) findViewById(R.id.eventDescription);
        description.setText(selectedEvent.getDescription());
    }

}
