package ch.epfl.sweng.project;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;
/**
 * Created by olga on 19.10.16.
 */


public class DisplayActivityTest extends ActivityInstrumentationTestCase2<DisplayActivity> {
    public DisplayActivityTest() {
        super(DisplayActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testCanDisplayActivity() {
        getActivity();

        onView(withId(R.id.ap)).perform(click());
    }
}
