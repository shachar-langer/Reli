package reli.reliapp.co.il.reli.dataStructures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.utils.Const;

public class ReliUser extends ParseUser {

    private ReliUserType userType;
    private String firstName;
    private String fullName;
    private Bitmap picture;
    private ProfilePictureView facebookPicture;
    private ParseGeoPoint location;
    private int notificationsRadius;
    private ArrayList<ReliTag> notificationsTags;
    private Context ctx;

    /* ========================================================================== */

    // Default constructor is a must
    public ReliUser() {

    }

    /* ========================================================================== */

    // Constructor
    public ReliUser(Context ctx, ReliUserType userType, String firstName, String fullName, ParseGeoPoint location) {
        this.ctx = ctx;
        String dummyUniqueString = UUID.randomUUID().toString();
        setUsername(dummyUniqueString);
        setPassword(dummyUniqueString);

        setUserType(userType);
        setFirstName(firstName);
        setFullName(fullName);
        setLocation(location);

        setNotificationsRadius(Const.DEFAULT_RADIUS);
        // TODO - which tags should be the default ones?
//        setNotificationsTags();
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
        return facebookPicture;
    }

    /* ========================================================================== */

    public void setFacebookPicture(ProfilePictureView facebookPicture) {
        // TODO
        this.facebookPicture = facebookPicture;
    }

    /* ========================================================================== */

    public Bitmap getPicture() {
        // TODO
        if (picture == null) {
            return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.useravatar);
        }
        return picture;
    }

    /* ========================================================================== */

    public void setPicture(Bitmap picture) {
        this.picture = picture;
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

       /* ========================================================================== */

    public void setNotificationsRadius(int notificationsRadius) {
        // TODO - don't we want to add saveEventually() to each setter?
        put(Const.COL_NAME_NOTIFICATIONS_RADIUS, notificationsRadius);
    }

    /* ========================================================================== */

    public int getNotificationsRadius() {
        return getInt(Const.COL_NAME_NOTIFICATIONS_RADIUS);
    }

   /* ========================================================================== */

    public void setNotificationsTags(ArrayList<ReliTag> notificationsTags) {
        // TODO - make sure that it works
//        // Remove the old list
//        removeAll(Const.COL_NAME_NOTIFICATIONS_TAGS, Arrays.asList(getNotificationsTags()));
//        // Add the new one
//        addAllUnique(Const.COL_NAME_NOTIFICATIONS_TAGS, notificationsTags);
    }

    /* ========================================================================== */

    public ArrayList<ReliTag> getNotificationsTags() {
        // TODO - make sure that it works
//        List<ReliTag> l = getList(Const.COL_NAME_NOTIFICATIONS_TAGS);
//        return new ArrayList<ReliTag>(l);

        return new ArrayList<ReliTag>();
    }

}