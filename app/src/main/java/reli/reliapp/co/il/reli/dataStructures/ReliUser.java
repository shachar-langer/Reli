package reli.reliapp.co.il.reli.dataStructures;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.UUID;

import reli.reliapp.co.il.reli.utils.Const;

public class ReliUser extends ParseUser {

    private String parseID;
    private ReliUserType userType;
    private String firstName;
    private String fullName;
    private ProfilePictureView facebookPicture;
    private ParseGeoPoint location;

    /* ========================================================================== */

    // Default constructor is a must
    public ReliUser() {

    }

    /* ========================================================================== */

    // Constructor
    public ReliUser(ReliUserType userType, String firstName, String fullName, ParseGeoPoint location) {
        String dummyUniqueString = UUID.randomUUID().toString();
        setUsername(dummyUniqueString);
        setPassword(dummyUniqueString);

        setUserType(userType);
        setFirstName(firstName);
        setFullName(fullName);
        setLocation(location);
    }

    /* ========================================================================== */

    public static ReliUser getCurrentReliUser() {
        return (ReliUser) ParseUser.getCurrentUser();
    }

    /* ========================================================================== */

    public static ParseQuery<ReliUser> getReliQuery() {
        return ParseQuery.getQuery(ReliUser.class);
    }

    /* ========================================================================== */

    public String getParseID() {
        return getObjectId();
    }

    /* ========================================================================== */

    public ReliUserType getUserType() {
        try {
            return ReliUserType.values()[getInt(Const.COL_NAME_USER_TYPE)];
        }
        catch (Exception e) {
            return ReliUserType.ANONYMOUS_USER;
        }
    }

    /* ========================================================================== */

    public void setUserType(ReliUserType userType) {
        put(Const.COL_NAME_USER_TYPE, userType.getUserTypeCode());
    }

    /* ========================================================================== */

    public String getFirstName() {
        return getString(Const.COL_NAME_FIRST_NAME);
    }

    /* ========================================================================== */

    public void setFirstName(String firstName) {
        put(Const.COL_NAME_FIRST_NAME, firstName);
    }

    /* ========================================================================== */

    public String getFullName() {
        return getString(Const.COL_NAME_FULL_NAME);
    }

    /* ========================================================================== */

    public void setFullName(String fullName) {
        put(Const.COL_NAME_FULL_NAME, fullName);
    }

    /* ========================================================================== */

    public ProfilePictureView getFacebookPicture() {
        // TODO
        return facebookPicture;
    }

    /* ========================================================================== */

    public void setFacebookPicture(ProfilePictureView facebookPicture) {
        // TODO
        this.facebookPicture = facebookPicture;
    }

    /* ========================================================================== */

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(Const.COL_NAME_LOCATION);
    }

    /* ========================================================================== */

    public void setLocation(ParseGeoPoint location) {

        // Update Parse with the user location
        put(Const.COL_NAME_LOCATION, location);

        // Update the user location in this object
        this.location = location;
    }
}