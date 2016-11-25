package ch.epfl.sweng.project.uiobjects;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.DeboxActivity;
import ch.epfl.sweng.project.ImageProvider;
import ch.epfl.sweng.project.R;

import java.util.List;
/**
 * Created by nathan on 07.10.16.
 */

public class ActivityPreview extends LinearLayout {

    private DeboxActivity event;

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

        TextView titleView = new TextView(context);
        TextView previewtextView = new TextView(context);
        titleView.setTextSize(22);

        titleView.setText(event.getTitle());
        previewtextView.setText(event.getShortDescription());


        titleView.setTextColor(Color.BLACK);
        previewtextView.setTextColor(Color.BLACK);

        this.addView(titleView);
        this.addView(previewtextView);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.rgb(0xCF, 0xD8, 0xDC)); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(10);
        gd.setStroke(2, 0xFF000000);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            this.setBackgroundDrawable(gd);
        } else {
            this.setBackground(gd);
        }

        this.setPadding(20, 20, 20, 20);
    }

}

