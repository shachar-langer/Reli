package reli.reliapp.co.il.reli.utils;

import android.location.Location;

import com.facebook.login.widget.ProfilePictureView;

import reli.reliapp.co.il.reli.dataStructures.ReliUserType;

/**
 * The Class Const is a single common class to hold all the app Constants.
 */
public class Const
{
    public static final int NOTIFICATION_TIME_INTERVAL_IN_SECONDS = 10 * 60;
    public static final int METERS_IN_KM = 1000;

    public static final String UNKNOWN_USER = "(Unknown)";
    public static final String HOME_FRAGMENT_TAG = "HomeFragment";
    public static final int STEP_SIZE = 20;

    // Default radius for receiving
    public static final int DEFAULT_RADIUS_FOR_NOTIFICATIONS = 50;
    public static final int DEFAULT_RADIUS_FOR_RELIS = 50;
    public static final int DEFAULT_EXPIRATION_FOR_RELIS = 150;

    // Columns in Parse for DiscussionActivity (the chat itself
	public static final String DISCUSSION_TOPIC = "buddyName";
    public static final String DISCUSSION_TABLE_NAME = "discussionTableName";

    public static final String CURRENT_LOCATION = "currentLocation";
    public static final String DUMMY_PASSWORD = "Dummy";
    public static final String ANONYMOUS_NAME = "Anonymous";

    // Columns in Parse for ReliUser
    public static final String COL_NAME_PARSE_ID = "parseID";
    public static final String COL_NAME_USER_TYPE = "userType";
    public static final String COL_NAME_FIRST_NAME = "firstName";
    public static final String COL_NAME_FULL_NAME = "fullName";
    public static final String COL_NAME_AVATAR = "userAvatar";
    public static final String COL_NAME_LOCATION = "location";
    public static final String COL_NAME_RELIS_RADIUS = "relisRadius";
    public static final String COL_NAME_RELIS_EXPIRATION = "relisExpirationInMinutes";
    public static final String COL_NAME_NOTIFICATIONS_RADIUS = "notificationsRadius";
    public static final String COL_NAME_NOTIFICATIONS_TAGS = "tags";
    public static final String COL_NAME_DISCUSSIONS_IM_IN = "discussionsImIn";

    // Columns in Parse for AbstructDiscussion
    public static final String COL_DISCUSSION_NAME = "discussionName";
    public static final String COL_DISCUSSION_LOCATION = "discussionLocation";
    public static final String COL_DISCUSSION_RADIUS = "discussionRadius";
    public static final String COL_DISCUSSION_LOGO = "discussionLogo";
    public static final String COL_DISCUSSION_CREATION_DATE = "discussionCreationDate";
    public static final String COL_DISCUSSION_EXPIRATION_DATE = "discussionexpirationDate";
    public static final String COL_DISCUSSION_OWNER_PARSE_ID = "discussionOwnerParseID";
    public static final String COL_DISCUSSION_TAG_IDS = "tagIDsForDiscussion";

    // Columns in Parse for the messages table
    public static final String COL_MESSAGE_CONTENT = "message";
    public static final String COL_MESSAGE_SENDER_ID = "sender";
    public static final String COL_MESSAGE_SENDER_NAME = "senderName";
    public static final String COL_MESSAGE_CREATED_AT = "createdAt";

    // Columns in Parse for Tag
    public static final String COL_TAG_NAME = "tagName";

    // Location argument to pass between activities
    public static final String LONGTITUDE = "longtitude";
    public static final String LATITUDE = "latitude";

    // Radius argument to pass between the fragment and activity of ReliNearMe
    public static final String RADIUS = "radius";

    // Expiration of Relis
    public static final int MINIMUM_TIME = 0;
    public static final int MAX_HOURS = 24;
    public static final int MAX_MINUTES = 59;
    public static final int MINUTES_IN_HOUR = 60;

    // SharedPreferences
    public static final String RELI_SHARED_PREF_FILE = "reliPrefFile";
    public static final String SHARED_PREF_KEEP_SIGNED_IN = "reliKeepSignedIn";

    // Installation (used for notifications)
    public static final String INSTALLATION_USER = "user";

}

