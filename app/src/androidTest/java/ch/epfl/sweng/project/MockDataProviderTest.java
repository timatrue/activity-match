package ch.epfl.sweng.project;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by jeremie on 13.11.16.
 */

public class MockDataProviderTest {


    final DeboxActivity activity1 = new DeboxActivity("idActivity_1","organiser_1", "titel_1",
            "description_1",
            Calendar.getInstance(),
            Calendar.getInstance(),
            122.01,
            121.0213,
            "Sports");

    final DeboxActivity activity2 = new DeboxActivity("idActivity_2","organiser_2", "titel_2",
            "description_2",
            Calendar.getInstance(),
            Calendar.getInstance(),
            122.01,
            121.0213,
            "Sports");

    final DeboxActivity activity3 = new DeboxActivity("idActivity_3","organiser_3", "titel_3",
            "description_3",
            Calendar.getInstance(),
            Calendar.getInstance(),
            122.01,
            121.0213,
            "Culture");

    final DeboxActivity activity4 = new DeboxActivity("idActivity_4","organiser_4", "titel_4",
            "description_4",
            Calendar.getInstance(),
            Calendar.getInstance(),
            122.01,
            121.0213,
            "Culture");



    @Test
    public void getAllActivitiesFromMockTest(){


        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();

        final List<DeboxActivity> activitiesListLocal = new ArrayList<>();

        activitiesListLocal.add(activity1);
        activitiesListLocal.add(activity2);
        activitiesListLocal.add(activity3);
        activitiesListLocal.add(activity4);

        mocDataProvider.setListOfActivitiesToMock(activitiesListLocal);

        dp.getAllActivities(new DataProvider.DataProviderListenerActivities() {
            @Override
            public void getActivities(List<DeboxActivity> activitiesList) {
                assertEquals(activitiesList,activitiesListLocal);
            }
        });

    }


    @Test
    public void getActivityFromUidFromMocTest(){

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();

        final List<DeboxActivity> activitiesList = new ArrayList<>();

        activitiesList.add(activity1);
        activitiesList.add(activity2);
        activitiesList.add(activity3);
        activitiesList.add(activity4);

        mocDataProvider.setListOfActivitiesToMock(activitiesList);

        dp.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
            @Override
            public void getActivity(DeboxActivity activity) {
                assertEquals(activity,activity2);

            }
        },activity2.getId());

        dp.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
            @Override
            public void getActivity(DeboxActivity activity) {
                assertEquals(activity,activity1);

            }
        },activity1.getId());

        dp.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
            @Override
            public void getActivity(DeboxActivity activity) {
                assertEquals(activity,activity4);

            }
        },activity4.getId());

        dp.getActivityFromUid(new DataProvider.DataProviderListenerActivity() {
            @Override
            public void getActivity(DeboxActivity activity) {
                assertEquals(activity,activity4);

            }
        },activity4.getId());
    }


    @Test
    public void getAllCategoriesFromMocTest(){

        MockDataProvider mocDataProvider = new MockDataProvider();
        DataProvider dp = mocDataProvider.getMockDataProvider();

        final List<DataProvider.CategoryName> listAllCategory = new ArrayList<>();

        listAllCategory.add(new DataProvider.CategoryName("1","Sports"));
        listAllCategory.add(new DataProvider.CategoryName("2","Culture"));

        mocDataProvider.setListOfCategoryToMock(listAllCategory);

        dp.getAllCategories(new DataProvider.DataProviderListenerCategories() {
            @Override
            public void getCategories(List<DataProvider.CategoryName> categoriesList) {
                assertEquals(listAllCategory,categoriesList);
            }
        });
    }

}
