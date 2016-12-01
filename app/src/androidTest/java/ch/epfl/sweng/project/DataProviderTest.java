package ch.epfl.sweng.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
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
import java.util.Iterator;
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
        final Map<String, Object> activityMap = toolsBuildMapFromDebox(deboxActivityTest);


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


        //final Map<String, Object> activityMapCat = new HashMap<>();
        final Map<String, Object> activityMapCat = toolsBuildMapFromDebox(deboxActivityTest);


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

        //final Map<String, Object> activityMap1 = new HashMap<>();
        final Map<String, Object> activityMap1 = toolsBuildMapFromDebox(deboxActivityTest);


        when(dsChild1.getKey()).thenReturn(uuidTest);
        when(dsChild1.getValue()).thenReturn(activityMap1);

        //final Map<String, Object> activityMap2 = new HashMap<>();
        final Map<String, Object> activityMap2 = toolsBuildMapFromDebox(deboxActivityTest2);

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


        //Create Map for deboxActivity
        final Map<String, Object> activityMap1 = toolsBuildMapFromDebox(deboxActivityTest,10,-1);

        //Create Map for deboxActivity
        final Map<String, Object> activityMap2 = toolsBuildMapFromDebox(deboxActivityTest,5,10);

        //Create Map for deboxActivity
        final Map<String, Object> activityMap3 = toolsBuildMapFromDebox(deboxActivityTest,10,10);

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

    @Test
    public void testInitUserInDB(){

        database = Mockito.mock(FirebaseDatabase.class);
        mUser = Mockito.mock(FirebaseUser.class);
        myRef = Mockito.mock(DatabaseReference.class);

        final String fakeUserID = "fakeUserID";
        final String fakeUserEmail = "fakeEmail@fake.com";
        final String fakeUserName = "fakeUserName";

        when(mUser.getUid()).thenReturn(fakeUserID);
        when(mUser.getEmail()).thenReturn(fakeUserEmail);
        when(mUser.getDisplayName()).thenReturn(fakeUserName);

        //when(myRef.child(anyString())).thenReturn(myRef);
        when(myRef.child("users")).thenReturn(myRef);
        when(myRef.child(fakeUserID)).thenReturn(myRef);


        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                HashMap<String, Object> objectBuild = (HashMap<String, Object>) args[0];

                assertEquals(objectBuild.get("user_email"),fakeUserEmail);
                assertEquals(objectBuild.get("default_user_name"),fakeUserName);

                return null;
            }
        }).when(myRef).updateChildren(anyMap());

        DataProvider dp = new DataProvider(myRef,database,mUser);
        dp.initUserInDB();

    }

    @Test
    public void testJoinActivityAndIncrementNbOfUserInActivity(){

        database = Mockito.mock(FirebaseDatabase.class);
        mUser = Mockito.mock(FirebaseUser.class);
        myRef = Mockito.mock(DatabaseReference.class);
        DatabaseReference myRefIncrement = Mockito.mock(DatabaseReference.class);


        // init moc for joinActivity
        final String fakeUserID = "fakeUserID";
        final String fakeEnrolledKey = "enrolledKeyID";
        when(mUser.getUid()).thenReturn(fakeUserID);
        when(myRef.child("users")).thenReturn(myRef);
        when(myRef.child(fakeUserID)).thenReturn(myRef);
        when(myRef.child("enrolled")).thenReturn(myRef);
        when(myRef.push()).thenReturn(myRef);
        when(myRef.getKey()).thenReturn(fakeEnrolledKey);

        final int nbOfParticipants = 10;
        final int nbMaxParticipants= 20;

        final DeboxActivity dbaTest = new DeboxActivity(uuidTest,"test", "user-test",
                "description",
                Calendar.getInstance(),
                Calendar.getInstance(),
                122.01,
                121.0213,
                "Sports",
                nbOfParticipants,
                nbMaxParticipants);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                HashMap<String, Object> objectBuild = (HashMap<String, Object>) args[0];

                HashMap<String, Object> enrolledChild = (HashMap<String, Object>) objectBuild.get("enrolled/"+fakeEnrolledKey);

                assertEquals(enrolledChild.get("activity ID:"),dbaTest.getId());

                return null;
            }
        }).when(myRef).updateChildren(anyMap());

        // init moc for incrementNbOfUserInActivity

        when(database.getReference("activities/" + dbaTest.getId())).thenReturn(myRefIncrement);

        final Map<String, Object> activityMap1 = toolsBuildMapFromDebox(dbaTest);
        final DataSnapshot ds1 = Mockito.mock(DataSnapshot.class);
        when(ds1.getValue()).thenReturn(activityMap1);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds1);
                return null;
            }
        }).when(myRefIncrement).addListenerForSingleValueEvent(any(ValueEventListener.class));


        when(myRef.child("activities")).thenReturn(myRef);
        when(myRef.child(dbaTest.getId())).thenReturn(myRefIncrement);


        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                HashMap<String, Object> objectBuild = (HashMap<String, Object>) args[0];

                assertEquals(objectBuild.get("nbOfParticipants"),nbOfParticipants+1);

                return null;
            }
        }).when(myRefIncrement).updateChildren(anyMap());


        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.joinActivity(dbaTest);

    }

    @Test
    public void testLeaveActivityAndDecreasesNbOfUserInActivity(){

        database = Mockito.mock(FirebaseDatabase.class);
        mUser = Mockito.mock(FirebaseUser.class);

        myRef = Mockito.mock(DatabaseReference.class);

        final int nbOfParticipants = 10;
        final int nbMaxParticipants= 20;

        final DeboxActivity dbaTest = new DeboxActivity(uuidTest,"test", "user-test",
                "description",
                Calendar.getInstance(),
                Calendar.getInstance(),
                122.01,
                121.0213,
                "Sports",
                nbOfParticipants,
                nbMaxParticipants);

        // init moc for joinActivity
        final String fakeUserID = "fakeUserID";
        final String fakeEnrolledKey = "enrolledKeyID";
        when(mUser.getUid()).thenReturn(fakeUserID);

        when(database.getReference("users/" + fakeUserID + "/enrolled")).thenReturn(myRef);


        final Map<String, Object> enrolledMap = new HashMap<>();
        final Map<String, Object> enrolledMap1 = new HashMap<>();
        enrolledMap1.put("activity ID:","fakeID");
        final Map<String, Object> enrolledMap2 = new HashMap<>();
        enrolledMap2.put("activity ID:",dbaTest.getId());

        enrolledMap.put("enrolledID1",enrolledMap1);
        enrolledMap.put(fakeEnrolledKey,enrolledMap2);



        final DataSnapshot ds1 = Mockito.mock(DataSnapshot.class);
        when(ds1.getValue()).thenReturn(enrolledMap);


        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds1);
                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));


        when(myRef.child("users")).thenReturn(myRef);
        when(myRef.child(fakeUserID)).thenReturn(myRef);
        when(myRef.child("enrolled")).thenReturn(myRef);
        when(myRef.child(fakeEnrolledKey)).thenReturn(myRef);



        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                assertEquals(true,true);

                return null;
            }
        }).when(myRef).removeValue();

        DatabaseReference myRefDec = Mockito.mock(DatabaseReference.class);

        when(database.getReference("activities/" + dbaTest.getId())).thenReturn(myRefDec);
        final DataSnapshot ds2 = Mockito.mock(DataSnapshot.class);

        //Create Map for deboxActivity
        final Map<String, Object> activityMap1 = toolsBuildMapFromDebox(dbaTest);


        //Override getValue() to always return the Map for the test
        when(ds2.getValue()).thenReturn(activityMap1);


        //Override addListenerForSingleValueEvent method for test to always return our Map
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds2);
                return null;
            }
        }).when(myRefDec).addListenerForSingleValueEvent(any(ValueEventListener.class));

        when(myRef.child("activities")).thenReturn(myRef);
        when(myRef.child(dbaTest.getId())).thenReturn(myRef);


        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                HashMap<String, Object> objectBuild = (HashMap<String, Object>) args[0];

                assertEquals(objectBuild.get("nbOfParticipants"),nbOfParticipants-1);

                return null;
            }
        }).when(myRef).updateChildren(anyMap());


        DataProvider dp = new DataProvider(myRef,database,mUser);
        dp.leaveActivity(dbaTest);

    }

    @Test
    public void testGetSpecifiedActivities(){


        final String[] idArray = {"id0","id1","id2","id3","id4","id5","id6","id7","id8","id9"};

        final Map<String, Object> activityMap = new HashMap<>();

        for(int i = 0 ; i<idArray.length;i++){
            activityMap.put(idArray[i],toolsBuildMapFromDebox(toolsBuildDummyDeboxActivity(idArray[i])));
        }


        //when(ds.getValue()).thenReturn(activityMap);

        final DataSnapshot[] dsArray = new DataSnapshot[10];

        for(int i = 0; i<idArray.length;i++){
            dsArray[i] = Mockito.mock(DataSnapshot.class);
            when(dsArray[i].getKey()).thenReturn(idArray[i]);
            when(dsArray[i].getValue()).thenReturn(toolsBuildMapFromDebox(toolsBuildDummyDeboxActivity(idArray[i])));
        }


        Iterable<DataSnapshot> iterable = Arrays.asList(dsArray);
        final DataSnapshot ds = Mockito.mock(DataSnapshot.class);
        when(ds.getChildren()).thenReturn(iterable);

        when(database.getReference("activities")).thenReturn(myRef);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds);
                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));


        final List<String> intEventIds = new ArrayList<>();
        intEventIds.add(idArray[0]);
        intEventIds.add(idArray[2]);
        intEventIds.add(idArray[7]);
        intEventIds.add(idArray[8]);

        final List<String> orgEventsIds = new ArrayList<>();
        orgEventsIds.add(idArray[0]);
        orgEventsIds.add(idArray[1]);
        orgEventsIds.add(idArray[5]);

        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.getSpecifiedActivities(new DataProvider.DataProviderListenerUserEvents() {
            @Override
            public void getUserActivities(List<DeboxActivity> intList, List<DeboxActivity> orgList) {

                assertEquals(intList.size(),intEventIds.size());
                Iterator<DeboxActivity> iterableIntList = intList.iterator();
                Iterator<String> iterableIntEventIds= intEventIds.iterator();

                while(iterableIntList.hasNext() && iterableIntEventIds.hasNext()){
                    assertEquals(iterableIntList.next().getId(),iterableIntEventIds.next());
                }

                assertEquals(orgList.size(),orgEventsIds.size());
                Iterator<DeboxActivity> iterableOrgList = orgList.iterator();
                Iterator<String> iterableOrgEventsIds= orgEventsIds.iterator();

                while(iterableOrgList.hasNext() && iterableOrgEventsIds.hasNext()){
                    assertEquals(iterableOrgList.next().getId(),iterableOrgEventsIds.next());
                }

            }
        },intEventIds,orgEventsIds);
    }

    @Test
    public void testRankUser(){

        database = Mockito.mock(FirebaseDatabase.class);
        mUser = Mockito.mock(FirebaseUser.class);
        myRef = Mockito.mock(DatabaseReference.class);

        final String mocUserID = "mocUserID";
        final String mocEnrolledKey1 = "mocEnrolledKeyID1";
        final String mocEnrolledKey2 = "mocEnrolledKeyID2";
        final String mocActivityIDToRank = "mocActivityIDToRank";
        final String mocOtherActivityID = "mocOtherActivityID";
        final String mocUserIDToRank = "mocUserIDToRank";

        when(mUser.getUid()).thenReturn(mocUserID);
        when(database.getReference("users/" + mocUserID + "/enrolled")).thenReturn(myRef);


        final Map<String, Object> enrolledMap = new HashMap<>();
        final Map<String, Object> enrolledMap1 = new HashMap<>();
        final Map<String, Object> enrolledMap2 = new HashMap<>();

        enrolledMap1.put("activity ID:",mocOtherActivityID);
        enrolledMap2.put("activity ID:",mocActivityIDToRank);

        enrolledMap.put(mocEnrolledKey1,enrolledMap1);
        enrolledMap.put(mocEnrolledKey2,enrolledMap2);

        final DataSnapshot ds1 = Mockito.mock(DataSnapshot.class);
        when(ds1.getValue()).thenReturn(enrolledMap);


        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds1);
                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));


        when(myRef.child("users")).thenReturn(myRef);
        when(myRef.child(mocUserID)).thenReturn(myRef);
        when(myRef.child("enrolled")).thenReturn(myRef);
        when(myRef.child(mocEnrolledKey2)).thenReturn(myRef);


        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                assertEquals(true,true);

                return null;
            }
        }).when(myRef).removeValue();


        // next build moc for part Add Activity Uid in User:ranked
        DatabaseReference addRankedRef = Mockito.mock(DatabaseReference.class);
        final String rankedKey = "rankedKey";

        when(myRef.child("ranked")).thenReturn(addRankedRef);
        when(addRankedRef.push()).thenReturn(addRankedRef);
        when(addRankedRef.getKey()).thenReturn(rankedKey);


        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                HashMap<String, Object> objectBuild = (HashMap<String, Object>) args[0];


                HashMap<String, Object> objectBuild2 = (HashMap<String, Object>)objectBuild.get("ranked/" + rankedKey);

                String id = (String) objectBuild2.get("activity ID:");
                assertEquals(id,mocActivityIDToRank);


                return null;
            }
        }).when(myRef).updateChildren(anyMap());


        final int nbOfParticipants = 10;
        final int nbMaxParticipants = 20;
        final DeboxActivity dbaTest = new DeboxActivity(mocActivityIDToRank,mocUserIDToRank, "user-test",
                "description",
                Calendar.getInstance(),
                Calendar.getInstance(),
                122.01,
                121.0213,
                "Sports",
                nbOfParticipants,
                nbMaxParticipants);

        DatabaseReference myRefGetActivity = Mockito.mock(DatabaseReference.class);

        when(database.getReference("activities/" + mocActivityIDToRank)).thenReturn(myRefGetActivity);

        final Map<String, Object> activityMap = toolsBuildMapFromDebox(dbaTest);
        final DataSnapshot ds2 = Mockito.mock(DataSnapshot.class);
        when(ds2.getValue()).thenReturn(activityMap);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds2);
                return null;
            }
        }).when(myRefGetActivity).addListenerForSingleValueEvent(any(ValueEventListener.class));


        when(myRef.child(mocUserIDToRank)).thenReturn(myRef);


        DatabaseReference myRefUserToRank = Mockito.mock(DatabaseReference.class);
        DatabaseReference myRefRatingNb = Mockito.mock(DatabaseReference.class);
        DatabaseReference myRefRatingSum = Mockito.mock(DatabaseReference.class);

        when(database.getReference("users/"+mocUserIDToRank)).thenReturn(myRefUserToRank);


        //build user to rank
        final Map<String, Object> userToRankMap= new HashMap<>();
        final Map<String, Object> enrolledMapEmpty= new HashMap<>();
        final Map<String, Object> organisedMapEmpty= new HashMap<>();

        userToRankMap.put("default_user_name","userToBeRanked");
        userToRankMap.put("enrolled",enrolledMapEmpty);
        userToRankMap.put("organised",organisedMapEmpty);
        userToRankMap.put("ratingNb",-1);
        userToRankMap.put("ratingSum",0);
        userToRankMap.put("user_email","mailToRank@gmail.com");

        final DataSnapshot ds3 = Mockito.mock(DataSnapshot.class);
        when(ds3.getValue()).thenReturn(userToRankMap);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds3);
                return null;
            }
        }).when(myRefUserToRank).addListenerForSingleValueEvent(any(ValueEventListener.class));


        when(myRef.child("ratingNb")).thenReturn(myRefRatingNb);
        when(myRef.child("ratingSum")).thenReturn(myRefRatingSum);


        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                int ratingNB = (int) args [0];
                assertEquals(ratingNB,1);
                return null;
            }
        }).when(myRefRatingNb).setValue(Matchers.anyObject());


        final int rank = 4;

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                int ratingNB = (int) args [0];
                assertEquals(ratingNB,rank);
                return null;
            }
        }).when(myRefRatingSum).setValue(Matchers.anyObject());

        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.rankUser(dbaTest.getId(),rank);

    }


    public Map<String, Object> toolsBuildMapFromDebox(DeboxActivity dba){

        Map<String, Object>  activityMap = new HashMap<>();

        activityMap.put("title", dba.getTitle());
        activityMap.put("description", dba.getDescription());
        activityMap.put("category", dba.getCategory());
        activityMap.put("latitude", dba.getLocation()[0]);
        activityMap.put("longitude", dba.getLocation()[1]);
        activityMap.put("organizer", dba.getOrganizer());
        activityMap.put("timeEnd", dba.getTimeEnd().getTimeInMillis());
        activityMap.put("timeStart", dba.getTimeStart().getTimeInMillis());
        activityMap.put("nbOfParticipants", dba.getNbOfParticipants());
        activityMap.put("nbMaxOfParticipants",dba.getNbMaxOfParticipants());

        return  activityMap;

    }

    public Map<String, Object> toolsBuildMapFromDebox(DeboxActivity dba, int nbOfParticipants, int nbMaxOfParticipants){

        Map<String, Object>  activityMap = new HashMap<>();

        activityMap.put("title", dba.getTitle());
        activityMap.put("description", dba.getDescription());
        activityMap.put("category", dba.getCategory());
        activityMap.put("latitude", dba.getLocation()[0]);
        activityMap.put("longitude", dba.getLocation()[1]);
        activityMap.put("organizer", dba.getOrganizer());
        activityMap.put("timeEnd", dba.getTimeEnd().getTimeInMillis());
        activityMap.put("timeStart", dba.getTimeStart().getTimeInMillis());
        activityMap.put("nbOfParticipants", nbOfParticipants);
        activityMap.put("nbMaxOfParticipants", nbMaxOfParticipants);

        return  activityMap;

    }


    public DeboxActivity toolsBuildDummyDeboxActivity(String id){

        DeboxActivity dba = new DeboxActivity(id,"dummyOrganiser","dummyTitle","dummyDescription",
                Calendar.getInstance(),Calendar.getInstance(),10.1,10.1,"Sports");
        return dba;
    }



}