package ch.epfl.sweng.project;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

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

    public ArrayList<DeboxActivity> getAllActivities() {  // array list

        return deboxActivityList;
    }

    public void test(){
        Log.e("Debug text","from dataprovider");

        String activitiesId = "sampleID";
        mDatabase.child("test_input_db").child(activitiesId).setValue("Hello my word!");
    }

    //public DeboxActivity getActivityByID(String id){}

    public void pushActivity(DeboxActivity da){

    }


}