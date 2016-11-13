package ch.epfl.sweng.project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olga on 07.11.16.
 * Displays the profile of the currently logged in user
 */

public class UserProfile extends AppCompatActivity {

    TextView emailTextView;
    User current_user;
    List<DeboxActivity> interested = new ArrayList<>();
    List<DeboxActivity> participated = new ArrayList<>();
    List<DeboxActivity> organized = new ArrayList<>();
    List<String> interestedIds = new ArrayList<>();

    private DataProvider dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dp = new DataProvider();
        dp.userProfile(new DataProvider.DataProviderListenerUserInfo(){

            @Override
            public void getUserInfo(User user) {
                current_user = user.copy();
                Log.d("current_user email: ", current_user.getEmail());
                interestedIds = user.getInterestedEvents();
                emailTextView = (TextView) findViewById(R.id.userEmail);
                emailTextView.setText(user.getEmail());
            }

            @Override
            public void getUserActivities(List<DeboxActivity> activitiesList) {}
        });
        dp = new DataProvider();
        dp.getSpecifiedActivities(new DataProvider.DataProviderListenerUserInfo(){

            @Override
            public void getUserInfo(User user) {}

            @Override
            public void getUserActivities(List<DeboxActivity> activitiesList) {
                interested = activitiesList;
                List<String> participatedIds = new ArrayList<String>();
            }
        }, interestedIds);
    }
}
