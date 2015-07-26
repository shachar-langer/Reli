package reli.reliapp.co.il.reli.utils;

import android.location.Location;

import com.facebook.login.widget.ProfilePictureView;

import reli.reliapp.co.il.reli.dataStructures.ReliUserType;

/**
 * The Class Const is a single common class to hold all the app Constants.
 */
public class Const
{
	public static final String BUDDY_NAME = "buddyName";
    public static final String CURRENT_LOCATION = "currentLocation";

    public static final String DUMMY_PASSWORD = "Dummy";
    public static final String ANONYMOUS_NAME = "Anonymous";

    // Columns in Parse for ReliUser
    public static final String COL_NAME_PARSE_ID = "parseID";
    public static final String COL_NAME_USER_TYPE = "userType";
    public static final String COL_NAME_FIRST_NAME = "firstName";
    public static final String COL_NAME_FULL_NAME = "fullName";
    public static final String COL_NAME_FACEBOOK_PICTURE = "facebookPicture";
    public static final String COL_NAME_LOCATION = "location";

    // Columns in Parse for AbstructDiscussion
    public static final String COL_DISCUSSION_NAME = "discussionName";
    public static final String COL_DISCUSSION_LOCATION = "discussionLocation";
    public static final String COL_DISCUSSION_RADIUS = "discussionRadius";
    public static final String COL_DISCUSSION_LOGO = "discussionLogo";
    public static final String COL_DISCUSSION_CREATION_DATE = "discussionCreationDate";
    public static final String COL_DISCUSSION_EXPIRATION_DATE = "discussionexpirationDate";
    public static final String COL_DISCUSSION_OWNER_PARSE_ID = "discussionOwnerParseID";

    // Location argument to pass between activities
    public static final String ALTITUDE = "altitude";
    public static final String LATITUDE = "latitude";

}
