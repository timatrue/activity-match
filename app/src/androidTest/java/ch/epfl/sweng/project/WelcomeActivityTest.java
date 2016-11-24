package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
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

    //The list of DeboxActivity designed for testing
    private final List<DeboxActivity> deboxActivityList = createDeboxActivityList();

    //The list of categories for the spinner in the UI tests
    private final List<DataProvider.CategoryName> categoryList = createCategoryList();

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
                addDays(currentCalendar, 13),
                addDays(currentCalendar, 14),
                46.777245,
                6.642266,
                "Sports");

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

        final DeboxActivity dA3 = new DeboxActivity(
                "id3",
                "Benoit",
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


    //Returns the list of possible categories for the spinner
    private List<DataProvider.CategoryName> createCategoryList() {
        List<DataProvider.CategoryName> listCategory = new ArrayList<>();
        String[] categoryList = {"Sports", "Culture"};
        String categoryId = "defaultId";
        for (String category : categoryList) {
            listCategory.add(new DataProvider.CategoryName(categoryId, category));
        }
        return listCategory;
    }

    private void initializeMockProvider(final WelcomeActivity activity) {
        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.setListOfActivitiesToMock(deboxActivityList);
        mocDataProvider.setListOfCategoryToMock(categoryList);
        activity.setDataProvider(dp);
        activity.getAllCategories();
    }

    //Shows how to use the mock data provider

    @Test
    public void mockDataProviderTest(){

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();

        final DeboxActivity dbat = new DeboxActivity(
                "-",
                "test",
                "user-test",
                "description",
                Calendar.getInstance(),
                Calendar.getInstance(),
                122.01,
                121.0213,
                "Sports");

        assertEquals(dp.pushActivity(dbat),dbat.getId());

        final List<DeboxActivity> deboxActivityList = new ArrayList<>();

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
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
                "Culture");
        final DeboxActivity dA2 = new DeboxActivity(
                "asdf",
                "Jeremie",
                "Handball in UNIL sport center",
                "Indoor Handball tournaments open to every student " +
                        "of UNIL and EPFL, teams are formed 15 minutes before and tournament consists of 11 " +
                        "minutes games",
                startDate,
                endDate,
                122.04,
                121.0243,
                "Sports");

        deboxActivityList.add(dA1);
        deboxActivityList.add(dA2);

        mocDataProvider.setListOfActivitiesToMock(deboxActivityList);

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
        enrolledList.add("sample2");

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

    //UI thread test because we need to access UI elements (Textviews, etc...)
    @UiThreadTest
    @Test
    public void DisplayActivitiesProperly() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        initializeMockProvider(activity);

        //Press on the "Display Events" button
        activity.displayActivities.performClick();

        //Check that the five events are displayed
        final int activityCount = activity.activityPreviewsLayout.getChildCount();
        assertThat(activityCount, is(deboxActivityList.size()));

        //Check that they are ActivityPreview
        for(int i=0; i<activityCount; i++) {
            assertTrue(activity.activityPreviewsLayout.getChildAt(i) instanceof ActivityPreview);
        }

        //Check that they have the same ID has the test DeboxActivity passed by the Mock
        for(int i=0; i<activityCount; i++) {
            assertTrue(((ActivityPreview) activity.activityPreviewsLayout.getChildAt(i)).getEventId().equals(deboxActivityList.get(i).getId()));
        }

        //Check that the title and short description of the ActivityPreviews corresponds to our two test DeboxActivities
        for(int i=0; i<activityCount; i++) {
            assertTrue(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(i)).getChildAt(0)).getText().toString().equals(deboxActivityList.get(i).getTitle()));
            assertTrue(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(i)).getChildAt(1)).getText().toString().equals(deboxActivityList.get(i).getShortDescription()));
        }
    }

    @Test
    public void distanceFromCenterIsCalculatedProperly() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        activity.centerLongitude = 6.631790;
        activity.centerLatitude = 46.519556;

        double distance = activity.distanceFromCenter(deboxActivityList.get(0));

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

        initializeMockProvider(activity);

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
        for(int i=0; i<deboxActivityList.size(); i++){
            if(activity.distanceFromCenter(deboxActivityList.get(i)) <= WelcomeActivity.maxDistanceMap.get(activity.maxDistanceString)) {
                assertThat(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(displayedActivityCount)).getChildAt(0)).getText().toString(), is(deboxActivityList.get(i).getTitle()));
                displayedActivityCount += 1;
            }
        }
    }


    @UiThreadTest
    @Test
    public void changingCenterLocationTest() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        initializeMockProvider(activity);

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
        for(int i=0; i<deboxActivityList.size(); i++){
            if(activity.distanceFromCenter(deboxActivityList.get(i)) <= WelcomeActivity.maxDistanceMap.get(activity.maxDistanceString)) {
                assertThat(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(displayedActivityCount)).getChildAt(0)).getText().toString(), is(deboxActivityList.get(i).getTitle()));
                displayedActivityCount += 1;
            }
        }
    }

    @UiThreadTest
    @Test
    public void CategoryFilterTest() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        final String filterCategory = "Culture";

        initializeMockProvider(activity);

        // Sets the parameters of the WelcomeActivity that are usually set by the user and are required
        // in the displaySpecifiedActivities() function
        Calendar startCalendar = currentCalendar;
        Calendar endCalendar = addDays(currentCalendar,30);
        activity.filterCategory = filterCategory;
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
        for(int i=0; i<deboxActivityList.size(); i++){
            if(deboxActivityList.get(i).getCategory().equals(filterCategory)) {
                assertThat(((TextView)((ActivityPreview) activity.activityPreviewsLayout.getChildAt(displayedActivityCount)).getChildAt(0)).getText().toString(), is(deboxActivityList.get(i).getTitle()));
                displayedActivityCount += 1;
            }
        }
    }

    @UiThreadTest
    @Test
    public void dateFilterTest() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        initializeMockProvider(activity);

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
        for(int i=0; i<deboxActivityList.size(); i++) {
            if (deboxActivityList.get(i).getTimeStart().before(activity.filterEndCalendar) && deboxActivityList.get(i).getTimeEnd().after(activity.filterStartCalendar)) {
                assertThat(((TextView) ((ActivityPreview) activity.activityPreviewsLayout.getChildAt(displayedActivityCount)).getChildAt(0)).getText().toString(), is(deboxActivityList.get(i).getTitle()));
                displayedActivityCount += 1;
            }
        }
    }

    @Test
    public void UITest() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        initializeMockProvider(activity);

        final String testCategory = "Culture";
        final String testMaxDistanceString = "50 km";

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DATE, 2);
        startCalendar.add(Calendar.HOUR, 2);
        startCalendar.add(Calendar.MINUTE, 30);
        final int startYear = startCalendar.get(Calendar.YEAR);
        final int startMonth = startCalendar.get(Calendar.MONTH);
        final int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        final int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        final int startMinute = startCalendar.get(Calendar.MINUTE);
        final String startDate = activity.makeDateString(startCalendar);
        final String startTime = activity.makeTimeString(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE, 9);
        endCalendar.add(Calendar.HOUR, 2);
        endCalendar.add(Calendar.MINUTE, 50);
        final int endYear = endCalendar.get(Calendar.YEAR);
        final int endMonth = endCalendar.get(Calendar.MONTH);
        final int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        final int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        final int endMinute = endCalendar.get(Calendar.MINUTE);
        final String endDate = activity.makeDateString(endCalendar);
        final String endTime = activity.makeTimeString(endCalendar);

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
    }

    @Test
    public void filterParametersAreSavedWhenTheFragmentIsClosed() throws Exception {

        final WelcomeActivity activity = welcomeActivityRule.getActivity();

        initializeMockProvider(activity);

        final String testCategory = "Culture";
        final String testMaxDistanceString = "50 km";

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DATE, 2);
        startCalendar.add(Calendar.HOUR, 2);
        startCalendar.add(Calendar.MINUTE, 30);
        final int startYear = startCalendar.get(Calendar.YEAR);
        final int startMonth = startCalendar.get(Calendar.MONTH);
        final int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        final int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        final int startMinute = startCalendar.get(Calendar.MINUTE);
        final String startDate = activity.makeDateString(startCalendar);
        final String startTime = activity.makeTimeString(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE, 9);
        endCalendar.add(Calendar.HOUR, 2);
        endCalendar.add(Calendar.MINUTE, 50);
        final int endYear = endCalendar.get(Calendar.YEAR);
        final int endMonth = endCalendar.get(Calendar.MONTH);
        final int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        final int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        final int endMinute = endCalendar.get(Calendar.MINUTE);
        final String endDate = activity.makeDateString(endCalendar);
        final String endTime = activity.makeTimeString(endCalendar);


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
    }


}