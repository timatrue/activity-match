package ch.epfl.sweng.project;

        import android.support.test.runner.AndroidJUnit4;

        import org.hamcrest.Matchers;
        import org.junit.Test;
        import org.junit.runner.RunWith;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Calendar;
        import java.util.List;

        import static org.hamcrest.CoreMatchers.is;
        import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public final class DeboxActivityTest {

    @Test
    public void DeboxActivityConstructorAndMethod() {

        final String testDaId = "zdkasKKLD";
        final String testOrganizer = "Nathan";
        final String testTitle = "Football in UNIL sport center";
        final String testDescription = "Indoor football tournaments open to every student " +
                "of UNIL and EPFL, teams are formed 15 minutes before and tournament consists of 11 " +
                "minutes games";

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DATE, 2);
        startCalendar.add(Calendar.HOUR, 2);
        startCalendar.add(Calendar.MINUTE, 30);
        final int startYear = startCalendar.get(Calendar.YEAR);
        final int startMonth = startCalendar.get(Calendar.MONTH);
        final int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        final int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        final int startMinute = startCalendar.get(Calendar.MINUTE);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DATE, 2);
        endCalendar.add(Calendar.HOUR, 2);
        endCalendar.add(Calendar.MINUTE, 50);
        final int endYear = endCalendar.get(Calendar.YEAR);
        final int endMonth = endCalendar.get(Calendar.MONTH);
        final int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        final int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        final int endMinute = endCalendar.get(Calendar.MINUTE);

        final double testLatitude = 0.3;
        final double testLongitude = 1;
        final String testCategory = "Sports";
        List<String> testImageList = new ArrayList<>();
        testImageList.add("image1");
        testImageList.add("image2");
        final int testNbOfParticipants = 10;
        final int testNbMaxParticipants = 20;

        DeboxActivity dA = new DeboxActivity(
                testDaId,
                testOrganizer,
                testTitle,
                testDescription,
                startCalendar,
                endCalendar,
                testLatitude,
                testLongitude,
                testCategory,
                testImageList,
                testNbOfParticipants,
                testNbMaxParticipants);

        DeboxActivity dA2 = new DeboxActivity(
                testDaId,
                testOrganizer,
                testTitle,
                testDescription,
                startCalendar,
                endCalendar,
                testLatitude,
                testLongitude,
                testCategory,
                testNbOfParticipants,
                testNbMaxParticipants);

        DeboxActivity dA3 = new DeboxActivity(
                testDaId,
                testOrganizer,
                testTitle,
                testDescription,
                startCalendar,
                endCalendar,
                testLatitude,
                testLongitude,
                testCategory,
                testImageList);

        DeboxActivity dA4 = new DeboxActivity(
                testDaId,
                testOrganizer,
                testTitle,
                testDescription,
                startCalendar,
                endCalendar,
                testLatitude,
                testLongitude,
                testCategory,
                null);


        //checks if each constructor works properly
        assertThat(dA.getId(), is (dA2.getId()));
        assertThat(dA.getId(), is (dA3.getId()));
        assertThat(dA2.getImageList().isEmpty(), is(true));
        assertThat(dA3.getNbOfParticipants(), is(0));
        assertThat(dA3.getNbMaxOfParticipants(), is(-1));

        //checks the methods
        assertThat(dA.getId(), is(testDaId));
        assertThat(dA.getOrganizer(), is(testOrganizer));
        assertThat(dA.getTitle(), is(testTitle));

        assertThat(dA.getShortDescription(13).length(), is(13));
        assertThat(dA.getShortDescription().length(), is(64));
        assertThat(dA.getShortDescription(testDescription.length() + 50).length(), is(testDescription.length()));
        assertThat(dA.getShortDescription(2), is("..."));
        assertThat(dA.getDescription(), is(testDescription));

        assertThat(dA.getTimeStart().get(Calendar.YEAR), Matchers.is(startYear));
        assertThat(dA.getTimeStart().get(Calendar.MONTH), Matchers.is(startMonth));
        assertThat(dA.getTimeStart().get(Calendar.DAY_OF_MONTH), Matchers.is(startDay));
        assertThat(dA.getTimeStart().get(Calendar.HOUR_OF_DAY), Matchers.is(startHour));
        assertThat(dA.getTimeStart().get(Calendar.MINUTE), Matchers.is(startMinute));
        assertThat(dA.getTimeEnd().get(Calendar.YEAR), Matchers.is(endYear));
        assertThat(dA.getTimeEnd().get(Calendar.MONTH), Matchers.is(endMonth));
        assertThat(dA.getTimeEnd().get(Calendar.DAY_OF_MONTH), Matchers.is(endDay));
        assertThat(dA.getTimeEnd().get(Calendar.HOUR_OF_DAY), Matchers.is(endHour));
        assertThat(dA.getTimeEnd().get(Calendar.MINUTE), Matchers.is(endMinute));

        assertThat(dA.getLocation()[0], Matchers.is(testLatitude));
        assertThat(dA.getLocation()[1], Matchers.is(testLongitude));
        assertThat(dA.getCategory(), is(testCategory));
        assertThat(dA.getImageList(), is(testImageList));
        assertThat(dA.getNbOfParticipants(), is(testNbOfParticipants));
        assertThat(dA.getNbMaxOfParticipants(), is(testNbMaxParticipants));

        //checks the addImage method
        final String newImage = "image3";
        testImageList.add(newImage);
        dA.addImage(newImage);
        assertThat(dA.getImageList(), is(testImageList));

        //checks the case the image list provided is null
        assertThat(dA4.getImageList(), is((List<String>) null));
    }
}