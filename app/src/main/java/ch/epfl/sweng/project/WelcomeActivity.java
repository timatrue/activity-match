package ch.epfl.sweng.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.sweng.project.uiobjects.ActivityPreview;

import static ch.epfl.sweng.project.R.attr.title;

public class WelcomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button testButton;
    LinearLayout activityPreviewsLayout;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(newActivityListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(testClickListener);

        activityPreviewsLayout = (LinearLayout) findViewById(R.id.activityPreviewsLayout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 20, 30, 0);

        ActivityPreview ap = new ActivityPreview(this, "Running", "Running with friend in Mouline");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Football", "Football with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Volleyball", "Volleyball with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Climbing", "Climbing with friend in Renens");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Running", "Running with friend in Mouline");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Football", "Football with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Football", "Football with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Volleyball", "Volleyball with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Climbing", "Climbing with friend in Renens");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Running", "Running with friend in Mouline");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Football", "Football with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Football", "Football with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Volleyball", "Volleyball with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Climbing", "Climbing with friend in Renens");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Running", "Running with friend in Mouline");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Football", "Football with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Football", "Football with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Volleyball", "Volleyball with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Climbing", "Climbing with friend in Renens");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Running", "Running with friend in Mouline");
        activityPreviewsLayout.addView(ap, layoutParams);
        ap = new ActivityPreview(this, "Football", "Football with friend in sport Center");
        activityPreviewsLayout.addView(ap, layoutParams);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    View.OnClickListener newActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener testClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            writeNewPost();
        }
    };



    private void writeNewPost() {
        String key = mDatabase.child("messages").push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();

        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", "result");
        childUpdates.put("/messages", result);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_log_in) {
            // Handle the camera action
        } else if (id == R.id.nav_sign_up) {

        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_contact) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",getResources().getString(R.string.company_mail), null));
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.contact_message)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
