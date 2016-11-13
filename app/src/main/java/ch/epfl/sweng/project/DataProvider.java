package ch.epfl.sweng.project;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


/**
 * Created by jeremie on 12.10.16.
 */

public class DataProvider {

    private static ArrayList<DeboxActivity> deboxActivityList;
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
        public String getCategory() {
            return this.nameCategory;
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

    public void getAllActivities(final DataProviderListener listener) {
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
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getSpecifiedActivities(final DataProviderListenerUserEvents listener, final List<String> eventIds) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activities");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<DeboxActivity> list = new ArrayList<DeboxActivity>();
                for(DataSnapshot child: dataSnapshot.getChildren()) {
                    if (eventIds.contains(child.getKey()))
                        list.add(getDeboxActivity(child.getKey(), (Map<String, Object>) child.getValue()));
                }
                listener.getUserActivities(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public String pushActivity(DeboxActivity da){


        String key = mDatabase.child("activities").push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
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

        Double latitude = Double.valueOf(activityMap.get("latitude").toString());
        Double longitude = Double.valueOf(activityMap.get("longitude").toString());

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

    public void getActivityFromUid(final DataProviderListener listener, final String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activities/" + uid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> activityMap = (Map<String, Object>) dataSnapshot.getValue();
                listener.getActivity(getDeboxActivity(uid, activityMap));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    private User getDeboxUser(String uid, Map<String, Object> activityMap) {
        String email = (String) activityMap.get("user_email");
        String username = "";
        List<String> interestedEvents = new ArrayList<>();
        Map<String,Map<String,Object> > enrolled = (Map<String,Map<String,Object> >) activityMap.get("enrolled");
        for (Map<String, Object> innerMap : enrolled.values()) {
            String activityID = (String) innerMap.get("activity ID:");
            interestedEvents.add(activityID);
        }
        List<String> participatedEvents = new ArrayList<String>();
        List<String> organizedEvents = new ArrayList<String>();
        String rating = "";
        String photoLink = "";

        return new User(uid, username, email, organizedEvents, participatedEvents, interestedEvents, rating, photoLink);
    }

    public void userProfile(final DataProviderListenerUserInfo listener){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userUid = user.getUid();
        //do try catch;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference getUserProfile = database.getReference("users/" + userUid);

        getUserProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> userMap = (Map<String, Object>) dataSnapshot.getValue();
                listener.getUserInfo(getDeboxUser(userUid, userMap));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }


    /**
     * Check if the current user is already enrolled in the uid activity.
     * Send response through the listener
     *
     * @param listener
     * @param uid
     */
    public void userEnrolledInActivity(final DataProviderListener listener, final String uid) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userUid;
        if (user != null)
            userUid = user.getUid();
        else
            userUid = "testUser";

        //DatabaseReference myRef = database.getReference("users/"+user.getUid()+"/enrolled");
        DatabaseReference myRef = database.getReference("users/" + userUid + "/enrolled");


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> listEnrolled = (Map<String, Object>) dataSnapshot.getValue();

                Boolean alreadyEnrolled = false;

                if (listEnrolled != null) {

                    for (Map.Entry<String, Object> enrolledEntry : listEnrolled.entrySet()) {

                        String activityID = (String) ((Map<String, Object>) enrolledEntry.getValue()).get("activity ID:");

                        if (activityID.equals(uid)) {
                            alreadyEnrolled = true;
                        }
                    }
                }
                listener.getIfEnrolled(alreadyEnrolled);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Enroll the user to the activity dba. If the user doesn't have an entry in users table, an
     * entry corresponding to the user is automatically added to the table users.
     *
     * @param dba
     */

    public void joinActivity(DeboxActivity dba){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String, Object> enrolledChild = new HashMap<>();
        enrolledChild.put("activity ID:",dba.getId());

        // get unique key for enroll the activity
        String enrolledKey = mDatabase.child("users").child(user.getUid()).child("enrolled").push().getKey();
        HashMap<String, Object> enrolled = new HashMap<>();

        enrolled.put("enrolled/"+enrolledKey,enrolledChild);
        enrolled.put("user_email",user.getEmail());

        // update the database
        mDatabase.child("users").child(user.getUid()).updateChildren(enrolled);

    }

    public interface DataProviderListener {
        void getActivity(DeboxActivity activity);
        void getActivities(List<DeboxActivity> activitiesList);
        void getIfEnrolled(boolean result);
    }
    public interface DataProviderListenerCategories {
        void getCategories(List<CategoryName> categoriesList);
    }
    public interface DataProviderListenerCategory {
        void getCategory(List<DeboxActivity> activitiesList);
    }
    public interface DataProviderListenerUserInfo {
        void getUserInfo(User user);
    }
    public interface DataProviderListenerUserEvents {
        void getUserActivities(List<DeboxActivity> activitiesList);
    }

}