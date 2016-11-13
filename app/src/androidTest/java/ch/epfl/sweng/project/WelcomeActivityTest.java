package ch.epfl.sweng.project;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.epfl.sweng.project.uiobjects.ActivityPreview;

//import static ch.epfl.sweng.project.MockDataProvider.setActivity;
//import static ch.epfl.sweng.project.MockDataProvider.getMockDataProvider;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {

    //@Mock
    //FirebaseUser testFirebaseUser;

    @Rule
    public ActivityTestRule<WelcomeActivity> welcomeActivityRule =
            new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class){
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, MainActivity.class);
                    result.putExtra(WelcomeActivity.WELCOME_ACTIVITY_TEST_KEY, WelcomeActivity.WELCOME_ACTIVITY_TEST);
                    return result;
                }
            };


    //UI thread test because we need to access UI elements (Textviews, etc...)
    @UiThreadTest
    @Test
    public void DisplayActivitiesProperly() throws Exception {

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


    @Test
    public void tmpTest(){

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();
        //DataProvider dp = getMockDataProvider();

        final DeboxActivity dbat = new DeboxActivity("-","test", "user-test",
                "description",
                Calendar.getInstance(),
                Calendar.getInstance(),
                122.01,
                121.0213,
                "Sports");

        if(dp==null)
        {
            assertEquals(true,false);

        } else
        {
            assertEquals(true,true);
        }

        //assertEquals(dp.pushActivity(dbat),"sample");
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

        mocDataProvider.setListOfActivitiesToMock(testActivityList);
        //setActivity(testActivityList);

        dp.getAllActivities(new DataProvider.DataProviderListenerActivities() {

            @Override
            public void getActivities(List<DeboxActivity> activitiesList) {

                for(DeboxActivity elem: activitiesList) {
                    assertEquals(elem.getCategory(),"Sports");
                }
            }
        });

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

        //DataProvider testDataProvider = getMockDataProvider();
        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();

        mocDataProvider.setListOfActivitiesToMock(testActivityList);

        //setActivity(testActivityList);

        final WelcomeActivity activity = welcomeActivityRule.getActivity();
        //Insert Mock DataProvider
        //activity.setDataProvider(testDataProvider);
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

}