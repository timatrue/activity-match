package ch.epfl.sweng.project.uiobjects;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.epfl.sweng.project.R;

public class NoConnectionPreview extends LinearLayout {

    public NoConnectionPreview(Context context) {
        super(context);
        setOrientation(VERTICAL);

        TextView noResults = new TextView(context);
        noResults.setText(R.string.no_internet_connection);
        noResults.setTextColor(Color.BLACK);
        noResults.setTextSize(20);

        this.addView(noResults);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.WHITE); // Changes this drawbale to use a single color instead of a gradient
        this.setPadding(10,10,10,10);
    }
}