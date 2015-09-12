package reli.reliapp.co.il.reli.login;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ObjectStreamException;
import java.net.URL;
import java.util.Arrays;

import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.dataStructures.ReliUserType;
import reli.reliapp.co.il.reli.utils.Const;
import reli.reliapp.co.il.reli.utils.Utils;


public class LoginFragment extends android.support.v4.app.Fragment {

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private LoginButton fbLoginButton;
    private CheckBox cb;
    private View view;

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
        addCheckBox(view);
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
            getActivity().finish();
            startActivity(new Intent(getActivity(), MainActivity.class));
        }

        else {
            Profile profile = Profile.getCurrentProfile();

            // Check if we just logged off
            if (profile.getCurrentProfile() == null) {
                MainActivity.user = null;
                return;
            }

            // Create a new ReliUser
            ReliUser reliUser = new ReliUser(getActivity().getApplicationContext(),
                    ReliUserType.FACEBOOK_USER,
                    profile.getFirstName(),
                    profile.getName(),
                    new ParseGeoPoint(),
                    null);

            addUserToParse(reliUser);

            // Save user's profile picture
            handleFacebookAvatar(reliUser, profile);
        }
    }

    /* ========================================================================== */

    private void addFacebookButton(View view) {
        Button fb_button = (Button) view.findViewById(R.id.fb_button);
        fb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLoginButton.performClick();
            }
        });
    }

    /* ========================================================================== */

    private void addAnonymousButton(View view) {
        final Button anonymousButton = (Button) view.findViewById(R.id.anonymous_button);
        anonymousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnonymousLogin();
            }
        });
    }

    /* ========================================================================== */

    private void handleAnonymousLogin() {
        ReliUser user = MainActivity.user;

        // Handle the case when the user is known
        if (user != null) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), MainActivity.class));
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

            }

            try {
            addUserToParse(reliUser);
            } catch (Exception e) {

            }
        }
    }

    /* ========================================================================== */

    private void addUserToParse(final ReliUser reliUser) {
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
                            MainActivity.installation = ParseInstallation.getCurrentInstallation();
                            MainActivity.installation.put(Const.INSTALLATION_USER, ParseUser.getCurrentUser());
                        }
                    });

                    getActivity().finish();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }
        });
    }

    /* ========================================================================== */

    private void addCheckBox(View view) {
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveKeepLogin();
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
}