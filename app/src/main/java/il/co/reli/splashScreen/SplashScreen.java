package il.co.reli.splashScreen;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Method;
import java.util.List;

import il.co.reli.R;
import il.co.reli.dataStructures.ReliTag;
import il.co.reli.dataStructures.ReliUser;
import il.co.reli.login.LoginActivity;
import il.co.reli.main.MainActivity;
import il.co.reli.utils.Const;
import il.co.reli.utils.Utils;

public class SplashScreen extends Activity {

    /* ========================================================================== */

    // Splash screen timer
    private static int SPLASH_TIME_OUT_MILLIS = 3000;

    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check if we have internet access. If not internet access found, exit
         if (!isOnline()) {
             DialogInterface.OnClickListener listener1 = new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int id) {
                     finish();
                 }
             };

             Utils.showDialog(SplashScreen.this,
                     getString(R.string.dialog_no_internet_connection_title),
                     getString(R.string.dialog_no_internet_connection_message),
                     getString(R.string.ok_exit),
                     null,
                     listener1,
                     null);

             return;
         }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Retrieve the list of Tags
                initTagsMaps();

                // Retrieve SharedPreferences
                SharedPreferences prefs = getSharedPreferences(Const.RELI_SHARED_PREF_FILE, Context.MODE_PRIVATE);
                boolean restoredShouldKeepSignedIn = prefs.getBoolean(Const.SHARED_PREF_KEEP_SIGNED_IN, false);
                String restoredParseUser = prefs.getString(Const.SHARED_PREF_PARSE_USER, null);

                // Check if we should use the saved user
                if (restoredShouldKeepSignedIn) {
                    final ReliUser user = (ReliUser) ParseUser.getCurrentUser();

                    if (user != null) {
                        // The Parse user is known
                        handleParseUserKnown(user);
                    }

                    else if (restoredParseUser != null) {
                        // The Parse user is known but we should retrieve it
                        handleParseUserIdKnown(restoredParseUser);
                    }

                    else {
                        // The parse user is not known
                        handleParseUserNotKnown();
                    }
                }

                // The parse user is not known
                else {
                    handleParseUserNotKnown();
                }

            }
        }, SPLASH_TIME_OUT_MILLIS);
    }

    /* ========================================================================== */

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /* ========================================================================== */

    private void initTagsMaps() {

        ParseQuery<ReliTag> query = ReliTag.getReliTagQuery();
        query.findInBackground(new FindCallback<ReliTag>() {
            @Override
            public void done(List<ReliTag> reliTags, ParseException e) {

                if (e == null) {
                    String currentID, currentTagName;
                    for (ReliTag tag : reliTags) {
                        currentID = tag.getObjectId();
                        currentTagName = tag.getString(Const.COL_TAG_NAME);
                        MainActivity.tagsIdToTag.put(currentID, new ReliTag(currentTagName, currentID));
                    }
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

    /* ========================================================================== */

    // To be used in rare cases - http://stackoverflow.com/questions/27628392/parseinstallation-getcurrentinstallation-saveinbackground-not-working
    public static void clearParseInstallation(Context context) {
        try {
            Method method = ParseInstallation.class.getDeclaredMethod("clearCurrentInstallationFromDisk", Context.class);
            method.setAccessible(true);
            method.invoke(null, context);
        } catch (Exception e) {

        }
    }

    /* ========================================================================== */

    private void handleParseUserNotKnown() {
        MainActivity.user = null;
        initLoginScreen();
    }

    /* ========================================================================== */

    private void handleParseUserKnown(ReliUser user) {
        MainActivity.user = user;
        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                // Set Installation object
                ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        ParseInstallation.getCurrentInstallation().put(Const.INSTALLATION_USER, ParseUser.getCurrentUser());
                    }
                });

                continueToMain();
            }
        });
    }

    /* ========================================================================== */

    private void handleParseUserIdKnown(String restoredParseUser) {
        ParseQuery<ReliUser> query = ReliUser.getReliQuery();
        query.getInBackground(restoredParseUser, new GetCallback<ReliUser>() {
            public void done(ReliUser reliUser, ParseException e) {
                if (e == null) {
                    MainActivity.user = reliUser;
                    continueToMain();
                }
                else {
                    handleParseUserNotKnown();
                }
            }
        });
    }

    /* ========================================================================== */

    private void continueToMain() {
        Utils.saveParseUserInSharedPreferences(this, MainActivity.user.getParseID());
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}