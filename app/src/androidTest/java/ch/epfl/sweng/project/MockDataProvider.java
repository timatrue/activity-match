package ch.epfl.sweng.project;

import android.support.test.filters.LargeTest;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@LargeTest
class MockDataProvider {

    @Mock
    DataProvider mockDataProvider;

    private List<DeboxActivity> listDeboxActivityStored;
    private List<DataProvider.CategoryName> listCategoryStored;
    private List<String> listUserActivityEnrolledStored;
    private String userID;
    private User user;

    DataProvider getMockDataProvider(){

        mockDataProvider = Mockito.mock(DataProvider.class);
        initMocPushActivity();
        initMocGetAllActivities();
        initMocGetActivityFromUid();
        initMockGetAllCategories();
        initMockGetSpecifiedCategory();
        initMockUserIsEnrolledInActivity();
        initJoinActivity();
        initMockUserProfile();
        initMockGetSpecifiedActivities();
        listDeboxActivityStored = new ArrayList<>();
        listCategoryStored = new ArrayList<>();
        listUserActivityEnrolledStored = new ArrayList<>();
        userID="default";
        user = new User("def_id", "def_username", "def_email", new ArrayList<String>(), new ArrayList<String>(),
                new ArrayList<String>(),10, 5, "def_photoLink", new ArrayList<Map<String, String>>());

        return mockDataProvider;
    }

    void setUserToMock(User newUser){
        user = newUser;
        initMockUserProfile();
    }

    public void setUserIDToMock(final String id){
        userID=id;
        initMockUserIsEnrolledInActivity();
        initJoinActivity();
    }

    void setListOfEnrolledActivityToMock(List<String> list){
        listUserActivityEnrolledStored = list;
        initMockUserIsEnrolledInActivity();
        initJoinActivity();

    }

    void setListOfCategoryToMock(final List<DataProvider.CategoryName> list){
        initMockGetAllCategories();
        listCategoryStored = list;
    }

    void setListOfActivitiesToMock(final List<DeboxActivity> list){

        listDeboxActivityStored=list;
        initMocGetAllActivities();
        initMocGetActivityFromUid();
    }

    void addActivityToMock(DeboxActivity dba){

        listDeboxActivityStored.add(dba);
        initMocGetAllActivities();
        initMocGetActivityFromUid();

    }

    private void initMocGetAllActivities(){
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivities listener = (DataProvider.DataProviderListenerActivities) args[0];
                listener.getActivities(listDeboxActivityStored);
                return null;
            }
        }).when(mockDataProvider).getAllActivities(any(DataProvider.DataProviderListenerActivities.class));

    }

    private void initMocGetActivityFromUid(){

        when(mockDataProvider.getUserStatusInActivity(any(DeboxActivity.class),any(User.class))).thenReturn(DataProvider.UserStatus.NOT_ENROLLED_NOT_FULL);
        //when(myRef.child("ratingNb")).thenReturn(myRefRatingNb);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivity listener = (DataProvider.DataProviderListenerActivity) args[0];

                DeboxActivity foundDbA = null;

                for (DeboxActivity deb : listDeboxActivityStored){

                    if(deb.getId().equals(args[1])){
                        foundDbA =  deb;
                    }
                }

                listener.getActivity(foundDbA);
                return null;
            }
        }).when(mockDataProvider).getActivityFromUid(any(DataProvider.DataProviderListenerActivity.class), anyString());

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivity listener = (DataProvider.DataProviderListenerActivity) args[0];

                DeboxActivity foundDbA = null;

                for (DeboxActivity deb : listDeboxActivityStored){

                    if(deb.getId().equals(args[1])){
                        foundDbA =  deb;
                    }
                }

                listener.getActivity(foundDbA);
                return null;
            }
        }).when(mockDataProvider).getActivityAndListenerOnChange(any(DataProvider.DataProviderListenerActivity.class), anyString());
    }

    private void initMocPushActivity(){

        doAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DeboxActivity dbatmp = (DeboxActivity) args[0];
                listDeboxActivityStored.add(dbatmp);

                return dbatmp.getId();
            }
        }).when(mockDataProvider).pushActivity(any(DeboxActivity.class));
    }

    private void initMockGetAllCategories(){

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerCategories listener = (DataProvider.DataProviderListenerCategories) args[0];
                listener.getCategories(listCategoryStored);
                return null;
            }
        }).when(mockDataProvider).getAllCategories(any(DataProvider.DataProviderListenerCategories.class));
    }

    private void initMockGetSpecifiedCategory(){

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerCategory listener = (DataProvider.DataProviderListenerCategory) args[0];

                List<DeboxActivity> result = new ArrayList<>();

                for (DeboxActivity deb : listDeboxActivityStored) {

                    if (deb.getCategory().equals(args[1])) {
                        result.add(deb);
                    }
                }
                listener.getCategory(result);
                return null;
            }
        }).when(mockDataProvider).getSpecifiedCategory(any(DataProvider.DataProviderListenerCategory.class),any(String.class));
    }

    private void initMockUserIsEnrolledInActivity(){

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {

                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerEnrolled listener = (DataProvider.DataProviderListenerEnrolled) args[0];

                boolean result = false;
                /*if(listUserActivityEnrolledStored.isEmpty()){
                    listener.getIfEnrolled(false);
                    return null;
                }*/

                for(String activityUid : listUserActivityEnrolledStored ) {

                    if (activityUid.equals(args[1])){
                        result = true;
                    }
                }

                listener.getIfEnrolled(result);
                return null;

            }
        }).when(mockDataProvider).userEnrolledInActivity(any(DataProvider.DataProviderListenerEnrolled.class),any(String.class));
    }

    @SuppressWarnings("deprecation")
    private void initJoinActivity(){
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                listUserActivityEnrolledStored.add(((DeboxActivity)args[0]).getId());

                return null;
            }
        }).when(mockDataProvider).joinActivity(any(DeboxActivity.class));
    }

    private void initMockUserProfile(){
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerUserInfo listener = (DataProvider.DataProviderListenerUserInfo) args[0];

                listener.getUserInfo(user);
                return null;
            }
        }).when(mockDataProvider).userProfile(any(DataProvider.DataProviderListenerUserInfo.class));


        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerUserInfo listener = (DataProvider.DataProviderListenerUserInfo) args[0];

                listener.getUserInfo(user);
                return null;
            }
        }).when(mockDataProvider).getUserProfileAndListenerOnChange(any(DataProvider.DataProviderListenerUserInfo.class));


        /*doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerUserInfo listener = (DataProvider.DataProviderListenerUserInfo) args[0];

                listener.getUserInfo(new User(
                        userID,
                        username,
                        email,
                        listUserActivityOrganizedStored,
                        listUserActivityEnrolledStored,
                        listUserRankedEvents,
                        ratingNb,
                        ratingSum,
                        photoLink));
                return null;
            }
        }).when(mockDataProvider).getUserProfileAndListenerOnChange(any(DataProvider.DataProviderListenerUserInfo.class));*/
    }

    private void initMockGetSpecifiedActivities(){
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerUserEvents listener = (DataProvider.DataProviderListenerUserEvents) args[0];
                List<String> intEvents = (List<String>) args[1];
                List<String> orgEvents = (List<String>) args[2];
                List<String> rankEvents = (List<String>) args[3];

                List<DeboxActivity> intDA = new ArrayList<>();
                List<DeboxActivity> orgDA = new ArrayList<>();
                List<DeboxActivity> rankDA = new ArrayList<>();

                for (String uid : intEvents){
                    for (DeboxActivity deb : listDeboxActivityStored){
                        if(deb.getId().equals(uid)){
                            intDA.add(deb);
                        }
                    }
                }
                for (String uid : orgEvents){
                    for (DeboxActivity deb : listDeboxActivityStored){
                        if(deb.getId().equals(uid)){
                            orgDA.add(deb);
                        }
                    }
                }

                for (String uid : rankEvents){
                    for (DeboxActivity deb : listDeboxActivityStored){
                        if(deb.getId().equals(uid)){
                            rankDA.add(deb);
                        }
                    }
                }

                listener.getUserActivities(intDA, orgDA,rankDA);
                return null;
            }
        }).when(mockDataProvider).getSpecifiedActivities(any(DataProvider.DataProviderListenerUserEvents.class), any(List.class), any(List.class),any(List.class));
    }
}
