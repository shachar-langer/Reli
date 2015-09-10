package reli.reliapp.co.il.reli.notifications;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.utils.Const;

public class ReliNotifications {

    /* ========================================================================== */

    // Find users near a given location
    public static ParseQuery<ParseInstallation> getQueryAccordingToLocation(Discussion discussionObject) {

        ParseQuery userQuery = ReliUser.getReliQuery();
        userQuery.whereWithinKilometers(Const.COL_NAME_LOCATION,
                discussionObject.getLocation(),
                (discussionObject.getRadius()) / Const.METERS_IN_KM);

        // Find devices associated users in the given location
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatchesQuery(Const.INSTALLATION_USER, userQuery);

        return pushQuery;
    }

    /* ========================================================================== */

    public static ParseQuery<ParseInstallation> getQueryAccordingToTags(Discussion discussionObject) {

        // Find all relevant tags
        List<ParseQuery<ParseInstallation>> tagQueries = new ArrayList<>();
        ArrayList<String> tagIDs = discussionObject.getTagIDsForDiscussion();
        for (String tagID : tagIDs) {
            tagQueries.add(ParseInstallation.getQuery().whereEqualTo(tagID, true));
        }

        ParseQuery pushQuery = ParseInstallation.getQuery();
        if (tagQueries.size() > 0) {
            pushQuery.or(tagQueries);
        }

        return pushQuery;
    }

    /* ========================================================================== */

    public static ParseQuery<ParseInstallation> getQueryAccordingToDiscussion(ParseQuery pushQuery, String discussionName) {
        pushQuery.whereEqualTo(discussionName, true);

        return pushQuery;
    }

    /* ========================================================================== */

    public static ParseQuery<ParseInstallation> getQueryExcludedCurrentUser(ParseQuery pushQuery) {

        // Remove the current user
        String currentUserInstallationID = ParseInstallation.getCurrentInstallation().getInstallationId();
        pushQuery.whereNotEqualTo(Const.INSTALLATION_ID, currentUserInstallationID);

        // In Future Versions - add more complicated conditions (filter according to user settings)

        return pushQuery;
    }

    /* ========================================================================== */

    public static void sendNotifications(final ParseQuery pushQuery, String title) {
        ParsePush.sendMessageInBackground(title, pushQuery);
    }
}