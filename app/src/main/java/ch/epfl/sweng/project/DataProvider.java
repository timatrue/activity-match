package ch.epfl.sweng.project;



import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
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



    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private Boolean localTestMode;

    public DataProvider() {

        //deboxActivityList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    // use for moc test
    public DataProvider(DatabaseReference mockDatabaseReference, FirebaseDatabase mockFireDataBase, FirebaseUser mockUser) {

        //deboxActivityList = new ArrayList<>();
        mDatabase = mockDatabaseReference;
        database = mockFireDataBase;
        user = mockUser;
        localTestMode = true;
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
        ACTIVITY_PAST,
        ORGANIZER,;

    }

    public void getCurrentUserStatusSimplified(final DeboxActivity currentActivity, final DataProviderListenerUserState listener) {

        userProfile( new DataProviderListenerUserInfo() {
            @Override
            public void getUserInfo(User currentUser) {
                listener.getUserState(getUserStatusInActivity(currentActivity,currentUser));
            }
        });

    }



/*

                       +-----------+
                       |Organizer ?|
                       +--+------+-+
                          |      |
                       +--+      +-------+
                       |                 |
                       +            +----+-----+
                   :Organizer       |Enrolled ?|
                                    +--+----+--+
                               yes     |    |     no
                    +------------------+    +------------------+
                    |                                          |
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


    public UserStatus getUserStatusInActivity(final DeboxActivity currentActivity, final User currentUser){

        if(userIsTheOrganizer(currentActivity,currentUser)){
            return UserStatus.ORGANIZER;

        } else {

            if(userIsEnrolledInActivity(currentActivity,currentUser)) {

                if(activityIsPast(currentActivity)){

                    return UserStatus.MUST_BE_RANKED;

                } else {

                    return UserStatus.ENROLLED;
                }

            } else {

                if(activityIsPast(currentActivity)){

                    if(userHasRankedActivity(currentActivity,currentUser)){

                        return UserStatus.ALREADY_RANKED;

                    } else {

                        return UserStatus.ACTIVITY_PAST;
                    }


                } else {

                    if(placeLeftInActivity(currentActivity)){

                        return UserStatus.NOT_ENROLLED_NOT_FULL;

                    } else {

                        return UserStatus.NOT_ENROLLED_FULL;
                    }
                }
            }
        }

    }

    private boolean userIsTheOrganizer(DeboxActivity dba, User user){
        if(dba.getOrganizer().equals(user.getId())){
            return true;
        } else {
            return false;
        }
    }

    private boolean userIsEnrolledInActivity(DeboxActivity dba, User user){

        return user.getInterestedEventIds().contains(dba.getId());
    }

    private boolean activityIsPast(DeboxActivity dba){

        return dba.getTimeEnd().before(Calendar.getInstance());
    }

    private boolean userHasRankedActivity(DeboxActivity dba, User user){

        return user.getRankedEventIds().contains(dba.getId());
    }

    private boolean placeLeftInActivity(DeboxActivity dba){

        if(dba.getNbMaxOfParticipants() <=0){
            return true;
        } else {
            if(dba.getNbMaxOfParticipants() > dba.getNbOfParticipants()){
                return true;
            }
        }
        return false;
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

    // TODO Remove this method... Very bad idea to fetch all database and proceed it in application !!!!!

    public void getSpecifiedActivities(final DataProviderListenerUserEvents listener, final List<String> intEventIds, final List<String> orgEventsIds,
                                       final List<String> rankedEventsIds) {
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activities");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<DeboxActivity> intList = new ArrayList<>();
                ArrayList<DeboxActivity> orgList = new ArrayList<>();
                ArrayList<DeboxActivity> rankedList = new ArrayList<>();
                for(DataSnapshot child: dataSnapshot.getChildren()) {
                    if (intEventIds.contains(child.getKey())) {
                        intList.add(getDeboxActivity(child.getKey(), (Map<String, Object>) child.getValue()));
                    }
                    if (orgEventsIds.contains(child.getKey())) {
                        orgList.add(getDeboxActivity(child.getKey(), (Map<String, Object>) child.getValue()));
                    }
                    if (rankedEventsIds.contains(child.getKey())) {
                        rankedList.add(getDeboxActivity(child.getKey(), (Map<String, Object>) child.getValue()));
                    }
                }
                listener.getUserActivities(intList, orgList, rankedList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void rankUser(final String uid, final int rank, final String comment){


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


                // To be check if it's work like this...
                //FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference getUserProfile = database.getReference("users/" + idOrganiser);


                getUserProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> userMap = (Map<String, Object>) dataSnapshot.getValue();
                        User deboxOrganiser = getDeboxUser(idOrganiser, userMap);

                        //write the comments field to the organizer in DB
                        String comments = mDatabase.child("users").child(idOrganiser).child("comments").push().getKey();
                        HashMap<String, Object> commentsChild = new HashMap<>();
                        commentsChild.put("eventId", uid);
                        commentsChild.put("rating", rank);
                        commentsChild.put("comment",comment);
                        HashMap<String, Object> commentsMap = new HashMap<>();
                        commentsMap.put("comments/" + comments, commentsChild);
                        mDatabase.child("users").child(idOrganiser).updateChildren(commentsMap);


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

    public String pushActivity(final DeboxActivity da){

        String key = mDatabase.child("activities").push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        HashMap<String, Object> result = createActivityMap(da);
        childUpdates.put("activities/"+key, result);
        mDatabase.updateChildren(childUpdates);

        copyIdOfCreatedEvent(key);
        return key;
    }

    private HashMap<String,Object> createActivityMap(DeboxActivity da) {
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

        return result;
    }

    public String updateActivity(DeboxActivity da) {
        String key = da.getId();

        Map<String, Object> childUpdates = new HashMap<>();
        HashMap<String, Object> result = createActivityMap(da);
        childUpdates.put("activities/"+key, result);
        mDatabase.updateChildren(childUpdates);

        return key;
    }

    public void deleteActivity(DeboxActivity da) {
        String key = da.getId();
        mDatabase.child("activities").child(key).removeValue();
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

    public Void getActivityAndListenerOnChange(final DataProviderListenerActivity listener, final String uid) {

        DatabaseReference myRef = database.getReference("activities/" + uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> activityMap = (Map<String, Object>) dataSnapshot.getValue();
                listener.getActivity(getDeboxActivity(uid, activityMap));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
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

        List<String> rankedEvents = new ArrayList<>();
        if (userMap.containsKey("ranked")) {
            Map<String, Map<String, Object>> ranked = (Map<String, Map<String, Object>>) userMap.get("ranked");
            for (Map<String, Object> innerMap : ranked.values()) {
                String activityID = (String) innerMap.get("activity ID:");
                rankedEvents.add(activityID);
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

        String photoLink = null;
        if (userMap.containsKey("image")) {
            //ratingSum = (int) userMap.get("ratingSum");
            photoLink = (String) userMap.get("image");
        }

        return new User(uid, username, email, organizedEvents, interestedEvents,rankedEvents, ratingNb, ratingSum, photoLink);
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

    public void getUserProfileAndListenerOnChange(final DataProviderListenerUserInfo listener){

        final String userUid = user.getUid();

        // Don't take FirebaseDatabase like this, it's break all test...
        // FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + userUid);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> userMap = (Map<String, Object>) dataSnapshot.getValue();
                listener.getUserInfo(getDeboxUser(userUid, userMap));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public void publicUserProfile(final String userUid, final DataProviderListenerUserInfo listener){
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

    public void changeUserImage(final String imageName){
        // Don't take userReference like this, it's break all test...
        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userUid = user.getUid();

        // Don't take FirebaseDatabase like this, it's break all test...
        // FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + userUid);

        myRef.child("image").setValue(imageName);

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
    @Deprecated
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
     * Try to atomically join the dba Activity. The incrementation of the number of user in application is done
     * atomically. The result (success or fail) is send back by the listener DataProviderListenerResultOfJoinActivity.
     * With this solution if multiple user want join an activity simultaneously one activity with one place left,
     * only one user can join it. (other user receive a message).
     *
     * @param dba       : deboxActivity to join
     * @param listener  : listener to send result of join
     */
    public void atomicJoinActivity(DeboxActivity dba, final DataProviderListenerResultOfJoinActivity listener){

        // Fetch activity to check if occupancy has change (the goal is to avoid an atomic operation
        // not necessary)
        getActivityFromUid(new DataProvider.DataProviderListenerActivity(){
            @Override
            public void getActivity(final DeboxActivity activity) {

                // check if place left
                if(!(activity.getNbMaxOfParticipants()>0 && activity.getNbMaxOfParticipants() < activity.getNbOfParticipants())) {

                    DatabaseReference participantsRef = mDatabase.child("activities/"+activity.getId()+"/nbOfParticipants");

                    // doTransaction on nbOfParticipants (transaction ~ atomic operation)
                    // transaction is call again and again until the value don't change between the
                    // fetch of the value and the update
                    participantsRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Integer nbOfParticipants = mutableData.getValue(Integer.class);

                            // check if there is still a place or not
                            if(activity.getNbMaxOfParticipants()>0 && nbOfParticipants<activity.getNbMaxOfParticipants()){
                                mutableData.setValue(nbOfParticipants+1);
                                return Transaction.success(mutableData);
                            } else {
                                // if there is not place, abort transaction
                                return Transaction.abort();
                            }
                        }

                        // call when transaction is completed or aborted
                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            // b is boolean that contain if transaction has been commited or aborted
                            if(b) {

                                // if transaction has been committed we add entry on user enrolled list
                                HashMap<String, Object> enrolledChild = new HashMap<>();
                                enrolledChild.put("activity ID:",activity.getId());

                                // get unique key for enroll the activity
                                String enrolledKey = mDatabase.child("users").child(user.getUid()).child("enrolled").push().getKey();
                                HashMap<String, Object> enrolled = new HashMap<>();

                                enrolled.put("enrolled/" + enrolledKey, enrolledChild);

                                // update the database
                                mDatabase.child("users").child(user.getUid()).updateChildren(enrolled);
                            }
                            // return result to listener
                            listener.getResultJoinActivity(b);

                        }
                    });
                } else {
                    listener.getResultJoinActivity(false);
                }
            }
        },dba.getId());

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
                        atomicDecreasesParticipant(dba);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Deprecated
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

    /**
     * Decrement the number of participant by one (if number reach negative value set it to 0)
     *
     * @param dba       : deboxActivity to decrement
     */
    public void atomicDecreasesParticipant(DeboxActivity dba){

        DatabaseReference participantsRef = mDatabase.child("activities/"+dba.getId()+"/nbOfParticipants");

        // doTransaction on nbOfParticipants (transaction ~ atomic operation)
        // transaction is call again and again until the value don't change between the
        // fetch of the value and the update
        participantsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer nbOfParticipants = mutableData.getValue(Integer.class);
                if(nbOfParticipants>0){
                    mutableData.setValue(nbOfParticipants-1);
                }
                if(!localTestMode) {
                    return Transaction.success(mutableData);
                } else {
                    return null;
                }
            }

            // call when transaction is completed or aborted
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // b is boolean that contain if transaction has been committed or aborted
                if(b) {

                } else {
                    //TODO implement error : impossible to update nbOfParticipant Activity deleted ?
                }
            }
        });
    }



    //DB Callbacks interfaces

    public interface DataProviderListenerResultOfJoinActivity{
        void getResultJoinActivity(boolean result);
    }

    public interface DataProviderListenerPlaceFreeInActivity{
        void getIfFreePlace(boolean result);
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
        void getUserActivities(List<DeboxActivity> intList, List<DeboxActivity> orgList, List<DeboxActivity> rankedList);
    }

}
