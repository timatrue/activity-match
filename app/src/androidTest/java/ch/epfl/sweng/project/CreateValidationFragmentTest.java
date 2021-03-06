package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.epfl.sweng.project.fragments.CreateValidationFragment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CreateValidationFragmentTest {

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


    private final List<DataProvider.CategoryName> categoryList = createCategoryList();

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
        MockImageProvider mockImageProvider = new MockImageProvider();
        ImageProvider ip = mockImageProvider.getMockImageProvider();

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();

        activity.setImageProvider(ip);
        activity.setDataProvider(dp);

        mocDataProvider.setListOfCategoryToMock(categoryList);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getAndDisplayCategories();
            }
        });
    }

    private void initFields(final CreateActivity activity) {
        final String testTitle = "test_title";
        final String testCategory = "Sports";
        final String testDescription = "test description";
        final double testLatitude = 0.3;
        final double testLongitude = 1;
        final String testOrganizer = "BobID";

        final Calendar startCalendar = CreateActivityTest.addDays(currentCalendar, 1);

        final Calendar endCalendar = CreateActivityTest.addDays(currentCalendar, 2);
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
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setErrorTextView(validation);
            }
        });

        onView(withId(R.id.createActivityTitleEditText)).perform(ViewActions.scrollTo()).perform(typeText(testTitle), closeSoftKeyboard());

        onView(withId(R.id.createActivityDescriptionEditText)).perform(ViewActions.scrollTo()).perform(typeText(testDescription), closeSoftKeyboard());

        onView(withId(R.id.createActivityEndDate)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(endYear, endMonth + 1, endDay));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.createActivityEndTime)).perform(ViewActions.scrollTo()).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(endHour, endMinute));
        onView(withId(android.R.id.button1)).perform(click());


    }

    private CreateActivity activity;

    @Before
    public void UploadSuccessfulTest() throws Exception {

        activity  = createActivityRule.getActivity();
      //  initializeMockProvider(activity);

        ArrayList<Uri> imagesUriList = new ArrayList<>();

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 1);
        endDate.add(Calendar.DATE, 11);

        imagesUriList.add(Uri.parse("test1"));
        imagesUriList.add(Uri.parse("test2"));
        imagesUriList.add(Uri.parse("test3"));
        imagesUriList.add(Uri.parse("test4"));

        initializeMockProvider(activity);

        initFields(activity);

        activity.setImageList(imagesUriList);

        onView(withId(R.id.createActivityValidateButton)).perform(ViewActions.scrollTo()).perform(click());

    }


    @Test
    public void setup() {
        CreateValidationFragment createValidationFragment = activity.getValidationFragment();

        if(createValidationFragment.getView() != null){
            TextView tv = (TextView) createValidationFragment.getView().findViewById(R.id.createValidationUploadRate);

            //assertEquals(tv.getText(), "0%");
        }
    }
}