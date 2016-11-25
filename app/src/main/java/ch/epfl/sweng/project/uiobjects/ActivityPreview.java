package ch.epfl.sweng.project.uiobjects;

import android.content.Context;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.DeboxActivity;
import ch.epfl.sweng.project.ImageProvider;
import ch.epfl.sweng.project.R;

import java.util.Calendar;
import java.text.DateFormat;
import java.util.List;
import java.util.Random;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static ch.epfl.sweng.project.R.id.imageView;
import static java.text.DateFormat.getDateInstance;


/**
 * Created by nathan on 07.10.16.
 */

public class ActivityPreview extends LinearLayout {

    private DeboxActivity event;
    private String eventTime;
    private Calendar timeStart;
    private DateFormat dateFormat;

    private TextView titleEvent;
    private TextView previewEvent;
    private TextView dateEvent;
    private TextView sizeEvent;

    private List<String> imagesList;
    private ImageView imageView;
    private String eventId;
    LinearLayout imagesLayout;

    public ActivityPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public String getEventId() {
        return event.getId();
    }

    public ActivityPreview(Context context, DeboxActivity event) {
        super(context);
        setOrientation(VERTICAL);

        this.event = event;
        dateFormat = getDateInstance();
        timeStart = event.getTimeStart();
        eventTime = dateFormat.format(timeStart.getTime());

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(R.layout.content_data_row, (ViewGroup) findViewById(R.id.activityPreviewsLayout));
        setEventPreview(event, childLayout, context);
        this.addView(childLayout);
    }

    private void setEventPreview(DeboxActivity event, View childLayout, Context context){
        titleEvent = (TextView) childLayout.findViewById(R.id.titleEvent);
        titleEvent.setText(event.getTitle());

        previewEvent = (TextView) childLayout.findViewById(R.id.previewEvent);
        previewEvent.setText(event.getShortDescription());

        dateEvent = (TextView) childLayout.findViewById(R.id.dateEvent);
        dateEvent.setText(eventTime);

        sizeEvent = (TextView) childLayout.findViewById(R.id.sizeEvent);
        sizeEvent.setText("Participants: " + event.getNbOfParticipants());

        imagesList = event.getImageList();
        imageView = (ImageView) childLayout.findViewById(R.id.activityImage);
        TextView imageTextImage = (TextView) childLayout.findViewById(R.id.activityTextImage);
        eventId = event.getId();

        if(imagesList != null){
            if(imagesList.size() > 0) {
                String image = imagesList.get(0);
                new ImageProvider().previewImage(context, eventId, childLayout, image);
            }

        } else {
            imageTextImage.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            String letter = event.getTitle().substring(0,1);
            imageTextImage.setText(letter);
            imageTextImage.setTextSize(getResources().getDimension(R.dimen.textsize));
        }
    }
    private int randomParticipants(){
        Random random = new Random();
        int number = random.nextInt(80 - 65) + 65;
        return number;
    }

}

