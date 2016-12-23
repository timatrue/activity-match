package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.fragments.CreateValidationFragment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class UserImageFragmentTest {

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

    private void initializeMockProvider(final UserProfile activity) {
        MockImageProvider mockImageProvider = new MockImageProvider();
        ImageProvider ip = mockImageProvider.getMockImageProvider();
        activity.setImageProvider(ip);

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        activity.setDataProvider(dp);
        activity.activityCollection = new LinkedHashMap<>();
        activity.createCollection();
        activity.setExpListView();
    }

    @UiThreadTest
    @Test
    public void LaunchFragment() {

        final UserProfile activity = userProfileRule.getActivity();

        MockImageProvider mockImageProvider = new MockImageProvider();
        ImageProvider ip = mockImageProvider.getMockImageProvider();
        activity.setImageProvider(ip);
        ImageView userImage = (ImageView) activity.findViewById(R.id.userImage);

        userImage.performClick();

    }

    public void test() {

        final UserProfile activity = userProfileRule.getActivity();


    }


}