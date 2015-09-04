package reli.reliapp.co.il.reli.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.dataStructures.ReliUserType;
import reli.reliapp.co.il.reli.utils.Const;


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
                Toast.makeText(getActivity().getApplicationContext(), "onCurrentProfileChanged", Toast.LENGTH_SHORT).show();
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
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));

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
        Toast.makeText(getActivity().getApplicationContext(), "in handleFacebookLogin()", Toast.LENGTH_SHORT).show();
        ReliUser user = MainActivity.user;

        // Handle the case when the user is known
        if (user != null) {
            Toast.makeText(getActivity().getApplicationContext(), "FB - ParseUser is known", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
//            getActivity().finish();
        }

        else {
            Toast.makeText(getActivity().getApplicationContext(), "FB - ParseUser is not known", Toast.LENGTH_SHORT).show();
            Profile profile = Profile.getCurrentProfile();

            // Check if we just logged off
            if (profile.getCurrentProfile() == null) {
                MainActivity.user = null;
                return;
            }

            // Create a new ReliUser
            final ReliUser reliUser = new ReliUser(getActivity().getApplicationContext(),
                    ReliUserType.FACEBOOK_USER,
                    profile.getFirstName(),
                    profile.getName(),
                    new ParseGeoPoint());

            addUserToParse(reliUser);
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
            Toast.makeText(getActivity().getApplicationContext(), "ParseUser is known", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
//            getActivity().finish();
        }

        else {
            Toast.makeText(getActivity().getApplicationContext(), "ParseUser is not known", Toast.LENGTH_SHORT).show();

            // Create a new ReliUser
            final ReliUser reliUser = new ReliUser(getActivity().getApplicationContext(),
                    ReliUserType.ANONYMOUS_USER,
                    Const.ANONYMOUS_NAME,
                    Const.ANONYMOUS_NAME,
                    new ParseGeoPoint());

            addUserToParse(reliUser);
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
                    Toast.makeText(getActivity().getApplicationContext(), "Added a new ReliUser to Parse", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), MainActivity.class));
//                    getActivity().finish();
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Error in addUserToParse(): " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity().getApplicationContext(), "SharedPreferences - changed to " + cb.isChecked(), Toast.LENGTH_SHORT).show();
        editor.commit();
    }
}









/*
Bitmap bm = null;
            try {
                BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
                bitmap_options.inScaled = false;
                bitmap_options.inDither = false;
                bitmap_options.inPreferQualityOverSpeed = true;
                bitmap_options.inSampleSize = 1;
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String imageURL = "https://graph.facebook.com/" + profile.getId()+ "/picture?type=small";
                URL url_value = new URL(imageURL);
                bm = BitmapFactory.decodeStream(url_value.openConnection().getInputStream(), null, bitmap_options);
            } catch (Exception e) {
            }

            ProfilePictureView profilePictureView = (ProfilePictureView) view.findViewById(R.id.fiv);
            profilePictureView.setProfileId(profile.getId());

////                ImageView profileImageView = ((ImageView) profilePictureView.getChildAt(0));
////                Bitmap bitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
//                Bitmap bitmap = profilePictureView.getDrawingCache();

            ImageView  tv1= (ImageView) view.findViewById(R.id.iv);
            tv1.setImageBitmap(bm);
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//            }

//            try {
////                String imageURL = "http://graph.facebook.com/105935396418298/picture";
//                String imageURL = "http://graph.facebook.com/" + profile.getId()+ "/picture?type=small";
//                URL url_value = new URL(imageURL);
//                Bitmap profPict = BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
////                profPict = BitmapFactory.decodeStream((InputStream)new URL(imageURL).getContent());
//            }
//            catch (Exception e) {
//                Toast.makeText(getActivity().getApplicationContext(), "Error with Picture - " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
 */