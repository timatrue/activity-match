package ch.epfl.sweng.project.uiobjects;

import android.content.Context;
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

    }

}

