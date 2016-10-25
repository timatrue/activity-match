package ch.epfl.sweng.project;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static ch.epfl.sweng.project.R.id.activityPreviewsLayout;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;


import ch.epfl.sweng.project.uiobjects.ActivityPreview;
/**
 * Created by artem on 21/10/2016.
 */
public class WelcomeActivityTest extends ActivityInstrumentationTestCase2<WelcomeActivity> {
    public WelcomeActivityTest() {
        super(WelcomeActivity.class);

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

    }

    public void testCanPreviewActivityBeClicked() {
        WelcomeActivity wa = getActivity();

        //onView((wa.activityPreviewsLayout).getChildAt(0)).perform(click());
        /*onData(instanceOf(ActivityPreview.class))
                .perform(click());
        */
        onView(withId(R.id.testButton)).perform(click());

        /*int tv_id = ((LinearLayout )wa.activityPreviewsLayout).getChildAt(0).getId();
        onView(withId(tv_id)).perform(click());*/

        /*onData(allOf(instanceOf(ActivityPreview.class)))
                .perform(click());*/

    }
}