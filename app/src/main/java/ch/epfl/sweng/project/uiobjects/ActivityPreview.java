package ch.epfl.sweng.project.uiobjects;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.DeboxActivity;
import ch.epfl.sweng.project.R;

/**
 * Created by nathan on 07.10.16.
 */

public class ActivityPreview extends LinearLayout {

    DeboxActivity event;

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

        TextView titleView = new TextView(context);
        TextView previewtextView = new TextView(context);

        titleView.setText(event.getTitle());
        previewtextView.setText(event.getShortDescription());


        titleView.setTextColor(Color.BLACK);
        previewtextView.setTextColor(Color.BLACK);

        this.addView(titleView);
        this.addView(previewtextView);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.LTGRAY); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(5);
        gd.setStroke(2, 0xFF000000);
        this.setBackgroundDrawable(gd);

        this.setPadding(10,10,10,10);


    }

}

