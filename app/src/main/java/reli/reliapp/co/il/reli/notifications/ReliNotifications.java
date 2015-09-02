package reli.reliapp.co.il.reli.notifications;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

        // TODO - check with Shachar
        ParseQuery userQuery = ReliUser.getReliQuery();
        userQuery.whereWithinKilometers(Const.COL_NAME_LOCATION,
                discussionObject.getLocation(),
                ((double)discussionObject.getRadius()) / Const.METERS_IN_KM);

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
//        pushQuery.whereEqualTo(discussionObject.getParseID(), true);
        pushQuery.whereEqualTo("w6GtTZ9ZK3", true);



        return pushQuery;
    }

    /* ========================================================================== */

    public static ParseQuery<ParseInstallation> getExcludedUsers(Discussion discussionObject) {

        // Remove the current user
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatches(Const.INSTALLATION_USER, MainActivity.user.getParseID());

        // TODO - add more complicated conditions

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

    /* ========================================================================== */

    // TODO - delete
    public static void testNotifications() {
        // Store app language and version
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("injuryReports",true);
        installation.saveInBackground();

        // Create our Installation query
        ParseQuery pushQuery2 = ParseInstallation.getQuery();
        pushQuery2.whereEqualTo("injuryReports", true);

        // Send push notification to query
        ParsePush push2 = new ParsePush();
        push2.setQuery(pushQuery2); // Set our Installation query
        push2.setMessage("Willie Hayes injured by own pop fly.");
        push2.sendInBackground();

    }
}