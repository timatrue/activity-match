package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.text.DateFormat.getDateInstance;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class DisplayActivityTest {


    @Mock
    FirebaseUser testFirebaseUser;

    @Rule
    public ActivityTestRule<DisplayActivity> displayActivityRule =
            new ActivityTestRule<DisplayActivity>(DisplayActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, DisplayActivity.class);
                    result.putExtra(DisplayActivity.DISPLAY_ACTIVITY_TEST_KEY, DisplayActivity.DISPLAY_ACTIVITY_TEST);
                    result.putExtra(DisplayActivity.DISPLAY_EVENT_ID, "id1");
                    return result;
                }
            };

    private Calendar currentCalendar = Calendar.getInstance();

    //Returns the calendar that is 'nDays' days later than the input calendar
    private Calendar addDays(Calendar calendar, int nDays) {
        Calendar newCalendar = (Calendar) calendar.clone();
        newCalendar.add(Calendar.DATE, nDays);
        return newCalendar;
    }


    @UiThreadTest
    @Test
    public void DisplayActivityProperly() throws Exception {

        final DisplayActivity activity = displayActivityRule.getActivity();

        final List<String> imageList = new ArrayList<>();
        imageList.add("image1");
        imageList.add("image2");

        final DeboxActivity dA1 = new DeboxActivity(
                "id1",
                "Benoit",
                "da1",
                "Doing a nice walk",
                addDays(currentCalendar, 13),
                addDays(currentCalendar, 14),
                46.777245,
                6.642266,
                "Sports",
                imageList,
                10,
                20);

        final DeboxActivity dA2 = new DeboxActivity(
                "id2",
                "Benoit",
                "da2",
                "Doing a nice walk",
                addDays(currentCalendar, 2),
                addDays(currentCalendar, 4),
                46.777245,
                6.642266,
                "Culture");

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.addActivityToMock(dA1);
        mocDataProvider.addActivityToMock(dA2);
        MockImageProvider mocImageProvider = new MockImageProvider();
        ImageProvider ip = mocImageProvider.getMockImageProvider();
        activity.setTestDBObjects(dp, testFirebaseUser, ip);

        activity.initDisplay(true);

        String categoryText = activity.getString(R.string.create_activity_category_text, dA1.getCategory());
        DateFormat dateFormat = getDateInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String stringScheduleStarts = dateFormat.format(dA1.getTimeStart().getTime()) +
                " at " + timeFormat.format(dA1.getTimeStart().getTime());
        String stringScheduleEnds = dateFormat.format(dA1.getTimeEnd().getTime()) +
                " at " + timeFormat.format(dA1.getTimeEnd().getTime());
        String scheduleStartsText = activity.getString(R.string.timeStart, stringScheduleStarts);
        String scheduleEndsText = activity.getString(R.string.timeEnd, stringScheduleEnds);

        assertThat(activity.title.getText().toString(), is(dA1.getTitle()));
        assertThat(activity.category.getText().toString(), is(categoryText));
        assertThat(activity.description.getText().toString(), is(dA1.getDescription()));
        assertThat(activity.scheduleStarts.getText().toString(), is(scheduleStartsText));
        assertThat(activity.scheduleEnds.getText().toString(), is(scheduleEndsText));
        assertThat(activity.activityToDisplay.getLocation()[1], is(dA1.getLocation()[1]));
        assertThat(activity.activityToDisplay.getLocation()[0], is(dA1.getLocation()[0]));
        //assertThat(activity.map, is(not(nullValue())));
        assertThat(activity.eventLocation, is(not(nullValue())));



        final String dA1OccupancyText;
        final int nbParticipants = dA1.getNbOfParticipants();
        final int nbMaxParticipants = dA1.getNbMaxOfParticipants();

        dA1OccupancyText = activity.getString(R.string.occupancy_with_max, nbParticipants, nbMaxParticipants);
        assertThat(activity.occupancyTextView.getText().toString(), is(dA1OccupancyText));

        assertThat(activity.imagesLayout.getChildCount(), is(imageList.size()));
    }

    @UiThreadTest
    @Test
    public void noMaxParticipantsTest() throws Exception {

        final DisplayActivity activity = displayActivityRule.getActivity();

        final DeboxActivity dA1 = new DeboxActivity(
                "id1",
                "Benoit",
                "da1",
                "Doing a nice walk",
                addDays(currentCalendar, 13),
                addDays(currentCalendar, 14),
                46.777245,
                6.642266,
                "Sports",
                10,
                -1);

        final DeboxActivity dA2 = new DeboxActivity(
                "id2",
                "Benoit",
                "da2",
                "Doing a nice walk",
                addDays(currentCalendar, 2),
                addDays(currentCalendar, 4),
                46.777245,
                6.642266,
                "Culture");

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.addActivityToMock(dA1);
        mocDataProvider.addActivityToMock(dA2);
        MockImageProvider mocImageProvider = new MockImageProvider();
        ImageProvider ip = mocImageProvider.getMockImageProvider();
        activity.setTestDBObjects(dp, testFirebaseUser, ip);

        activity.initDisplay(true);

        final String dA1OccupancyText;
        final int nbParticipants = dA1.getNbOfParticipants();

        dA1OccupancyText = activity.getString(R.string.occupancy, nbParticipants);
        assertThat(activity.occupancyTextView.getText().toString(), is(dA1OccupancyText));
    }

    @UiThreadTest
    @Test
    public void invalidNumberOfParticipantsTest() throws Exception {

        final DisplayActivity activity = displayActivityRule.getActivity();

        final DeboxActivity dA1 = new DeboxActivity(
                "id1",
                "Benoit",
                "da1",
                "Doing a nice walk",
                addDays(currentCalendar, 13),
                addDays(currentCalendar, 14),
                46.777245,
                6.642266,
                "Sports",
                -1,
                20);

        final DeboxActivity dA2 = new DeboxActivity(
                "id2",
                "Benoit",
                "da2",
                "Doing a nice walk",
                addDays(currentCalendar, 2),
                addDays(currentCalendar, 4),
                46.777245,
                6.642266,
                "Culture");

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.addActivityToMock(dA1);
        mocDataProvider.addActivityToMock(dA2);
        MockImageProvider mocImageProvider = new MockImageProvider();
        ImageProvider ip = mocImageProvider.getMockImageProvider();
        activity.setTestDBObjects(dp, testFirebaseUser, ip);

        activity.initDisplay(true);

        final String dA1OccupancyText;

        dA1OccupancyText = activity.getString(R.string.invalid_occupancy);
        assertThat(activity.occupancyTextView.getText().toString(), is(dA1OccupancyText));

    }
    /*
    @UiThreadTest
    @Test
    public void DisplayActivityUI() throws Exception {

        List<String> organizedEvents = Arrays.asList("id2", "id3");
        List<String> interestedEvents = Arrays.asList("id1", "id4", "id5");
        List<String> rankedEvents = Arrays.asList("id2");

        User testUser = new User("Bob", "username", "email", organizedEvents,
                interestedEvents, rankedEvents, 4, 8, "slls");

        final DisplayActivity activity = displayActivityRule.getActivity();

        final List<String> imageList = new ArrayList<>();
        imageList.add("image1");
        imageList.add("image2");

        final DeboxActivity dA1 = new DeboxActivity(
                "id1",
                "Benoit",
                "da1",
                "Doing a nice walk",
                addDays(currentCalendar, 13),
                addDays(currentCalendar, 14),
                46.777245,
                6.642266,
                "Sports",
                imageList,
                10,
                20);

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.addActivityToMock(dA1);
        mocDataProvider.setUserToMock(testUser);

        MockImageProvider mocImageProvider = new MockImageProvider();
        ImageProvider ip = mocImageProvider.getMockImageProvider();
        activity.setTestDBObjects(dp, testFirebaseUser, ip);



        activity.initDisplay(false);


        onView(withId(R.id.location)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.userSignture)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.scrollLayoutDisplayActivity)).perform(scrollTo());


    }*/
    /*
    @UiThreadTest
    @Test
    public void joinTest() throws Exception {

        final DisplayActivity activity = displayActivityRule.getActivity();

        final DeboxActivity dA1 = new DeboxActivity(
                "id1",
                "Benoit",
                "da1",
                "Doing a nice walk",
                addDays(currentCalendar, 13),
                addDays(currentCalendar, 14),
                46.777245,
                6.642266,
                "Sports",
                10,
                20);

        final DeboxActivity dA2 = new DeboxActivity(
                "id2",
                "Benoit",
                "da2",
                "Doing a nice walk",
                addDays(currentCalendar, 2),
                addDays(currentCalendar, 4),
                46.777245,
                6.642266,
                "Culture");

        List<String> organizedEvents = Arrays.asList("id2", "id3");
        List<String> interestedEvents = Arrays.asList("id4", "id5");
        List<String> rankedEvents = Arrays.asList("id2");



        User testUser = new User("Bob", "username", "email", organizedEvents,
                interestedEvents, rankedEvents, 4, 8, "slls");

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.addActivityToMock(dA1);
        mocDataProvider.addActivityToMock(dA2);
        mocDataProvider.setUserToMock(testUser);
        MockImageProvider mocImageProvider = new MockImageProvider();
        ImageProvider ip = mocImageProvider.getMockImageProvider();
        activity.setTestDBObjects(dp, testFirebaseUser,ip);
        activity.initDisplay(true);

        onView(withId(R.id.joinActivity)).check(matches(isDisplayed())).perform(click());
    }
    */
}
