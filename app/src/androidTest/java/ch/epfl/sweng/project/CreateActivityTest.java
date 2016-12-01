package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CreateActivityTest {

    @Rule
    public ActivityTestRule<CreateActivity> createActivityRule =
            new ActivityTestRule<CreateActivity>(CreateActivity.class){
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, CreateActivity.class);
                    result.putExtra(CreateActivity.CREATE_ACTIVITY_TEST_KEY, CreateActivity.CREATE_ACTIVITY_TEST);
                    return result;
                }
            };

    private Calendar currentCalendar = Calendar.getInstance();

    //The list of categories for the spinner in the UI tests
    private final List<DataProvider.CategoryName> categoryList = createCategoryList();

    //Returns the calendar that is 'nDays' days later than the input calendar
    public static Calendar addDays(Calendar calendar, int nDays) {
        Calendar newCalendar = (Calendar) calendar.clone();
        newCalendar.add(Calendar.DATE, nDays);
        return newCalendar;
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

    private void initializeMockProvider(final CreateActivity activity) {
        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        mocDataProvider.setListOfCategoryToMock(categoryList);
        activity.setDataProvider(dp);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getAndDisplayCategories();
            }
        });
    }

    @Test
    public void UITest() throws Exception {
        final CreateActivity activity = createActivityRule.getActivity();

        initializeMockProvider(activity);

        final String testTitle = "test_title";
        final String testCategory = "Culture";
        final String testDescription = "test description";
        final double testLatitude = 0.3;
        final double testLongitude = 1;

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
        endCalendar.add(Calendar.DATE, 2);
        endCalendar.add(Calendar.HOUR, 2);
        endCalendar.add(Calendar.MINUTE, 50);
        final int endYear = endCalendar.get(Calendar.YEAR);
        final int endMonth = endCalendar.get(Calendar.MONTH);
        final int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        final int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        final int endMinute = endCalendar.get(Calendar.MINUTE);
        final String endDate = activity.makeDateString(endCalendar);
        final String endTime = activity.makeTimeString(endCalendar);

        String testOrganizer;
        if(activity.user != null) {
            testOrganizer = activity.user.getUid();
        }
        else {
            testOrganizer = activity.getString(R.string.unlogged_user);
        }

        activity.activityLatitude = testLatitude;
        activity.activityLongitude = testLongitude;

        onView(withId(R.id.createActivityTitleEditText)).perform(ViewActions.scrollTo()).perform(typeText(testTitle), closeSoftKeyboard());

        onView(withId(R.id.proSpinner)).perform(ViewActions.scrollTo()).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(testCategory)))
                .inRoot(RootMatchers.withDecorView(not(is(activity.getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.createActivityDescriptionEditText)).perform(ViewActions.scrollTo()).perform(typeText(testDescription), closeSoftKeyboard());

        onView(withId(R.id.createActivityStartDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(startYear, startMonth + 1, startDay));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.createActivityStartDate)).perform(ViewActions.scrollTo()).check(matches(withText(startDate)));

        onView(withId(R.id.createActivityStartTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(startHour, startMinute));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.createActivityStartTime)).perform(ViewActions.scrollTo()).check(matches(withText(startTime)));

        onView(withId(R.id.createActivityEndDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(endYear, endMonth + 1, endDay));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.createActivityEndDate)).perform(ViewActions.scrollTo()).check(matches(withText(endDate)));

        onView(withId(R.id.createActivityEndTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(endHour, endMinute));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.createActivityEndTime)).perform(ViewActions.scrollTo()).check(matches(withText(endTime)));

        onView(withId(R.id.createActivityValidateButton)).perform(ViewActions.scrollTo()).perform(click());

        assertThat(activity.activityTitle, is(testTitle));
        assertThat(activity.activityDescription, is(testDescription));
        assertThat(activity.activityOrganizer, is(testOrganizer));
        assertThat(activity.activityStartCalendar.get(Calendar.YEAR), is(startYear));
        assertThat(activity.activityStartCalendar.get(Calendar.MONTH), is(startMonth));
        assertThat(activity.activityStartCalendar.get(Calendar.DAY_OF_MONTH), is(startDay));
        assertThat(activity.activityStartCalendar.get(Calendar.HOUR_OF_DAY), is(startHour));
        assertThat(activity.activityStartCalendar.get(Calendar.MINUTE), is(startMinute));
        assertThat(activity.activityEndCalendar.get(Calendar.YEAR), is(endYear));
        assertThat(activity.activityEndCalendar.get(Calendar.MONTH), is(endMonth));
        assertThat(activity.activityEndCalendar.get(Calendar.DAY_OF_MONTH), is(endDay));
        assertThat(activity.activityEndCalendar.get(Calendar.HOUR_OF_DAY), is(endHour));
        assertThat(activity.activityEndCalendar.get(Calendar.MINUTE), is(endMinute));
        assertThat(activity.activityLatitude, is(testLatitude));
        assertThat(activity.activityLongitude, is(testLongitude));
    }

    @Test
    public void missingTitle() throws Exception {

        final CreateActivity activity = createActivityRule.getActivity();
        Context context = InstrumentationRegistry.getTargetContext();

        initializeMockProvider(activity);

        activity.activityDescription = "test description";
        activity.activityLatitude = 1;
        activity.activityLongitude = 1;

        onView(withId(R.id.createActivityTitleEditText)).perform(closeSoftKeyboard());

        final String validation = activity.validateActivity();
        final DeboxActivity da = activity.createActivityMethod(validation);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setErrorTextView(validation);
            }
        });

        assertThat(validation, is(ConfirmationCodes.get_missing_field_error(context)));
        assertTrue(da == null);
        onView(withId(R.id.createActivityError)).perform(ViewActions.scrollTo()).check(matches(withText(ConfirmationCodes.get_missing_field_error(context))));
    }

    @Test
    public void missingDescription() throws Exception {

        final CreateActivity activity = createActivityRule.getActivity();
        Context context = InstrumentationRegistry.getTargetContext();

        initializeMockProvider(activity);

        activity.activityTitle = "test_title";
        activity.activityLatitude = 1;
        activity.activityLongitude = 1;

        onView(withId(R.id.createActivityTitleEditText)).perform(closeSoftKeyboard());

        final String validation = activity.validateActivity();
        final DeboxActivity da = activity.createActivityMethod(validation);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setErrorTextView(validation);
            }
        });

        assertThat(validation, is(ConfirmationCodes.get_missing_field_error(context)));
        assertTrue(da == null);
        onView(withId(R.id.createActivityError)).perform(ViewActions.scrollTo()).check(matches(withText(ConfirmationCodes.get_missing_field_error(context))));
    }

    @Test
    public void endDateBeforeCurrentDate() throws Exception {

        final CreateActivity activity = createActivityRule.getActivity();
        Context context = InstrumentationRegistry.getTargetContext();

        initializeMockProvider(activity);

        activity.activityTitle = "test_title";
        activity.activityDescription = "test description";
        activity.activityLatitude = 1;
        activity.activityLongitude = 1;

        activity.activityCategory = "Culture";

        onView(withId(R.id.createActivityTitleEditText)).perform(closeSoftKeyboard());

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE, -2);
        final int endYear = endCalendar.get(Calendar.YEAR);
        final int endMonth = endCalendar.get(Calendar.MONTH);
        final int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);

        onView(withId(R.id.createActivityEndDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(endYear, endMonth, endDay));

        onView(withId(android.R.id.button1)).perform(click());


        final String validation = activity.validateActivity();
        final DeboxActivity da = activity.createActivityMethod(validation);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setErrorTextView(validation);
            }
        });

        assertThat(validation, is(ConfirmationCodes.get_date_error(context)));
        assertTrue(da == null);
        onView(withId(R.id.createActivityError)).perform(ViewActions.scrollTo()).check(matches(withText(ConfirmationCodes.get_date_error(context))));
    }

    @Test
    public void startDateAfterEndDate() throws Exception {

        final CreateActivity activity = createActivityRule.getActivity();
        Context context = InstrumentationRegistry.getTargetContext();

        initializeMockProvider(activity);

        activity.activityTitle = "test_title";
        activity.activityDescription = "test description";
        activity.activityLatitude = 1;
        activity.activityLongitude = 1;
        activity.activityStartCalendar = addDays(currentCalendar, 3);
        activity.activityEndCalendar = addDays(currentCalendar, 2);
        activity.activityCategory = "Culture";

        onView(withId(R.id.createActivityTitleEditText)).perform(closeSoftKeyboard());

        final String validation = activity.validateActivity();
        final DeboxActivity da = activity.createActivityMethod(validation);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setErrorTextView(validation);
            }
        });

        assertThat(validation, is(ConfirmationCodes.get_date_error(context)));
        assertTrue(da == null);
        onView(withId(R.id.createActivityError)).perform(ViewActions.scrollTo()).check(matches(withText(ConfirmationCodes.get_date_error(context))));
    }

    @Test
    public void startTimeBeforeCurrentTimeIsReplaced() throws Exception {

        final CreateActivity activity = createActivityRule.getActivity();
        Context context = InstrumentationRegistry.getTargetContext();

        initializeMockProvider(activity);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MINUTE, -10);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.HOUR, 2);

        Calendar lowerBound = Calendar.getInstance();
        lowerBound.add(Calendar.MINUTE, -1);

        Calendar upperBound = Calendar.getInstance();
        upperBound.add(Calendar.MINUTE, 1);

        activity.activityTitle = "test_title";
        activity.activityDescription = "test description";
        activity.activityLatitude = 1;
        activity.activityLongitude = 0.3;
        activity.activityStartCalendar = startCalendar;
        activity.activityEndCalendar = endCalendar;

        activity.activityCategory = "Culture";

        onView(withId(R.id.createActivityTitleEditText)).perform(closeSoftKeyboard());

        final String validation = activity.validateActivity();
        final DeboxActivity da = activity.createActivityMethod(validation);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setErrorTextView(validation);
            }
        });

        assertThat(validation, is(ConfirmationCodes.get_success(context)));
        assertTrue(da !=  null);
        assertTrue(da.getTimeStart().after(lowerBound));
        assertTrue(da.getTimeStart().before(upperBound));
    }

    @Test
    public void noLocationChosen() throws Exception {

        final CreateActivity activity = createActivityRule.getActivity();
        Context context = InstrumentationRegistry.getTargetContext();

        initializeMockProvider(activity);

        activity.activityTitle = "test_title";
        activity.activityDescription = "test description";
        activity.activityStartCalendar = addDays(currentCalendar, 1);
        activity.activityEndCalendar = addDays(currentCalendar, 2);

        onView(withId(R.id.createActivityTitleEditText)).perform(closeSoftKeyboard());

        final String validation = activity.validateActivity();
        final DeboxActivity da = activity.createActivityMethod(validation);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setErrorTextView(validation);
            }
        });

        assertThat(validation, is(ConfirmationCodes.get_missing_location_error(context)));
        assertTrue(da == null);
        onView(withId(R.id.createActivityError)).perform(ViewActions.scrollTo()).check(matches(withText(ConfirmationCodes.get_missing_location_error(context))));
    }

    @Test
    public void validActivityCreation() throws Exception {

        final CreateActivity activity = createActivityRule.getActivity();
        Context context = InstrumentationRegistry.getTargetContext();

        initializeMockProvider(activity);

        final String testTitle = "test_title";
        final String testCategory = "Sports";
        final String testDescription = "test description";
        final double testLatitude = 0.3;
        final double testLongitude = 1;
        final String testOrganizer = "BobID";

        final Calendar startCalendar = addDays(currentCalendar, 1);
        final int startYear = startCalendar.get(Calendar.YEAR);
        final int startMonth = startCalendar.get(Calendar.MONTH);
        final int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        final int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        final int startMinute = startCalendar.get(Calendar.MINUTE);

        final Calendar endCalendar = addDays(currentCalendar, 2);
        final int endYear = endCalendar.get(Calendar.YEAR);
        final int endMonth = endCalendar.get(Calendar.MONTH);
        final int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        final int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        final int endMinute = endCalendar.get(Calendar.MINUTE);

        activity.activityTitle = testTitle;
        activity.activityCategory = testCategory;
        activity.activityDescription = testDescription;
        activity.activityLatitude = testLatitude;
        activity.activityLongitude = testLongitude;
        activity.activityStartCalendar = startCalendar;
        activity.activityEndCalendar = endCalendar;
        activity.activityOrganizer = testOrganizer;

        onView(withId(R.id.createActivityTitleEditText)).perform(closeSoftKeyboard());

        final String validation = activity.validateActivity();
        final DeboxActivity da = activity.createActivityMethod(validation);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setErrorTextView(validation);
            }
        });

        assertThat(validation, is(ConfirmationCodes.get_success(context)));
        assertTrue(da != null);
        assertThat(da.getTitle(), is(testTitle));
        assertThat(da.getCategory(), is(testCategory));
        assertThat(da.getDescription(), is(testDescription));
        assertThat(da.getTimeStart().get(Calendar.YEAR), is(startYear));
        assertThat(da.getTimeStart().get(Calendar.MONTH), is(startMonth));
        assertThat(da.getTimeStart().get(Calendar.DAY_OF_MONTH), is(startDay));
        assertThat(da.getTimeStart().get(Calendar.HOUR_OF_DAY), is(startHour));
        assertThat(da.getTimeStart().get(Calendar.MINUTE), is(startMinute));
        assertThat(da.getTimeEnd().get(Calendar.YEAR), is(endYear));
        assertThat(da.getTimeEnd().get(Calendar.MONTH), is(endMonth));
        assertThat(da.getTimeEnd().get(Calendar.DAY_OF_MONTH), is(endDay));
        assertThat(da.getTimeEnd().get(Calendar.HOUR_OF_DAY), is(endHour));
        assertThat(da.getTimeEnd().get(Calendar.MINUTE), is(endMinute));
        assertThat(da.getOrganizer(), is(testOrganizer));
        assertThat(da.getLocation()[0], is(testLatitude));
        assertThat(da.getLocation()[1], is(testLongitude));
    }
}