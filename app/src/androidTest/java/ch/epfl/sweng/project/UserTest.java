package ch.epfl.sweng.project;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
public class UserTest {

    @Test
    public void UserConstructorAndMethod() {

        final String testId = "fuhrh";
        final String testUsername = "Bob";
        final String testEmail = "Bob@epfl.ch";
        List<String> testOrganizedEvents = new ArrayList<>();
        testOrganizedEvents.add("Event1");
        testOrganizedEvents.add("Event2");
        List<String> testInterestedEvents = new ArrayList<>();
        testInterestedEvents.add("Event3");
        testInterestedEvents.add("Event4");
        final int testRatingNb = 10;
        final int testRatingSum = 44;
        final String testPhotoLink = "photo";


        User testUser = new User(
                testId,
                testUsername,
                testEmail,
                testOrganizedEvents,
                testInterestedEvents,
                testRatingNb,
                testRatingSum,
                testPhotoLink);

        final int newTestratingNb = 0;

        User testUser2 = new User(
                testId,
                testUsername,
                testEmail,
                testOrganizedEvents,
                testInterestedEvents,
                newTestratingNb,
                testRatingSum,
                testPhotoLink);

        assertThat(testUser.getId(), is(testId));
        assertThat(testUser.getUsername(), is(testUsername));
        assertThat(testUser.getEmail(), is(testEmail));
        assertThat(testUser.getOrganizedEventIds(), is(testOrganizedEvents));
        assertThat(testUser.getInterestedEventIds(), is(testInterestedEvents));
        assertThat(testUser.getRatingNb(), is(testRatingNb));
        assertThat(testUser.getRatingSum(), is(testRatingSum));
        assertThat(testUser.getRating(), is((((double)testRatingSum)/testRatingNb)));
        assertThat(testUser.getPhotoLink(), is(testPhotoLink));
        assertThat(testUser.getId(), is(testUser.copy().getId()));
        assertThat(testUser2.getRating(), is(-1.0));
    }
}
