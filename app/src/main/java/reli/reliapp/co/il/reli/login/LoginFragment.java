package reli.reliapp.co.il.reli.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private ProfilePictureView profilePictureView;

    /* ========================================================================== */

    public LoginFragment() {
    }

    /* ========================================================================== */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Facebook SDK
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // Facebook tracker
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                changeUser();
            }
        };
    }

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        profilePictureView = (ProfilePictureView) view.findViewById(R.id.profilePicture);

        // Set callback for Login Button
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.facebook_login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {}

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException exception) {}
        });

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

    private void changeUser() {
        if (ParseUser.getCurrentUser() != null) {

            Toast.makeText(getActivity().getApplicationContext(), "in if", Toast.LENGTH_SHORT).show();

            // TODO - delete this line. It was only for testing
            ParseUser.getCurrentUser().put(Const.COL_NAME_DISCUSSIONS_IM_IN, "");

            startActivity(new Intent(getActivity(), MainActivity.class));
        }

        else {

            Toast.makeText(getActivity().getApplicationContext(), "in else", Toast.LENGTH_SHORT).show();
            Log.w("dfd", "I'm in else");

            Profile profile = Profile.getCurrentProfile();

            // Check if we are disconnected
            if (profile.getCurrentProfile() == null) {
                MainActivity.user = null;
                profilePictureView.setProfileId(null);
                Toast.makeText(getActivity().getApplicationContext(), "currentProfile NULL", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO - add "please wait" dialog
            // final ProgressDialog dia = ProgressDialog.show(this, null, getString(R.string.alert_wait));

            profilePictureView.setProfileId(profile.getId());
//            Bitmap profPict = null;

            // TODO - check why it doesn't work :(
//            try {
//                String imageURL = "http://graph.facebook.com/105935396418298/picture";
////                String imageURL = "http://graph.facebook.com/" + profile.getId()+ "/picture?type=small";
//                URL url_value = new URL(imageURL);
//                profPict = BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
////                profPict = BitmapFactory.decodeStream((InputStream)new URL(imageURL).getContent());
//            }
//            catch (Exception e) {
//                Toast.makeText(getActivity().getApplicationContext(), "Error with Picture - " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }

            // Create a new ReliUser
            final ReliUser reliUser = new ReliUser(getActivity().getApplicationContext(),
                    ReliUserType.FACEBOOK_USER,
                    profile.getFirstName(),
                    profile.getName(),
                    new ParseGeoPoint());
            reliUser.put(Const.COL_NAME_DISCUSSIONS_IM_IN, "");
//            reliUser.setPicture(profPict);

            // Add the new user to Parse
            reliUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        //                    dia.dismiss();
                        MainActivity.user = reliUser;
                        Toast.makeText(getActivity().getApplicationContext(), ReliUser.getCurrentReliUser().getUserType().toString(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    } else {
                        // TODO - change
                        //                        Utils.showDialog(this, getString(R.string.err_singup) + " " + e.getMessage());
                        Toast.makeText(getActivity().getApplicationContext(), "Bassa", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}