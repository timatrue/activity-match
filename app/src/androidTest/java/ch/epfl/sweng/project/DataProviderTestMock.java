package ch.epfl.sweng.project;

/**
 * Created by nathan on 03.11.16.
 */



import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.Calendar;

@RunWith(MockitoJUnitRunner.class)
public class DataProviderTestMock {

    //private static final String FAKE_KEY = "fake_key";

    @Mock
    DatabaseReference mDataBaseRef;
    @Mock
    DatabaseReference mChild;
    @Mock
    DatabaseReference mPush;

    @Test
    public void testPushActivity() {

        String uuidTest = "fake-uuid-test";
        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        mChild = Mockito.mock(DatabaseReference.class);
        mPush = Mockito.mock(DatabaseReference.class);


        when(mDataBaseRef.child("activities")).thenReturn(mChild);
        when(mChild.push()).thenReturn(mPush);
        when(mPush.getKey()).thenReturn(uuidTest);

        when(mDataBaseRef.updateChildren(anyMap())).thenReturn(null);

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


        DataProvider dp = new DataProvider(mDataBaseRef);
        String result = dp.pushActivity(dA);

        assertEquals(uuidTest,result);

    }
}