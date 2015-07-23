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
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.UserList;
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
            public void onSuccess(LoginResult loginResult) {

            }

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
        // TODO - add a logout function in the drawer
//        ParseUser.logOut();

        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(getActivity(), UserList.class));
        }

        else {
            Profile profile = Profile.getCurrentProfile();

            // Check if we are disconnected
            if (profile.getCurrentProfile() == null) {
                UserList.user = null;
                profilePictureView.setProfileId(null);
                Toast.makeText(getActivity().getApplicationContext(), "currentProfile NULL", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO - add "please wait" dialog
            // final ProgressDialog dia = ProgressDialog.show(this, null, getString(R.string.alert_wait));

            profilePictureView.setProfileId(profile.getId());

            // Create a new ReliUser
            final ReliUser reliUser = new ReliUser(ReliUserType.FACEBOOK_USER,
                    profile.getFirstName(),
                    profile.getName(),
                    new ParseGeoPoint());

            // Add the new user to Parse
            reliUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        //                    dia.dismiss();
                        UserList.user = reliUser;
                        Toast.makeText(getActivity().getApplicationContext(), ReliUser.getCurrentReliUser().getUserType().toString(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), UserList.class));
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