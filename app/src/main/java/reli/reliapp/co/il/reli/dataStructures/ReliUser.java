package reli.reliapp.co.il.reli.dataStructures;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.utils.Const;

public class ReliUser extends ParseUser {

    private ReliUserType userType;
    private String firstName;
    private String fullName;
    private Bitmap picture;
    private ProfilePictureView facebookPicture;
    private ParseGeoPoint location;
    private int relisRadius;
    private int relisExpirationInMinutes;
    private int notificationsRadius;
    private ArrayList<ReliTag> notificationsTags;
    private Context ctx;

    /* ========================================================================== */

    // Default constructor is a must
    public ReliUser() {

    }

    /* ========================================================================== */

    // Constructor
    public ReliUser(Context ctx, ReliUserType userType, String firstName, String fullName, ParseGeoPoint location, byte[] userAvatar) {
        this.ctx = ctx;
        initDiscussionImIn();

        String dummyUniqueString = UUID.randomUUID().toString();
        setUsername(dummyUniqueString);
        setPassword(dummyUniqueString);

        setUserType(userType);
        setFirstName(firstName);
        setFullName(fullName);
        setLocation(location);

        setRelisRadius(Const.DEFAULT_RADIUS_FOR_RELIS);
        setRelisExpirationInMinutes(Const.DEFAULT_EXPIRATION_FOR_RELIS);

        setNotificationsRadius(Const.DEFAULT_RADIUS_FOR_NOTIFICATIONS);
        setNotificationsTagsIDs(new ArrayList<>(MainActivity.tagsIdToTag.keySet()));

        if (userAvatar != null) {
            setAvatar(userAvatar);
        }
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

    public ParseFile getAvatar() {
        ParseFile avatarFile = (ParseFile) get(Const.COL_NAME_AVATAR);
        return avatarFile;
    }

    /* ========================================================================== */

    public void setAvatar(byte[] avatar) {
        final ParseFile file = new ParseFile("avatar.png", avatar);

        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                put(Const.COL_NAME_AVATAR, file);
                saveEventually();
            }
        });
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

    public int getRelisRadius() {
        return getInt(Const.COL_NAME_RELIS_RADIUS);
    }

    /* ========================================================================== */

    public void setRelisRadius(int relisRadius) {
        put(Const.COL_NAME_RELIS_RADIUS, relisRadius);
    }

    /* ========================================================================== */

    public int getRelisExpirationInMinutes() {
        return getInt(Const.COL_NAME_RELIS_EXPIRATION);
    }

    /* ========================================================================== */

    public void setRelisExpirationInMinutes(int relisExpirationInMinutes) {
        put(Const.COL_NAME_RELIS_EXPIRATION, relisExpirationInMinutes);
    }

    /* ========================================================================== */

    public void setNotificationsRadius(int notificationsRadius) {
        put(Const.COL_NAME_NOTIFICATIONS_RADIUS, notificationsRadius);
    }

    /* ========================================================================== */

    public int getNotificationsRadius() {
        return getInt(Const.COL_NAME_NOTIFICATIONS_RADIUS);
    }

   /* ========================================================================== */

    public void setNotificationsTagsIDs(ArrayList<String> tagsIDs) {
        if (tagsIDs != null) {
            // Remove previous values
            remove(Const.COL_NAME_NOTIFICATIONS_TAGS);

            // Add the new ones
            addAllUnique(Const.COL_NAME_NOTIFICATIONS_TAGS, tagsIDs);
        }
    }

    /* ========================================================================== */

    public ArrayList<String> getNotificationsTagsIDs() {
        List<String> tagIDs = getList(Const.COL_NAME_NOTIFICATIONS_TAGS);
        ArrayList<String> res;

        if (tagIDs != null) {
            res = new ArrayList<String>(tagIDs);
        } else {
            res = new ArrayList<>();
        }

        return res;
    }

    /* ========================================================================== */

    public void addDiscussionImIn(ArrayList<String> discussionImInIDs) {
        // Add the new ones
        addAllUnique(Const.COL_NAME_DISCUSSIONS_IM_IN, discussionImInIDs);
    }

    /* ========================================================================== */

    public void addDiscussionImIn(String discussionImInID) {
        ArrayList<String> al = new ArrayList<>();
        al.add(discussionImInID);
        addDiscussionImIn(al);
    }

    /* ========================================================================== */

    public ArrayList<String> getDiscussionImIn() {
        List<String> discussionImInIDs = getList(Const.COL_NAME_DISCUSSIONS_IM_IN);
        ArrayList<String> res;

        if (discussionImInIDs != null) {
            res = new ArrayList<String>(discussionImInIDs);
        } else {
            res = new ArrayList<>();
        }

        return res;
    }

    /* ========================================================================== */

    public void removeFromDiscussionImIn(ArrayList<String> discussionImInIDs) {
        removeAll(Const.COL_NAME_DISCUSSIONS_IM_IN, discussionImInIDs);
    }

    /* ========================================================================== */

    public void removeFromDiscussionImIn(String discussionImInID) {
        ArrayList<String> al = new ArrayList<>();
        al.add(discussionImInID);
        removeAll(Const.COL_NAME_DISCUSSIONS_IM_IN, al);
    }

    /* ========================================================================== */

    public void initDiscussionImIn() {
        ArrayList<String> al = new ArrayList<>();
        addDiscussionImIn(al);
    }
}