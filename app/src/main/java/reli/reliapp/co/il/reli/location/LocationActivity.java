package reli.reliapp.co.il.reli.location;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import reli.reliapp.co.il.reli.R;

public class LocationActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {

    /* ========================================================================== */

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int UPDATE_INTERVAL = 10 * 1000;
    private final static int FASTEST_INTERVAL = 5 * 1000;
    private final static int DISPLACEMENT = 10; // 10 meters
    private static final String TAG = LocationActivity.class.getSimpleName();

    /* ========================================================================== */

    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    // UI elements
    private Button btnStartLocationUpdates;

    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        btnStartLocationUpdates = (Button) findViewById(R.id.btnLocationUpdates);

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }

        // Show location button click listener
        Button btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });

        // Toggling the periodic location updates
        btnStartLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePeriodicLocationUpdates();
            }
        });

    }

    /* ========================================================================== */

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /* ========================================================================== */

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /* ========================================================================== */

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /* ========================================================================== */

    @Override
    public void onProviderEnabled(String provider) {

    }

    /* ========================================================================== */

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /* ========================================================================== */

    @Override
    public void onProviderDisabled(String provider) {

    }

    /* ========================================================================== */

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /* ========================================================================== */

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {

        TextView lblLocation = (TextView) findViewById(R.id.lblLocation);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            lblLocation.setText(latitude + ", " + longitude);
        } else {
            lblLocation.setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    /* ========================================================================== */

    /**
     * Method to toggle periodic location updates
     * */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            btnStartLocationUpdates.setText("btn_stop_location_updates");
            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();
            Toast.makeText(getApplicationContext(), "Periodic location updates started!", Toast.LENGTH_SHORT).show();

        } else {
            // Changing the button text
            btnStartLocationUpdates.setText("btn_start_location_updates");
            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();
            Toast.makeText(getApplicationContext(), "Periodic location updates stopped!", Toast.LENGTH_SHORT).show();
        }
    }

    /* ========================================================================== */

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /* ========================================================================== */

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(DISPLACEMENT);
    }

    /* ========================================================================== */

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    /* ========================================================================== */

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // TODO - update the user instance
                        Toast.makeText(getApplicationContext(), "onLocationChanged - Location has been changed!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /* ========================================================================== */
    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }
                });
    }

    /* ========================================================================== */

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(getApplicationContext(), "onConnectionFailed", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /* ========================================================================== */

    @Override
    public void onConnected(Bundle connectionHint) {
        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /* ========================================================================== */

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    /* ========================================================================== */

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocation();
    }
}
