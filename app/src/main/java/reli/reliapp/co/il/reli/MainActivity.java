package reli.reliapp.co.il.reli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
//import android.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import reli.reliapp.co.il.reli.createReli.CreateReliActivity;
import reli.reliapp.co.il.reli.createReli.ReliApp;
import reli.reliapp.co.il.reli.custom.CustomActivity;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.utils.Const;
import reli.reliapp.co.il.reli.utils.ErrorDialogFragment;
import reli.reliapp.co.il.reli.utils.Utils;

/**
 * The Class UserList is the Activity class. It shows a list of all users of
 * this app. It also shows the Offline/Online status of users.
 */
public class MainActivity extends CustomActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Serializable
{

    /*
    * Define a request code to send to Google Play services This code is returned in
    * Activity.onActivityResult
    */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

     /*
     * Constants for location update parameters
     */

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;

    /*
     * Constants for handling location results
     */
    private static final float METERS_PER_FEET = 0.3048f;
    private static final int METERS_PER_KILOMETER = 1000;

    // Maximum results returned from a Parse query
    private static final int MAX_POST_SEARCH_RESULTS = 20;
    // Maximum post search radius for map in kilometers
    private static final int MAX_POST_SEARCH_DISTANCE = 100;

    /* ========================================================================== */

    // Location parameters

    private Location lastLocation;
    private Location currentLocation;

    // A request to connect to Location Services
    private LocationRequest locationRequest;

    // Stores the current instantiation of the location client in this object
    private GoogleApiClient locationClient;

    /* ========================================================================== */

    private ArrayList<ParseUser> chatsList;
    public static ReliUser user;

    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Toast.makeText(getApplicationContext(), "In OnCreate", Toast.LENGTH_SHORT).show();

        // *** Location starts

        // Create a new global location parameters object
        locationRequest = LocationRequest.create();

        // Set the update interval
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Create a new location client, using the enclosing class to handle callbacks.
        locationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // *** Location ends



        // TODO - what does this function do? Why false and not true?
        getActionBar().setDisplayHomeAsUpEnabled(false);

        user = ReliUser.getCurrentReliUser();
        updateUserStatus(true);
    }

    /* ========================================================================== */

    /*
     * Called when the Activity is no longer visible at all. Stop updates and disconnect.
     */
    @Override
    public void onStop() {

        Toast.makeText(getApplicationContext(), "In onStop", Toast.LENGTH_SHORT).show();

        // If the client is connected
        if (locationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        locationClient.disconnect();

        super.onStop();
    }

    /* ========================================================================== */

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {

        Toast.makeText(getApplicationContext(), "In OnStart", Toast.LENGTH_SHORT).show();

        super.onStart();

        // Connect to the location services client
        locationClient.connect();
    }

    /* ========================================================================== */

    @Override
    protected void onDestroy()
    {
        Toast.makeText(getApplicationContext(), "In OnDestroy", Toast.LENGTH_SHORT).show();

        super.onDestroy();
        updateUserStatus(false);
    }

    /* ========================================================================== */

    @Override
    protected void onResume()
    {

        Toast.makeText(getApplicationContext(), "In OnResume", Toast.LENGTH_SHORT).show();

        super.onResume();
    }

    /* ========================================================================== */

    // TODO - move to ReliUser
    private void updateUserStatus(boolean online)
    {
        user.put("online", online);
        user.saveEventually();
    }

   /* ========================================================================== */

    // *** Location Functions

    /*
     * Called by Location Services when the request to connect the client finishes successfully. At
     * this point, you can request the current location or start periodic updates
     */
    public void onConnected(Bundle bundle) {

        Toast.makeText(getApplicationContext(), "In OnConnected", Toast.LENGTH_SHORT).show();

        if (ReliApp.APPDEBUG) {
            Log.d("Connected to loc_s", ReliApp.APPTAG);
        }
        currentLocation = getLocation();
        startPeriodicUpdates();

        updateParseLocation();
    }

    /* ========================================================================== */

    /*
     * Called by Location Services if the connection to the location client drops because of an error.
     */
    public void onDisconnected() {

        Toast.makeText(getApplicationContext(), "In OnDisconnected", Toast.LENGTH_SHORT).show();

        if (ReliApp.APPDEBUG) {
            Log.d("Disconnected from loc_s", ReliApp.APPTAG);
        }
    }

    /* ========================================================================== */

    @Override
    public void onConnectionSuspended(int i) {

        Toast.makeText(getApplicationContext(), "In OnConnectionSuspended", Toast.LENGTH_SHORT).show();

        Log.i(ReliApp.APPTAG, "GoogleApiClient connection has been suspend");
    }

    /* ========================================================================== */

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(getApplicationContext(), "In OnConnectionFailed", Toast.LENGTH_SHORT).show();

        // Google Play services can resolve some errors it detects. If the error has a resolution, try
        // sending an Intent to start a Google Play services activity that can resolve error.
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {

                if (ReliApp.APPDEBUG) {
                    // Thrown if Google Play services canceled the original PendingIntent
                    Log.d(ReliApp.APPTAG, "An error occurred when connecting to location services.", e);
                }
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /* ========================================================================== */

    /*
     * Helper method to get the Parse GEO point representation of a location
     */
    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    /* ========================================================================== */

    /*
     * Report location updates to the UI.
     */
    public void onLocationChanged(Location location) {

        Toast.makeText(getApplicationContext(), "In OnLocationChanged", Toast.LENGTH_SHORT).show();

        currentLocation = location;
        if (lastLocation != null
                && geoPointFromLocation(location)
                .distanceInKilometersTo(geoPointFromLocation(lastLocation)) < 0.01) {
            // If the location hasn't changed by more than 10 meters, ignore it.
            return;
        }
        lastLocation = location;

        updateParseLocation();

//        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//        if (!hasSetUpInitialLocation) {
//            // Zoom to the current location.
//            updateZoom(myLatLng);
//            hasSetUpInitialLocation = true;
//        }
//        // Update map radius indicator
//        updateCircle(myLatLng);
//        doMapQuery();
//        doListQuery();
    }

    /* ========================================================================== */

    private void updateParseLocation() {
        if (getLocation() != null) {
            user.setLocation(geoPointFromLocation(getLocation()));
            user.saveEventually();
        }
    }

    /* ========================================================================== */

    /*
     * Connect the location client when the activity is about to become visible.
     * In response to a request to start updates, send a request to Location Services
     */
    private void startPeriodicUpdates() {

        Toast.makeText(getApplicationContext(), "In startPeriodUpdates", Toast.LENGTH_SHORT).show();

        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, this);
    }

    /* ========================================================================== */

    /*
     * Disconnect the location client when the activity is no longer visible
     * In response to a request to stop updates, send a request to Location Services
     */
    private void stopPeriodicUpdates() {

        Toast.makeText(getApplicationContext(), "In stopPeriodUpdates", Toast.LENGTH_SHORT).show();

        locationClient.disconnect();
    }

    /* ========================================================================== */

    /*
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            if (ReliApp.APPDEBUG) {
                // In debug mode, log the status
                Log.d(ReliApp.APPTAG, "Google play services available");
            }
            // Continue
            return true;

            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), ReliApp.APPTAG);
            }
            return false;
        }
    }

    /* ========================================================================== */

    /*
     * Get the current location
     */
    private Location getLocation() {
        // If Google Play Services is available
        if (servicesConnected()) {
            // Get the current location
            return LocationServices.FusedLocationApi.getLastLocation(locationClient);
        } else {
            return null;
        }
    }

    /* ========================================================================== */

    // TODO - move to Utils
    /*
     * Show a dialog returned by Google Play services for the connection error code
     */
    private void showErrorDialog(int errorCode) {
        // Get the error dialog from Google Play services
        Dialog errorDialog =
                GooglePlayServicesUtil.getErrorDialog(errorCode, this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), ReliApp.APPTAG);
        }
    }

 }
