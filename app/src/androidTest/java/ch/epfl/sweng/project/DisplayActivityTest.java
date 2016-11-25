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
import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.text.DateFormat.getDateInstance;
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
                    result.putExtra(DisplayActivity.DISPLAY_EVENT_ID, "zdkasKKLD");
                    return result;
                }
            };

    @UiThreadTest
    @Test
    public void DisplayActivityProperly() throws Exception {

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 1);
        endDate.add(Calendar.DATE, 11);

        final DeboxActivity dA1 = new DeboxActivity(
                "zdkasKKLD",
                "Nathan",
                "Football in UNIL sport center",
                "Indoor football tournaments open to every student " +
                        "of UNIL and EPFL, teams are formed 15 minutes before and tournament consists of 11 " +
                        "minutes games",
                startDate,
                endDate,
                122.01,
                121.0213,
                "Sports");

        final DeboxActivity dA2 = new DeboxActivity(
                "asdf",
                "Jeremie",
                "Handball in UNIL sport center", "Indoor Handball tournaments open to every student " +
                "of UNIL and EPFL, teams are formed 15 minutes before and tournament consists of 11 " +
                "minutes games",
                startDate,
                endDate,
                122.04,
                121.0243,
                "Sports");

        final DisplayActivity activity = displayActivityRule.getActivity();

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.addActivityToMock(dA1);
        mocDataProvider.addActivityToMock(dA2);

        activity.setTestDBObjects(dp, testFirebaseUser);

        activity.initDisplay(true);

        String categoryText = activity.getResources().getString(R.string.create_activity_category_text) + " " + dA1.getCategory();
        DateFormat dateFormat = getDateInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String scheduleText = dateFormat.format(dA1.getTimeStart().getTime()) +
                " at " + timeFormat.format(dA1.getTimeStart().getTime()) + " to " +
                dateFormat.format(dA1.getTimeEnd().getTime()) +
                " at " + timeFormat.format(dA1.getTimeEnd().getTime());

        assertThat(activity.title.getText().toString(), is(dA1.getTitle()));
        assertThat(activity.category.getText().toString(), is(categoryText));
        assertThat(activity.description.getText().toString(), is(dA1.getDescription()));
        assertThat(activity.schedule.getText().toString(), is(scheduleText));

        final String dA1OccupancyText;
        if (!(dA1.getNbMaxOfParticipants() == -1 && dA1.getNbOfParticipants() == -1)) {
            if (dA1.getNbMaxOfParticipants() >= 0) {
                dA1OccupancyText = "Occupancy : " + dA1.getNbOfParticipants() + " / " + dA1.getNbMaxOfParticipants();
            } else {
                dA1OccupancyText = "Occupancy : " + dA1.getNbOfParticipants();
            }
        } else {
            dA1OccupancyText = "Invalid information about occupancy";
        }
        assertThat(activity.occupancyTextView.getText().toString(), is(dA1OccupancyText));
    }
}
