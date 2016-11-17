package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import android.widget.DatePicker;

import android.widget.TextView;
import android.widget.TimePicker;


import org.hamcrest.Matchers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.epfl.sweng.project.uiobjects.ActivityPreview;


import static org.junit.Assert.assertEquals;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;


// TODO For now all test on this class are suspended ! Waiting on issue #54 to fixe and improve new features test

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {

    @Rule
    public ActivityTestRule<WelcomeActivity> welcomeActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class){
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, WelcomeActivity.class);
                    result.putExtra(WelcomeActivity.WELCOME_ACTIVITY_TEST_KEY, WelcomeActivity.WELCOME_ACTIVITY_TEST);
                    return result;
                }
            };

    private Calendar currentCalendar = Calendar.getInstance();

    private final DeboxActivity dA1 = new DeboxActivity(
            "id1",
            "Benoit",
            "da1",
            "Doing a nice walk",
            addDays(currentCalendar, 13),
            addDays(currentCalendar, 14),
            46.777245,
            6.642266,
            "Sports");

    private final DeboxActivity dA2 = new DeboxActivity(
            "id2",
            "Benoit",
            "da2",
            "Doing a nice walk",
            addDays(currentCalendar, 2),
            addDays(currentCalendar, 4),
            46.777245,
            6.642266,
            "Culture");

    private final DeboxActivity dA3 = new DeboxActivity(
            "id3",
            "Benoit",
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

    private Calendar addDays(Calendar calendar, int days) {
        Calendar newCalendar = (Calendar) calendar.clone();
        newCalendar.add(Calendar.DATE, days);
        return newCalendar;
    }

    private void addDeboxActivities(List <DeboxActivity> list) {
        list.add(dA1);
        list.add(dA2);
        list.add(dA3);
        list.add(dA4);
        list.add(dA5);
    }




    @Test
    public void dummyTest(){
        assertEquals(true,true);
    }

    /*
    /*

    //UI thread test because we need to access UI elements (Textviews, etc...)
    @UiThreadTest
    @Test
    public void DisplayActivitiesProperly() throws Exception {

        final List<DeboxActivity> testActivityList = new ArrayList<>();
        addDeboxActivities(testActivityList);

        DataProvider testDataProvider = mock(DataProvider.class);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivities listener = (DataProvider.DataProviderListenerActivities) args[0];
                listener.getActivities(testActivityList);
                return null;
            }
        }).when(testDataProvider).getAllActivities(any(DataProvider.DataProviderListenerActivities.class));


        final WelcomeActivity activity = welcomeActivityRule.getActivity();
        //Insert Mock DataProvider
        activity.setDataProvider(testDataProvider);

        //Press on the "Display Events" button
        activity.displayActivities.performClick();

        //Check that the five events are displayed
        final int activityCount = activity.activityPreviewsLayout.getChildCount();
        assertThat(activityCount, is(testActivityList.size()));

        //Check that they are ActivityPreview
        for(int i=0; i<activityCount; i++) {
            assertTrue(activity.activityPreviewsLayout.getChildAt(i) instanceof ActivityPreview);
        }

        //Check that they have the same ID has the test DeboxActivity passed by the Mock
        for(int i=0; i<activityCount; i++) {
            assertTrue(((ActivityPreview) activity.activityPreviewsLayout.getChildAt(i)).getEventId().equals(testActivityList.get(i).getId()));
        }

        //Check that the title and short description of the ActivityPreviews corresponds to our two test DeboxActivities
        for(int i=0; i<activityCount; i++) {
            assertTrue(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(i)).getChildAt(0)).getText().toString().equals(testActivityList.get(i).getTitle()));
            assertTrue(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(i)).getChildAt(1)).getText().toString().equals(testActivityList.get(i).getShortDescription()));
        }
    }/*

    @Test
    public void distanceFromCenterIsCalculatedProperly() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        activity.centerLongitude = 6.631790;
        activity.centerLatitude = 46.519556;

        double distance = activity.distanceFromCenter(dA1);

        //Calculated distance is 28.5 and real distance is 28.7
        assertTrue(distance > 28 && distance < 29);

    }

    @Test
    public void maxDistanceMapTest() throws Exception {

        assertThat(WelcomeActivity.maxDistanceMap.get("10 km"), is(10));
        assertThat(WelcomeActivity.maxDistanceMap.get("All"), is(21000));
        assertTrue(WelcomeActivity.maxDistanceMap.get("") == null);
    }

    @UiThreadTest
    @Test
    public void maxDistanceFilterTest() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        final List<DeboxActivity> testActivityList = new ArrayList<>();
        addDeboxActivities(testActivityList);

        DataProvider testDataProvider = mock(DataProvider.class);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivities listener = (DataProvider.DataProviderListenerActivities) args[0];
                listener.getActivities(testActivityList);
                return null;
            }
        }).when(testDataProvider).getAllActivities(any(DataProvider.DataProviderListenerActivities.class));

        //Insert Mock DataProvider
        activity.setDataProvider(testDataProvider);

        // Sets the parameters of the WelcomeActivity that are usually set by the user and are required
        // in the displaySpecifiedActivities() function
        Calendar startCalendar = currentCalendar;
        Calendar endCalendar = addDays(currentCalendar,30);
        activity.filterCategory = "All";
        activity.maxDistanceString = "50 km";
        activity.centerLongitude = 6.642266;
        activity.centerLatitude = 46.777245;
        activity.filterStartCalendar = startCalendar;
        activity.filterEndCalendar = endCalendar;

        //Call the method that filters and displays the activities
        activity.displaySpecifiedActivities();

        //Checks that the number of filtered events is correct
        final int activityCount = activity.activityPreviewsLayout.getChildCount();
        assertThat(activityCount, is(4));

        //Checks that the DeboxActivities titles correspond to the ones that should have been selected
        int displayedActivityCount = 0;
        for(int i=0; i<testActivityList.size(); i++){
            if(activity.distanceFromCenter(testActivityList.get(i)) <= WelcomeActivity.maxDistanceMap.get(activity.maxDistanceString)) {
                assertThat(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(displayedActivityCount)).getChildAt(0)).getText().toString(), is(testActivityList.get(i).getTitle()));
                displayedActivityCount += 1;
            }
        }
    }*/

    /*

    @UiThreadTest
    @Test
    public void changingCenterLocationTest() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        final List<DeboxActivity> testActivityList = new ArrayList<>();
        addDeboxActivities(testActivityList);

        DataProvider testDataProvider = mock(DataProvider.class);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivities listener = (DataProvider.DataProviderListenerActivities) args[0];
                listener.getActivities(testActivityList);
                return null;
            }
        }).when(testDataProvider).getAllActivities(any(DataProvider.DataProviderListenerActivities.class));

        //Insert Mock DataProvider
        activity.setDataProvider(testDataProvider);

        // Sets the parameters of the WelcomeActivity that are usually set by the user and are required
        // in the displaySpecifiedActivities() function
        Calendar startCalendar = currentCalendar;
        Calendar endCalendar = addDays(currentCalendar,30);
        activity.filterCategory = "All";
        activity.maxDistanceString = "5 km";
        activity.centerLongitude = 5;
        activity.centerLatitude = 46.775;
        activity.filterStartCalendar = startCalendar;
        activity.filterEndCalendar = endCalendar;

        //Calls the method that filters and displays the activities
        activity.displaySpecifiedActivities();

        //Checks that the number of filtered events is correct
        final int activityCount = activity.activityPreviewsLayout.getChildCount();
        assertThat(activityCount, is(1));

        //Checks that the DeboxActivities titles correspond to the ones that should have been selected
        int displayedActivityCount = 0;
        for(int i=0; i<testActivityList.size(); i++){
            if(activity.distanceFromCenter(testActivityList.get(i)) <= WelcomeActivity.maxDistanceMap.get(activity.maxDistanceString)) {
                assertThat(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(displayedActivityCount)).getChildAt(0)).getText().toString(), is(testActivityList.get(i).getTitle()));
                displayedActivityCount += 1;
            }
        }
    }

    @UiThreadTest
    @Test
    public void CategoryFilterTest() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        final List<DeboxActivity> testActivityList = new ArrayList<>();
        addDeboxActivities(testActivityList);

        String filterCategoryTest = "Culture";

        final List<DeboxActivity> filteredTestActivityList = new ArrayList<>();
        for(DeboxActivity da : testActivityList){
            if(da.getCategory().equals(filterCategoryTest)){
                filteredTestActivityList.add(da);
            }
        }
        
        DataProvider testDataProvider = mock(DataProvider.class);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerCategory listener = (DataProvider.DataProviderListenerCategory) args[0];
                listener.getCategory(filteredTestActivityList);
                return null;
            }
        }).when(testDataProvider).getSpecifiedCategory(any(DataProvider.DataProviderListenerCategory.class), anyString());

        //Insert Mock DataProvider
        activity.setDataProvider(testDataProvider);

        // Sets the parameters of the WelcomeActivity that are usually set by the user and are required
        // in the displaySpecifiedActivities() function
        Calendar startCalendar = currentCalendar;
        Calendar endCalendar = addDays(currentCalendar,30);
        activity.filterCategory = filterCategoryTest;
        activity.maxDistanceString = "All";
        activity.centerLongitude = 6.642266;
        activity.centerLatitude = 46.777245;
        activity.filterStartCalendar = startCalendar;
        activity.filterEndCalendar = endCalendar;

        //Calls the method that filters and displays the activities
        activity.displaySpecifiedActivities();

        //Checks that the number of filtered events is correct
        final int activityCount = activity.activityPreviewsLayout.getChildCount();
        assertThat(activityCount, is(3));

        //Checks that the DeboxActivities titles correspond to the ones that should have been selected
        int displayedActivityCount = 0;
        for(int i=0; i<testActivityList.size(); i++){
            if(testActivityList.get(i).getCategory().equals(filterCategoryTest)) {
                assertThat(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(displayedActivityCount)).getChildAt(0)).getText().toString(), is(testActivityList.get(i).getTitle()));
                displayedActivityCount += 1;
            }
        }
    }


    @Test
    public void tmpTest(){

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();

        final DeboxActivity dbat = new DeboxActivity("-","test", "user-test",
                "description",
                Calendar.getInstance(),
                Calendar.getInstance(),
                122.01,
                121.0213,
                "Sports");

        assertEquals(dp.pushActivity(dbat),dbat.getId());

        final List<DeboxActivity> testActivityList = new ArrayList<DeboxActivity>();

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        final DeboxActivity dA1 = new DeboxActivity("zdkasKKLD", "Nathan",
                "Football in UNIL sport center", "Indoor football tournaments open to every student " +
                "of UNIL and EPFL, teams are formed 15 minutes before and tournament consists of 11 " +
                "minutes games",
                startDate,
                endDate,
                122.01,
                121.0213,
                "Culture");
        final DeboxActivity dA2 = new DeboxActivity("asdf", "Jeremie",
                "Handball in UNIL sport center", "Indoor Handball tournaments open to every student " +
                "of UNIL and EPFL, teams are formed 15 minutes before and tournament consists of 11 " +
                "minutes games",
                startDate,
                endDate,
                122.04,
                121.0243,
                "Sports");

        testActivityList.add(dA1);
        testActivityList.add(dA2);

        mocDataProvider.setListOfActivitiesToMock(testActivityList);

        List<DataProvider.CategoryName> listCategoryStored = new ArrayList<>();
        listCategoryStored.add(new DataProvider.CategoryName("1","Sports"));
        listCategoryStored.add(new DataProvider.CategoryName("1","Culture"));
        mocDataProvider.setListOfCategoryToMock(listCategoryStored);

        final String cat = "Sports";

        dp.getSpecifiedCategory(new DataProvider.DataProviderListenerCategory() {

            @Override
            public void getCategory(List<DeboxActivity> activitiesList) {
                for(DeboxActivity elem: activitiesList) {
                    assertEquals(elem.getCategory(),cat);
                }
            }
        },cat);

        List<String> enrolledList = new ArrayList<>();
        enrolledList.add("sample");
        enrolledList.add(dA1.getId());
        enrolledList.add("sampl2e");

        mocDataProvider.setListOfEnrolledActivityToMock(enrolledList);

        dp.userEnrolledInActivity(new DataProvider.DataProviderListenerEnrolled() {
            @Override
            public void getIfEnrolled(boolean result) {
                assertEquals(result,true);
            }
        }, dA1.getId());

        dp.userEnrolledInActivity(new DataProvider.DataProviderListenerEnrolled() {
            @Override
            public void getIfEnrolled(boolean result) {
                assertEquals(result,false);
            }
        },"noid");



    }

    @UiThreadTest
    @Test
    public void dpMockDisplayActivitiesProperly() throws Exception {

        final List<DeboxActivity> testActivityList = new ArrayList<DeboxActivity>();

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        final DeboxActivity dA1 = new DeboxActivity("zdkasKKLD", "Nathan",
                "Football in UNIL sport center", "Indoor football tournaments open to every student " +
                "of UNIL and EPFL, teams are formed 15 minutes before and tournament consists of 11 " +
                "minutes games",
                startDate,
                endDate,
                122.01,
                121.0213,
                "Sports");
        final DeboxActivity dA2 = new DeboxActivity("asdf", "Jeremie",
                "Handball in UNIL sport center", "Indoor Handball tournaments open to every student " +
                "of UNIL and EPFL, teams are formed 15 minutes before and tournament consists of 11 " +
                "minutes games",
                startDate,
                endDate,
                122.04,
                121.0243,
                "Sports");

        testActivityList.add(dA1);
        testActivityList.add(dA2);

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();

        mocDataProvider.setListOfActivitiesToMock(testActivityList);

        final WelcomeActivity activity = welcomeActivityRule.getActivity();
        //Insert the moc DataProvider
        activity.setDataProvider(dp);

        //Press on the "Display Events" button
        activity.displayActivities.performClick();

        //Check that the two events are displayed
        assertTrue(activity.activityPreviewsLayout.getChildCount() == 2);
        //Check that they are ActivityPreview
        assertTrue(activity.activityPreviewsLayout.getChildAt(0) instanceof ActivityPreview);
        assertTrue(activity.activityPreviewsLayout.getChildAt(1) instanceof ActivityPreview);
        //Check that they have the same ID has the test DeboxActivity passed by the Mock
        assertTrue(((ActivityPreview) activity.activityPreviewsLayout.getChildAt(0)).getEventId().equals(dA1.getId())) ;
        assertTrue(((ActivityPreview) activity.activityPreviewsLayout.getChildAt(1)).getEventId().equals(dA2.getId())) ;
        //Check that the title and short description of the ActivityPreviews corresponds to our two test DeboxActivities
        assertTrue(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(0)).getChildAt(0)).getText().toString().equals(dA1.getTitle()));
        assertTrue(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(0)).getChildAt(1)).getText().toString().equals(dA1.getShortDescription()));
        assertTrue(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(1)).getChildAt(0)).getText().toString().equals(dA2.getTitle()));
        assertTrue(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(1)).getChildAt(1)).getText().toString().equals(dA2.getShortDescription()));

    }


    @UiThreadTest
    @Test
    public void dateFilterTest() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        final List<DeboxActivity> testActivityList = new ArrayList<>();
        addDeboxActivities(testActivityList);

        DataProvider testDataProvider = mock(DataProvider.class);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivities listener = (DataProvider.DataProviderListenerActivities) args[0];
                listener.getActivities(testActivityList);
                return null;
            }
        }).when(testDataProvider).getAllActivities(any(DataProvider.DataProviderListenerActivities.class));

        //Insert Mock DataProvider
        activity.setDataProvider(testDataProvider);

        // Sets the parameters of the WelcomeActivity that are usually set by the user and are required
        //in the displaySpecifiedActivities() function
        Calendar startCalendar = addDays(currentCalendar,3);
        Calendar endCalendar = addDays(currentCalendar,6);
        activity.filterCategory = "All";
        activity.maxDistanceString = "All";
        activity.centerLongitude = 6.642266;
        activity.centerLatitude = 46.777245;
        activity.filterStartCalendar = startCalendar;
        activity.filterEndCalendar = endCalendar;

        //Calls the method that filters and displays the activities
        activity.displaySpecifiedActivities();

        //Checks that the number of filtered events is correct
        final int activityCount = activity.activityPreviewsLayout.getChildCount();
        assertThat(activityCount, is(4));

        //Checks that the DeboxActivities titles correspond to the ones that should have been selected
        int displayedActivityCount = 0;
        for(int i=0; i<testActivityList.size(); i++) {
            if (testActivityList.get(i).getTimeStart().before(activity.filterEndCalendar) && testActivityList.get(i).getTimeEnd().after(activity.filterStartCalendar)) {
                assertThat(((TextView) ((ActivityPreview) activity.activityPreviewsLayout.getChildAt(displayedActivityCount)).getChildAt(0)).getText().toString(), is(testActivityList.get(i).getTitle()));
                displayedActivityCount += 1;
            }
        }
    }

    @Test
    public void clickingFilterButtonsPicksTheRightParameters() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        DataProvider testDataProvider = mock(DataProvider.class);

        when(testDataProvider.pushActivity(any(DeboxActivity.class))).thenReturn(null);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                final DataProvider.DataProviderListenerCategories listener = (DataProvider.DataProviderListenerCategories) args[0];
                final List<DataProvider.CategoryName> list = new ArrayList<>();
                DataProvider.CategoryName cat1 = new DataProvider.CategoryName("Hello", "Sport");
                DataProvider.CategoryName cat2 = new DataProvider.CategoryName("Hello", "Culture");
                list.add(cat1);
                list.add(cat2);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.getCategories(list);
                    }
                });
                return null;
            }
        }).when(testDataProvider).getAllCategories(any(DataProvider.DataProviderListenerCategories.class));
        activity.setDataProvider(testDataProvider);
        activity.getAllCategories();

        String testCategory = "Culture";
        String testMaxDistanceString = "50 km";

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DATE, 2);
        startCalendar.add(Calendar.HOUR, 2);
        startCalendar.add(Calendar.MINUTE, 30);
        int startYear = startCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH);
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = startCalendar.get(Calendar.MINUTE);
        String startDate = activity.makeDateString(startCalendar);
        String startTime = activity.makeTimeString(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE, 9);
        endCalendar.add(Calendar.HOUR, 2);
        endCalendar.add(Calendar.MINUTE, 50);
        int endYear = endCalendar.get(Calendar.YEAR);
        int endMonth = endCalendar.get(Calendar.MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = endCalendar.get(Calendar.MINUTE);
        String endDate = activity.makeDateString(endCalendar);
        String endTime = activity.makeTimeString(endCalendar);

        onView(allOf(withId(R.id.filterActivity), withText("Filter the events"), withParent(withId(R.id.include)), isDisplayed())).perform(click());

        onView(withId(R.id.filterMaxDistanceDropDown)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(String.class)), is(testMaxDistanceString))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.filterMaxDistanceDropDown)).perform(scrollTo()).check(matches(withSpinnerText(testMaxDistanceString)));

        onView(withId(R.id.filterCategoriesDropDown)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(String.class)), is(testCategory))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.filterCategoriesDropDown)).perform(scrollTo()).check(matches(withSpinnerText(testCategory)));

        onView(withId(R.id.startDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(startYear, startMonth + 1, startDay));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.startDate)).perform(ViewActions.scrollTo()).check(matches(withText(startDate)));

        onView(withId(R.id.startTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(startHour, startMinute));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.startTime)).perform(ViewActions.scrollTo()).check(matches(withText(startTime)));

        onView(withId(R.id.endDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(endYear, endMonth + 1, endDay));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.endDate)).perform(ViewActions.scrollTo()).check(matches(withText(endDate)));

        onView(withId(R.id.endTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(endHour, endMinute));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.endTime)).perform(ViewActions.scrollTo()).check(matches(withText(endTime)));

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.validate), withText("Validate")));
        appCompatButton6.perform(scrollTo(), click());


        assertThat(activity.filterCategory, is(testCategory));
        assertThat(activity.maxDistanceString, is(testMaxDistanceString));
        assertThat(activity.centerLongitude, is(0.0));
        assertThat(activity.centerLatitude, is(0.0));
        assertThat(activity.filterStartCalendar.get(Calendar.YEAR), is(startYear));
        assertThat(activity.filterStartCalendar.get(Calendar.MONTH), is(startMonth));
        assertThat(activity.filterStartCalendar.get(Calendar.DAY_OF_MONTH), is(startDay));
        assertThat(activity.filterStartCalendar.get(Calendar.HOUR_OF_DAY), is(startHour));
        assertThat(activity.filterStartCalendar.get(Calendar.MINUTE), is(startMinute));
        assertThat(activity.filterEndCalendar.get(Calendar.YEAR), is(endYear));
        assertThat(activity.filterEndCalendar.get(Calendar.MONTH), is(endMonth));
        assertThat(activity.filterEndCalendar.get(Calendar.DAY_OF_MONTH), is(endDay));
        assertThat(activity.filterEndCalendar.get(Calendar.HOUR_OF_DAY), is(endHour));
        assertThat(activity.filterEndCalendar.get(Calendar.MINUTE), is(endMinute));
    }

    @Test
    public void filterParametersAreSavedWhenTheFragmentIsClosed() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        DataProvider testDataProvider = mock(DataProvider.class);

        when(testDataProvider.pushActivity(any(DeboxActivity.class))).thenReturn(null);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                final DataProvider.DataProviderListenerCategories listener = (DataProvider.DataProviderListenerCategories) args[0];
                final List<DataProvider.CategoryName> list = new ArrayList<>();
                DataProvider.CategoryName cat1 = new DataProvider.CategoryName("Hello", "Sport");
                DataProvider.CategoryName cat2 = new DataProvider.CategoryName("Hello", "Culture");
                list.add(cat1);
                list.add(cat2);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.getCategories(list);
                    }
                });
                return null;
            }
        }).when(testDataProvider).getAllCategories(any(DataProvider.DataProviderListenerCategories.class));
        activity.setDataProvider(testDataProvider);
        activity.getAllCategories();

        String testCategory = "Culture";
        String testMaxDistanceString = "50 km";

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DATE, 2);
        startCalendar.add(Calendar.HOUR, 2);
        startCalendar.add(Calendar.MINUTE, 30);
        int startYear = startCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH);
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = startCalendar.get(Calendar.MINUTE);
        String startDate = activity.makeDateString(startCalendar);
        String startTime = activity.makeTimeString(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE, 9);
        endCalendar.add(Calendar.HOUR, 2);
        endCalendar.add(Calendar.MINUTE, 50);
        int endYear = endCalendar.get(Calendar.YEAR);
        int endMonth = endCalendar.get(Calendar.MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = endCalendar.get(Calendar.MINUTE);
        String endDate = activity.makeDateString(endCalendar);
        String endTime = activity.makeTimeString(endCalendar);


        onView(allOf(withId(R.id.filterActivity), withText("Filter the events"), withParent(withId(R.id.include)), isDisplayed())).perform(click());

        //Sets the parameters instead of clicking them to gain time
        activity.filterCategory = testCategory;
        activity.maxDistanceString = testMaxDistanceString;
        activity.filterStartCalendar = startCalendar;
        activity.filterEndCalendar = endCalendar;

        onView(withId(R.id.filterAddLocation)).perform(pressBack());

        onView(allOf(withId(R.id.filterActivity), withText("Filter the events"), withParent(withId(R.id.include)), isDisplayed())).perform(click());

        onView(allOf(withId(R.id.validate), withText("Validate"))).perform(scrollTo(), click());

        onView(allOf(withId(R.id.filterActivity), withText("Filter the events"), withParent(withId(R.id.include)), isDisplayed())).perform(click());

        onView(withId(R.id.filterMaxDistanceDropDown)).perform(scrollTo()).check(matches(withSpinnerText(testMaxDistanceString)));
        onView(withId(R.id.filterCategoriesDropDown)).perform(scrollTo()).check(matches(withSpinnerText(testCategory)));
        onView(withId(R.id.startDate)).perform(ViewActions.scrollTo()).check(matches(withText(startDate)));
        onView(withId(R.id.startTime)).perform(ViewActions.scrollTo()).check(matches(withText(startTime)));
        onView(withId(R.id.endDate)).perform(ViewActions.scrollTo()).check(matches(withText(endDate)));
        onView(withId(R.id.endTime)).perform(ViewActions.scrollTo()).check(matches(withText(endTime)));

        onView(allOf(withId(R.id.validate), withText("Validate"))).perform(scrollTo(), click());

        assertThat(activity.filterCategory, is(testCategory));
        assertThat(activity.maxDistanceString, is(testMaxDistanceString));
        assertThat(activity.centerLongitude, is(0.0));
        assertThat(activity.centerLatitude, is(0.0));
        assertThat(activity.filterStartCalendar.get(Calendar.YEAR), is(startYear));
        assertThat(activity.filterStartCalendar.get(Calendar.MONTH), is(startMonth));
        assertThat(activity.filterStartCalendar.get(Calendar.DAY_OF_MONTH), is(startDay));
        assertThat(activity.filterStartCalendar.get(Calendar.HOUR_OF_DAY), is(startHour));
        assertThat(activity.filterStartCalendar.get(Calendar.MINUTE), is(startMinute));
        assertThat(activity.filterEndCalendar.get(Calendar.YEAR), is(endYear));
        assertThat(activity.filterEndCalendar.get(Calendar.MONTH), is(endMonth));
        assertThat(activity.filterEndCalendar.get(Calendar.DAY_OF_MONTH), is(endDay));
        assertThat(activity.filterEndCalendar.get(Calendar.HOUR_OF_DAY), is(endHour));
        assertThat(activity.filterEndCalendar.get(Calendar.MINUTE), is(endMinute));
    }*/

}