package reli.reliapp.co.il.reli;

import android.support.v4.app.Fragment;
//import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;


import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import reli.reliapp.co.il.reli.custom.CustomActivity;
import reli.reliapp.co.il.reli.location.LocationActivity;
import reli.reliapp.co.il.reli.sidebar.AboutFragment;
import reli.reliapp.co.il.reli.sidebar.FaqFragment;

public class NewTryActivity extends CustomActivity {

    /* ========================================================================== */

    private static String TAG = MainActivity.class.getSimpleName();

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems;


    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_try);

        // Enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Load the names of the possible Fragments in the drawer
        mNavItems = getNavDrawerItems();

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name) { // nav drawer close - description for accessibility) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
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
            setTitle(mNavItems.get(position).mTitle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, f)
                    .addToBackStack(null)
                    .commit();
        }
        else if (c != null) {
            Intent intent = new Intent(this, c);
            startActivity(intent);
        }

        // Update selected item
        mDrawerList.setItemChecked(position, true);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
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

    private Class findClass(String positionMeaning) {
        // Return the Grand Tour class
        if (positionMeaning.equals(getString(R.string.nav_drawer_tour)))
            return LocationActivity.class;

        return null;
    }

    /* ========================================================================== */

    private Fragment getSelectedFragment(String positionMeaning) {
//        TODO - change
//        Return to home screen
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

        // TODO - uncomment
//        Return the About class
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.future_main, menu);
        return true;
    }
}