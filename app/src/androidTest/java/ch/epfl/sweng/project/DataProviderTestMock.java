package ch.epfl.sweng.project;

/**
 * Created by nathan on 03.11.16.
 */



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.asm.tree.analysis.Value;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class DataProviderTestMock {

    final private String uuidTest = "uuid-test-111";

    @Mock
    DatabaseReference mDataBaseRef;
    @Mock
    DatabaseReference mChild;
    @Mock
    DatabaseReference mPush;
    @Mock
    DatabaseReference myRef;
    @Mock
    FirebaseDatabase database;
    @Mock
    DataSnapshot ds;

    final DeboxActivity deboxActivityTest = new DeboxActivity(uuidTest,"test", "user-test",
            "description",
            Calendar.getInstance(),
            Calendar.getInstance(),
            122.01,
            121.0213,
            "Sports");

    final Map<String, Object> activityMap = new HashMap<String, Object>();


    @Test
    public void testPushActivity() {

        String uuidTest = "fake-uuid-test";
        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        mChild = Mockito.mock(DatabaseReference.class);
        mPush = Mockito.mock(DatabaseReference.class);
        database = Mockito.mock(FirebaseDatabase.class);


        when(mDataBaseRef.child("activities")).thenReturn(mChild);
        when(mChild.push()).thenReturn(mPush);
        when(mPush.getKey()).thenReturn(uuidTest);

        when(mDataBaseRef.updateChildren(anyMap())).thenReturn(null);


        DataProvider dp = new DataProvider(mDataBaseRef,database);
        String result = dp.pushActivity(deboxActivityTest);

        assertEquals(uuidTest,result);

    }



    @Test
    public void testGetActivityFromUid() {


        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        database = Mockito.mock(FirebaseDatabase.class);
        myRef = Mockito.mock(DatabaseReference.class);

        when(database.getReference("activities/" + uuidTest)).thenReturn(myRef);


        activityMap.put("title", deboxActivityTest.getTitle());
        activityMap.put("description", deboxActivityTest.getDescription());
        activityMap.put("category", deboxActivityTest.getCategory());
        activityMap.put("latitude", deboxActivityTest.getLocation()[0]);
        activityMap.put("longitude", deboxActivityTest.getLocation()[1]);
        activityMap.put("organizer", deboxActivityTest.getOrganizer());
        activityMap.put("timeEnd", deboxActivityTest.getTimeEnd().getTimeInMillis());
        activityMap.put("timeStart", deboxActivityTest.getTimeStart().getTimeInMillis());


        when(ds.getValue()).thenReturn(activityMap);
        //when(myRef.addListenerForSingleValueEvent(any(ValueEventListener.class))).

        // to be verified!! absolutely not sure ...


        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();

                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds);

                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));


        when(database.getReference("activities/" + uuidTest)).thenReturn(myRef);

        DataProvider dp = new DataProvider(myRef,database);

        dp.getActivityFromUid(new DataProvider.DataProviderListener() {
            @Override
            public void getActivity(DeboxActivity activity) {
                assertEquals(activity.getTitle(),deboxActivityTest.getTitle());
                assertEquals(activity.getDescription(),deboxActivityTest.getDescription());
                assertEquals(activity.getCategory(),deboxActivityTest.getCategory());
                assertTrue(activity.getLocation()[0] == deboxActivityTest.getLocation()[0]);
                assertTrue(activity.getLocation()[1] == deboxActivityTest.getLocation()[1]);
                assertEquals(activity.getTimeEnd().getTimeInMillis(),deboxActivityTest.getTimeEnd().getTimeInMillis());
                assertEquals(activity.getTimeStart().getTimeInMillis(),deboxActivityTest.getTimeStart().getTimeInMillis());
            }

            @Override
            public void getActivities(List<DeboxActivity> activitiesList) {

            }

            @Override
            public void getIfEnrolled(boolean result) {

            }
        }, uuidTest);
    }




}