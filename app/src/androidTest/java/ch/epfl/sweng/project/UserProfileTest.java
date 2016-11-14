package ch.epfl.sweng.project;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ExpandableListView;
import android.widget.ListView;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.uiobjects.UserProfileExpandableListAdapter;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;

/**
 * Created by artem on 14/11/2016.
 */

public class UserProfileTest extends ActivityInstrumentationTestCase2<UserProfile>{
    public UserProfileTest(){
        super(UserProfile.class);
    }
    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }
    public void testGroupsOfEventsAreExist() {
         //Test works but in order to be compiled require fake database
        /*
        onData(anything())
                .inAdapterView(withId(R.id.userProfileActivityList))
                .atPosition(0)
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasDescendant(isDisplayed())));
        onData(anything())
                .inAdapterView(withId(R.id.userProfileActivityList))
                .atPosition(1)
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasDescendant(isDisplayed())));
        onData(anything())
                .inAdapterView(withId(R.id.userProfileActivityList))
                .atPosition(2)
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasDescendant(isDisplayed())));

        */
    }


}