package il.co.reli.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;


import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import il.co.reli.il.reli.R;
import il.co.reli.ReliApp;
import il.co.reli.custom.CustomActivity;
import il.co.reli.dataStructures.ReliTag;
import il.co.reli.dataStructures.ReliUser;
import il.co.reli.dataStructures.ReliUserType;
import il.co.reli.login.LoginActivity;
import il.co.reli.sidebar.AboutFragment;
import il.co.reli.sidebar.DefaultSettingsFragment;
import il.co.reli.sidebar.FaqFragment;
import il.co.reli.sidebar.GuidedTourActivity;
import il.co.reli.sidebar.TagSelectionFragment;
import il.co.reli.utils.Const;
import il.co.reli.utils.ErrorDialogFragment;
import il.co.reli.utils.Utils;

/**
 * The Class UserList is the Activity class. It shows a list of all users of
 * this app. It also shows the Offline/Online status of users.
 */
public class MainActivity extends CustomActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /*
    * Define a request code to send to Google Play services This code is returned in
    * Activity.onActivityResult
    */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Constants for location update parameters
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final int FAST_CEILING_IN_SECONDS = 1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;


    /* ========================================================================== */

    // Location parameters

    private Location lastLocation;
    private Location currentLocation;

    // A request to connect to Location Services
    private LocationRequest locationRequest;

    // Stores the current instantiation of the location client in this object
    private GoogleApiClient locationClient;

    /* ========================================================================== */

    public static ReliUser user;
    public static Fragment homeFragment;
    public static HashSet<String> discussionsImIn = new HashSet<>();
    public static HashMap<String, ReliTag> tagsIdToTag = new HashMap<>();
    public static HashMap<String, Bitmap> usersAvatars = new HashMap<>();

    // UI Stuff
    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ArrayList<NavItem> mNavItems;

    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_drawer);

        // Load the names of the possible Fragments in the drawer
        mNavItems = getNavDrawerItems();

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigation Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_action_navigation_menu, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name) { // nav drawer close - description for accessibility) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ImageView iv = (ImageView) drawerView.findViewById(R.id.avatar);
                Utils.setAvatar(iv, user.getObjectId());
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        // Set the "Hamburger"
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

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

        // Set the user name and image in the drawer
        if (user != null) {
            TextView tv = (TextView) findViewById(R.id.userName);
            tv.setText(user.getFullName());

            ImageView iv = (ImageView) findViewById(R.id.avatar);
            Utils.setAvatar(iv, user.getObjectId());
        }

        // Start
        homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContent, homeFragment)
                .addToBackStack(Const.HOME_FRAGMENT_TAG)
                .commit();
    }

    /* ========================================================================== */

    /*
     * Called when the Activity is no longer visible at all. Stop updates and disconnect.
     */
    @Override
    public void onStop() {
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
        super.onStart();

        // Connect to the location services client
        locationClient.connect();
    }

   /* ========================================================================== */

    // *** Location Functions

    /*
     * Called by Location Services when the request to connect the client finishes successfully. At
     * this point, you can request the current location or start periodic updates
     */
    public void onConnected(Bundle bundle) {
        if (ReliApp.APPDEBUG) {
            Log.d("Connected to loc_s", ReliApp.APPTAG);
        }
        currentLocation = getLocation();

        checkUsersLocationServices(currentLocation);

        startPeriodicUpdates();

        updateParseLocation();
    }

    /* ========================================================================== */

    /*
     *  Validate that user's location services are on. If they are off, but we
     *  can retrieve its last known location, print a warning. If they are off,
     *  and we can't retrive its last location, exit.
     */
    private void checkUsersLocationServices(Location location) {

        if (location != null) {
            if (!isLocationServicesEnables()) {
                Utils.showDialog(MainActivity.this,
                        getString(R.string.dialog_using_last_location_title),
                        getString(R.string.dialog_using_last_location_message),
                        getString(R.string.ok_exit),
                        null,
                        null,
                        null);
            }
        }
        else {
            handleExit(R.string.dialog_no_location_found_title,
                    R.string.dialog_no_location_found_message, false);
        }

}
    /*
     * Called by Location Services if the connection to the location client drops because of an error.
     */
    public void onDisconnected() {
        if (ReliApp.APPDEBUG) {
            Log.d("Disconnected from loc_s", ReliApp.APPTAG);
        }
    }

    /* ========================================================================== */

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(ReliApp.APPTAG, "GoogleApiClient connection has been suspend");
    }

    /* ========================================================================== */

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    public void onConnectionFailed(ConnectionResult connectionResult) {
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
    public ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    /* ========================================================================== */

    /*
     * Report location updates to the UI.
     */
    public void onLocationChanged(Location location) {

        //Toast.makeText(getApplicationContext(), "In OnLocationChanged", Toast.LENGTH_SHORT).show();

        currentLocation = location;
        if (lastLocation != null
                && geoPointFromLocation(location)
                .distanceInKilometersTo(geoPointFromLocation(lastLocation)) < 0.01) {
            // If the location hasn't changed by more than 10 meters, ignore it.
            return;
        }
        lastLocation = location;

        updateParseLocation();
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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, this);
    }

    /* ========================================================================== */

    /*
     * Disconnect the location client when the activity is no longer visible
     * In response to a request to stop updates, send a request to Location Services
     */
    private void stopPeriodicUpdates() {
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
    public Location getLocation() {

        // If Google Play Services is available
        if (servicesConnected()) {
            // Get the current location
            return LocationServices.FusedLocationApi.getLastLocation(locationClient);
        } else {
            return null;
        }
    }

    /* ========================================================================== */

    public boolean isLocationServicesEnables() {
        Context ctx = getApplicationContext();
        LocationManager lm = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        // In case at least one of the location services is not working, return an error.
        if(!gps_enabled && !network_enabled) {
            // notify user
            return false;
        }

        return true;
    }

    /* ========================================================================== */

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

    /* UI CODE */

    /* ========================================================================== */

    private Class findClass(String positionMeaning) {
        // Return the Grand Tour class
        if (positionMeaning.equals(getString(R.string.nav_drawer_tour)))
            return GuidedTourActivity.class;

        return null;
    }

    /* ========================================================================== */

    /**
     * Populate the drawer with the different possible fragments
     */
    private ArrayList<NavItem> getNavDrawerItems() {
        String[] navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        String[] navMenuSubtitles = getResources().getStringArray(R.array.nav_drawer_items_sub);
        ArrayList<NavItem> navDrawerItems = new ArrayList<NavItem>();

        for (int i = 0; i < navMenuTitles.length; i++) {
            navDrawerItems.add(new NavItem(navMenuTitles[i], navMenuSubtitles[i], R.drawable.arrow));
        }

        return navDrawerItems;
    }

    /* ========================================================================== */


    private Fragment getSelectedFragment(String positionMeaning) {
        // Return the Notification Settings fragment
        if (positionMeaning.equals(getString(R.string.nav_drawer_notification_settings)))
            return new TagSelectionFragment();

        // Return the Default Settings class
        if (positionMeaning.equals(getString(R.string.nav_drawer_default_settings)))
            return new DefaultSettingsFragment();

        // Return FAQ class
        if (positionMeaning.equals(getString(R.string.nav_drawer_faq)))
            return new FaqFragment();

        // Return the About class
        if (positionMeaning.equals(getString(R.string.nav_drawer_about)))
            return new AboutFragment();

        return null;
    }

/* ========================================================================== */

    @Override
    public void onBackPressed() {
        String currentFragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

        if (currentFragmentTag != null && currentFragmentTag.equals(Const.HOME_FRAGMENT_TAG)) {
            handleExit();
        } else {
            getHomeFragment();
        }
    }

    /* ========================================================================== */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /* ========================================================================== */

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

/* ========================================================================== */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* ========================================================================== */

    /*
    * Called when a particular item from the navigation drawer
    * is selected.
    * */
    private void selectItemFromDrawer(int position) {

        // Find out the task that should be done
        String positionMeaning = mNavItems.get(position).mTitle;
        Class c = findClass(positionMeaning);
        Fragment f = getSelectedFragment(positionMeaning);

        if (f != null) {
            getSupportActionBar().setTitle(mNavItems.get(position).mTitle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, f)
                    .addToBackStack(null)
                    .commit();
        } else if (c != null) {
            Intent intent = new Intent(this, c);
            startActivity(intent);
        } else if (positionMeaning.equals(getString(R.string.nav_drawer_sign_out))) {
            handleLogout();
        }
        else if (positionMeaning.equals(getString(R.string.nav_drawer_exit))) {
            handleExit();
        }
        else if (positionMeaning.equals(getString(R.string.nav_drawer_inbox))) {
            // Return to home screen
            getHomeFragment();
        }
        else if (positionMeaning.equals(getString(R.string.nav_drawer_facebook))) {
            // Open Reli's page on Facebook
            Intent intent;
            String profileId = "528014970706716";
            try {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + profileId));
            } catch (Exception e) {
                intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + profileId));
            }

            this.startActivity(intent);
        }

        // Update selected item
        mDrawerList.setItemChecked(position, true);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    /* ========================================================================== */

    private void handleLogout() {
        DialogInterface.OnClickListener listener1 = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Logout from Facebook session
                facebookLogout();
                // Logout from Parse user
                ParseUser.logOut();
                Utils.saveParseUserInSharedPreferences(MainActivity.this, null);
                MainActivity.user = null;

                Toast.makeText(getApplicationContext(), R.string.successful_sign_out, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        };

        Utils.showDialog(MainActivity.this,
                R.string.dialog_sign_out_title,
                R.string.dialog_sign_out_message,
                R.string.ok_sign_out,
                R.string.cancel,
                listener1,
                null);
    }

    /* ========================================================================== */

    private void handleExit() {
        handleExit(R.string.dialog_exit_title, R.string.dialog_exit_message, true);
    }

    /* ========================================================================== */

    private void handleExit(int dialogTitle, int dialogMessage, boolean shouldShowCancelBtn) {
        DialogInterface.OnClickListener listener1 = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.finish();
            }
        };

        Utils.showDialog(MainActivity.this,
                getString(dialogTitle),
                getString(dialogMessage),
                getString(R.string.ok_exit),
                (shouldShowCancelBtn) ? getString(R.string.cancel): null,
                listener1,
                null);
    }

    /* ========================================================================== */

    private void getHomeFragment() {
        String currentFragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

        // Check if we're already in home fragment
        if (currentFragmentTag != null && currentFragmentTag.equals(Const.HOME_FRAGMENT_TAG)) {
            return;
        } else {
            getSupportFragmentManager().popBackStackImmediate(Const.HOME_FRAGMENT_TAG, 0);
        }
    }

    /* ========================================================================== */

    public static void updateDiscussionsImIn(String discussionID) {
        // Update the static variable
        discussionsImIn.add(discussionID);

        // Update parse
        user.addDiscussionImIn(discussionID);

        // Update Installation (in order to get notifications)
        ParseInstallation.getCurrentInstallation().put(discussionID, true);

        // Save
        user.saveEventually();
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    /* ========================================================================== */

    public static void removeDiscussionFromMyRelis(String discussionID) {
        // Update the static variable
        if (discussionsImIn.contains(discussionID)) {
            discussionsImIn.remove(discussionID);
        }

        // Update parse
        user.removeFromDiscussionImIn(discussionID);

        // Update Installation (in order to stop notifications)
        ParseInstallation.getCurrentInstallation().put(discussionID, false);

        // Remove discussion from chat list
        HomeFragment.getMyRelisFragmentInstance().removeFromChatList(discussionID);

        // Save
        user.saveEventually();
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    /* ========================================================================== */

    private void facebookLogout() {
        // Logout from Facebook session
        if ((user != null) && (user.getUserType() == ReliUserType.FACEBOOK_USER)) {
            if (Profile.getCurrentProfile() != null) {
                // Sign out
                LoginManager.getInstance().logOut();
            }
        }
    }
 }