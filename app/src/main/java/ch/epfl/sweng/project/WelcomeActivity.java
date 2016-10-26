package ch.epfl.sweng.project;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.project.fragments.FilterFragment;
import ch.epfl.sweng.project.uiobjects.ActivityPreview;

import static ch.epfl.sweng.project.R.attr.title;


public class WelcomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button testButton;
    Button displayCategoriesButton;
    TextView testListener;
    LinearLayout activityPreviewsLayout;
    LinearLayout categoryLayout;

    // private DatabaseReference mDatabase;
    private DataProvider mDataProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addActivityButton = (FloatingActionButton) findViewById(R.id.addActivity);
        addActivityButton.setOnClickListener(newActivityListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        activityPreviewsLayout = (LinearLayout) findViewById(R.id.activityPreviewsLayout);

        testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(testClickListener);
        displayCategoriesButton = (Button) findViewById(R.id.displayCategories);
        displayCategoriesButton.setOnClickListener(categoriesListener);

        mDataProvider = new DataProvider();

    }
    protected void CategoryFragment(View v){
        FragmentManager fm = getFragmentManager();
        FilterFragment dialogFragment = new FilterFragment ();
        dialogFragment.show(fm, "Sample Fragment");
    }

    View.OnClickListener previewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof ActivityPreview) {
                String eventId = ((ActivityPreview) v).getEventId();
                Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                intent.putExtra(DisplayActivity.DISPLAY_EVENT_ID, eventId);
                startActivity(intent);
            }
        }
    };


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
            //writeNewPost();
            displaySpecifiedActivities();
        }
    };
    View.OnClickListener categoriesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayCategories();
        }
    };

    private void displayCategories(){
        mDataProvider.getAllCategories(new DataProvider.DataProviderListenerCategories(){
            @Override
            public void getCategories(List<DataProvider.CategoryName> categoriesList) {

                String category = categoriesList.get(0).getCategory();
                displayCategoriesButton.setText(category);
            }
        });
    }
    private void displaySpecifiedActivities() {

        mDataProvider.getSpecifiedCategory(new DataProvider.DataProviderListenerCategory() {

            @Override
            public void getCategory(List<DeboxActivity> activitiesList) {

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 20, 30, 0);

                for(DeboxActivity elem: activitiesList) {
                    ActivityPreview ap = new ActivityPreview(getApplicationContext(), elem);
                    activityPreviewsLayout.addView(ap, layoutParams);
                    ap.setOnClickListener(previewClickListener);
                }
                mDataProvider = new DataProvider();
            }
        },"Culture");
    }

    private void writeNewPost() {

        mDataProvider.getAllActivities(new DataProvider.DataProviderListenerActivities() {
            @Override
            public void getActivity(DeboxActivity activity) {

            }

            @Override
            public void getActivities(List<DeboxActivity> activitiesList) {

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 20, 30, 0);

                for(DeboxActivity elem: activitiesList) {
                    ActivityPreview ap = new ActivityPreview(getApplicationContext(), elem);
                    activityPreviewsLayout.addView(ap, layoutParams);
                    ap.setOnClickListener(previewClickListener);
                }

                //mDatabase = FirebaseDatabase.getInstance().getReference();
                mDataProvider = new DataProvider();
            }
        });
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

        if (id == R.id.nav_log_out) {

            //FirebaseAuth.getInstance().signOut();
            //GoogleApiClient mGoogleApiClient = Login.mGoogleApiClient;
            //Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.putExtra("LOGOUT_ORDER", "logout");
            startActivity(intent);

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
