package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Calendar;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;



/**
 * Created by olga on 19.10.16.
 */


public class DisplayActivityTest {


    boolean testFinished = false;


    @Mock
    DataProvider testDataProvider;

    @Mock
    FirebaseUser testFirebaseUser;

    @Rule
    public ActivityTestRule<DisplayActivity> displayActivityRule =
            new ActivityTestRule<DisplayActivity>(DisplayActivity.class){
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, MainActivity.class);
                    result.putExtra(DisplayActivity.DISPLAY_ACTIVITY_TEST_KEY, DisplayActivity.DISPLAY_ACTIVITY_TEST);
                    result.putExtra(DisplayActivity.DISPLAY_EVENT_ID, "zdkasKKLD");
                    return result;
                }
            };

    @Test
    public void DisplayActivityTitle() throws Exception {


        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        startDate.set(Calendar.YEAR, 2016);
        startDate.set(Calendar.MONTH, 11);
        final DeboxActivity dA = new DeboxActivity("zdkasKKLD", "Nathan",
                "Football in UNIL sport center", "Indoor football tournaments open to every student " +
                "of UNIL and EPFL, teams are formed 15 minutes before and tournament consists of 11 " +
                "minutes games",
                startDate,
                endDate,
                122.01,
                121.0213,
                "Sports");



        final DataProvider testDataProvider = mock(DataProvider.class);

        when(testDataProvider.getActivityFromUid(any(DataProvider.DataProviderListenerActivity.class), anyString())).thenAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivity listener = (DataProvider.DataProviderListenerActivity) args[0];
                listener.getActivity(dA);
                return null;
            }

            ;

        });


        final DisplayActivity activity = displayActivityRule.getActivity();

        activity.setTestDBObjects(testDataProvider, testFirebaseUser);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.initDisplay();

                Assert.assertThat(activity.title.getText().toString(), is(dA.getTitle()));
                Assert.assertThat(activity.description.getText().toString(), is(dA.getDescription()));
                Assert.assertThat(activity.description.getText().toString(), is(dA.getDescription()));


                testFinished = true;
            }
        });


        while(!testFinished) {

        }

   }

}
