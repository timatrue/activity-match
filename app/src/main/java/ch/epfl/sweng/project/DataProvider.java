package ch.epfl.sweng.project;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
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

    /*public ArrayList<DeboxActivity> getAllActivities() {

        return deboxActivityList;
    }*/


    //public void getActivityByID(String id){}

    public void pushActivity(DeboxActivity da){

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


        childActivityUpdate.put(da.getId(),result);

        childUpdates.put("activities", childActivityUpdate);

        mDatabase.updateChildren(childUpdates);
    }

    public DeboxActivity getActivityFromUid(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activities/" + uid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                int c = 2;
            }
        });
        return null;
    }


}