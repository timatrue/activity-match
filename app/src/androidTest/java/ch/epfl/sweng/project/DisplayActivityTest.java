package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.UiThread;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Calendar;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;




@LargeTest
@RunWith(AndroidJUnit4.class)
public class DisplayActivityTest {


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


    //UI thread test because we need to access UI elements (Textviews, etc...)
    @UiThreadTest
    @Test
    public void DisplayActivityProperly() throws Exception {


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

        DataProvider testDataProvider = mock(DataProvider.class);


        when(testDataProvider.getActivityFromUid(any(DataProvider.DataProviderListenerActivity.class), anyString())).thenAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivity listener = (DataProvider.DataProviderListenerActivity) args[0];
                listener.getActivity(dA);
                return null;
            }
        });


        final DisplayActivity activity = displayActivityRule.getActivity();

        activity.setTestDBObjects(testDataProvider, testFirebaseUser);

        activity.initDisplay();

        Assert.assertThat(activity.title.getText().toString(), is(dA.getTitle()));
        Assert.assertThat(activity.description.getText().toString(), is(dA.getDescription()));
        Assert.assertThat(activity.description.getText().toString(), is(dA.getDescription()));

   }

}
