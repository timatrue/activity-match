package ch.epfl.sweng.project;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Created by jeremie on 09.11.16.
 */

public class MockDataProvider {

    @Mock
    DataProvider mockDataProvider;

    List<DeboxActivity> ll;

    //when(mockDataProvider.pushActivity(any(DeboxActivity.class))).thenReturn("return");
    //
    //  fonction : setListActivity
    //
    //

    public DataProvider getMockDataProvider(){

            mockDataProvider = Mockito.mock(DataProvider.class);
            initBasicDataProvider();
            return mockDataProvider;
    }

    private void initBasicDataProvider(){

        when(mockDataProvider.pushActivity(any(DeboxActivity.class))).thenReturn("sample");

    }



    public void setActivities(final List<DeboxActivity> list){

        ll=list;
        initMocGetAllActivities();


    }

    public void addActivityToMock(DeboxActivity dba){

        ll.add(dba);
        initMocGetAllActivities();

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
}



/*


      }
        }).when(myRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        //Override getReference method to return the Mock reference
        when(database.getReference("activities/" + uuidTest)).thenReturn(myRef);
 */