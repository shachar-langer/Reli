package reli.reliapp.co.il.reli.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Arrays;
import java.util.Date;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.dataStructures.ReliUserType;


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
                //Toast.makeText(getActivity().getApplicationContext(), "In ProfileTracker", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(getActivity().getApplicationContext(), "In onActivityResult", Toast.LENGTH_SHORT).show();
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
        Profile profile = Profile.getCurrentProfile();
        if (profile.getCurrentProfile() == null) {
            profilePictureView.setProfileId(null);
            Toast.makeText(getActivity().getApplicationContext(), "currentProfile NULL", Toast.LENGTH_SHORT).show();
        } else {
            profilePictureView.setProfileId(profile.getId());
            Toast.makeText(getActivity().getApplicationContext(), "newProfile " + profile.getCurrentProfile().getName(), Toast.LENGTH_SHORT).show();
//            ReliUser ru = new ReliUser(ReliUserType.FACEBOOK_USER, profile.getId(), profile.getFirstName(), profile.getMiddleName(), profile.getLastName(), profile.getName());
        }
    }
}