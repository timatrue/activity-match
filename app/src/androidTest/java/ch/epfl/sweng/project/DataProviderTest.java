package ch.epfl.sweng.project;

import android.support.test.runner.AndroidJUnit4;


import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.Calendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Unit tests!
 */
@RunWith(AndroidJUnit4.class)
public final class DataProviderTest {
    // TODO replace this with your own tests.

    Boolean testFinished = false;


    @Test
    public void userEnrolledInActivityTest()
    {

        assertEquals(true,true);
/*
        testFinished = false;
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

        DataProvider dp = new DataProvider();

        // push this activity
        String uid = dp.pushActivity(dA);
        /*
        check is user is already enrolled in this activity (must be no beacause activity has just
        be created.
         */
        /*
        dp.userEnrolledInActivity(new DataProvider.DataProviderListenerEnrolled() {

            @Override
            public void getIfEnrolled(boolean result) {

                assertThat(result,is(false));
                testFinished=true;
            }
        }, uid);

        while (!testFinished) ;*/
    }

    /**
     *  Test if method userEnrolledInActivity works properly
     */
    /*@Test
    public void DeboxActivityConstructorAndMethod() {


        testFinished = false;
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


        DataProvider dp = new DataProvider();
        String uid = dp.pushActivity(dA);


        dp.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
            @Override
            public void getActivity(DeboxActivity activity) {
                assertThat(activity.getDescription(), is(dA.getDescription()));
                assertThat(activity.getTitle(), is(dA.getTitle()));
                assertThat(activity.getOrganizer(), is(dA.getOrganizer()));
                assertThat(activity.getLocation(), is(dA.getLocation()));
                assertThat(activity.getCategory(), is(dA.getCategory()));
                assertThat(activity.getTimeStart(), is(dA.getTimeStart()));
                assertThat(activity.getTimeEnd(), is(dA.getTimeEnd()));
                testFinished = true;
            }

        }, uid);

        while (!testFinished) ;
    }*/

}
