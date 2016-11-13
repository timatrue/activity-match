package ch.epfl.sweng.project;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Created by jeremie on 09.11.16.
 */

public class MockDataProvider {

    @Mock
    DataProvider mockDataProvider;

    private List<DeboxActivity> ll;

    //when(mockDataProvider.pushActivity(any(DeboxActivity.class))).thenReturn("return");
    //
    //  fonction : setListActivity
    //
    //

    public DataProvider getMockDataProvider(){

            mockDataProvider = Mockito.mock(DataProvider.class);
            initBasicDataProvider();
            ll= new ArrayList<DeboxActivity>();
            return mockDataProvider;
    }


    private void initBasicDataProvider(){
        //TODO - add activity to list


        //when(mockDataProvider.pushActivity(any(DeboxActivity.class))).thenReturn("sample");
        doAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                //DataProvider.DataProviderListenerActivities listener = (DataProvider.DataProviderListenerActivities) args[0];
                //listener.getActivities(ll);

                DeboxActivity dbatmp = (DeboxActivity) args[0];

                ll.add(dbatmp);

                return dbatmp.getId();
            }
        }).when(mockDataProvider).pushActivity(any(DeboxActivity.class));




    }





    public void setListOfActivitiesToMock(final List<DeboxActivity> list){

        ll=list;
        initMocGetAllActivities();
        initMocGetActivityFromUid();
    }

    public void addActivityToMock(DeboxActivity dba){

        if(ll == null)
        {
            ll= new ArrayList<DeboxActivity>();
        }
        ll.add(dba);
        initMocGetAllActivities();
        initMocGetActivityFromUid();

    }

    private void initMocGetAllActivities(){
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivities listener = (DataProvider.DataProviderListenerActivities) args[0];
                listener.getActivities(ll);
                return null;
            }
        }).when(mockDataProvider).getAllActivities(any(DataProvider.DataProviderListenerActivities.class));

    }

    private void initMocGetActivityFromUid(){
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataProvider.DataProviderListenerActivity listener = (DataProvider.DataProviderListenerActivity) args[0];

                String id = (String) args[1];

                DeboxActivity foundDbA = null;

                for (DeboxActivity deb : ll){

                    if(deb.getId().equals((String) args[1])){
                        foundDbA =  deb;
                    }
                }

                listener.getActivity(foundDbA);

                return null;
            }
        }).when(mockDataProvider).getActivityFromUid(any(DataProvider.DataProviderListenerActivity.class), anyString());


    }
}



/*


      }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        //Override getReference method to return the Mock reference
        when(database.getReference("activities/" + uuidTest)).thenReturn(myRef);
 */