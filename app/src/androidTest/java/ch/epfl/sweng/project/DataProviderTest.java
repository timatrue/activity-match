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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
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

    //private final Map<String, Object> activityMap = new HashMap<>();


    /**
     * Test the function public String pushActivity(DeboxActivity da) of the dataProvider
     *
     */
    @Test
    public void testDataProvider(){
        DataProvider dp = new DataProvider();
        assertEquals(true,true);

    }

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


    /**
     * Test the function :  public Void getActivityFromUid(final DataProviderListenerActivity
     * listener, final String uid) of the dataProvider. getActivityFromUid return the activity with
     * the uid.
     */
    @Test
    public void testGetActivityFromUid() {
        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        database = Mockito.mock(FirebaseDatabase.class);
        myRef = Mockito.mock(DatabaseReference.class);
        mUser = Mockito.mock(FirebaseUser.class);

        //Create Map from deboxactivities
        final Map<String, Object> activityMap = toolsBuildMapFromDebox(deboxActivityTest);

        //Override getValue() to always return the Map for the test
        when(ds.getValue()).thenReturn(activityMap);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRef,ds);

        //Override getReference method to return the Mock reference
        when(database.getReference("activities/" + uuidTest)).thenReturn(myRef);

        //Test DataProvider getActivityFromUid method, check that it calls the listener and gives
        //it a proper DeboxActivity, corresponding to the Map values
        DataProvider dp = new DataProvider(myRef,database,mUser);
        dp.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
            @Override
            public void getActivity(DeboxActivity activity) {
                assertTrue(toolsActivitiesEquals(activity,deboxActivityTest));
            }

        }, uuidTest);
    }

    /**
     * Test the function :  public Void GetActivityAndListenerOnChange(final DataProviderListenerActivity
     * listener, final String uid) of the dataProvider. GetActivityAndListenerOnChange return the activity
     * with the uid.
     */
    @Test
    public void testGetActivityAndListenerOnChange(){
        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        database = Mockito.mock(FirebaseDatabase.class);
        myRef = Mockito.mock(DatabaseReference.class);
        mUser = Mockito.mock(FirebaseUser.class);

        //Create Map from deboxactivities
        final Map<String, Object> activityMap = toolsBuildMapFromDebox(deboxActivityTest);

        //Override getValue() to always return the Map for the test
        when(ds.getValue()).thenReturn(activityMap);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRef,ds);

        //Override getReference method to return the Mock reference
        when(database.getReference("activities/" + uuidTest)).thenReturn(myRef);

        //Test DataProvider getActivityFromUid method, check that it calls the listener and gives
        //it a proper DeboxActivity, corresponding to the Map values
        DataProvider dp = new DataProvider(myRef,database,mUser);
        dp.getActivityAndListenerOnChange(new DataProvider.DataProviderListenerActivity() {
            @Override
            public void getActivity(DeboxActivity activity) {
                assertTrue(toolsActivitiesEquals(activity,deboxActivityTest));
            }

        }, uuidTest);
    }




    /**
     * Test the function : public void userEnrolledInActivity(final DataProviderListenerEnrolled listener
     * , final String uid).  userEnrolledInActivity check if the user is enrolled in the activity corresponding
     * to the uid
     */
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
        toolsBuildAnswerForListener(myRef,ds);

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

    /**
     * Test the function :  public void getAllCategories(final DataProviderListenerCategories listener).
     * getAllCategories return all categories that are in the dataBase
     */
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
        toolsBuildAnswerForListener(myRef,ds);

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

    /**
     * Test the function public void getSpecifiedCategory(final DataProviderListenerCategory listener,
     * String specifiedCategory). getSpecifiedCategory return all activities corresponding to the specified
     * category.
     */
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

                assertEquals(activitiesList.size(),2);
                for(int i = 0; i<activitiesList.size();i++)
                {
                    DeboxActivity activity = activitiesList.get(i);
                    assertTrue(toolsActivitiesEquals(activity,deboxActivityTest));

                }

            }
        },deboxActivityTest.getCategory());

    }


    /**
     * Test the function public void getAllActivities(final DataProviderListenerActivities listener).
     * getAllActivities return to the listener all activities contain in the database.
     */
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

        final Map<String, Object> activityMap1 = toolsBuildMapFromDebox(deboxActivityTest);

        when(dsChild1.getKey()).thenReturn(uuidTest);
        when(dsChild1.getValue()).thenReturn(activityMap1);

        final Map<String, Object> activityMap2 = toolsBuildMapFromDebox(deboxActivityTest2);

        when(dsChild2.getKey()).thenReturn(uuidTest2);
        when(dsChild2.getValue()).thenReturn(activityMap2);


        final DataSnapshot [] listDS = {dsChild1,dsChild2};

        Iterable<DataSnapshot> iterable = Arrays.asList(listDS);

        //Overrride getChildren to always return interable of DataSnapshot
        when(ds.getChildren()).thenReturn(iterable);


        //Override addListenerForSingleValueEvent method for test to always return our value
        toolsBuildAnswerForListener(myRef,ds);


        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.getAllActivities(new DataProvider.DataProviderListenerActivities() {
            @Override
            public void getActivities(List<DeboxActivity> activitiesList) {

                assertEquals(activitiesList.size(),listDS.length);

                DeboxActivity activity = activitiesList.get(0);
                assertTrue(toolsActivitiesEquals(activity,deboxActivityTest));

                activity = activitiesList.get(1);
                assertTrue(toolsActivitiesEquals(activity,deboxActivityTest2));


            }
        });
    }

    /**
     * Test the function public void getIfPlaceLeftInActivity(final String uid,
     * final DataProviderListenerPlaceFreeInActivity listener). getIfPlaceLeftInActivity take the
     * activity with the id uid on the dataBase and go check if the activity has place left or not
     */
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
        toolsBuildAnswerForListener(myRef, ds1);
        toolsBuildAnswerForListener(myRef2, ds2);
        toolsBuildAnswerForListener(myRef3, ds3);


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

    /**
     * Test the function : public void userProfile(final DataProviderListenerUserInfo listener)
     * userProfile return the userProfile of the current user.
     *
     */
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
        final String imageLink = "link-Image";
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


        final Map<String, Object> rankedMap1 = new HashMap<>();
        rankedMap1.put("activity ID:",uuidTest3);

        final Map<String, Object> rankedMap = new HashMap<>();
        rankedMap.put("rankedID1",rankedMap1);


        final Map<String, Object> userMap = new HashMap<>();

        userMap.put("default_user_name",fakeName);
        userMap.put("enrolled",enrolledMap);
        userMap.put("organised",organisedMap);
        userMap.put("ranked",rankedMap);
        userMap.put("ratingNb",ratingNb);
        userMap.put("ratingSum",ratingSum);
        userMap.put("user_email",fakeEmail);
        userMap.put("image",imageLink);


        when(ds1.getValue()).thenReturn(userMap);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRef,ds1);


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

                List<String> checkRankedList = new ArrayList<>();
                checkRankedList.add(uuidTest3);
                assertEquals(user.getRankedEventIds(),checkRankedList);

                assertEquals(user.getRatingNb(),ratingNb);
                assertEquals(user.getRatingSum(),ratingSum);
                assertEquals(user.getRating(),((double)ratingSum)/ratingNb,0.0);
                assertEquals(user.getEmail(),fakeEmail);
                assertEquals(user.getUsername(),fakeName);
                assertEquals(user.getPhotoLink(),imageLink);

            }
        });

    }


    /**
     * Test the function : public void getUserProfileAndListenerOnChange(final DataProviderListenerUserInfo listener)
     * getUserProfileAndListenerOnChange return the userProfile of the current user.
     *
     */
    @Test
    public void testGetUserProfileAndListenerOnChange(){

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


        final Map<String, Object> rankedMap1 = new HashMap<>();
        rankedMap1.put("activity ID:",uuidTest3);

        final Map<String, Object> rankedMap = new HashMap<>();
        rankedMap.put("rankedID1",rankedMap1);


        final Map<String, Object> userMap = new HashMap<>();

        userMap.put("default_user_name",fakeName);
        userMap.put("enrolled",enrolledMap);
        userMap.put("organised",organisedMap);
        userMap.put("ranked",rankedMap);
        userMap.put("ratingNb",ratingNb);
        userMap.put("ratingSum",ratingSum);
        userMap.put("user_email",fakeEmail);

        when(ds1.getValue()).thenReturn(userMap);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRef,ds1);


        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.getUserProfileAndListenerOnChange(new DataProvider.DataProviderListenerUserInfo() {
            @Override
            public void getUserInfo(User user) {
                List<String> checkInterested = new ArrayList<>();
                checkInterested.add(uuidTest1);
                checkInterested.add(uuidTest2);
                assertEquals(user.getInterestedEventIds(),checkInterested);

                List<String> checkOrganizedList = new ArrayList<>();
                checkOrganizedList.add(uuidTest3);
                assertEquals(user.getOrganizedEventIds(),checkOrganizedList);

                List<String> checkRankedList = new ArrayList<>();
                checkRankedList.add(uuidTest3);
                assertEquals(user.getRankedEventIds(),checkRankedList);

                assertEquals(user.getRatingNb(),ratingNb);
                assertEquals(user.getRatingSum(),ratingSum);
                assertEquals(user.getRating(),((double)ratingSum)/ratingNb,0.0);
                assertEquals(user.getPhotoLink(),null);
                assertEquals(user.getEmail(),fakeEmail);
                assertEquals(user.getUsername(),fakeName);

            }
        });

    }

    /**
     * Test the function : public void publicUserProfile(final String userUid, final DataProviderListenerUserInfo listener)
     * return the userProfile of userUid user asked.
     */
    @Test
    public void testPublicUserProfile() {

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


        when(database.getReference("users/"+fakeUserID1)).thenReturn(myRef);

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


        final Map<String, Object> rankedMap1 = new HashMap<>();
        rankedMap1.put("activity ID:",uuidTest3);

        final Map<String, Object> rankedMap = new HashMap<>();
        rankedMap.put("rankedID1",rankedMap1);


        final Map<String, Object> userMap = new HashMap<>();

        userMap.put("default_user_name",fakeName);
        userMap.put("enrolled",enrolledMap);
        userMap.put("organised",organisedMap);
        userMap.put("ranked",rankedMap);
        userMap.put("ratingNb",ratingNb);
        userMap.put("ratingSum",ratingSum);
        userMap.put("user_email",fakeEmail);

        when(ds1.getValue()).thenReturn(userMap);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRef,ds1);

        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.publicUserProfile(fakeUserID1,new DataProvider.DataProviderListenerUserInfo() {
            @Override
            public void getUserInfo(User user) {
                List<String> checkInterested = new ArrayList<>();
                checkInterested.add(uuidTest1);
                checkInterested.add(uuidTest2);
                assertEquals(user.getInterestedEventIds(),checkInterested);

                List<String> checkOrganizedList = new ArrayList<>();
                checkOrganizedList.add(uuidTest3);
                assertEquals(user.getOrganizedEventIds(),checkOrganizedList);

                List<String> checkRankedList = new ArrayList<>();
                checkRankedList.add(uuidTest3);
                assertEquals(user.getRankedEventIds(),checkRankedList);

                assertEquals(user.getRatingNb(),ratingNb);
                assertEquals(user.getRatingSum(),ratingSum);
                assertEquals(user.getRating(),((double)ratingSum)/ratingNb,0.0);
                assertEquals(user.getPhotoLink(),null);
                assertEquals(user.getEmail(),fakeEmail);
                assertEquals(user.getUsername(),fakeName);

            }
        });


    }


    /**
     * Test the function : public void initUserInDB(). initUserInDB is use to be sure that an user
     * profile corresponding to the current user is present in the database. If there is no userProfile
     * in the dataBase, the profile is automatically created.
     */
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

    /**
     * This function test the joinActivity if no place are available. (handler cannot be tested
     * with mock, they must be test with integration test
     */
    @Test
    public void testAtomicJoinFullActivity() {

        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        database = Mockito.mock(FirebaseDatabase.class);
        myRef = Mockito.mock(DatabaseReference.class);
        mUser = Mockito.mock(FirebaseUser.class);

        DataSnapshot dsActivityFull = Mockito.mock(DataSnapshot.class);

        final int nbOfParticipants = 20;
        final int nbMaxParticipants = 20;

        final DeboxActivity testActivityFull = new DeboxActivity(uuidTest, "test", "user-test",
                "description",
                Calendar.getInstance(),
                Calendar.getInstance(),
                122.01,
                121.0213,
                "Sports",
                nbOfParticipants,
                nbMaxParticipants);

        //Create Map from testActivityFull
        final Map<String, Object> activityFullMap = toolsBuildMapFromDebox(testActivityFull);

        //Override getValue() to always return the Map for the test
        when(dsActivityFull.getValue()).thenReturn(activityFullMap);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRef, dsActivityFull);

        //Override getReference method to return the Mock reference
        when(database.getReference("activities/" + uuidTest)).thenReturn(myRef);

        DataProvider dp = new DataProvider(myRef, database, mUser);

        dp.atomicJoinActivity(testActivityFull, new DataProvider.DataProviderListenerResultOfJoinActivity() {
            @Override
            public void getResultJoinActivity(boolean result) {

                // if no place available result of joinActivity must be false
                assertEquals(result,false);

            }
        });
    }



    /**
     * This function test the functions public void joinActivity(DeboxActivity dba) and
     * private void incrementNbOfUserInActivity(DeboxActivity dba). incrementNbOfUserInActivity is
     * a private method of dataProvider, so it is test by calling the method joinActivity.
     */
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
        toolsBuildAnswerForListener(myRefIncrement,ds1);


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

    /**
     * Test the functions public void leaveActivity(final DeboxActivity dba) and private void
     * decreasesNbOfUserInActivity(DeboxActivity dba). decreasesNbOfUserInActivity is
     * a private method of dataProvider, so it is test by calling the method leaveActivity.
     */
    @Test
    public void testLeaveActivityAndDecreasesNbOfUserInActivity(){

        database = Mockito.mock(FirebaseDatabase.class);
        mUser = Mockito.mock(FirebaseUser.class);

        myRef = Mockito.mock(DatabaseReference.class);
        DatabaseReference partRef = Mockito.mock(DatabaseReference.class);

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

        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRef,ds1);


        when(myRef.child("users")).thenReturn(myRef);
        when(myRef.child(fakeUserID)).thenReturn(myRef);
        when(myRef.child("enrolled")).thenReturn(myRef);
        when(myRef.child(fakeEnrolledKey)).thenReturn(myRef);


        /*doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                assertEquals(true,true);

                return null;
            }
        }).when(myRef).removeValue();*/

        DatabaseReference myRefDec = Mockito.mock(DatabaseReference.class);

        when(database.getReference("activities/" + dbaTest.getId())).thenReturn(myRefDec);
        final DataSnapshot ds2 = Mockito.mock(DataSnapshot.class);

        //Create Map for deboxActivity
        final Map<String, Object> activityMap1 = toolsBuildMapFromDebox(dbaTest);

        //Override getValue() to always return the Map for the test
        when(ds2.getValue()).thenReturn(activityMap1);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRefDec,ds2);

        when(myRef.child("activities")).thenReturn(myRef);
        when(myRef.child(dbaTest.getId())).thenReturn(myRef);
        when(myRef.child("activities/"+dbaTest.getId()+"/nbOfParticipants")).thenReturn(partRef);

        final MutableData mockMutableData = Mockito.mock(MutableData.class);
        when(mockMutableData.getValue(Integer.class)).thenReturn(nbOfParticipants);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Transaction.Handler listener = (Transaction.Handler) args[0];
                listener.doTransaction(mockMutableData);
                return null;
            }
        }).when(partRef).runTransaction(any(Transaction.Handler.class));

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();

                int participantUpdated = (int) args[0];
                assertEquals(participantUpdated,nbOfParticipants-1);
                return null;
            }
        }).when(mockMutableData).setValue(anyInt());

        DataProvider dp = new DataProvider(myRef,database,mUser);
        dp.leaveActivity(dbaTest);

        verify(mockMutableData, atLeastOnce()).setValue(anyInt());
        verify(myRef,atLeastOnce()).removeValue();

    }

    /**
     *  This function test ublic void getSpecifiedActivities(final DataProviderListenerUserEvents
     *  listener, final List<String> intEventIds, final List<String> orgEventsIds). This function fetch
     *  all activity in the dataBase and store them in the intEventIds, orgEventsIDs if they are related
     *  to the current User... getSpecifiedActivities must be deleted
     */
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

        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRef,ds);


        final List<String> intEventIds = new ArrayList<>();
        intEventIds.add(idArray[0]);
        intEventIds.add(idArray[2]);
        intEventIds.add(idArray[7]);
        intEventIds.add(idArray[8]);

        final List<String> orgEventsIds = new ArrayList<>();
        orgEventsIds.add(idArray[0]);
        orgEventsIds.add(idArray[1]);
        orgEventsIds.add(idArray[5]);

        final List<String> rankedEventsIds = new ArrayList<>();
        rankedEventsIds.add(idArray[9]);

        DataProvider dp = new DataProvider(myRef,database,mUser);

        dp.getSpecifiedActivities(new DataProvider.DataProviderListenerUserEvents() {
            @Override
            public void getUserActivities(List<DeboxActivity> intList, List<DeboxActivity> orgList, List<DeboxActivity> rankList) {

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

                assertEquals(rankList.size(),rankedEventsIds.size());
                Iterator<DeboxActivity> iterableRankList = rankList.iterator();
                Iterator<String> iterableRankEventsIds= rankedEventsIds.iterator();

                while(iterableRankList.hasNext() && iterableRankEventsIds.hasNext()){
                    assertEquals(iterableRankList.next().getId(),iterableRankEventsIds.next());
                }


            }
        },intEventIds,orgEventsIds,rankedEventsIds);
    }

    @Test
    public void testChangeUserImage(){

        database = Mockito.mock(FirebaseDatabase.class);
        mUser = Mockito.mock(FirebaseUser.class);
        myRef = Mockito.mock(DatabaseReference.class);

        DatabaseReference refImage = Mockito.mock(DatabaseReference.class);

        final String testUserID = "testUserID";

        when(mUser.getUid()).thenReturn(testUserID);
        when(database.getReference("users/" + testUserID)).thenReturn(myRef);
        when(myRef.child("image")).thenReturn(refImage);

        final String newUserImage ="userImageLink";

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                String updatedValue = (String) args[0];
                assertEquals(updatedValue,newUserImage);
                return null;
            }
        }).when(refImage).setValue(anyString());


        DataProvider dp = new DataProvider(myRef,database,mUser);
        dp.changeUserImage(newUserImage);

        Mockito.verify(refImage,atLeastOnce()).setValue(anyString());

    }

    /**
     * Test the function : public void rankUser(final String uid, final int rank)
     * the function rankUser attribute a rank of the organiser of the activity with id uid
     *
     */
    /*
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
        final String mocRatingComment = "mocRatingComment"; //mine

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


        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRef,ds1);


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
        toolsBuildAnswerForListener(myRefGetActivity,ds2);


        when(myRef.child(mocUserIDToRank)).thenReturn(myRef);


        DatabaseReference myRefUserToRank = Mockito.mock(DatabaseReference.class);
        DatabaseReference myRefRatingNb = Mockito.mock(DatabaseReference.class);
        DatabaseReference myRefRatingSum = Mockito.mock(DatabaseReference.class);
        DatabaseReference myRefComments = Mockito.mock(DatabaseReference.class); //mine

        when(database.getReference("users/"+mocUserIDToRank)).thenReturn(myRefUserToRank);


        //build user to rank
        final Map<String, Object> userToRankMap= new HashMap<>();
        final Map<String, Object> enrolledMapEmpty= new HashMap<>();
        final Map<String, Object> organisedMapEmpty= new HashMap<>();
        final Map<String, Object> commentsMapEmpty = new HashMap<>(); //mine

        userToRankMap.put("default_user_name","userToBeRanked");
        userToRankMap.put("enrolled",enrolledMapEmpty);
        userToRankMap.put("organised",organisedMapEmpty);
        userToRankMap.put("ratingNb",-1);
        userToRankMap.put("ratingSum",0);
        userToRankMap.put("user_email","mailToRank@gmail.com");
        userToRankMap.put("comments", commentsMapEmpty); //mine

        final DataSnapshot ds3 = Mockito.mock(DataSnapshot.class);
        when(ds3.getValue()).thenReturn(userToRankMap);

        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRefUserToRank,ds3);


        when(myRef.child("ratingNb")).thenReturn(myRefRatingNb);
        when(myRef.child("ratingSum")).thenReturn(myRefRatingSum);
        when(myRef.child("comments")).thenReturn(myRefComments); //mine


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

        final String comment = "comment";//mine

        dp.rankUser(dbaTest.getId(),rank, comment); //mine

    } */

    /**
     * Test the function : public void getCurrentUserStatusSimplified(final DeboxActivity currentActivity,
     * final DataProviderListenerUserState listener). The function getCurrentUserStatusSimplified return
     * the status between the user and the activity passed in parameter.
     */

    @Test
    public void testGetCurrentUserStatusSimplified(){

        final Map<String, Object> enrolledMap = new HashMap<>();
        final Map<String, Object> organisedMap = new HashMap<>();
        final Map<String, Object> rankedMap = new HashMap<>();

        // build to getStatus ENROLLED
        Calendar start1 = Calendar.getInstance();
        start1.add(Calendar.HOUR,2);
        Calendar end1 = Calendar.getInstance();
        end1.add(Calendar.HOUR,2);

        final String id1 = "id1";
        DeboxActivity dbaEnrolled = new DeboxActivity(id1,"dummyOrganiser","dummyTitle","dummyDescription",
                start1 ,end1 ,10.1,10.1,"Sports");

        final Map<String, Object> enrolledMap1 = new HashMap<>();
        enrolledMap1.put("activity ID:",id1);
        enrolledMap.put("enrolledID1",enrolledMap1);


        // build to getStatus NOT_ENROLLED_NOT_FULL
        Calendar start2 = Calendar.getInstance();
        start2.add(Calendar.HOUR,2);
        Calendar end2 = Calendar.getInstance();
        end2.add(Calendar.HOUR,2);

        final String id2 = "id2";
        DeboxActivity dbaNoEnrolledNoFull = new DeboxActivity(id2,"dummyOrganiser","dummyTitle","dummyDescription",
                start2 ,end2 ,10.1,10.1,"Sports");

        // build to getStatus NOT_ENROLLED_FULL
        Calendar start3 = Calendar.getInstance();
        start3.add(Calendar.HOUR,2);
        Calendar end3 = Calendar.getInstance();
        end3.add(Calendar.HOUR,2);

        final String id3 = "id3";
        DeboxActivity dbaNoEnrolledFull = new DeboxActivity(id3,"dummyOrganiser","dummyTitle","dummyDescription",
                start3 ,end3 ,10.1,10.1,"Sports",10,10);


        // build to getStatus MUST_BE_RANKED
        Calendar start4 = Calendar.getInstance();
        start4.add(Calendar.HOUR,-2);
        Calendar end4 = Calendar.getInstance();
        end4.add(Calendar.HOUR,-2);

        final String id4 = "id4";
        DeboxActivity dbaNoEnrolledMustBeRanked = new DeboxActivity(id4,"dummyOrganiser","dummyTitle","dummyDescription",
                start4 ,end4 ,10.1,10.1,"Sports");

        final Map<String, Object> enrolledMap2 = new HashMap<>();
        enrolledMap2.put("activity ID:",id4);
        enrolledMap.put("enrolledID2",enrolledMap2);


        // build to getStatus ALREADY_RANKED
        Calendar start5 = Calendar.getInstance();
        start5.add(Calendar.HOUR,-2);
        Calendar end5 = Calendar.getInstance();
        end5.add(Calendar.HOUR,-2);

        final String id5 = "id5";
        DeboxActivity dbaNoEnrolledAlreadyRanked = new DeboxActivity(id5,"dummyOrganiser","dummyTitle","dummyDescription",
                start5 ,end5 ,10.1,10.1,"Sports");

        final Map<String, Object> rankedMap1 = new HashMap<>();
        rankedMap1.put("activity ID:",id5);
        rankedMap.put("rankedID1",rankedMap1);


        // build to getStatus ACTIVITY_PAST
        Calendar start6 = Calendar.getInstance();
        start6.add(Calendar.HOUR,-2);
        Calendar end6 = Calendar.getInstance();
        end6.add(Calendar.HOUR,-2);

        final String id6 = "id6";
        DeboxActivity dbaNoEnrolledActivityPast = new DeboxActivity(id6,"dummyOrganiser","dummyTitle","dummyDescription",
                start6 ,end6 ,10.1,10.1,"Sports");


        // build to getStatus ORGANIZER
        Calendar start7 = Calendar.getInstance();
        start7.add(Calendar.HOUR,-2);
        Calendar end7 = Calendar.getInstance();
        end7.add(Calendar.HOUR,-2);

        final String id7 = "id7";
        final String userID ="userID";
        DeboxActivity dbaOrganizer = new DeboxActivity(id7,userID,"dummyTitle","dummyDescription",
                start7 ,end7 ,10.1,10.1,"Sports");


        // build to getStatus NOT_ENROLLED_NOT_FULL
        Calendar start8 = Calendar.getInstance();
        start8.add(Calendar.HOUR,2);
        Calendar end8 = Calendar.getInstance();
        end8.add(Calendar.HOUR,2);

        final String id8 = "id8";
        DeboxActivity dbaNoEnrolledNoFull2 = new DeboxActivity(id8,"dummyOrganiser","dummyTitle","dummyDescription",
                start8 ,end8 ,10.1,10.1,"Sports",8,10);



        final Map<String, Object> userMap = new HashMap<>();


        userMap.put("default_user_name","fakeUserName");
        userMap.put("enrolled",enrolledMap);
        userMap.put("organised",organisedMap);
        userMap.put("ranked",rankedMap);
        userMap.put("ratingNb",1);
        userMap.put("ratingSum",3);
        userMap.put("user_email","fakeemail@noemail.com");


        final DataSnapshot ds = Mockito.mock(DataSnapshot.class);
        when(ds.getValue()).thenReturn(userMap);

        when(mUser.getUid()).thenReturn(userID);
        when(database.getReference(anyString())).thenReturn(myRef);


        //Override addListenerForSingleValueEvent method for test to always return our Map
        toolsBuildAnswerForListener(myRef,ds);



        DataProvider dp = new DataProvider(mDataBaseRef,database,mUser);

        dp.getCurrentUserStatusSimplified(dbaEnrolled, new DataProvider.DataProviderListenerUserState() {
            @Override
            public void getUserState(DataProvider.UserStatus status) {
                assertEquals(status, DataProvider.UserStatus.ENROLLED);
            }
        });

        dp.getCurrentUserStatusSimplified(dbaNoEnrolledNoFull, new DataProvider.DataProviderListenerUserState() {
            @Override
            public void getUserState(DataProvider.UserStatus status) {
                assertEquals(status, DataProvider.UserStatus.NOT_ENROLLED_NOT_FULL);
            }
        });

        dp.getCurrentUserStatusSimplified(dbaNoEnrolledFull, new DataProvider.DataProviderListenerUserState() {
            @Override
            public void getUserState(DataProvider.UserStatus status) {
                assertEquals(status, DataProvider.UserStatus.NOT_ENROLLED_FULL);
            }
        });

        dp.getCurrentUserStatusSimplified(dbaNoEnrolledMustBeRanked, new DataProvider.DataProviderListenerUserState() {
            @Override
            public void getUserState(DataProvider.UserStatus status) {
                assertEquals(status, DataProvider.UserStatus.MUST_BE_RANKED);
            }
        });

        dp.getCurrentUserStatusSimplified(dbaNoEnrolledAlreadyRanked, new DataProvider.DataProviderListenerUserState() {
            @Override
            public void getUserState(DataProvider.UserStatus status) {
                assertEquals(status, DataProvider.UserStatus.ALREADY_RANKED);
            }
        });

        dp.getCurrentUserStatusSimplified(dbaNoEnrolledActivityPast, new DataProvider.DataProviderListenerUserState() {
            @Override
            public void getUserState(DataProvider.UserStatus status) {
                assertEquals(status, DataProvider.UserStatus.ACTIVITY_PAST);
            }
        });

        dp.getCurrentUserStatusSimplified(dbaOrganizer, new DataProvider.DataProviderListenerUserState() {
            @Override
            public void getUserState(DataProvider.UserStatus status) {
                assertEquals(status, DataProvider.UserStatus.ORGANIZER);
            }
        });

        dp.getCurrentUserStatusSimplified(dbaNoEnrolledNoFull2, new DataProvider.DataProviderListenerUserState() {
            @Override
            public void getUserState(DataProvider.UserStatus status) {
                assertEquals(status, DataProvider.UserStatus.NOT_ENROLLED_NOT_FULL);
            }
        });






    }

    /**
     *  Test the function : public String updateActivity(DeboxActivity da)
     *  the function updateActivity update an activity on the dataBase and keep his original
     *  key
     */
    @Test
    public void testUpdateActivity(){

        // build to getStatus ENROLLED
        Calendar start1 = Calendar.getInstance();
        start1.add(Calendar.HOUR,2);
        Calendar end1 = Calendar.getInstance();
        end1.add(Calendar.HOUR,2);

        final String id1 = "id1";
        final DeboxActivity dba = new DeboxActivity(id1,"dummyOrganiser","dummyTitle","dummyDescription",
                start1 ,end1 ,10.1,10.1,"Sports");

        mDataBaseRef = Mockito.mock(DatabaseReference.class);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                HashMap<String, Object> objectBuild = (HashMap<String, Object>) args[0];
                HashMap<String, Object> activityMap = (HashMap<String, Object>) objectBuild.get("activities/"+id1);

                assertEquals(activityMap.get("category"),dba.getCategory());
                assertEquals(activityMap.get("description"),dba.getDescription());
                assertEquals(activityMap.get("latitude"),dba.getLocation()[0]);
                assertEquals(activityMap.get("longitude"),dba.getLocation()[1]);
                assertEquals(activityMap.get("organizer"),dba.getOrganizer());
                assertEquals(activityMap.get("title"),dba.getTitle());

                return null;

            }
        }).when(mDataBaseRef).updateChildren(anyMap());


        DataProvider dp = new DataProvider(mDataBaseRef,database,mUser);

        dp.updateActivity(dba);
    }

    /**
     *  Test the function : public void deleteActivity(DeboxActivity da)
     *  the function deleteActivity remove an activity of the dataBase
     */
    @Test
    public void testDeleteActivity(){

        final String id1 = "id1";
        final DeboxActivity dba = new DeboxActivity(id1,"dummyOrganiser","dummyTitle","dummyDescription",
                Calendar.getInstance() ,Calendar.getInstance() ,10.1,10.1,"Sports");

        mDataBaseRef = Mockito.mock(DatabaseReference.class);
        when(mDataBaseRef.child("activities")).thenReturn(mDataBaseRef);
        when(mDataBaseRef.child(id1)).thenReturn(mDataBaseRef);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {

                assertEquals(true,true);
                return null;

            }
        }).when(mDataBaseRef).removeValue();

        DataProvider dp = new DataProvider(mDataBaseRef,database,mUser);
        dp.deleteActivity(dba);



    }

    /**
     * This function compare to activities to check if they are equalls.
     * @param dba1 first activity
     * @param dba2 second activity
     * @return return true if they are equals otherwise return false
     */
    private boolean toolsActivitiesEquals(DeboxActivity dba1, DeboxActivity dba2){

        if(dba1.getTitle().equals(dba2.getTitle()) &&
                dba1.getDescription().equals(dba2.getDescription()) &&
                dba1.getCategory().equals(dba2.getCategory()) &&
                dba1.getId().equals(dba2.getId()) &&
                dba1.getLocation()[0] == dba2.getLocation()[0] &&
                dba1.getLocation()[1] == dba2.getLocation()[1] &&
                dba1.getTimeEnd().equals(dba2.getTimeEnd()) &&
                dba1.getTimeStart().equals(dba2.getTimeStart())) {

            return true;
        }
        return false;
    }

    /**
     * This function use to build the hashMap corresponding to the activity passed in parameter.
     * Use to emulate the result of the dataBase.
     *
     * @param dba the deboxActivity use to build the hashMap
     * @return an hashMap that contains all information of the activity
     */
    private Map<String, Object> toolsBuildMapFromDebox(DeboxActivity dba){

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

    /**
     * This function use to build the hashMap corresponding to the activity passed in parameter (we can
     * specified the number max of participants and the number of participants. Use to emulate the result
     * of the dataBase.
     *
     * @param dba the deboxActivity use to build the hashMap
     * @param nbOfParticipants the number of participants
     * @param nbMaxOfParticipants the number max of participants
     * @return an hashMap that contains all information of the activity
     */

    private Map<String, Object> toolsBuildMapFromDebox(DeboxActivity dba, int nbOfParticipants, int nbMaxOfParticipants){

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

    /**
     * This function is used to create an activity with a specified unique id.
     *
     * @param UID unique id of the application
     * @return standard DeboxActivity with specified id
     */
    private DeboxActivity toolsBuildDummyDeboxActivity(String UID){

        String organiser = "emptyOrganiser";
        String title = "emptyTitle";
        String description = "emptydescription";
        double latitude = 10.1;
        double longitude = 10.1;
        String category = "Sports";

        DeboxActivity dba = new DeboxActivity(UID,organiser,title,description,
                Calendar.getInstance(),Calendar.getInstance(),latitude,longitude,category);
        return dba;
    }

    /**
     * This function is use to set the answer to send when a listener is add on the a Database
     * reference.
     *
     * @param myRef the DataBaseReference
     * @param ds the DataSnapshot to send at the DataBaseReference Listener
     */
    private void toolsBuildAnswerForListener(DatabaseReference myRef, final DataSnapshot ds){

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ValueEventListener listener = (ValueEventListener) args[0];
                listener.onDataChange(ds);
                return null;
            }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

    }

}
