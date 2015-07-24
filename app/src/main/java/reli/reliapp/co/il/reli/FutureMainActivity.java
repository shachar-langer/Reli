package reli.reliapp.co.il.reli;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import reli.reliapp.co.il.reli.custom.CustomActivity;
import reli.reliapp.co.il.reli.location.LocationActivity;
import reli.reliapp.co.il.reli.login.LoginActivity;
import reli.reliapp.co.il.reli.sidebar.AboutFragment;
import reli.reliapp.co.il.reli.sidebar.FaqFragment;

public class FutureMainActivity extends CustomActivity {

    /* ========================================================================== */

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] navMenuTitles;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    // TODO - make sure that we want to use this boolean
    private static boolean alreadyInBackStack = false;

    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_main);

        // Set the title
        mTitle = mDrawerTitle = getTitle();

        // Load the names of the possible Fragments in the drawer
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        ArrayList<NavDrawerItem> navDrawerItems = getNavDrawerItems();

        // Setting the nav drawer list adapter
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);

        // Enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Set Slide menu item click listener
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        mDrawerList.setAdapter(adapter);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /* ========================================================================== */

    /**
     * Populate the drawer with the different possible fragments
     */
    private ArrayList<NavDrawerItem> getNavDrawerItems() {
        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
        for (int i = 0; i < navMenuTitles.length; i++) {
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i]));
        }

        return navDrawerItems;
    }

    /* ========================================================================== */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.future_main, menu);
        return true;
    }

    /* ========================================================================== */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
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

    /**
     * Displaying fragment view for selected nav drawer list item
     * update the main content by replacing fragments
     * */
    private void displayView(int position) {
        // Update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);

        // Find out the task that should be done
        String positionMeaning = navMenuTitles[position];
        Class c = findClass(positionMeaning);
        Fragment f = getSelectedFragment(positionMeaning);

        // Handle tasks that are done with fragments
        if (f != null) {
            setTitle(positionMeaning);
            FragmentManager fragmentManager = getFragmentManager();
            if (alreadyInBackStack) {
                fragmentManager.beginTransaction().replace(R.id.frame_container, f).commit();
            }
            else {
                alreadyInBackStack = true;
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.frame_container, f).commit();
            }
        }

        // Handle task that are done with Activity
        else if (c != null) {
            Intent intent = new Intent(this, c);
            startActivity(intent);
        }
    }

    /* ========================================================================== */

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /* ========================================================================== */

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
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

    private Class findClass(String positionMeaning) {
        // Return the Grand Tour class
        if (positionMeaning.equals(getString(R.string.nav_drawer_tour)))
            return LocationActivity.class;

        return null;
    }

    /* ========================================================================== */

    private Fragment getSelectedFragment(String positionMeaning) {
        // TODO - change
        // Return to home screen
        if (positionMeaning.equals(getString(R.string.nav_drawer_inbox)))
            return new HomeFragment();

        // TODO - change
        // Return the Notification Settings fragment
        if (positionMeaning.equals(getString(R.string.nav_drawer_notification_settings)))
            return new FaqFragment();

        // TODO - change
        // Return the Default Settings class
        if (positionMeaning.equals(getString(R.string.nav_drawer_default_settings)))
            return new FaqFragment();

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
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /* ========================================================================== */

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
}