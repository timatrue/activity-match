package ch.epfl.sweng.project.uiobjects;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by nathan on 07.10.16.
 */

public class ActivityPreview extends LinearLayout {

    public ActivityPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public ActivityPreview(Context context, String title, String previewText) {
        super(context);
        setOrientation(VERTICAL);

        TextView titleView = new TextView(context);
        TextView previewtextView = new TextView(context);

        titleView.setText(title);
        previewtextView.setText(previewText);

        this.addView(titleView);
        this.addView(previewtextView);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFDDDDDD); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(5);
        gd.setStroke(2, 0xFF000000);
        this.setBackgroundDrawable(gd);

        this.setPadding(10,10,10,10);


    }

}

