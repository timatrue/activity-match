package ch.epfl.sweng.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class DataProviderTest {

    final private String uuidTest = "uuid-test-111";
    final private String uuidTest2 = "uuid-test-222";


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
    @Mock
    FirebaseUser mUser;
    @Mock
    DataSnapshot dsChild1;
    @Mock
    DataSnapshot dsChild2;
    @Mock
    DataSnapshot dsChild3;
    @Mock
    Query mQuery;


    private final DeboxActivity deboxActivityTest = new DeboxActivity(uuidTest,"test", "user-test",
            "description",
            Calendar.getInstance(),
            Calendar.getInstance(),
            122.01,
            121.0213,
            "Sports");

    private final DeboxActivity deboxActivityTest2 = new DeboxActivity(uuidTest2,"test2", "user-test2",
            "description2",
            Calendar.getInstance(),
            Calendar.getInstance(),
            122.01,
            121.0213,
            "Sports");



    private final Map<String, Object> activityMap = new HashMap<>();


    @Test
    public void testPushActivity() {

        String uuidTest = "fake-uuid-test";
        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        mChild = Mockito.mock(DatabaseReference.class);
        mPush = Mockito.mock(DatabaseReference.class);
        database = Mockito.mock(FirebaseDatabase.class);
        mUser = Mockito.mock(FirebaseUser.class);

        final String userUid = "user-uid-test";
        final String fakeOrganisedKey = "fake-organiser-uid-key";
        when(mUser.getUid()).thenReturn(userUid);
        when(mDataBaseRef.child("users")).thenReturn(mDataBaseRef);
        when(mDataBaseRef.child(userUid)).thenReturn(mDataBaseRef);
        when(mDataBaseRef.child("organised")).thenReturn(mDataBaseRef);
        when(mDataBaseRef.push()).thenReturn(mDataBaseRef);
        when(mDataBaseRef.getKey()).thenReturn(fakeOrganisedKey);

        when(mDataBaseRef.child("activities")).thenReturn(mChild);
        when(mChild.push()).thenReturn(mPush);
        when(mPush.getKey()).thenReturn(uuidTest);
        when(mDataBaseRef.updateChildren(anyMap())).thenReturn(null);


        DataProvider dp = new DataProvider(mDataBaseRef,database,mUser);
        String result = dp.pushActivity(deboxActivityTest);

        assertEquals(uuidTest,result);

    }



    @Test
    public void testGetActivityFromUid() {
        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        database = Mockito.mock(FirebaseDatabase.class);
        myRef = Mockito.mock(DatabaseReference.class);
        mUser = Mockito.mock(FirebaseUser.class);

        when(database.getReference("activities/" + uuidTest)).thenReturn(myRef);

        //Create Map from deboxactivities
        activityMap.put("title", deboxActivityTest.getTitle());
        activityMap.put("description", deboxActivityTest.getDescription());
        activityMap.put("category", deboxActivityTest.getCategory());
        activityMap.put("latitude", deboxActivityTest.getLocation()[0]);
        activityMap.put("longitude", deboxActivityTest.getLocation()[1]);
        activityMap.put("organizer", deboxActivityTest.getOrganizer());
        activityMap.put("timeEnd", deboxActivityTest.getTimeEnd().getTimeInMillis());
        activityMap.put("timeStart", deboxActivityTest.getTimeStart().getTimeInMillis());

        //Override getValue() to always return the Map for the test
        when(ds.getValue()).thenReturn(activityMap);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds);
                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        //Override getReference method to return the Mock reference
        when(database.getReference("activities/" + uuidTest)).thenReturn(myRef);

        //Test DataProvider getActivityFromUid method, check that it calls the listener and gives
        //it a proper DeboxActivity, corresponding to the Map values
        DataProvider dp = new DataProvider(myRef,database,mUser);
        dp.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
            @Override
            public void getActivity(DeboxActivity activity) {
                assertEquals(activity.getTitle(),deboxActivityTest.getTitle());
                assertEquals(activity.getDescription(),deboxActivityTest.getDescription());
                assertEquals(activity.getCategory(),deboxActivityTest.getCategory());
                assertEquals(activity.getId(),deboxActivityTest.getId());
                assertTrue(activity.getLocation()[0] == deboxActivityTest.getLocation()[0]);
                assertTrue(activity.getLocation()[1] == deboxActivityTest.getLocation()[1]);
                assertEquals(activity.getTimeEnd().getTimeInMillis(),deboxActivityTest.getTimeEnd().getTimeInMillis());
                assertEquals(activity.getTimeStart().getTimeInMillis(),deboxActivityTest.getTimeStart().getTimeInMillis());
            }

        }, uuidTest);
    }



    @Test
    public void testUserEnrolledInActivity() {

        database = Mockito.mock(FirebaseDatabase.class);
        myRef = Mockito.mock(DatabaseReference.class);
        mUser = Mockito.mock(FirebaseUser.class);
        ds = Mockito.mock(DataSnapshot.class);


        final String userUid = "user-uid-test";
        final String userEmail ="test-fake-email@gmail.com";

        when(mUser.getUid()).thenReturn(userUid);
        when(mUser.getEmail()).thenReturn(userEmail);


        //Override getReference method to return the Mock reference
        when(database.getReference("users/" + userUid + "/enrolled")).thenReturn(myRef);


        //Build fake result of the database (user enrolled in activity : fakeActivityID,uuidTest and fakeActivityID2
        final Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> entry;

        entry = new HashMap<>();
        entry.put("activity ID:","fakeActivityID");
        resultMap.put("testnumber1",entry);

        entry = new HashMap<>();
        entry.put("activity ID:",uuidTest);
        resultMap.put("testnumber2",entry);

        entry = new HashMap<>();
        entry.put("activity ID:","fakeActivityID2");
        resultMap.put("testnumber3",entry);

        //Override getValue() to always return the Map for the test
        when(ds.getValue()).thenReturn(resultMap);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds);
                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        DataProvider dp = new DataProvider(myRef,database,mUser);

        //check if the user is indeed enrolled in activity uuidTest
        dp.userEnrolledInActivity(new DataProvider.DataProviderListenerEnrolled() {
            @Override
            public void getIfEnrolled(boolean result) {
                assertEquals(true,result);

            }
        },uuidTest);

        final String nonEnrolledUidActivity ="1111";

        //check if the user is indeed not enrolled in activity nonEnrolledUidActivity
        dp.userEnrolledInActivity(new DataProvider.DataProviderListenerEnrolled() {
            @Override
            public void getIfEnrolled(boolean result) {
                assertEquals(false,result);

            }
        },nonEnrolledUidActivity);

    }

    @Test
    public void testGetAllCategories() {

        database = Mockito.mock(FirebaseDatabase.class);
        myRef = Mockito.mock(DatabaseReference.class);
        ds = Mockito.mock(DataSnapshot.class);
        mUser = Mockito.mock(FirebaseUser.class);

        when(database.getReference("categories")).thenReturn(myRef);

        final String [][] testCategory = {{"1","2","3"},{"Sports","Culture","Undefined"}};

        dsChild1 = Mockito.mock(DataSnapshot.class);
        dsChild2 = Mockito.mock(DataSnapshot.class);
        dsChild3 = Mockito.mock(DataSnapshot.class);

        when(dsChild1.getKey()).thenReturn(testCategory[0][0]);
        when(dsChild1.getValue()).thenReturn(testCategory[1][0]);

        when(dsChild2.getKey()).thenReturn(testCategory[0][1]);
        when(dsChild2.getValue()).thenReturn(testCategory[1][1]);

        when(dsChild3.getKey()).thenReturn(testCategory[0][2]);
        when(dsChild3.getValue()).thenReturn(testCategory[1][2]);

        DataSnapshot [] listDS = {dsChild1,dsChild2,dsChild3};

        Iterable<DataSnapshot> iterable = Arrays.asList(listDS);

        //Overrride getChildren to always return interable of DataSnapshot
        when(ds.getChildren()).thenReturn(iterable);

        //Override addListenerForSingleValueEvent method for test to always return our value
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds);
                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.getAllCategories(new DataProvider.DataProviderListenerCategories() {
            @Override
            public void getCategories(List<DataProvider.CategoryName> categoriesList) {
                assertEquals(true,true);

                for(int i = 0; i<testCategory.length;i++){
                    assertEquals(categoriesList.get(i).getCategoryId(),testCategory[0][i]);
                    assertEquals(categoriesList.get(i).getCategory(),testCategory[1][i]);
                }
            }
        });
    }

    @Test
    public void testGetSpecifiedCategory() {

        database = Mockito.mock(FirebaseDatabase.class);
        myRef = Mockito.mock(DatabaseReference.class);
        mQuery = Mockito.mock(Query.class);
        ds = Mockito.mock(DataSnapshot.class);
        mUser = Mockito.mock(FirebaseUser.class);
        ds = Mockito.mock(DataSnapshot.class);
        dsChild1 = Mockito.mock(DataSnapshot.class);
        dsChild2 = Mockito.mock(DataSnapshot.class);

        when(database.getReference("activities")).thenReturn(myRef);
        when(myRef.orderByChild("category")).thenReturn(mQuery);
        when(mQuery.equalTo(anyString())).thenReturn(mQuery);


        final Map<String, Object> activityMapCat = new HashMap<>();

        activityMapCat.put("title", deboxActivityTest.getTitle());
        activityMapCat.put("description", deboxActivityTest.getDescription());
        activityMapCat.put("category", deboxActivityTest.getCategory());
        activityMapCat.put("latitude", deboxActivityTest.getLocation()[0]);
        activityMapCat.put("longitude", deboxActivityTest.getLocation()[1]);
        activityMapCat.put("organizer", deboxActivityTest.getOrganizer());
        activityMapCat.put("timeEnd", deboxActivityTest.getTimeEnd().getTimeInMillis());
        activityMapCat.put("timeStart", deboxActivityTest.getTimeStart().getTimeInMillis());

        when(dsChild1.getKey()).thenReturn(uuidTest);
        when(dsChild1.getValue()).thenReturn(activityMapCat);

        when(dsChild2.getKey()).thenReturn(uuidTest);
        when(dsChild2.getValue()).thenReturn(activityMapCat);


        DataSnapshot [] listDS = {dsChild1,dsChild2};

        Iterable<DataSnapshot> iterable = Arrays.asList(listDS);

        //Overrride getChildren to always return interable of DataSnapshot
        when(ds.getChildren()).thenReturn(iterable);

        //Override addListenerForSingleValueEvent method for test to always return our value
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds);
                return null;
            }
        }).when(mQuery).addListenerForSingleValueEvent(any(ValueEventListener.class));


        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.getSpecifiedCategory(new DataProvider.DataProviderListenerCategory() {
            @Override
            public void getCategory(List<DeboxActivity> activitiesList) {
                //for(DeboxActivity activity: activitiesList.)
                assertEquals(activitiesList.size(),2);
                for(int i = 0; i<activitiesList.size();i++)
                {
                    DeboxActivity activity = activitiesList.get(i);
                    assertEquals(activity.getTitle(),deboxActivityTest.getTitle());
                    assertEquals(activity.getDescription(),deboxActivityTest.getDescription());
                    assertEquals(activity.getCategory(),deboxActivityTest.getCategory());
                    assertEquals(activity.getId(),deboxActivityTest.getId());
                    assertTrue(activity.getLocation()[0] == deboxActivityTest.getLocation()[0]);
                    assertTrue(activity.getLocation()[1] == deboxActivityTest.getLocation()[1]);
                    assertEquals(activity.getTimeEnd().getTimeInMillis(),deboxActivityTest.getTimeEnd().getTimeInMillis());
                    assertEquals(activity.getTimeStart().getTimeInMillis(),deboxActivityTest.getTimeStart().getTimeInMillis());
                }

            }
        },deboxActivityTest.getCategory());

    }


    @Test
    public void testGetAllActivities() {

        database = Mockito.mock(FirebaseDatabase.class);
        myRef = Mockito.mock(DatabaseReference.class);
        mQuery = Mockito.mock(Query.class);
        ds = Mockito.mock(DataSnapshot.class);
        mUser = Mockito.mock(FirebaseUser.class);
        ds = Mockito.mock(DataSnapshot.class);
        dsChild1 = Mockito.mock(DataSnapshot.class);
        dsChild2 = Mockito.mock(DataSnapshot.class);


        when(database.getReference("activities")).thenReturn(myRef);

        final Map<String, Object> activityMap1 = new HashMap<>();

        activityMap1.put("title", deboxActivityTest.getTitle());
        activityMap1.put("description", deboxActivityTest.getDescription());
        activityMap1.put("category", deboxActivityTest.getCategory());
        activityMap1.put("latitude", deboxActivityTest.getLocation()[0]);
        activityMap1.put("longitude", deboxActivityTest.getLocation()[1]);
        activityMap1.put("organizer", deboxActivityTest.getOrganizer());
        activityMap1.put("timeEnd", deboxActivityTest.getTimeEnd().getTimeInMillis());
        activityMap1.put("timeStart", deboxActivityTest.getTimeStart().getTimeInMillis());

        when(dsChild1.getKey()).thenReturn(uuidTest);
        when(dsChild1.getValue()).thenReturn(activityMap1);

        final Map<String, Object> activityMap2 = new HashMap<>();

        activityMap2.put("title", deboxActivityTest2.getTitle());
        activityMap2.put("description", deboxActivityTest2.getDescription());
        activityMap2.put("category", deboxActivityTest2.getCategory());
        activityMap2.put("latitude", deboxActivityTest2.getLocation()[0]);
        activityMap2.put("longitude", deboxActivityTest2.getLocation()[1]);
        activityMap2.put("organizer", deboxActivityTest2.getOrganizer());
        activityMap2.put("timeEnd", deboxActivityTest2.getTimeEnd().getTimeInMillis());
        activityMap2.put("timeStart", deboxActivityTest2.getTimeStart().getTimeInMillis());



        when(dsChild2.getKey()).thenReturn(uuidTest2);
        when(dsChild2.getValue()).thenReturn(activityMap2);


        final DataSnapshot [] listDS = {dsChild1,dsChild2};

        Iterable<DataSnapshot> iterable = Arrays.asList(listDS);

        //Overrride getChildren to always return interable of DataSnapshot
        when(ds.getChildren()).thenReturn(iterable);


        //Override addListenerForSingleValueEvent method for test to always return our value
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds);
                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));


        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.getAllActivities(new DataProvider.DataProviderListenerActivities() {
            @Override
            public void getActivities(List<DeboxActivity> activitiesList) {

                assertEquals(activitiesList.size(),listDS.length);

                DeboxActivity activity = activitiesList.get(0);

                assertEquals(activity.getTitle(),deboxActivityTest.getTitle());
                assertEquals(activity.getDescription(),deboxActivityTest.getDescription());
                assertEquals(activity.getCategory(),deboxActivityTest.getCategory());
                assertEquals(activity.getId(),deboxActivityTest.getId());
                assertTrue(activity.getLocation()[0] == deboxActivityTest.getLocation()[0]);
                assertTrue(activity.getLocation()[1] == deboxActivityTest.getLocation()[1]);
                assertEquals(activity.getTimeEnd().getTimeInMillis(),deboxActivityTest.getTimeEnd().getTimeInMillis());
                assertEquals(activity.getTimeStart().getTimeInMillis(),deboxActivityTest.getTimeStart().getTimeInMillis());


                activity = activitiesList.get(1);

                assertEquals(activity.getTitle(),deboxActivityTest2.getTitle());
                assertEquals(activity.getDescription(),deboxActivityTest2.getDescription());
                assertEquals(activity.getCategory(),deboxActivityTest2.getCategory());
                assertEquals(activity.getId(),deboxActivityTest2.getId());
                assertTrue(activity.getLocation()[0] == deboxActivityTest2.getLocation()[0]);
                assertTrue(activity.getLocation()[1] == deboxActivityTest2.getLocation()[1]);
                assertEquals(activity.getTimeEnd().getTimeInMillis(),deboxActivityTest2.getTimeEnd().getTimeInMillis());
                assertEquals(activity.getTimeStart().getTimeInMillis(),deboxActivityTest2.getTimeStart().getTimeInMillis());

            }
        });
    }

    @Test
    public void testGetIfPlaceLeftInActivity(){

        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        database = Mockito.mock(FirebaseDatabase.class);
        mUser = Mockito.mock(FirebaseUser.class);

        DatabaseReference myRef1 = Mockito.mock(DatabaseReference.class);
        DatabaseReference myRef2= Mockito.mock(DatabaseReference.class);
        DatabaseReference myRef3 = Mockito.mock(DatabaseReference.class);

        final String uuidTest1 = "uuid-test-111";
        final String uuidTest2 = "uuid-test-222";
        final String uuidTest3 = "uuid-test-333";

        final DataSnapshot ds1 = Mockito.mock(DataSnapshot.class);
        final DataSnapshot ds2 = Mockito.mock(DataSnapshot.class);
        final DataSnapshot ds3 = Mockito.mock(DataSnapshot.class);

        when(database.getReference("activities/" + uuidTest1)).thenReturn(myRef1);
        when(database.getReference("activities/" + uuidTest2)).thenReturn(myRef2);
        when(database.getReference("activities/" + uuidTest3)).thenReturn(myRef3);


        final Map<String, Object> activityMap1 = new HashMap<>();
        //Create Map for deboxActivity
        activityMap1.put("title", deboxActivityTest.getTitle());
        activityMap1.put("description", deboxActivityTest.getDescription());
        activityMap1.put("category", deboxActivityTest.getCategory());
        activityMap1.put("latitude", deboxActivityTest.getLocation()[0]);
        activityMap1.put("longitude", deboxActivityTest.getLocation()[1]);
        activityMap1.put("organizer", deboxActivityTest.getOrganizer());
        activityMap1.put("timeEnd", deboxActivityTest.getTimeEnd().getTimeInMillis());
        activityMap1.put("timeStart", deboxActivityTest.getTimeStart().getTimeInMillis());
        activityMap1.put("nbMaxOfParticipants",-1);
        activityMap1.put("nbOfParticipants", 10);

        final Map<String, Object> activityMap2 = new HashMap<>();
        //Create Map for deboxActivity
        activityMap2.put("title", deboxActivityTest.getTitle());
        activityMap2.put("description", deboxActivityTest.getDescription());
        activityMap2.put("category", deboxActivityTest.getCategory());
        activityMap2.put("latitude", deboxActivityTest.getLocation()[0]);
        activityMap2.put("longitude", deboxActivityTest.getLocation()[1]);
        activityMap2.put("organizer", deboxActivityTest.getOrganizer());
        activityMap2.put("timeEnd", deboxActivityTest.getTimeEnd().getTimeInMillis());
        activityMap2.put("timeStart", deboxActivityTest.getTimeStart().getTimeInMillis());
        activityMap2.put("nbMaxOfParticipants",10);
        activityMap2.put("nbOfParticipants", 5);

        final Map<String, Object> activityMap3 = new HashMap<>();
        //Create Map for deboxActivity
        activityMap3.put("title", deboxActivityTest.getTitle());
        activityMap3.put("description", deboxActivityTest.getDescription());
        activityMap3.put("category", deboxActivityTest.getCategory());
        activityMap3.put("latitude", deboxActivityTest.getLocation()[0]);
        activityMap3.put("longitude", deboxActivityTest.getLocation()[1]);
        activityMap3.put("organizer", deboxActivityTest.getOrganizer());
        activityMap3.put("timeEnd", deboxActivityTest.getTimeEnd().getTimeInMillis());
        activityMap3.put("timeStart", deboxActivityTest.getTimeStart().getTimeInMillis());
        activityMap3.put("nbMaxOfParticipants",10);
        activityMap3.put("nbOfParticipants", 10);


        //Override getValue() to always return the Map for the test
        when(ds1.getValue()).thenReturn(activityMap1);
        when(ds2.getValue()).thenReturn(activityMap2);
        when(ds3.getValue()).thenReturn(activityMap3);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds1);
                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds2);
                return null;
            }
        }).when(myRef2).addListenerForSingleValueEvent(any(ValueEventListener.class));

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds3);
                return null;
            }
        }).when(myRef3).addListenerForSingleValueEvent(any(ValueEventListener.class));


        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.getIfPlaceLeftInActivity(uuidTest, new DataProvider.DataProviderListenerPlaceFreeInActivity() {
            @Override
            public void getIfFreePlace(boolean result) {
                assertTrue(result);
            }
        });

        dp.getIfPlaceLeftInActivity(uuidTest2, new DataProvider.DataProviderListenerPlaceFreeInActivity() {
            @Override
            public void getIfFreePlace(boolean result) {
                assertTrue(result);
            }
        });

        dp.getIfPlaceLeftInActivity(uuidTest3, new DataProvider.DataProviderListenerPlaceFreeInActivity() {
            @Override
            public void getIfFreePlace(boolean result) {
                assertFalse(result);
            }
        });

    }

    @Test
    public void testUserProfile(){

        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        database = Mockito.mock(FirebaseDatabase.class);
        mUser = Mockito.mock(FirebaseUser.class);
        myRef = Mockito.mock(DatabaseReference.class);

        final DataSnapshot ds1 = Mockito.mock(DataSnapshot.class);

        final String fakeUserID1 = "fakeUserID1";
        final String fakeName = "fakeUserName";
        final String fakeEmail = "fakeemail@gmail.com";
        final String uuidTest1 = "uuid-test-111";
        final String uuidTest2 = "uuid-test-222";
        final String uuidTest3 = "uuid-test-333";
        final int ratingNb = 3;
        final int ratingSum = 14;

        when(mUser.getUid()).thenReturn(fakeUserID1);
        when(database.getReference(any(String.class))).thenReturn(myRef);

        final Map<String, Object> enrolledMap = new HashMap<>();
        final Map<String, Object> enrolledMap1 = new HashMap<>();
        enrolledMap1.put("activity ID:",uuidTest1);
        final Map<String, Object> enrolledMap2 = new HashMap<>();
        enrolledMap2.put("activity ID:",uuidTest2);

        enrolledMap.put("enrolledID1",enrolledMap1);
        enrolledMap.put("enrolledID2",enrolledMap2);


        final Map<String, Object> organisedMap1 = new HashMap<>();
        organisedMap1.put("activity ID:",uuidTest3);

        final Map<String, Object> organisedMap = new HashMap<>();
        organisedMap.put("organisedID1",organisedMap1);

        final Map<String, Object> userMap = new HashMap<>();

        userMap.put("default_user_name",fakeName);
        userMap.put("enrolled",enrolledMap);
        userMap.put("organised",organisedMap);
        userMap.put("ratingNb",ratingNb);
        userMap.put("ratingSum",ratingSum);
        userMap.put("user_email",fakeEmail);

        when(ds1.getValue()).thenReturn(userMap);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds1);
                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));


        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.userProfile(new DataProvider.DataProviderListenerUserInfo() {
            @Override
            public void getUserInfo(User user) {
                List<String> checkInterested = new ArrayList<>();
                checkInterested.add(uuidTest1);
                checkInterested.add(uuidTest2);
                assertEquals(user.getInterestedEventIds(),checkInterested);

                List<String> checkOrganizedList = new ArrayList<>();
                checkOrganizedList.add(uuidTest3);
                assertEquals(user.getOrganizedEventIds(),checkOrganizedList);

                assertEquals(user.getRatingNb(),ratingNb);
                assertEquals(user.getRatingSum(),ratingSum);
                assertEquals(user.getRating(),((double)ratingSum)/ratingNb,0.0);
                assertEquals(user.getPhotoLink(),"");
                assertEquals(user.getEmail(),fakeEmail);
                assertEquals(user.getUsername(),fakeName);

            }
        });

    }

}