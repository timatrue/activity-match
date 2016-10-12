package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by olga on 10.10.16.
 * This class displays the details of a certain event, that comes from the list of events shown in WelcomeActivity class.
 */

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super .onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String eventId = intent.getStringExtra(WelcomeActivity.EXTRA_EVENT_ID);
        DeboxActivity selectedEvent = DBProvider.getEventById(eventId);

        //display also schedule
        TextView title = (TextView) findViewById(R.id.eventTitle);
        title.setText(selectedEvent.getTitle());

        TextView description = (TextView) findViewById(R.id.eventDescription);
        description.setText(selectedEvent.getDescription());
    }

}
