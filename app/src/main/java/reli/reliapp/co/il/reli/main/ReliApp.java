package reli.reliapp.co.il.reli.main;

import android.app.Application;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import reli.reliapp.co.il.reli.dataStructures.ReliUser;

public class ReliApp extends Application {

    private final String PARSE_APP_ID = "jmxBnVR9fWE39FZxG5tCuVCVwpMp7j62DjdjTFj2";
    private final String PARSE_CLIENT_KEY = "LILbHV2CYBZp10mqzXUKr1lwGv2Oguo82dvp5c2P";

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the extended ParseUser (aka ReliUser)
        ParseUser.registerSubclass(ReliUser.class);

        // Initialize Parse
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
    }
}
