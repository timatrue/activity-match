package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class CreateActivityTest {

    @Rule
    public ActivityTestRule<CreateActivity> createActivityRule =
            new ActivityTestRule<CreateActivity>(CreateActivity.class){
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, MainActivity.class);
                    result.putExtra(CreateActivity.CREATE_ACTIVITY_TEST_KEY, CreateActivity.CREATE_ACTIVITY_TEST);
                    return result;
                }
            };


    @Mock
    DataProvider testDataProvider;


    @Test
    public void missingTitle() throws Exception {

        CreateActivity activity = createActivityRule.getActivity();

        String testDescription = "test description";

        activity.activityLongitude=1;
        activity.activityLatitude=1;


        onView(withId(R.id.createActivityDescriptionEditText)).perform(ViewActions.scrollTo()).perform(typeText(testDescription), closeSoftKeyboard());

        onView(withId(R.id.createActivityValidateButton)).perform(ViewActions.scrollTo()).perform(click());
        assertTrue(activity.validateActivity().equals("missing_field_error"));
        assertTrue(activity.createActivityMethod() == null);
        onView(withId(R.id.createActivityConfirmation)).perform(ViewActions.scrollTo()).check(matches(withText(R.string.create_activity_missing_field_error_message)));
    }

    @Test
    public void missingDescription() throws Exception {

        CreateActivity activity = createActivityRule.getActivity();

        String testTitle = "test_title";

        activity.activityLongitude=1;
        activity.activityLatitude=1;

        onView(withId(R.id.createActivityTitleEditText)).perform(ViewActions.scrollTo()).perform(typeText(testTitle), closeSoftKeyboard());

        onView(withId(R.id.createActivityValidateButton)).perform(ViewActions.scrollTo()).perform(click());
        assertTrue(activity.validateActivity().equals("missing_field_error"));
        assertTrue(activity.createActivityMethod() == null);
        onView(withId(R.id.createActivityConfirmation)).perform(ViewActions.scrollTo()).check(matches(withText(R.string.create_activity_missing_field_error_message)));
    }

    @Test
    public void endDateBeforeCurrentDate() throws Exception {

        CreateActivity activity = createActivityRule.getActivity();

        String testTitle = "test_title";
        String testDescription = "test description";

        activity.activityLongitude=1;
        activity.activityLatitude=1;

        onView(withId(R.id.createActivityTitleEditText)).perform(ViewActions.scrollTo()).perform(typeText(testTitle), closeSoftKeyboard());
        onView(withId(R.id.createActivityDescriptionEditText)).perform(ViewActions.scrollTo()).perform(typeText(testDescription), closeSoftKeyboard());

        onView(withId(R.id.createActivityValidateButton)).perform(ViewActions.scrollTo()).perform(click());

        assertTrue(activity.createActivityMethod() == null);
        assertTrue(activity.validateActivity().equals("date_error"));
        onView(withId(R.id.createActivityConfirmation)).perform(ViewActions.scrollTo()).check(matches(withText(R.string.create_activity_date_error_message)));
    }

    @Test
    public void startDateAfterEndDate() throws Exception {

        CreateActivity activity = createActivityRule.getActivity();

        String testTitle = "test_title";
        String testDescription = "test description";

        activity.activityLongitude=1;
        activity.activityLatitude=1;

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DATE, 3);
        int startYear = startCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH);
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = startCalendar.get(Calendar.MINUTE);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE, 2);
        int endYear = endCalendar.get(Calendar.YEAR);
        int endMonth = endCalendar.get(Calendar.MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = endCalendar.get(Calendar.MINUTE);

        onView(withId(R.id.createActivityTitleEditText)).perform(ViewActions.scrollTo()).perform(typeText(testTitle), closeSoftKeyboard());
        onView(withId(R.id.createActivityDescriptionEditText)).perform(ViewActions.scrollTo()).perform(typeText(testDescription), closeSoftKeyboard());

        onView(withId(R.id.createActivityStartDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(startYear, startMonth + 1, startDay));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityStartTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(startHour, startMinute));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityEndDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(endYear, endMonth + 1, endDay));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityEndTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(endHour, endMinute));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityValidateButton)).perform(ViewActions.scrollTo()).perform(click());

        assertTrue(activity.validateActivity().equals("date_error"));
        assertTrue(activity.createActivityMethod() == null);
        onView(withId(R.id.createActivityConfirmation)).perform(ViewActions.scrollTo()).check(matches(withText(R.string.create_activity_date_error_message)));
    }

    @Test
    public void validActivityCreation() throws Exception {
        CreateActivity activity = createActivityRule.getActivity();



        when(testDataProvider.pushActivity(any(DeboxActivity.class))).thenAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivity listener = (DataProvider.DataProviderListenerActivity) args[0];
                listener.getActivity(dA);
                return null;
            }
        });

        activity.setDataProvider(testDataProvider);

        String testTitle = "test_title";
        String testDescription = "test description";


        double longitude = 1;
        double latitude = 0.3;
        double[] location = {latitude, longitude};

        activity.activityLatitude=location[0];
        activity.activityLongitude=location[1];

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DATE, 2);
        startCalendar.add(Calendar.HOUR, 2);
        startCalendar.add(Calendar.MINUTE, 30);
        int startYear = startCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH);
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = startCalendar.get(Calendar.MINUTE);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE, 2);
        endCalendar.add(Calendar.HOUR, 2);
        endCalendar.add(Calendar.MINUTE, 50);
        int endYear = endCalendar.get(Calendar.YEAR);
        int endMonth = endCalendar.get(Calendar.MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = endCalendar.get(Calendar.MINUTE);

        String expectedUid;
        if(activity.user != null) {
            expectedUid = activity.user.getUid();
        }
        else {
            expectedUid = activity.getString(R.string.unlogged_user);
        }

        onView(withId(R.id.createActivityTitleEditText)).perform(ViewActions.scrollTo()).perform(typeText(testTitle), closeSoftKeyboard());
        onView(withId(R.id.createActivityDescriptionEditText)).perform(ViewActions.scrollTo()).perform(typeText(testDescription), closeSoftKeyboard());

        onView(withId(R.id.createActivityStartDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(startYear, startMonth + 1, startDay));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityStartTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(startHour, startMinute));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityEndDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(endYear, endMonth + 1, endDay));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityEndTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(endHour, endMinute));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityValidateButton)).perform(ViewActions.scrollTo()).perform(click());

        assertTrue(activity.validateActivity().equals("success"));
        DeboxActivity da = activity.createActivityMethod();
        assertTrue(da != null);
        assertTrue(da.getTitle().equals(testTitle));
        assertTrue(da.getDescription().equals(testDescription));
        assertTrue(da.getTimeStart().get(Calendar.YEAR) == startYear);
        assertTrue(da.getTimeStart().get(Calendar.MONTH) == startMonth);
        assertTrue(da.getTimeStart().get(Calendar.DAY_OF_MONTH) == startDay);
        assertTrue(da.getTimeStart().get(Calendar.HOUR_OF_DAY) == startHour);
        assertTrue(da.getTimeStart().get(Calendar.MINUTE) == startMinute);
        assertTrue(da.getTimeEnd().get(Calendar.YEAR) == endYear);
        assertTrue(da.getTimeEnd().get(Calendar.MONTH) == endMonth);
        assertTrue(da.getTimeEnd().get(Calendar.DAY_OF_MONTH) == endDay);
        assertTrue(da.getTimeEnd().get(Calendar.HOUR_OF_DAY) == endHour);
        assertTrue(da.getTimeEnd().get(Calendar.MINUTE) == endMinute);
        assertTrue(da.getOrganizer().equals(expectedUid));
        assertTrue(da.getLocation()[0] == location[0]);
        assertTrue(da.getLocation()[1] == location[1]);
    }

    @Test
    public void correctlyDisplaysDateAndTime() throws Exception {

        CreateActivity activity = createActivityRule.getActivity();

        activity.activityLongitude=1;
        activity.activityLatitude=1;

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
        endCalendar.add(Calendar.DATE, 2);
        endCalendar.add(Calendar.HOUR, 2);
        endCalendar.add(Calendar.MINUTE, 50);
        int endYear = endCalendar.get(Calendar.YEAR);
        int endMonth = endCalendar.get(Calendar.MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = endCalendar.get(Calendar.MINUTE);
        String endDate = activity.makeDateString(endCalendar);
        String endTime = activity.makeTimeString(endCalendar);

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
    }

    @Test
    public void startTimeBeforeCurrentTimeIsReplaced() throws Exception {

        CreateActivity activity = createActivityRule.getActivity();

        String testTitle = "test_title";
        String testDescription = "test description";

        activity.activityLongitude=1;
        activity.activityLatitude=1;

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MINUTE, -10);
        int startYear = startCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH);
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = startCalendar.get(Calendar.MINUTE);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.HOUR, 2);
        int endYear = endCalendar.get(Calendar.YEAR);
        int endMonth = endCalendar.get(Calendar.MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = endCalendar.get(Calendar.MINUTE);

        onView(withId(R.id.createActivityTitleEditText)).perform(ViewActions.scrollTo()).perform(typeText(testTitle), closeSoftKeyboard());
        onView(withId(R.id.createActivityDescriptionEditText)).perform(ViewActions.scrollTo()).perform(typeText(testDescription), closeSoftKeyboard());

        onView(withId(R.id.createActivityStartDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(startYear, startMonth + 1, startDay));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityStartTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(startHour, startMinute));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityEndDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(endYear, endMonth + 1, endDay));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityEndTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(endHour, endMinute));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityValidateButton)).perform(ViewActions.scrollTo()).perform(click());

        assertTrue(activity.validateActivity().equals("success"));
        assertTrue(activity.createActivityMethod() != null);
        assertTrue(activity.createActivityMethod().getTimeStart().after(startCalendar));
    }

    @Test
    public void noLocationChoose() throws Exception {

        CreateActivity activity = createActivityRule.getActivity();

        String testTitle = "test_title";
        String testDescription = "test description";

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.HOUR, 2);
        int endYear = endCalendar.get(Calendar.YEAR);
        int endMonth = endCalendar.get(Calendar.MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);

        onView(withId(R.id.createActivityTitleEditText)).perform(ViewActions.scrollTo()).perform(typeText(testTitle), closeSoftKeyboard());
        onView(withId(R.id.createActivityDescriptionEditText)).perform(ViewActions.scrollTo()).perform(typeText(testDescription), closeSoftKeyboard());

        onView(withId(R.id.createActivityEndDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(endYear, endMonth + 1, endDay));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityValidateButton)).perform(ViewActions.scrollTo()).perform(click());
        assertTrue(activity.validateActivity().equals("missing_location"));
        assertTrue(activity.createActivityMethod() == null);
        onView(withId(R.id.createActivityConfirmation)).perform(ViewActions.scrollTo()).check(matches(withText(R.string.create_activity_location_error_message)));
    }
}