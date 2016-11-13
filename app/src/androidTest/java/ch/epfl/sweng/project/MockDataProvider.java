package ch.epfl.sweng.project;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

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

    public DataProvider getMockDataProvider(){

            mockDataProvider = Mockito.mock(DataProvider.class);
            initBasicDataProvider();
            listDeboxActivityStored= new ArrayList<>();
            return mockDataProvider;
    }

    public void setListOfActivitiesToMock(final List<DeboxActivity> list){

        listDeboxActivityStored=list;
        initMocGetAllActivities();
        initMocGetActivityFromUid();
    }

    public void addActivityToMock(DeboxActivity dba){

        if(listDeboxActivityStored == null)
        {
            listDeboxActivityStored= new ArrayList<>();
        }
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

    private void initBasicDataProvider(){

        doAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DeboxActivity dbatmp = (DeboxActivity) args[0];
                listDeboxActivityStored.add(dbatmp);

                return dbatmp.getId();
            }
        }).when(mockDataProvider).pushActivity(any(DeboxActivity.class));
    }
}