package ch.epfl.sweng.project;

/**
 * Created by nathan on 03.11.16.
 */



import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.asm.tree.analysis.Value;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;

@RunWith(MockitoJUnitRunner.class)
public class DataProviderTestMock {

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

    final DeboxActivity deboxActivityTest = new DeboxActivity("id","test", "user-test",
            "description",
            Calendar.getInstance(),
            Calendar.getInstance(),
            122.01,
            121.0213,
            "Sports");



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

        String uuidTest = "uuid-test-111";
        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        database = Mockito.mock(FirebaseDatabase.class);
        myRef = Mockito.mock(DatabaseReference.class);

        when(database.getReference("activities/" + uuidTest)).thenReturn(myRef);



        //when(myRef.addListenerForSingleValueEvent(any(ValueEventListener.class))).

        // to be verified!! absolutely not sure ...

        Mockito.doThrow(new Exception()).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();

                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));









    }




}