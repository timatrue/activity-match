package ch.epfl.sweng.project;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by jeremie on 12.10.16.
 */

public class DataProvider {

    private static ArrayList<DeboxActivity> deboxActivityList;//= new ArrayList<String>();
    private static ArrayList<CategoryName> deboxCategoriesList;
    private DatabaseReference mDatabase;

    public DataProvider() {

      deboxActivityList = new ArrayList<DeboxActivity>();
      mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public class CategoryName{
        String categoryId;
        String nameCategory;
        public CategoryName(String categoryId, String nameCategory){
            this.categoryId = categoryId;
            this.nameCategory = nameCategory;
        }
        public String getCategoryId() {
            return this.categoryId;
        }
    }
    public void getAllCategories(final DataProviderListenerCategories listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myCategories = database.getReference("categories");

        myCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<CategoryName> categoriesList = new ArrayList<CategoryName>();
                for(DataSnapshot child: dataSnapshot.getChildren()) {
                    String id = child.getKey().toString();
                    String name = child.getValue().toString();
                    CategoryName category = new CategoryName(id,name);
                    categoriesList.add(category);
                }
                listener.getCategories(categoriesList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                int c = 2;
            }
        });

    }
    public void getSpecifiedCategory(final DataProviderListenerCategory listener, String specifiedCategory) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference getActivities = database.getReference("activities");
        Query getCategory = getActivities.orderByChild("category").equalTo(specifiedCategory);

        getCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<DeboxActivity> activitiesList = new ArrayList<DeboxActivity>();
                for(DataSnapshot child: dataSnapshot.getChildren()) {
                    activitiesList.add(getDeboxActivity(child.getKey(), (Map<String, Object>) child.getValue()));
                }
                listener.getCategory(activitiesList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                int c = 2;
            }
        });

    }

    public void getAllActivities(final DataProviderListenerActivities listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activities");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<DeboxActivity> list = new ArrayList<DeboxActivity>();
                for(DataSnapshot child: dataSnapshot.getChildren()) {
                    list.add(getDeboxActivity(child.getKey(), (Map<String, Object>) child.getValue()));
                }

                listener.getActivities(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                int c = 2;
            }
        });
    }


    public String pushActivity(DeboxActivity da){


        String key = mDatabase.child("activities").push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> childActivityUpdate = new HashMap<>();
        HashMap<String, Object> result = new HashMap<>();

        double location [] = da.getLocation();

        result.put("organizer",da.getOrganizer());
        result.put("title",da.getTitle());
        result.put("description",da.getDescription());


        long tmStart = da.getTimeStart().getTimeInMillis();
        long tmEnd = da.getTimeEnd().getTimeInMillis();
        result.put("timeStart",tmStart);
        result.put("timeEnd",tmEnd);


        result.put("latitude",location[0]);
        result.put("longitude",location[1]);
        result.put("category",da.getCategory());


        childUpdates.put("activities/"+key, result);

        mDatabase.updateChildren(childUpdates);

        return key;
    }

    private DeboxActivity getDeboxActivity(String uid, Map<String, Object> activityMap) {

        String category = (String) activityMap.get("category");
        String description = (String) activityMap.get("description");
        Double latitude = (Double) activityMap.get("latitude");
        Double longitude = (Double) activityMap.get("longitude");
        String organizer = (String) activityMap.get("organizer");
        String title = (String) activityMap.get("title");

        Calendar timeStart = Calendar.getInstance();
        Long timeStartMillis = (Long) activityMap.get("timeStart");
        if(timeStartMillis != null) {
            timeStart.setTimeInMillis(timeStartMillis);
        }

        Calendar timeEnd = Calendar.getInstance();
        Long timeEndMillis = (Long) activityMap.get("timeEnd");
        if(timeEndMillis != null) {
            timeEnd.setTimeInMillis(timeEndMillis);
        }

        return new DeboxActivity(uid, organizer, title, description,timeStart, timeEnd, latitude, longitude, category);

    }

    public void getActivityFromUid(final DataProviderListenerActivities listener, final String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activities/" + uid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> activityMap = (Map<String, Object>) dataSnapshot.getValue();



                listener.getActivity(getDeboxActivity(uid, activityMap));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                int c = 2;
            }
        });
    }

    public interface DataProviderListenerActivities {
        public void getActivity(DeboxActivity activity);
        public void getActivities(List<DeboxActivity> activitiesList);
    }
    public interface DataProviderListenerCategories {
        public void getCategories(ArrayList<CategoryName> deboxCategoriesList);
    }
    public interface DataProviderListenerCategory {
        public void getCategory(List<DeboxActivity> activitiesList);
    }


}