package ch.epfl.sweng.project;

import android.support.test.filters.LargeTest;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@LargeTest
public class MockDataProvider {

    @Mock
    DataProvider mockDataProvider;

    private List<DataProvider.CategoryName> listCategoryStored;
    private List<DeboxActivity> listDeboxActivityStored;
    private String userID;
    private String username;
    private String email;
    private List<String> listUserActivityOrganizedStored;
    private List<String> listUserActivityEnrolledStored;
    private List<String> listUserRankedEvents;
    private int ratingNb;
    private int ratingSum;
    private String photoLink;

    public DataProvider getMockDataProvider(){

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
        listCategoryStored = new ArrayList<>();
        listDeboxActivityStored = new ArrayList<>();
        userID = "def_id";
        username = "def_username";
        email = "def_email";
        listUserActivityOrganizedStored = new ArrayList<>();
        listUserActivityEnrolledStored = new ArrayList<>();
        listUserRankedEvents = new ArrayList<>();
        ratingNb = 10;
        ratingSum = 44;
        photoLink = "def_photoLink";

        return mockDataProvider;
    }

    public void setUserToMock(User newUser){
        userID = newUser.getId();
        username = newUser.getUsername();
        email = newUser.getEmail();
        listUserActivityOrganizedStored = newUser.getOrganizedEventIds();
        listUserActivityEnrolledStored = newUser.getInterestedEventIds();
        ratingNb = newUser.getRatingNb();
        ratingSum = newUser.getRatingSum();
        photoLink = newUser.getPhotoLink();
        initMockUserProfile();
    }

    public void setUserIDToMock(final String id){
        userID=id;
        initMockUserIsEnrolledInActivity();
        initJoinActivity();
    }

    public void setListOfEnrolledActivityToMock(List<String> list){
        listUserActivityEnrolledStored = list;
        initMockUserIsEnrolledInActivity();
        initJoinActivity();

    }

    public void setListOfCategoryToMock(final List<DataProvider.CategoryName> list){
        initMockGetAllCategories();
        listCategoryStored = list;
    }

    public void setListOfActivitiesToMock(final List<DeboxActivity> list){

        listDeboxActivityStored=list;
        initMocGetAllActivities();
        initMocGetActivityFromUid();
    }

    public void addActivityToMock(DeboxActivity dba){

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
        }).when(mockDataProvider).userProfile(any(DataProvider.DataProviderListenerUserInfo.class));

        doAnswer(new Answer<Void>() {
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
        }).when(mockDataProvider).getUserProfileAndListenerOnChange(any(DataProvider.DataProviderListenerUserInfo.class));
    }

    private void initMockGetSpecifiedActivities(){
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerUserEvents listener = (DataProvider.DataProviderListenerUserEvents) args[0];
                List<String> intEvents = (List<String>) args[1];
                List<String> orgEvents = (List<String>) args[2];
                List<DeboxActivity> intDA = new ArrayList<>();
                List<DeboxActivity> orgDA = new ArrayList<>();
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

                listener.getUserActivities(intDA, orgDA);
                return null;
            }
        }).when(mockDataProvider).getSpecifiedActivities(any(DataProvider.DataProviderListenerUserEvents.class), any(List.class), any(List.class));
    }
}
