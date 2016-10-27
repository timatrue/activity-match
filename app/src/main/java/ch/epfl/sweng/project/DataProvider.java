package ch.epfl.sweng.project;


import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    private static ArrayList<DeboxActivity> deboxActivityList;//= new ArrayList<String>();
    private DatabaseReference mDatabase;

    public DataProvider() {

      deboxActivityList = new ArrayList<DeboxActivity>();
      mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void getAllActivities(final DataProviderListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activities");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<DeboxActivity> list = new ArrayList<DeboxActivity>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    list.add(getDeboxActivity(snapshot.getKey(), (Map<String, Object>) snapshot.getValue()));
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
            public void onCancelled(DatabaseError databaseError) {
                int c = 2;
            }
        });
    }

    public interface DataProviderListener {
        public void getActivity(DeboxActivity activity);
        public void getActivities(List<DeboxActivity> activitiesList);
    }

    public void joinActivity(DeboxActivity dba){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String, Object> enrolledChild = new HashMap<>();
        enrolledChild.put("activity ID:",dba.getId());

        String enrolledKey = mDatabase.child("users").child(user.getUid()).child("enrolled").push().getKey();
        HashMap<String, Object> enrolled = new HashMap<>();
        enrolled.put("user_email",user.getEmail());
        enrolled.put("enrolled/"+enrolledKey,enrolledChild);

        mDatabase.child("users").child(user.getUid()).updateChildren(enrolled);

    }

}