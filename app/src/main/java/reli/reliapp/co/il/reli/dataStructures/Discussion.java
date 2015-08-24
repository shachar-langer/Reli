package reli.reliapp.co.il.reli.dataStructures;

import android.graphics.Bitmap;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;

import reli.reliapp.co.il.reli.utils.Const;

@ParseClassName("Discussions")
public class Discussion extends AbstractDiscussion {

    private ArrayList<Message> messagesList;

    public Discussion() {
        super();
    }

    public Discussion(String discussionName, ParseGeoPoint location, int radius,
                      Bitmap discussionLogo, Date creationDate, Date expirationDate,
                      String ownerParseID, ArrayList<String> tagIDsForDiscussion) {
        super(discussionName, location, radius, discussionLogo, creationDate, expirationDate, ownerParseID, tagIDsForDiscussion);
    }

    public void setDiscussionName(String discussionName){
        put(Const.COL_DISCUSSION_NAME, discussionName);
    }

    public String getDiscussionName() {
        return getString(Const.COL_DISCUSSION_NAME);
    }

    public void setLocation(ParseGeoPoint location) {
        put(Const.COL_DISCUSSION_LOCATION, location);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(Const.COL_DISCUSSION_LOCATION);
    }

    public void setRadius(int radius) {
        put(Const.COL_DISCUSSION_RADIUS, radius);
    }

    public int getRadius() {
        return getInt(Const.COL_DISCUSSION_RADIUS);
    }

    public void setDiscussionLogo(Bitmap discussionLogo) {
        // TODO
    }

    public Bitmap getDiscussionLogo() {
        // TODO
        return null;
    }

    // No setter for the creation date
    public Date getCreationDate() {
        return getDate(Const.COL_DISCUSSION_CREATION_DATE);
    }

    /* ========================================================================== */

    public void setExpirationDate(Date expirationDate) {
        put(Const.COL_DISCUSSION_EXPIRATION_DATE, expirationDate);
    }

    /* ========================================================================== */

    public Date getExpirationDate() {
        return getDate(Const.COL_DISCUSSION_EXPIRATION_DATE);
    }

    /* ========================================================================== */

    public void setOwnerParseID(String ownerParseID) {
        put(Const.COL_DISCUSSION_OWNER_PARSE_ID, ownerParseID);
    }

    /* ========================================================================== */

    public String getOwnerParseID() {
        return getString(Const.COL_DISCUSSION_OWNER_PARSE_ID);
    }

    /* ========================================================================== */

    public String getParseID() {
        return getObjectId();
    }

    /* ========================================================================== */

    public static ParseQuery<Discussion> getDiscussionQuery() {
        return ParseQuery.getQuery(Discussion.class);
    }
}

