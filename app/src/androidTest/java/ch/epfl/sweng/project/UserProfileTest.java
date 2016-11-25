package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class UserProfileTest {
    @Rule
    public ActivityTestRule<UserProfile> userProfileRule =
            new ActivityTestRule<UserProfile>(UserProfile.class){
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, UserProfile.class);
                    result.putExtra(UserProfile.USER_PROFILE_TEST_KEY, UserProfile.USER_PROFILE_TEST);
                    return result;
                }
            };

    private Calendar currentCalendar = Calendar.getInstance();

    private List<String> organizedEvents = Arrays.asList("id2", "id3");
    private List<String> interestedEvents = Arrays.asList("id1", "id4", "id5");

    private User testUser = new User("Bob", "username", "email", organizedEvents,
            interestedEvents, 4, 8, "slls");

    //The list of DeboxActivity designed for testing
    private final List<DeboxActivity> deboxActivityList = createDeboxActivityList();

    //Returns the calendar that is 'nDays' days later than the input calendar
    private Calendar addDays(Calendar calendar, int nDays) {
        Calendar newCalendar = (Calendar) calendar.clone();
        newCalendar.add(Calendar.DATE, nDays);
        return newCalendar;
    }

    //Returns a list of designed DeboxActivity for testing
    private List<DeboxActivity> createDeboxActivityList() {

        final List<DeboxActivity> activityList = new ArrayList<>();

        final DeboxActivity dA1 = new DeboxActivity(
                "id1",
                "Benoit",
                "da1",
                "Doing a nice walk",
                addDays(currentCalendar, -5),
                addDays(currentCalendar, -4),
                46.777245,
                6.642266,
                "Sports");

        final DeboxActivity dA2 = new DeboxActivity(
                "id2",
                "Bob",
                "da2",
                "Doing a nice walk",
                addDays(currentCalendar, 2),
                addDays(currentCalendar, 4),
                46.777245,
                6.642266,
                "Culture");

        final DeboxActivity dA3 = new DeboxActivity(
                "id3",
                "Bob",
                "da3",
                "Doing a nice walk",
                addDays(currentCalendar, 5),
                addDays(currentCalendar, 7),
                46.777245,
                6.642266,
                "Sport");

        final DeboxActivity dA4 = new DeboxActivity(
                "id4",
                "Benoit",
                "da4",
                "Doing a nice walk",
                addDays(currentCalendar, 4),
                addDays(currentCalendar, 5),
                46.777245,
                6.642266,
                "Culture");

        final DeboxActivity dA5 = new DeboxActivity(
                "id5",
                "Benoit",
                "da5",
                "Doing a nice walk",
                addDays(currentCalendar, 2),
                addDays(currentCalendar, 7),
                46.777245,
                5,
                "Culture");

        activityList.add(dA1);
        activityList.add(dA2);
        activityList.add(dA3);
        activityList.add(dA4);
        activityList.add(dA5);

        return activityList;
    }

    private void initializeMockProvider(final UserProfile activity) {
        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.setListOfActivitiesToMock(deboxActivityList);
        mocDataProvider.setUserToMock(testUser);
        activity.setDataProvider(dp);
        activity.activityCollection = new LinkedHashMap<String, List<String>>();
        activity.createCollection();
        activity.setExpListView();
    }


    @UiThreadTest
    @Test
    public void ListsCorrectDeboxActivitiesUnderCorrectLabels(){

        final UserProfile activity = userProfileRule.getActivity();
        initializeMockProvider(activity);

       // assertThat((String) activity.emailTextView.getText(), is(testUser.getEmail()));

        List<String> orgEvents = activity.activityCollection.get(activity.organizedEvents);
        assertThat(orgEvents.size(), is(2));
        assertTrue(orgEvents.contains("da2"));
        assertTrue(orgEvents.contains("da3"));

        List<String> intEvents = activity.activityCollection.get(activity.interestedEvents);
        assertThat(intEvents.size(), is(2));
        assertTrue(intEvents.contains("da4"));
        assertTrue(intEvents.contains("da5"));

        List<String> parEvents = activity.activityCollection.get(activity.participatedEvents);
        assertThat(parEvents.size(), is(1));
        assertTrue(parEvents.contains("da1"));

        /*ViewInteraction textView = onView(
                allOf(withId(R.id.userProfileActivityChild), withText("da2"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.userProfileActivityList),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("da2")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.userProfileActivityChild), withText("Festival Balelec"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.userProfileActivityList),
                                        3),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("Festival Balelec")));

        ViewInteraction relativeLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.userProfileActivityList),
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1)),
                        5),
                        isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));

        ViewInteraction relativeLayout2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.userProfileActivityList),
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1)),
                        6),
                        isDisplayed()));
        relativeLayout2.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.userProfileActivityChild), withText("Hockey"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.userProfileActivityList),
                                        6),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("Hockey")));

        ViewInteraction relativeLayout3 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.userProfileActivityList),
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1)),
                        4),
                        isDisplayed()));
        relativeLayout3.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
    final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };*/
    }

    @UiThreadTest
    @Test
    public void EmptyEventLists() {

        final UserProfile activity = userProfileRule.getActivity();
        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.setListOfActivitiesToMock(deboxActivityList);
        activity.setDataProvider(dp);
        activity.activityCollection = new LinkedHashMap<String, List<String>>();
        activity.createCollection();
        activity.setExpListView();


//        assertThat((String) activity.emailTextView.getText(), is("def_email"));

        List<String> orgEvents = activity.activityCollection.get(activity.organizedEvents);
        assertThat(orgEvents.size(), is(1));
        assertTrue(orgEvents.contains("No Events"));

        List<String> intEvents = activity.activityCollection.get(activity.interestedEvents);
        assertThat(intEvents.size(), is(1));
        assertTrue(intEvents.contains("No Events"));

        List<String> parEvents = activity.activityCollection.get(activity.participatedEvents);
        assertThat(parEvents.size(), is(1));
        assertTrue(parEvents.contains("No Events"));
    }
}
