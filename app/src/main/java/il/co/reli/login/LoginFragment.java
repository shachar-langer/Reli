package il.co.reli.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import il.co.reli.main.MainActivity;
import il.co.reli.il.reli.R;
import il.co.reli.dataStructures.ReliUser;
import il.co.reli.dataStructures.ReliUserType;
import il.co.reli.utils.Const;
import il.co.reli.utils.Utils;


public class LoginFragment extends android.support.v4.app.Fragment {

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private LoginButton fbLoginButton;
    private CheckBox cb;
    private View view;
    private ProgressDialog dia = null;

    /* ========================================================================== */

    public LoginFragment() {
    }

    /* ========================================================================== */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        // Facebook tracker
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                handleFacebookLogin();
            }
        };
    }

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        fbLoginButton = (LoginButton) view.findViewById(R.id.fbLoginButton);
        fbLoginButton.setFragment(this);
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile"));

        cb = (CheckBox) view.findViewById(R.id.login_keep_login);

        addFacebookButton(view);
        addAnonymousButton(view);
        saveKeepLogin();

        return view;
    }

    /* ========================================================================== */

    @Override
    // Getting called after clicking on the login button
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /* ========================================================================== */

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    /* ========================================================================== */

    private void handleFacebookLogin() {
        ReliUser user = MainActivity.user;

        // Handle the case when the user is known
        if (user != null) {
            continueToMain();
        }

        else {
            final Profile profile = Profile.getCurrentProfile();

            // Check if we just logged off
            if (profile == null) {
                MainActivity.user = null;
                return;
            }

            dia = ProgressDialog.show(getActivity(), null, getString(R.string.alert_loading));

            // Check if the Facebook user already has a ParseID
            ParseQuery query = ParseQuery.getQuery(Const.FACEBOOK_TO_PARSE_MAPPING);
            query.whereEqualTo(Const.FACEBOOK_TO_PARSE_MAPPING_FACEBOOK_ID, profile.getId());
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> parseIDsList, ParseException e) {

                    // The user has already logged in in the past
                    if ((e == null) && (parseIDsList.size() > 0)) {
                        String parseID = parseIDsList.get(0).getString(Const.FACEBOOK_TO_PARSE_MAPPING_PARSE_ID);
                        handleFacebookLoginExistingUser(parseID, profile);
                    }

                    // This is a new user
                    else {
                        handleFacebookLoginNewUser(profile);
                    }
                }
            });
        }
    }

    /* ========================================================================== */

    private void addFacebookButton(View view) {
        FloatingActionButton fb_button = (FloatingActionButton) view.findViewById(R.id.fb_button);
        fb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLoginButton.performClick();
            }
        });
    }

    /* ========================================================================== */

    private void addAnonymousButton(View view) {
        final FloatingActionButton anonymousButton = (FloatingActionButton) view.findViewById(R.id.anonymous_button);
        anonymousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia = ProgressDialog.show(getActivity(), null, getString(R.string.alert_loading));
                handleAnonymousLogin();
            }
        });
    }

    /* ========================================================================== */

    private void handleAnonymousLogin() {
        ReliUser user = MainActivity.user;

        // Handle the case when the user is known
        if (user != null) {
            continueToMain();
        }

        else {
            ReliUser reliUser = null;
            try {
                // Create a new ReliUser
                reliUser = new ReliUser(getActivity().getApplicationContext(),
                        ReliUserType.ANONYMOUS_USER,
                        Const.ANONYMOUS_NAME,
                        Const.ANONYMOUS_NAME,
                        new ParseGeoPoint(),
                        null);
            } catch (Exception e) {
                dia.dismiss();
            }

            try {
                addUserToParse(reliUser, false);
            } catch (Exception e) {
                dia.dismiss();
            }
        }
    }

    /* ========================================================================== */

    private void addUserToParse(final ReliUser reliUser, final boolean isFacebookUser) {
        MainActivity.user = reliUser;

        // Add the new user to Parse
        reliUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Set Installation object
                    ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            addToInstallationTable();

                            // Add to Facebook-Parse mapping
                            if (isFacebookUser) {
                                addToFacebookParseMapping();
                            }
                        }
                    });

                    continueToMain();
                }
            }
        });
    }

    /* ========================================================================== */

    private void saveKeepLogin() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(Const.RELI_SHARED_PREF_FILE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Const.SHARED_PREF_KEEP_SIGNED_IN, cb.isChecked());
        editor.commit();
    }

    /* ========================================================================== */

    private void handleFacebookAvatar(ReliUser reliUser, Profile profile) {
        try {
            String imageURL = "https://graph.facebook.com/" + profile.getId()+ "/picture?type=small";
            URL url_value = new URL(imageURL);

            // Find the Avatar in background
            new HandleFacebookAvatar().execute(url_value, reliUser);

        } catch (Exception e) {

        }
    }

    /* ========================================================================== */

    private class HandleFacebookAvatar extends AsyncTask<Object, Void, byte[]> {
        ReliUser reliUser;

        @Override
        protected byte[] doInBackground(Object... params) {
            URL url_value = (URL) params[0];
            reliUser = (ReliUser) params[1];
            Bitmap bitmap;

            try {
                BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
                // bitmap_options.inScaled = false;
                // bitmap_options.inDither = false;
                // bitmap_options.inPreferQualityOverSpeed = true;
                // bitmap_options.inSampleSize = 1;

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                bitmap = BitmapFactory.decodeStream(url_value.openConnection().getInputStream(), null, bitmap_options);
            } catch (Exception e) {
                Resources res = getResources();
                Drawable drawable = res.getDrawable(R.drawable.user_chat1);
                bitmap = ((BitmapDrawable)drawable).getBitmap();
            }

            return Utils.convertBitmapToByteArray(bitmap);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            reliUser.setAvatar(bytes);
        }
    }

    /* ========================================================================== */

    private void continueToMain() {
        if (dia != null) {
            dia.dismiss();
        }

        if (cb.isChecked()) {
            Utils.saveParseUserInSharedPreferences(getActivity(), MainActivity.user.getParseID());
        }
        saveKeepLogin();

        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    /* ========================================================================== */

    private void addToInstallationTable() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            ParseInstallation.getCurrentInstallation().put(Const.INSTALLATION_USER, user);
            ParseInstallation.getCurrentInstallation().saveInBackground();
        }
    }

    /* ========================================================================== */

    private void handleFacebookLoginExistingUser(String parseID, final Profile profile) {
        ParseQuery<ReliUser> userQuery = ReliUser.getReliQuery();
        userQuery.getInBackground(parseID, new GetCallback<ReliUser>() {
            public void done(ReliUser reliUser, ParseException e) {
                if (e == null) {
                    MainActivity.user = reliUser;

                    // Save user's profile picture
                    handleFacebookAvatar(reliUser, profile);

                    continueToMain();
                }
            }
        });
    }

    /* ========================================================================== */

    private void handleFacebookLoginNewUser(Profile profile) {
        // Create a new ReliUser
        ReliUser reliUser = new ReliUser(getActivity().getApplicationContext(),
                ReliUserType.FACEBOOK_USER,
                profile.getFirstName(),
                profile.getName(),
                new ParseGeoPoint(),
                null);

        addUserToParse(reliUser, true);

        // Save user's profile picture
        handleFacebookAvatar(reliUser, profile);
    }

    /* ========================================================================== */

    private void addToFacebookParseMapping() {
        ParseObject po = new ParseObject(Const.FACEBOOK_TO_PARSE_MAPPING);
        po.put(Const.FACEBOOK_TO_PARSE_MAPPING_FACEBOOK_ID, Profile.getCurrentProfile().getCurrentProfile().getId());
        po.put(Const.FACEBOOK_TO_PARSE_MAPPING_PARSE_ID, ParseUser.getCurrentUser().getObjectId());
        po.saveEventually();
    }
}