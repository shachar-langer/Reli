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

    public static final String DUMMY_PASSWORD = "Dummy";
    public static final String ANONYMOUS_NAME = "Anonymous";

    // Columns in Parse for ReliUser
    public static final String COL_NAME_PARSE_ID = "parseID";
    public static final String COL_NAME_USER_TYPE = "userType";
    public static final String COL_NAME_FIRST_NAME = "firstName";
    public static final String COL_NAME_FULL_NAME = "fullName";
    public static final String COL_NAME_FACEBOOK_PICTURE = "facebookPicture";
    public static final String COL_NAME_LOCATION = "location";
}
