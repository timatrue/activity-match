package ch.epfl.sweng.project.uiobjects;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.epfl.sweng.project.R;

/**
 * Created by artem on 27/10/2016.
 */

public class NoResultsPreview extends LinearLayout {

    public NoResultsPreview(Context context) {
        super(context);
        setOrientation(VERTICAL);

        TextView noResults = new TextView(context);
        noResults.setText(R.string.no_results_found);

        this.addView(noResults);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFDDDDDD); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(5);
        gd.setStroke(2, 0xFF000000);
        this.setBackgroundDrawable(gd);
        this.setPadding(10,10,10,10);

    }
}