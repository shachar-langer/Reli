package reli.reliapp.co.il.reli.splashScreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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

    // Splash screen timer
    private static int SPLASH_TIME_OUT_MILLIS = 1000;

    private void initLoginScreen() {
        // This method will be executed once the timer is over
        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
        startActivity(intent);

        // close this activity
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initTagsMaps();

                ReliUser user = (ReliUser) (ParseUser.getCurrentUser());

                if (user == null) {
                    initLoginScreen();
                }
                else {
                    user.fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {

                            MainActivity.user = (ReliUser) parseObject;

                            initLoginScreen();
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
}
