package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ExpandableListView;
import android.widget.RatingBar;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.uiobjects.UserProfileExpandableListAdapter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


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

    private List<String> organizedEvents = Arrays.asList("id2", "id3", "id7");
    private List<String> interestedEvents = Arrays.asList("id1", "id4", "id5");
    private List<String> rankedEvents = Collections.singletonList("id6");

    private Map<String, String> createCommentsMap() {
        Map<String, String> commentsMap = new Hashtable<>();
        commentsMap.put("eventId", "id1");
        commentsMap.put("rating", "5");
        commentsMap.put("comment", "cool");
        return commentsMap;
    }
    private Map<String, String> commentsMap = createCommentsMap();

    private List<Map<String, String>> comments = Collections.singletonList(commentsMap);
    private User testUser = new User("Bob", "username", "email", organizedEvents,
            interestedEvents, rankedEvents, 4, 8, "slls", comments);

    //The list of DeboxActivity designed for testing
    private List<DeboxActivity> deboxActivityList;

    //Returns the calendar that is 'nDays' days later than the input calendar
    private Calendar addDays(Calendar calendar, int nDays) {
        Calendar newCalendar = (Calendar) calendar.clone();
        newCalendar.add(Calendar.DATE, nDays);
        return newCalendar;
    }

    private final DeboxActivity dA1 = new DeboxActivity(
            "id1",
            "Benoit",
            "da1",
            "Doing a nice walk",
            addDays(currentCalendar, -5),
            addDays(currentCalendar, -4),
            46.777245,
            6.642266,
            "Sports");

    private final DeboxActivity dA2 = new DeboxActivity(
            "id2",
            "Bob",
            "da2",
            "Doing a nice walk",
            addDays(currentCalendar, 2),
            addDays(currentCalendar, 4),
            46.777245,
            6.642266,
            "Culture");

    private final DeboxActivity dA3 = new DeboxActivity(
            "id3",
            "Bob",
            "da3",
            "Doing a nice walk",
            addDays(currentCalendar, 5),
            addDays(currentCalendar, 7),
            46.777245,
            6.642266,
            "Sport");

    private final DeboxActivity dA4 = new DeboxActivity(
            "id4",
            "Benoit",
            "da4",
            "Doing a nice walk",
            addDays(currentCalendar, 4),
            addDays(currentCalendar, 5),
            46.777245,
            6.642266,
            "Culture");

    private final DeboxActivity dA5 = new DeboxActivity(
            "id5",
            "Benoit",
            "da5",
            "Doing a nice walk",
            addDays(currentCalendar, 2),
            addDays(currentCalendar, 7),
            46.777245,
            5,
            "Culture");

    private final DeboxActivity dA6 = new DeboxActivity(
            "id6",
            "Benoit",
            "da6",
            "Doing a nice walk",
            addDays(currentCalendar, -2),
            addDays(currentCalendar, -1),
            46.777245,
            5,
            "Culture");

    private final DeboxActivity dA7 = new DeboxActivity(
            "id7",
            "Bob",
            "da7",
            "Doing a nice walk",
            addDays(currentCalendar, -2),
            addDays(currentCalendar, -1),
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
        activityList.add(dA6);
        activityList.add(dA7);


        return activityList;
    }

    private void initializeMockProvider(final UserProfile activity) {
        MockImageProvider mockImageProvider = new MockImageProvider();
        ImageProvider ip = mockImageProvider.getMockImageProvider();
        activity.setImageProvider(ip);

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
    public void EmptyEventLists() {

        final UserProfile activity = userProfileRule.getActivity();

        MockImageProvider mockImageProvider = new MockImageProvider();
        ImageProvider ip = mockImageProvider.getMockImageProvider();
        activity.setImageProvider(ip);

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        deboxActivityList = createDeboxActivityList();
        activity.setDataProvider(dp);
        activity.activityCollection = new LinkedHashMap<>();
        activity.createCollection();
        activity.setExpListView();

        final User newUser = new User("id", "Bob", "email", new ArrayList<String>(),
                new ArrayList<String>(), new ArrayList<String>(), 4, 8, "slls", new ArrayList<Map<String, String>>());
        mocDataProvider.setUserToMock(newUser);


        List<DeboxActivity> interestedEvents = activity.activityCollection.get(activity.interestedEvents);
        assertThat(interestedEvents.size(), is(0));

        List<DeboxActivity> participatedEvents = activity.activityCollection.get(activity.participatedEvents);
        assertThat(participatedEvents.size(), is(0));

        List<DeboxActivity> organizedEvents = activity.activityCollection.get(activity.organizedEvents);
        assertThat(organizedEvents.size(), is(0));

        List<DeboxActivity> pastOrganizedEvents = activity.activityCollection.get(activity.pastOrganizedEvents);
        assertThat(pastOrganizedEvents.size(), is(0));

        List<DeboxActivity> toRankEvents = activity.activityCollection.get(activity.toRankEvents);
        assertThat(toRankEvents.size(), is(0));
    }

    @UiThreadTest
    @Test
    public void ListsCorrectDeboxActivitiesUnderCorrectLabels(){

        final UserProfile activity = userProfileRule.getActivity();

        initializeMockProvider(activity);


        List<DeboxActivity> interestedEvents = activity.activityCollection.get(activity.interestedEvents);
        assertThat(interestedEvents.size(), is(2));
        assertTrue(interestedEvents.contains(dA4));
        assertTrue(interestedEvents.contains(dA5));

        List<DeboxActivity> participatedEvents = activity.activityCollection.get(activity.participatedEvents);
        assertThat(participatedEvents.size(), is(1));
        assertTrue(participatedEvents.contains(dA6));

        List<DeboxActivity> organizedEvents = activity.activityCollection.get(activity.organizedEvents);
        assertThat(organizedEvents.size(), is(2));
        assertTrue(organizedEvents.contains(dA2));
        assertTrue(organizedEvents.contains(dA3));

        List<DeboxActivity> pastOrganizedEvents = activity.activityCollection.get(activity.pastOrganizedEvents);
        assertThat(pastOrganizedEvents.size(), is(1));
        assertTrue(pastOrganizedEvents.contains(dA7));

        List<DeboxActivity> toRankEvents = activity.activityCollection.get(activity.toRankEvents);
        assertThat(toRankEvents.size(),is(1));
        assertTrue(toRankEvents.contains(dA1));

        ExpandableListView expListView = activity.expListView;
        UserProfileExpandableListAdapter upexla = (UserProfileExpandableListAdapter) expListView.getExpandableListAdapter();
        assertTrue(upexla.getChild(0,0) == dA7);
        assertTrue(upexla.getChild(1,0) == dA2);
        assertTrue(upexla.getChild(1,1) == dA3);
        assertTrue(upexla.getChild(2,0) == dA6);
        assertTrue(upexla.getChild(3,0) == dA1);
        assertTrue(upexla.getChild(4,0) == dA4);
        assertTrue(upexla.getChild(4,1) == dA5);
    }

    @UiThreadTest
    @Test
    public void RatingTest() {

        final UserProfile activity = userProfileRule.getActivity();

        initializeMockProvider(activity);

        RatingBar userRank;
        userRank = (RatingBar) activity.findViewById(R.id.userRank);

        double testRank = testUser.getRating();
        double displayedRank = ((double) userRank.getProgress()/(double) userRank.getMax()) * 5;

        assertThat(displayedRank, is(testRank));
    }
}
