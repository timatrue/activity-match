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

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
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
public class ModifyActivityTest {
    @Rule
    public ActivityTestRule<ModifyActivity> modifyActivityRule =
            new ActivityTestRule<ModifyActivity>(ModifyActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, ModifyActivity.class);
                    result.putExtra(ModifyActivity.MODIFY_ACTIVITY_EVENT_ID, "id1");
                    result.putExtra(ModifyActivity.CREATE_ACTIVITY_TEST_KEY, ModifyActivity.CREATE_ACTIVITY_TEST);
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
        String[] categoryList = {"Culture", "Sports"};
        String categoryId = "defaultId";
        for (String category : categoryList) {
            listCategory.add(new DataProvider.CategoryName(categoryId, category));
        }
        return listCategory;
    }

    private void initializeMockProvider(final ModifyActivity activity) {
        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        MockImageProvider mocImageProvider = new MockImageProvider();
        ImageProvider ip = mocImageProvider.getMockImageProvider();
        mocDataProvider.setListOfCategoryToMock(categoryList);
        mocDataProvider.addActivityToMock(dA1);
        mocDataProvider.addActivityToMock(dA2);
        activity.setDataProvider(dp);
        activity.setImageProvider(ip);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getDeboxActivityAndDisplay();
                activity.getAndDisplayCategories();
            }
        });
    }

    private final List<String> imageList = createImageList();

    private List<String> createImageList() {
        List<String> newImageList = new ArrayList<>();
        newImageList.add("image1");
        newImageList.add("image2");
        return newImageList;
    }

    private final DeboxActivity dA1 = new DeboxActivity(
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

    @Test
    public void UIAndCorrectBehaviourTest() throws Exception {

        final ModifyActivity activity = modifyActivityRule.getActivity();
        Context context = InstrumentationRegistry.getTargetContext();

        initializeMockProvider(activity);


        final String testTitle = "_new";
        final String testCategory = "Culture";
        final String testDescription = " new";
        final double testLatitude = 0.3;
        final double testLongitude = 1;
        final String testNbMaxParticipantsString = "30";
        final int testNbMaxParticipants = 30;

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

        onView(withId(R.id.createActivityTitleEditText)).perform(ViewActions.scrollTo()).perform(typeText(testTitle), closeSoftKeyboard());

        onView(withId(R.id.proSpinner)).perform(ViewActions.scrollTo()).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(testCategory)))
                .inRoot(RootMatchers.withDecorView(not(is(activity.getWindow().getDecorView()))))
                .perform(click());
        onView(withId(R.id.proSpinner)).perform(closeSoftKeyboard());

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

        onView(withId(R.id.createActivityMaxNbParticipantsEditText)).perform(ViewActions.scrollTo()).perform(clearText(), typeText(testNbMaxParticipantsString), closeSoftKeyboard());

        activity.activityLatitude = testLatitude;
        activity.activityLongitude = testLongitude;

        onView(withId(R.id.createActivityValidateButton)).perform(ViewActions.scrollTo()).perform(click());

        assertThat(activity.activityTitle, is(dA1.getTitle() + testTitle));
        assertThat(activity.activityDescription, is(dA1.getDescription() + testDescription));
        assertThat(activity.activityCategory, is(testCategory));
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
        assertThat(activity.initialNumberParticipants, is(dA1.getNbOfParticipants()));
        assertThat(activity.activityMaxNbParticipants, is(testNbMaxParticipants));
        assertThat(activity.imagesNameList.size(), is(dA1.getImageList().size()));

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
        assertThat(da.getTitle(), is(dA1.getTitle() + testTitle));
        assertThat(da.getCategory(), is(testCategory));
        assertThat(da.getDescription(), is(dA1.getDescription() + testDescription));
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
        assertThat(da.getLocation()[0], is(testLatitude));
        assertThat(da.getLocation()[1], is(testLongitude));
        assertThat(da.getImageList().size(), is(dA1.getImageList().size()));
        assertThat(da.getImageList().contains("image1"), is(true));
        assertThat(da.getImageList().contains("image2"), is(true));
        assertThat(da.getNbMaxOfParticipants(), is(testNbMaxParticipants));
        assertThat(da.getNbOfParticipants(), is(dA1.getNbOfParticipants()));
    }
}
