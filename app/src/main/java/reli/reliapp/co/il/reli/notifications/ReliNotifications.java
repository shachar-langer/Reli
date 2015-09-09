package reli.reliapp.co.il.reli.notifications;

import com.parse.ParseInstallation;
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

    public static ParseQuery<ParseInstallation> getQueryAccordingToDiscussion(Discussion discussionObject) {
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo(discussionObject.getParseID(), true);

        return pushQuery;
    }

    /* ========================================================================== */

    public static ParseQuery<ParseInstallation> getExcludedUsers(Discussion discussionObject) {

        // Remove the current user
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatches(Const.INSTALLATION_USER, MainActivity.user.getParseID());

        // In Future Versions - add more complicated conditions (filter according to user settings)

        return pushQuery;
    }


    /* ========================================================================== */

    public static ParseQuery<ParseInstallation> combineQueries(ArrayList<ParseQuery<ParseInstallation>> queries, ParseQuery<ParseInstallation> queryToExclude) {
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.or(queries);

        // TODO - use queryToExclude

        return pushQuery;
    }

    /* ========================================================================== */

    public static ParseQuery<ParseInstallation> combineQueries(ParseQuery<ParseInstallation> query, ParseQuery<ParseInstallation> queryToExclude) {
        ArrayList<ParseQuery<ParseInstallation>> queries = new ArrayList<>();
        queries.add(query);

        return combineQueries(queries, queryToExclude);
    }

    /* ========================================================================== */

    public static void sendNotifications(ParseQuery pushQuery, String title) {
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setExpirationTimeInterval(Const.NOTIFICATION_TIME_INTERVAL_IN_SECONDS);
        push.setMessage(title);
        push.sendInBackground();
    }
}