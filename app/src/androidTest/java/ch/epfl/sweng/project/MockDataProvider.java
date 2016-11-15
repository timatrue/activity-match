package ch.epfl.sweng.project;

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


/**
 * Created by jeremie on 09.11.16.
 */

public class MockDataProvider {

    @Mock
    DataProvider mockDataProvider;

    private List<DeboxActivity> listDeboxActivityStored;
    private List<DataProvider.CategoryName> listCategoryStored;
    private List<String> listUserActivityEnrolledStored;
    private String userID;

    public DataProvider getMockDataProvider(){

        mockDataProvider = Mockito.mock(DataProvider.class);
        initMocPushActivity();
        initMocGetAllActivities();
        initMocGetActivityFromUid();
        initMockGetAllCategories();
        initMockGetSpecifiedCategory();
        initMockUserIsEnrolledInActivity();
        initJoinActivity();
        listDeboxActivityStored = new ArrayList<>();
        listCategoryStored = new ArrayList<>();
        listUserActivityEnrolledStored = new ArrayList<>();
        userID="default";

        return mockDataProvider;
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
}