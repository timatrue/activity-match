package ch.epfl.sweng.project;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests!
 */
@RunWith(AndroidJUnit4.class)
public final class DeboxActivityTest {
    // TODO replace this with your own tests.

    @Test
    public void DeboxActivityConstructorAndMethod() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        startDate.set(Calendar.YEAR, 2016);
        startDate.set(Calendar.MONTH, 11);

        DeboxActivity dA = new DeboxActivity("Nathan",
                "Football in UNIL sport center", "Indoor football tournaments open to every student " +
                "of UNIL and EPFL, teams are formed 15 minutes before and tournament consists of 11 " +
                "minutes games",
                startDate,
                endDate,
                122.01,
                121.0213,
                "Sports");


        assertThat(dA.getOrganizer(), is("Nathan"));
        assertThat(dA.getTitle(), is("Football in UNIL sport center"));

        assertThat(dA.getShortDescription(13).length(), is(13));

        assertThat(dA.getShortDescription().length(), is(64));

        assertThat(dA.getLocation()[0], is(122.01));
        assertThat(dA.getLocation()[1], is(121.0213));

        assertThat(dA.getTimeStart().get(Calendar.MONTH), is(11));
    }
}
