package reli.reliapp.co.il.reli.splashScreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.ReliTag;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.login.LoginActivity;
import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.sidebar.GuidedTourActivity;
import reli.reliapp.co.il.reli.utils.Const;

public class SplashScreen extends Activity {

    /* ========================================================================== */

    // Splash screen timer
    private static int SPLASH_TIME_OUT_MILLIS = 3000;

    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Retrieve the list of Tags
                initTagsMaps();

                // Check if we should use the saved user
                final ReliUser user;
                SharedPreferences prefs = getSharedPreferences(Const.RELI_SHARED_PREF_FILE, Context.MODE_PRIVATE);
                boolean restoredShouldKeepSignedIn = prefs.getBoolean(Const.SHARED_PREF_KEEP_SIGNED_IN, false);
                Toast.makeText(getApplicationContext(), "In Splash - restoredShouldKeepSignedIn == " + restoredShouldKeepSignedIn, Toast.LENGTH_SHORT).show();
                if (restoredShouldKeepSignedIn) {
                    user = (ReliUser) (ParseUser.getCurrentUser());
                } else {
                    user = null;
                }

                Toast.makeText(getApplicationContext(), "In Splash - user == " + user, Toast.LENGTH_SHORT).show();
                MainActivity.user = user;

                if (user == null) {
                    initLoginScreen();
                }
                else {
                    user.fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            // TODO - add?
//                            MainActivity.user = (ReliUser) parseObject;
//                            initLoginScreen();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });
                }
            }


        }, SPLASH_TIME_OUT_MILLIS);
    }

    /* ========================================================================== */

    private void initTagsMaps() {

        ParseQuery<ReliTag> query = ReliTag.getReliTagQuery();
        query.findInBackground(new FindCallback<ReliTag>() {
            @Override
            public void done(List<ReliTag> reliTags, ParseException e) {

                String currentID, currentTagName;
                for (ReliTag tag : reliTags) {
                    currentID = tag.getObjectId();
                    currentTagName = tag.getString(Const.COL_TAG_NAME);
                    MainActivity.tagsIdToTag.put(currentID, new ReliTag(currentTagName, currentID));
                }
            }
        });
    }

    /* ========================================================================== */

    private void initLoginScreen() {
        // This method will be executed once the timer is over
        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
        startActivity(intent);

        // close this activity
        finish();
    }

}
