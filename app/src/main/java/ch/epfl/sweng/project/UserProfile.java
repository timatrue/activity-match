package ch.epfl.sweng.project;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.fragments.UserImageFragment;
import ch.epfl.sweng.project.uiobjects.CommentsView;
import ch.epfl.sweng.project.uiobjects.UserProfileExpandableListAdapter;


/*
 * Displays the profile of the currently logged in user
 */

public class UserProfile extends AppCompatActivity {

    final static public String USER_PROFILE_TEST_KEY = "ch.epfl.sweng.project.UserProfile.USER_PROFILE_TEST_KEY";
    final static public String USER_PROFILE_NO_TEST = "ch.epfl.sweng.project.UserProfile.USER_PROFILE_NO_TEST";
    final static public String USER_PROFILE_TEST = "ch.epfl.sweng.project.UserProfile.USER_PROFILE_TEST";

    private String test;


    RatingBar userRank;

    protected UserProfileExpandableListAdapter eventsExpListAdapter;

    protected DataProvider mDataProvider;
    protected ImageProvider mImageProvider;

    TextView nameTextView;
    User current_user;

    List<String> interestedIds = new ArrayList<>();
    List<String> organizedIds = new ArrayList<>();
    List<String> rankedIds = new ArrayList<>();

    ArrayList<String> intTitles = new ArrayList<>();
    ArrayList<String> orgTitles = new ArrayList<>();
    ArrayList<String> pastOrgTitles = new ArrayList<>();
    ArrayList<String> partTitles = new ArrayList<>();
    ArrayList<String> toRankpastTitles = new ArrayList<>();


    ArrayList<DeboxActivity> intEvents = new ArrayList<>();
    ArrayList<DeboxActivity> orgEvents = new ArrayList<>();
    ArrayList<DeboxActivity> pastOrgEvents = new ArrayList<>();
    ArrayList<DeboxActivity> partEvents = new ArrayList<>();
    ArrayList<DeboxActivity> toRankPartEvents = new ArrayList<>();
    ArrayList<Map<String, String>> comments = new ArrayList<>();

    public String interestedEvents;
    public String participatedEvents;
    public String toRankEvents;
    public String organizedEvents;
    public String pastOrganizedEvents;
    public String commentsField;

    TextView headerComment;


    List<String> groupList;
    Map<String, List<DeboxActivity>> activityCollection;
    ExpandableListView expListView;
    Context mContext;

    ImageView userImage;
    FragmentManager fm;
    UserImageFragment imageFragment;

    public LinearLayout commentsLayout;


    @Override
    protected void onResume(){
        super.onResume();


        activityCollection = new LinkedHashMap<>();

        intEvents = new ArrayList<>();
        orgEvents = new ArrayList<>();
        pastOrgEvents = new ArrayList<>();
        partEvents = new ArrayList<>();
        toRankPartEvents = new ArrayList<>();
        comments = new ArrayList<>();



        if (test != null) {
            if (test.equals(USER_PROFILE_NO_TEST)) {
                createCollection();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mContext = getApplicationContext();

        interestedEvents = getResources().getString(R.string.interested_events);
        participatedEvents = getResources().getString(R.string.participated_events);
        organizedEvents = getResources().getString(R.string.organised_events);
        pastOrganizedEvents = getResources().getString(R.string.past_organised_events);
        toRankEvents = getResources().getString(R.string.to_rank_events);
        commentsField = getResources().getString(R.string.comment_field);


        createGroupList();
        commentsLayout = (LinearLayout) findViewById(R.id.commentsLayout);
        headerComment = (TextView) findViewById(R.id.headerComment);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            test = bundle.getString(USER_PROFILE_TEST_KEY);
            if (test != null) {
                if (test.equals(USER_PROFILE_NO_TEST)) {
                    setDataProvider(new DataProvider());
                    setImageProvider(new ImageProvider());
                }
            }
        }


        fm = getFragmentManager();
        userImage = (ImageView) findViewById(R.id.userImage);
        userImage.setOnClickListener(imageClickListener);

        setupUserToolBar();

    }

    View.OnClickListener imageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imageFragment = new UserImageFragment();
            imageFragment.setUser(current_user);
            imageFragment.setDataProvider(mDataProvider);
            imageFragment.setImageProvider(mImageProvider);
            if(userImage.getDrawable() instanceof GlideBitmapDrawable) {
                imageFragment.setImage(((GlideBitmapDrawable) userImage.getDrawable()).getBitmap());
            }
            else if (userImage.getDrawable() instanceof  BitmapDrawable){
                imageFragment.setImage(((BitmapDrawable) userImage.getDrawable()).getBitmap());
            }
            imageFragment.show(fm, "Validating your event");
        }
    };


    public void setDataProvider(DataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    public void setImageProvider(ImageProvider imageProvider) {
        mImageProvider = imageProvider;
    }


    public void updateUser(User user, boolean newImage) {
        current_user = user;
        if(newImage) {
            displayUserImage();

        }
    }

    public void displayUserImage() {
        mImageProvider.downloadUserImage(this, current_user.getId(), current_user.getPhotoLink(), userImage, null);
    }

    protected void displayUserRanking() {
        userRank = (RatingBar) findViewById(R.id.userRank);
        if(userRank == null) {
            return;
        }

        double rank = current_user.getRating();
        if(rank != -1) {
            userRank.setProgress((int) (rank/5 * userRank.getMax()));
        }
        else {
            ((RelativeLayout) userRank.getParent()).removeAllViews();
        }
        userRank.setVisibility(View.VISIBLE);
    }

    public void createGroupList() {
        groupList = new ArrayList<>();
        groupList.add(pastOrganizedEvents);
        groupList.add(organizedEvents);
        groupList.add(participatedEvents);
        groupList.add(toRankEvents);
        groupList.add(interestedEvents);

    }

    public void createCollection() {

        mDataProvider.userProfile(new DataProvider.DataProviderListenerUserInfo(){
            @Override
            public void getUserInfo(User user) {
                current_user = user.copy();
                displayUserImage();
                displayUserRanking();
                interestedIds = new ArrayList<>(user.getInterestedEventIds());
                organizedIds = new ArrayList<>(user.getOrganizedEventIds());
                rankedIds = new ArrayList<>(user.getRankedEventIds());
                comments = new ArrayList<>(user.getCommentField());

                addUsersComments(comments);
                
                mDataProvider.getSpecifiedActivities(new DataProvider.DataProviderListenerUserEvents (){

                    @Override
                    public void getUserActivities(List<DeboxActivity> intList, List<DeboxActivity> orgList, List<DeboxActivity> rankedList) {

                        for (DeboxActivity event : intList) {
                            if (event.getTimeEnd().after(Calendar.getInstance())) {
                                intTitles.add(event.getTitle());
                                intEvents.add(event);
                            } else {

                                toRankpastTitles.add(event.getTitle());
                                toRankPartEvents.add(event);

                            }
                        }

                        activityCollection.put(interestedEvents, intEvents);
                        activityCollection.put(toRankEvents, toRankPartEvents);

                        for (DeboxActivity event : rankedList){
                            partTitles.add(event.getTitle());
                            partEvents.add(event);

                        }

                        activityCollection.put(participatedEvents, partEvents);

                        for (DeboxActivity event : orgList) {
                            if (event.getTimeEnd().after(Calendar.getInstance())) {
                                orgTitles.add(event.getTitle());
                                orgEvents.add(event);
                            } else {
                                pastOrgTitles.add(event.getTitle());
                                pastOrgEvents.add(event);
                            }


                        }
                        activityCollection.put(organizedEvents, orgEvents);
                        activityCollection.put(pastOrganizedEvents, pastOrgEvents);

                    }
                }, interestedIds, organizedIds, rankedIds);
                nameTextView = (TextView) findViewById(R.id.userName);
                String userName =  user.getUsername();
                if(userName == null) {
                    userName = user.getEmail();
                }
                if(userName != null) {
                    nameTextView.setText(user.getUsername());
                }

                setExpListView();

                if(expListView != null) {
                    expListView.setVisibility(View.VISIBLE);
                }

                (findViewById(R.id.loadingProgressBar)).setVisibility(View.GONE);

            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String test = bundle.getString(USER_PROFILE_TEST_KEY);
            if (test != null) {
                if (test.equals(USER_PROFILE_NO_TEST)) {
                    activityCollection = new LinkedHashMap<>();
                }
            }
        }

    }


    private void setupUserToolBar(){
        Toolbar mUserToolBar = (Toolbar) findViewById(R.id.user_toolbar);
        mUserToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setAdapter() {
        eventsExpListAdapter = new UserProfileExpandableListAdapter(this, activityCollection, groupList, organizedEvents, eventsModifyDeleteListener);
        expListView.setAdapter(eventsExpListAdapter);
    }

    public void setExpListView() {

        expListView = (ExpandableListView) findViewById(R.id.userProfileActivityList);
        setAdapter();
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent,
                                        View v, int groupPosition, int childPosition, long id) {
                final DeboxActivity selected = (DeboxActivity) eventsExpListAdapter.getChild(groupPosition, childPosition);
                if(selected != null) {
                    Toast.makeText(getBaseContext(), selected.getTitle(), Toast.LENGTH_SHORT)
                            .show();

                    String eventId = selected.getId();
                    launchDisplayActivity(eventId);
                }
                return true;
            }
        });
    }

    UserProfileExpandableListAdapter.modifyDeleteListener eventsModifyDeleteListener = new UserProfileExpandableListAdapter.modifyDeleteListener() {

        @Override
        public void onItemModified(int groupPosition, int childPosition) {
            final DeboxActivity selected = (DeboxActivity) eventsExpListAdapter.getChild(groupPosition, childPosition);
            Toast.makeText(getBaseContext(), selected.getTitle(), Toast.LENGTH_SHORT)
                    .show();

            String eventId = selected.getId();
            launchModifyActivity(eventId);
        }

        @Override
        public void onItemDeleted(int groupPosition, int childPosition) {
            final DeboxActivity selected = (DeboxActivity) eventsExpListAdapter.getChild(groupPosition, childPosition);
            Toast.makeText(getBaseContext(), "Event deleted", Toast.LENGTH_SHORT)
                    .show();

            if (selected != null) {
                mDataProvider.deleteActivity(selected);
                activityCollection.get(organizedEvents).remove(selected);

                ((UserProfileExpandableListAdapter) expListView.getExpandableListAdapter()).notifyDataSetChanged();
                //createCollection();
                //setExpListView();
            }
        }

        @Override
        public void onItemClicked(int groupPosition, int childPosition) {

            final DeboxActivity selected = (DeboxActivity) eventsExpListAdapter.getChild(groupPosition, childPosition);
            Toast.makeText(getBaseContext(), selected.getTitle(), Toast.LENGTH_SHORT)
                    .show();

            String eventId = selected.getId();
            launchDisplayActivity(eventId);
        }

    };

    private void launchDisplayActivity(String eventId) {
        Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
        intent.putExtra(DisplayActivity.DISPLAY_ACTIVITY_TEST_KEY, DisplayActivity.DISPLAY_ACTIVITY_NO_TEST);
        intent.putExtra(DisplayActivity.DISPLAY_EVENT_ID, eventId);
        startActivity(intent);
    }

    private void launchModifyActivity(String eventId) {
        Intent intent = new Intent(getApplicationContext(), ModifyActivity.class);
        intent.putExtra(ModifyActivity.CREATE_ACTIVITY_TEST_KEY, ModifyActivity.CREATE_ACTIVITY_NO_TEST);
        intent.putExtra(ModifyActivity.MODIFY_ACTIVITY_EVENT_ID, eventId);
        startActivity(intent);
    }

    public void addUsersComments( List<Map <String,String>> userComments) {
        cleanLinearLayout(commentsLayout);

        if(userComments.size() > 0 ) headerComment.setVisibility(View.VISIBLE);


        for(Map<String,String> elem : userComments){
            if (!String.valueOf(elem.get("comment")).contentEquals("")) {
                CommentsView comment = new CommentsView(getApplicationContext(), elem);
                comment.setOnClickListener(eventIdCommentsListener);
                commentsLayout.addView(comment);
            }
        }

    }

    View.OnClickListener eventIdCommentsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof CommentsView) {
                String eventId = ((CommentsView) v).getEventId();
                Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                intent.putExtra(DisplayActivity.DISPLAY_ACTIVITY_TEST_KEY, DisplayActivity.DISPLAY_ACTIVITY_NO_TEST);
                intent.putExtra(DisplayActivity.DISPLAY_EVENT_ID, eventId);
                startActivity(intent);
            }
        }
    };

    public void cleanLinearLayout(LinearLayout linearLayout){
        if((linearLayout).getChildCount() > 0)
            (linearLayout).removeAllViews();
    }

}
