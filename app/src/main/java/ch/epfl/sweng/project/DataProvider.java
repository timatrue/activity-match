package ch.epfl.sweng.project;

import android.util.Log;
import android.view.View;

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
import java.util.Objects;


/**
 * Created by jeremie on 12.10.16.
 */

public class DataProvider {

    private static ArrayList<DeboxActivity> deboxActivityList;
    private static ArrayList<CategoryName> deboxCategoriesList;

    private DatabaseReference mDatabase;
    private FirebaseUser user ;
    private FirebaseDatabase database;

    public DataProvider() {

        deboxActivityList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    // use for moc test
    public DataProvider(DatabaseReference mockDatabaseReference, FirebaseDatabase mockFireDataBase, FirebaseUser mockUser) {

        deboxActivityList = new ArrayList<>();
        mDatabase = mockDatabaseReference;
        database = mockFireDataBase;
        user = mockUser;
    }


    public static class CategoryName{
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

    public enum UserStatus{
        ENROLLED,
        NOT_ENROLLED_NOT_FULL,
        NOT_ENROLLED_FULL,
        MUST_BE_RANKED,
        ALREADY_RANKED,
        ACTIVITY_PAST,;
    }


    /*
                                     +----------+
                                     |Enrolled ?|
                                     +--+----+--+
                                yes     |    |     no
                     +------------------+    +------------------+
                     |                           +-----------+  |
                 +---+--+                                    +--+---+
                 |Past ?|                                    |Past ?|
                 +-+--+-+                                    +-+--+-+
             yes   |  |    no                           yes    |  |      no
          +--------+  +---------+                +-------------+  +-----------------+
          |                     |                |                                  |
          +                     +            +---+----+                         +---+--------+
    :MustBeRanked           :Enrolled        |Ranked ?|                         |Place left ?|
                                             +-+----+-+                         +-+--------+-+
                                           yes |    | no                     yes  |        |   no
                                        +------+    +---+                   +-----+        +------+
                                        +               +                   +                     +
                                 :AlreadyRanked   :ActivityPast    :NotEnrolledPlaceLeft   :NotEnrolledFull



 */


    public void getCurrentUserStatus(final String uid,final DataProviderListenerUserState listener){


        userEnrolledInActivity(new DataProvider.DataProviderListenerEnrolled() {
            @Override
            public void getIfEnrolled(boolean isAlreadyEnrolled) {

                if (isAlreadyEnrolled) {

                    getIfActivityIsPast(uid, new DataProviderListenerIsPast() {
                        @Override
                        public void getIfActivityIsPast(boolean result) {
                            if(result){
                                listener.getUserState(UserStatus.MUST_BE_RANKED);

                            } else {
                                listener.getUserState(UserStatus.ENROLLED);
                            }
                        }
                    });

                } else {

                    getIfActivityIsPast(uid, new DataProviderListenerIsPast() {
                        @Override
                        public void getIfActivityIsPast(boolean result) {
                            if(result){

                                getIfAlreadyRanked(uid,new DataProviderListenerAlreadyRanked(){
                                    @Override
                                    public void getIfRanked(boolean result) {
                                        if(result){
                                            listener.getUserState(UserStatus.ALREADY_RANKED);

                                        } else {
                                            listener.getUserState(UserStatus.ACTIVITY_PAST);
                                        }

                                    }
                                });

                            } else {

                                getIfPlaceLeftInActivity(uid, new DataProviderListenerPlaceFreeInActivity() {
                                    @Override
                                    public void getIfFreePlace(boolean result) {
                                        if(result){
                                            listener.getUserState(UserStatus.NOT_ENROLLED_NOT_FULL);
                                        } else {
                                            listener.getUserState(UserStatus.NOT_ENROLLED_FULL);
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        }, uid);

    }

    public void getIfPlaceLeftInActivity(final String uid, final DataProviderListenerPlaceFreeInActivity listener){

        getActivityFromUid(new DataProvider.DataProviderListenerActivity(){

            @Override
            public void getActivity(DeboxActivity activity) {

                if(activity.getNbMaxOfParticipants()<=0){
                    listener.getIfFreePlace(true);
                } else {
                    if(activity.getNbMaxOfParticipants()>activity.getNbOfParticipants()){
                        listener.getIfFreePlace(true);
                    } else {
                        listener.getIfFreePlace(false);
                    }
                }
            }
        },uid);

    }


    private void getIfAlreadyRanked(final String uid, final DataProviderListenerAlreadyRanked listener){

        String userUid = user.getUid();
        DatabaseReference myRef = database.getReference("users/" + userUid + "/ranked");


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
                listener.getIfRanked(alreadyEnrolled);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void getIfActivityIsPast(final String uid, final DataProviderListenerIsPast listener){
        getActivityFromUid(new DataProvider.DataProviderListenerActivity(){

            @Override
            public void getActivity(DeboxActivity activity) {

                Calendar currentCalendar = Calendar.getInstance();
                if(activity.getTimeEnd().before(currentCalendar)){
                    listener.getIfActivityIsPast(true);
                } else {
                    listener.getIfActivityIsPast(false);

                }
            }
        },uid);

    }


    public void getAllCategories(final DataProviderListenerCategories listener) {

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

        DatabaseReference myRef = database.getReference("activities");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<DeboxActivity> list = new ArrayList<>();
                for(DataSnapshot child: dataSnapshot.getChildren()) {
                    list.add(getDeboxActivity(child.getKey(), (Map<String, Object>) child.getValue()));

                }

                listener.getActivities(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getSpecifiedActivities(final DataProviderListenerUserEvents listener, final List<String> intEventIds, final List<String> orgEventsIds) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activities");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<DeboxActivity> intList = new ArrayList<>();
                ArrayList<DeboxActivity> orgList = new ArrayList<>();
                for(DataSnapshot child: dataSnapshot.getChildren()) {
                    if (intEventIds.contains(child.getKey())) {
                        intList.add(getDeboxActivity(child.getKey(), (Map<String, Object>) child.getValue()));
                    }
                    if (orgEventsIds.contains(child.getKey())) {
                        orgList.add(getDeboxActivity(child.getKey(), (Map<String, Object>) child.getValue()));
                    }
                }
                listener.getUserActivities(intList, orgList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void rankUser(final String uid, final int rank){


        // Remove Activity Uid of User:enrolled


        String userUid = user.getUid();
        DatabaseReference myRef = database.getReference("users/" + userUid + "/enrolled");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> listEnrolled = (Map<String, Object>) dataSnapshot.getValue();

                if (listEnrolled != null) {

                    String idOfEntryToRemove = null;

                    for (Map.Entry<String, Object> enrolledEntry : listEnrolled.entrySet()) {

                        String activityID = (String) ((Map<String, Object>) enrolledEntry.getValue()).get("activity ID:");

                        if (activityID.equals(uid)) {
                            idOfEntryToRemove = enrolledEntry.getKey();

                        }
                    }

                    if(idOfEntryToRemove != null) {
                        mDatabase.child("users").child(user.getUid()).child("enrolled").child(idOfEntryToRemove).removeValue();
                    } else {
                        //TODO Something wrong happens ...
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Add Activity Uid in User:ranked

        HashMap<String, Object> rankedChild = new HashMap<>();
        rankedChild.put("activity ID:",uid);
        rankedChild.put("rank:",rank);

        // get unique key for enroll the activity
        String enrolledKey = mDatabase.child("users").child(user.getUid()).child("ranked").push().getKey();
        HashMap<String, Object> ranked = new HashMap<>();

        ranked.put("ranked/" + enrolledKey, rankedChild);

        // update the database
        mDatabase.child("users").child(user.getUid()).updateChildren(ranked);


        // Get organizer of activity
        getActivityFromUid(new DataProvider.DataProviderListenerActivity(){

            @Override
            public void getActivity(DeboxActivity activity) {

                final String idOrganiser = activity.getOrganizer();

                // icii


                // To be check if it's work like this...
                //FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference getUserProfile = database.getReference("users/" + idOrganiser);


                getUserProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> userMap = (Map<String, Object>) dataSnapshot.getValue();
                        User deboxOrganiser = getDeboxUser(idOrganiser, userMap);

                        int ratingSum = deboxOrganiser.getRatingSum();
                        int ratingNb = deboxOrganiser.getRatingNb();

                        if(ratingNb==-1){
                            ratingNb = 1;
                        } else {
                            ratingNb += 1;
                        }

                        ratingSum += rank;

                        mDatabase.child("users").child(idOrganiser).child("ratingNb").setValue(ratingNb);
                        mDatabase.child("users").child(idOrganiser).child("ratingSum").setValue(ratingSum);
                    }




                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        },uid);


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

        result.put("nbOfParticipants",da.getNbOfParticipants());
        result.put("nbMaxOfParticipants",da.getNbMaxOfParticipants());

        result.put("images",da.getImageList());

        childUpdates.put("activities/"+key, result);

        mDatabase.updateChildren(childUpdates);

        copyIdOfCreatedEvent(key);
        return key;
    }
    private void copyIdOfCreatedEvent(String activityId){

        String organisedEventsKey = mDatabase.child("users").child(user.getUid()).child("organised").push().getKey();

        HashMap<String, Object> organisedEventsChild = new HashMap<>();
        organisedEventsChild.put("activity ID:",activityId);

        HashMap<String, Object> organisedEvents = new HashMap<>();
        organisedEvents.put("organised/" + organisedEventsKey, organisedEventsChild);

        mDatabase.child("users").child(user.getUid()).updateChildren(organisedEvents);
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

        List<String> imagesList = (ArrayList<String>) activityMap.get("images");


        boolean check_nbParticipants = activityMap.containsKey("nbOfParticipants");
        int nbOfParticipants = -1;
        if(check_nbParticipants){
            //nbOfParticipants = (int) activityMap.get("nbOfParticipants");
            nbOfParticipants = Integer.valueOf(activityMap.get("nbOfParticipants").toString());
        }

        boolean check_nbMaxOfParticipants = activityMap.containsKey("nbMaxOfParticipants");
        int nbMaxOfParticipants = -1;
        if(check_nbMaxOfParticipants){
            //nbMaxOfParticipants = (int) activityMap.get("nbMaxOfParticipants");
            nbMaxOfParticipants = Integer.valueOf(activityMap.get("nbMaxOfParticipants").toString());
        }

        return new DeboxActivity(uid, organizer, title, description,timeStart, timeEnd, latitude, longitude, category, imagesList,nbOfParticipants,nbMaxOfParticipants);

    }

    public Void getActivityFromUid(final DataProviderListenerActivity listener, final String uid) {

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

        return null;
    }

    public void initUserInDB(){

        //user = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String, Object> enrolled = new HashMap<>();

        enrolled.put("user_email", user.getEmail());
        enrolled.put("default_user_name",user.getDisplayName());

        mDatabase.child("users").child(user.getUid()).updateChildren(enrolled);

    }

    private User getDeboxUser(String uid, Map<String, Object> userMap) {
        String email = (String) userMap.get("user_email");
        String username = (String) userMap.get("default_user_name");

        List<String> interestedEvents = new ArrayList<>();
        if (userMap.containsKey("enrolled")) {
            Map<String, Map<String, Object>> enrolled = (Map<String, Map<String, Object>>) userMap.get("enrolled");
            for (Map<String, Object> innerMap : enrolled.values()) {
                String activityID = (String) innerMap.get("activity ID:");
                interestedEvents.add(activityID);
            }
        }

        List<String> organizedEvents = new ArrayList<>();
        if (userMap.containsKey("organised")) {
            Map<String, Map<String, Object>> organized = (Map<String, Map<String, Object>>) userMap.get("organised");
            for (Map<String, Object> innerMap : organized.values()) {
                String activityID = (String) innerMap.get("activity ID:");
                organizedEvents.add(activityID);
            }
        }

        Integer ratingNb = -1;
        if (userMap.containsKey("ratingNb")) {
            //ratingNb = (int) userMap.get("ratingNb");
            ratingNb = Integer.valueOf(userMap.get("ratingNb").toString());

        }

        Integer ratingSum = 0;
        if (userMap.containsKey("ratingSum")) {
            //ratingSum = (int) userMap.get("ratingSum");
            ratingSum = Integer.valueOf(userMap.get("ratingSum").toString());
        }

        String photoLink = "";

        return new User(uid, username, email, organizedEvents, interestedEvents, ratingNb, ratingSum, photoLink);
    }

    public void userProfile(final DataProviderListenerUserInfo listener){
        // Don't take userReference like this, it's break all test...
        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userUid = user.getUid();

        // Don't take FirebaseDatabase like this, it's break all test...
        // FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + userUid);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
    public void userEnrolledInActivity(final DataProviderListenerEnrolled listener, final String uid) {

        String userUid = user.getUid();
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
     * Enroll the user to the activity dba. If the user doesn't have an enrolled table, the enrolled
     * table is automatically added to the user.
     *
     * @param dba
     */

    public void joinActivity(DeboxActivity dba){

        HashMap<String, Object> enrolledChild = new HashMap<>();
        enrolledChild.put("activity ID:",dba.getId());

        // get unique key for enroll the activity
        String enrolledKey = mDatabase.child("users").child(user.getUid()).child("enrolled").push().getKey();
        HashMap<String, Object> enrolled = new HashMap<>();

        enrolled.put("enrolled/" + enrolledKey, enrolledChild);


        // TODO remove
        incrementNbOfUserInActivity(dba);

        // update the database
        mDatabase.child("users").child(user.getUid()).updateChildren(enrolled);

    }

    /**
     * Remove the enrollment of the user in the activity dba.
     *
     * @param dba
     */
    public void leaveActivity(final DeboxActivity dba){

        String userUid = user.getUid();
        DatabaseReference myRef = database.getReference("users/" + userUid + "/enrolled");
        final String uid = dba.getId();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> listEnrolled = (Map<String, Object>) dataSnapshot.getValue();

                if (listEnrolled != null) {

                    String idOfEntryToRemove = null;

                    for (Map.Entry<String, Object> enrolledEntry : listEnrolled.entrySet()) {

                        String activityID = (String) ((Map<String, Object>) enrolledEntry.getValue()).get("activity ID:");

                        if (activityID.equals(uid)) {
                            idOfEntryToRemove = enrolledEntry.getKey();

                        }
                    }

                    if(idOfEntryToRemove != null) {
                        mDatabase.child("users").child(user.getUid()).child("enrolled").child(idOfEntryToRemove).removeValue();
                        decreasesNbOfUserInActivity(dba);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void incrementNbOfUserInActivity(DeboxActivity dba){

        final String uid = dba.getId();

        DatabaseReference myRef = database.getReference("activities/" + dba.getId());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> activityMap = (Map<String, Object>) dataSnapshot.getValue();

                int nbOfUser = getDeboxActivity(uid,activityMap).getNbOfParticipants();

                if(nbOfUser<0){
                    nbOfUser=0;
                }

                HashMap<String, Object> childToUpDate = new HashMap<>();
                childToUpDate.put("nbOfParticipants",nbOfUser+1);
                mDatabase.child("activities").child(uid).updateChildren(childToUpDate);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void decreasesNbOfUserInActivity(DeboxActivity dba){

        final String uid = dba.getId();

        DatabaseReference myRef = database.getReference("activities/" + dba.getId());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> activityMap = (Map<String, Object>) dataSnapshot.getValue();

                int nbOfUser = getDeboxActivity(uid,activityMap).getNbOfParticipants();

                nbOfUser -= 1;

                if(nbOfUser<0){
                    nbOfUser=0;
                }

                HashMap<String, Object> childToUpDate = new HashMap<>();
                childToUpDate.put("nbOfParticipants",nbOfUser);
                mDatabase.child("activities").child(uid).updateChildren(childToUpDate);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    //DB Callbacks interfaces


    public interface DataProviderListenerPlaceFreeInActivity{
        void getIfFreePlace(boolean result);
    }

    private interface DataProviderListenerAlreadyRanked{
        void getIfRanked(boolean result);
    }

    private interface DataProviderListenerIsPast{
        void getIfActivityIsPast(boolean result);
    }

    public interface DataProviderListenerEnrolled {
        void getIfEnrolled(boolean result);
    }

    public interface DataProviderListenerUserState {
        void getUserState(UserStatus status);
    }

    public interface DataProviderListenerActivity {
        void getActivity(DeboxActivity activity);
    }

    public interface DataProviderListenerActivities {
        void getActivities(List<DeboxActivity> activitiesList);
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
        void getUserActivities(List<DeboxActivity> intList, List<DeboxActivity> orgList);
    }
    
}