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
    static DataProvider mockDataProvider;



    //when(mockDataProvider.pushActivity(any(DeboxActivity.class))).thenReturn("return");
    //
    //  fonction : setListActivity
    //
    //

    static public DataProvider getMockDataProvider(){
        //if(mockDataProvider == null){
            mockDataProvider = Mockito.mock(DataProvider.class);
            initBasicDataProvider();


            return mockDataProvider;
        //} else {
         //   return mockDataProvider;
       // }
    }

    static private void initBasicDataProvider(){

        when(mockDataProvider.pushActivity(any(DeboxActivity.class))).thenReturn("sample");

    }

    static List<DeboxActivity> ll;

    static public void setActivityWhenAskAll(final List<DeboxActivity> list){

        ll=list;
        buildAnswerAllActivities();


    }

    static public void addActivityToMock(DeboxActivity dba){
        ll.add(dba);
        buildAnswerAllActivities();


    }

    static private void buildAnswerAllActivities(){
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