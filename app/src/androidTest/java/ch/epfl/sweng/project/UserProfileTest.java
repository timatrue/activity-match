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
    private List<String> rankedEvents = Arrays.asList("id2");

    private User testUser = new User("id", "Bob", "email", organizedEvents,
            interestedEvents, rankedEvents, 4, 8, "slls");

    //The list of DeboxActivity designed for testing
    private List<DeboxActivity> deboxActivityList;

    //Returns the calendar that is 'nDays' days later than the input calendar
    private Calendar addDays(Calendar calendar, int nDays) {
        Calendar newCalendar = (Calendar) calendar.clone();
        newCalendar.add(Calendar.DATE, nDays);
        return newCalendar;
    }

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

    //Returns a list of designed DeboxActivity for testing
    private List<DeboxActivity> createDeboxActivityList() {

        final List<DeboxActivity> activityList = new ArrayList<>();

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
        deboxActivityList = createDeboxActivityList();
        mocDataProvider.setListOfActivitiesToMock(deboxActivityList);
        mocDataProvider.setUserToMock(testUser);
        activity.setDataProvider(dp);
        activity.activityCollection = new LinkedHashMap<>();
        activity.createCollection();
        activity.setExpListView();
    }


    @UiThreadTest
    @Test
    public void ListsCorrectDeboxActivitiesUnderCorrectLabels(){

        final UserProfile activity = userProfileRule.getActivity();
        initializeMockProvider(activity);


        assertThat((String) activity.nameTextView.getText(), is(testUser.getUsername()));

        List<DeboxActivity> orgEvents = activity.activityCollection.get(activity.organizedEvents);
        assertThat(orgEvents.size(), is(2));
        assertTrue(orgEvents.contains(dA2));
        assertTrue(orgEvents.contains(dA3));

        List<DeboxActivity> intEvents = activity.activityCollection.get(activity.interestedEvents);
        assertThat(intEvents.size(), is(2));
        assertTrue(intEvents.contains(dA4));
        assertTrue(intEvents.contains(dA5));

        List<DeboxActivity> parEvents = activity.activityCollection.get(activity.participatedEvents);
        assertThat(parEvents.size(), is(1));
        assertTrue(parEvents.contains(dA1));

    }


    @UiThreadTest
    @Test
    public void EmptyEventLists() {

        final UserProfile activity = userProfileRule.getActivity();

        final User newUser = new User("id", "Bob", "email", new ArrayList<String>(),
                new ArrayList<String>(), new ArrayList<String>(), 4, 8, "slls");

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.setListOfActivitiesToMock(deboxActivityList);
        mocDataProvider.setUserToMock(newUser);
        activity.setDataProvider(dp);
        activity.activityCollection = new LinkedHashMap<>();
        activity.createCollection();
        activity.setExpListView();

        List<DeboxActivity> orgEvents = activity.activityCollection.get(activity.organizedEvents);
        assertThat(orgEvents.size(), is(0));

        List<DeboxActivity> intEvents = activity.activityCollection.get(activity.interestedEvents);
        assertThat(intEvents.size(), is(0));

        List<DeboxActivity> parEvents = activity.activityCollection.get(activity.participatedEvents);
        assertThat(parEvents.size(), is(0));
    }
}
