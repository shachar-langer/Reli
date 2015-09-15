package il.co.reli;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import il.co.reli.dataStructures.AbstractDiscussion;
import il.co.reli.dataStructures.Discussion;
import il.co.reli.dataStructures.ReliUser;
import il.co.reli.dataStructures.ReliTag;

public class ReliApp extends Application {

    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "Reli";

    private final String PARSE_APP_ID = "jmxBnVR9fWE39FZxG5tCuVCVwpMp7j62DjdjTFj2";
    private final String PARSE_CLIENT_KEY = "LILbHV2CYBZp10mqzXUKr1lwGv2Oguo82dvp5c2P";

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the extended ParseUser (aka ReliUser)
        ParseUser.registerSubclass(ReliUser.class);

        // Register the extended ParseObject (aka AbstractDiscussion)
        ParseObject.registerSubclass(AbstractDiscussion.class);
        ParseObject.registerSubclass(Discussion.class);

        // Register the extended ParseObject (aka ReliTag)
        ParseObject.registerSubclass(ReliTag.class);

        // Initialize Parse
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);

        // Save the current Installation to Parsel
        ParseInstallation.getCurrentInstallation().saveInBackground();

        // Initialize the Facebook SDK
        FacebookSdk.sdkInitialize(this);
    }
}
