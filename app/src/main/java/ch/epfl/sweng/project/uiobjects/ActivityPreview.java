package ch.epfl.sweng.project.uiobjects;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.DeboxActivity;
import ch.epfl.sweng.project.R;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
        commaSpace = getResources().getString(R.string.commaSpace);
        title = eventTime + commaSpace + event.getTitle();

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
        //separator.setPadding(5,2,5,0);

        this.addView(titleView);
        this.addView(previewtextView);
        this.addView(separator);
        //this.setPadding(0,2,0,0);
        /*GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.rgb(0xCF, 0xD8, 0xDC)); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(10);
        gd.setStroke(2, 0xFF000000);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            this.setBackgroundDrawable(gd);
        } else {
            this.setBackground(gd);
        }*/
        //left, top, right, bottom



    }

}

