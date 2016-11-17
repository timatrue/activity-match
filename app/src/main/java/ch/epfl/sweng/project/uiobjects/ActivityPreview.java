package ch.epfl.sweng.project.uiobjects;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.DeboxActivity;
import ch.epfl.sweng.project.R;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static java.text.DateFormat.getDateInstance;


/**
 * Created by nathan on 07.10.16.
 */

public class ActivityPreview extends LinearLayout {

    private DeboxActivity event;
    private String commaSpace;
    private String eventTime;
    private Calendar timeStart;
    private DateFormat dateFormat;
    private String title;

    private TextView titleEvent;
    private TextView previewEvent;
    private TextView dateEvent;
    private TextView sizeEvent;
    private int size;

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
        //commaSpace = getResources().getString(R.string.commaSpace);
        //title = eventTime + commaSpace + event.getTitle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(R.layout.content_data_row, (ViewGroup) findViewById(R.id.activityPreviewsLayout));
        setEventPreview(event, childLayout, context);
        this.addView(childLayout);
        /*
        TextView titleView = new TextView(context);
        TextView previewtextView = new TextView(context);
        TextView separator = new TextView(context);
        titleView.setTextSize(16);

        titleView.setText(title);
        previewtextView.setText(event.getShortDescription());

        titleView.setTextColor(ContextCompat.getColor(context, R.color.blueDark));
        previewtextView.setTextColor(ContextCompat.getColor(context, R.color.lightGrey));
        previewtextView.setPadding(0,0,0,8);

        separator.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey));
        separator.setHeight(1);


        this.addView(titleView);
        this.addView(previewtextView);
        this.addView(separator);
        */

    }

    private void setEventPreview(DeboxActivity event, View childLayout, Context context){
        titleEvent = (TextView) childLayout.findViewById(R.id.titleEvent);
        titleEvent.setText(event.getTitle());
        titleEvent.setTextColor(ContextCompat.getColor(context, R.color.blueDark));

        previewEvent = (TextView) childLayout.findViewById(R.id.previewEvent);
        previewEvent.setText(event.getShortDescription());
        previewEvent.setTextColor(ContextCompat.getColor(context, R.color.lightGrey));
        previewEvent.setPadding(0,0,0,8);

        dateEvent = (TextView) childLayout.findViewById(R.id.dateEvent);
        dateEvent.setText(eventTime);
        dateEvent.setTextColor(ContextCompat.getColor(context, R.color.darkGrey));

        sizeEvent = (TextView) childLayout.findViewById(R.id.sizeEvent);
        sizeEvent.setText("Participants: "+17);
        sizeEvent.setTextColor(ContextCompat.getColor(context, R.color.niceRed));
    }

}

