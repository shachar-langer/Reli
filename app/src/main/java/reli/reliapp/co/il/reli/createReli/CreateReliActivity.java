package reli.reliapp.co.il.reli.createReli;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseGeoPoint;

import java.util.Date;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.custom.CustomActivity;
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.utils.Const;

public class CreateReliActivity extends CustomActivity { //extends AppCompatActivity implements ActionBar.TabListener {

//    TabsPagerAdapter mTabsPagerAdapter;
//    ViewPager mViewPager;

    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reli);

        Button test_btn = (Button) findViewById(R.id.just_another_button);

        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateReliActivity.this, DiscussionActivity.class);
                String discussionName = "Subject";
                ParseGeoPoint location = new ParseGeoPoint();
                int radius = 17;
                Bitmap discussionLogo = null;
                Date creationDate = new Date();
                Date expirationDate = new Date();
                String ownerParseID = "Yossi";
//                ParseObject po = new ParseObject("Discussions");
//                po.put(Const.COL_DISCUSSION_NAME, discussionName);
                Discussion po = new Discussion(discussionName, location, radius, discussionLogo,
                        creationDate, expirationDate, ownerParseID);
                po.saveEventually();
//                po.saveEventually(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e)
//                    {
//                        Toast.makeText(getApplicationContext(), "Did it!", Toast.LENGTH_SHORT).show();
////                        message.setStatus((e == null) ? MessageStatus.STATUS_SENT : MessageStatus.STATUS_FAILED);
////                        chatAdapter.notifyDataSetChanged();
//                    }
//                });
                intent.putExtra(Const.BUDDY_NAME, "Subject");
                startActivity(intent);
            }
        });

//        // Set up the action bar
//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//
//        // Create the adapter that will return a fragment for each of the three
//        // primary sections of the activity.
//        mTabsPagerAdapter = new TabsPagerAdapter(getApplicationContext(), getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.pager);
//        mViewPager.setAdapter(mTabsPagerAdapter);
//
//        // When swiping between different sections, select the corresponding
//        // tab. We can also use ActionBar.Tab#select() to do this if we have
//        // a reference to the Tab.
//        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                actionBar.setSelectedNavigationItem(position);
//            }
//        });
//
//        // For each of the sections in the app, add a tab to the action bar.
//        for (int i = 0; i < mTabsPagerAdapter.getCount(); i++) {
//            // Create a tab with text corresponding to the page title defined by
//            // the adapter. Also specify this Activity object, which implements
//            // the TabListener interface, as the callback (listener) for when
//            // this tab is selected.
//            actionBar.addTab(
//                    actionBar.newTab()
//                            .setText(mTabsPagerAdapter.getPageTitle(i))
//                            .setTabListener(this));
//        }
    }

    /* ========================================================================== */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_reli, menu);
        return true;
    }

    /* ========================================================================== */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* ========================================================================== */

//    @Override
//    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//        // When the given tab is selected, switch to the corresponding page in
//        // the ViewPager.
//        mViewPager.setCurrentItem(tab.getPosition());
//        // TODO - change the style of the tab
//    }
//
//    /* ========================================================================== */
//
//    @Override
//    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//    }
//
//    /* ========================================================================== */
//
//    @Override
//    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//    }
}
