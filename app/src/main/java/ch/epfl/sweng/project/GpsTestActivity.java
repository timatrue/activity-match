package ch.epfl.sweng.project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.*;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;


public class GpsTestActivity extends AppCompatActivity implements LocationListener {


    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private LocationManager locationManager;
    private boolean positionPermissionGranted = false;
    private TextView gpsStatusText = null;
    private TextView gpsLocationText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_test);

        gpsStatusText = (TextView) findViewById(R.id.gpsStatusText);
        gpsLocationText = (TextView) findViewById(R.id.locationText);

        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        positionPermissionGranted = askForLocationPerm();

        if(positionPermissionGranted) {
            gpsStatusText.setText("Status : position permission granted");
            startRequestLocation();

        } else {
            gpsStatusText.setText("Status : position permission still refused");
        }

        //Todo: Must check if the gps is enable or not

    }

    private void startRequestLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            gpsLocationText.setText("Location : GPS is enable ?");

        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        }

    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        gpsLocationText.setText("Location : (lat, long) "+latitude+", "+longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    //Todo: ADD support for version with android version < 23.

    //Todo: GPS : Display message to explain why the app needs permissions.
    private boolean askForLocationPerm() {
        // look if the permission has already been give
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        // if the permission hasn't been given ask for it and return
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);

            return false;
        }
        // permission has already been given
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    positionPermissionGranted = true;
                    gpsStatusText.setText("Status : position permission granted");
                    startRequestLocation();

                } else {
                    // Permission Denied
                    positionPermissionGranted = false;
                    gpsStatusText.setText("Status : position permission definitely refused");

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
