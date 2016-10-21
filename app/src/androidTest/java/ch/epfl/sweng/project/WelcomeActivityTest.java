package ch.epfl.sweng.project;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
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

    public void testCanPrewiewActivityBeClicked() {
        getActivity();

        onData(allOf(instanceOf(ActivityPreview.class)))
                .perform(click());
        //onView(allOf(isAssignableFrom(ActivityPreview.class)))
          //      .perform(click());
    }
}