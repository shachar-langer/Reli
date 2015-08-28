package reli.reliapp.co.il.reli.main;


import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.utils.Const;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutDiscussionFragment extends Fragment {

    /* ========================================================================== */

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    /* ========================================================================== */

    public AboutDiscussionFragment() {
        // Required empty public constructor
    }

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_about_discussion, container, false);

        String discussionParseID = getArguments().getString(Const.DISCUSSION_TABLE_NAME);
        if (discussionParseID != null) {
            ParseQuery<Discussion> discussionQuery = Discussion.getDiscussionQuery();
            discussionQuery.getInBackground(discussionParseID, new GetCallback<Discussion>() {
                public void done(Discussion currentDiscussion, ParseException e) {
                    if (e == null) {
                        updateDataOnScreen(v, currentDiscussion);
                    }
                }
            });
        }

        return v;
    }

    /* ========================================================================== */

    private void updateDataOnScreen(View v, Discussion currentDiscussion) {
        // Get the creation date of the current discussion
        TextView creationDate = (TextView) v.findViewById(R.id.about_discussion_value_creation);
        creationDate.setText(sdf.format(currentDiscussion.getCreationDate()));

        // Get the expiration date of the current discussion
        TextView expirationDate = (TextView) v.findViewById(R.id.about_discussion_value_expiration);
        expirationDate.setText(sdf.format(currentDiscussion.getExpirationDate()));

        // Get the owner of the current discussion
        final TextView owner = (TextView) v.findViewById(R.id.about_discussion_value_owner);
        String ownerID = currentDiscussion.getOwnerParseID();
        if (ownerID != null) {
            ParseQuery<ReliUser> userQuery = ReliUser.getReliQuery();
            userQuery.getInBackground(ownerID, new GetCallback<ReliUser>() {
                public void done(ReliUser reliUser, ParseException e) {
                    if (e == null) {
                        owner.setText(reliUser.getFullName());
                    } else {
                        owner.setText(Const.UNKNOWN_USER);
                    }
                }
            });
        } else {
            owner.setText(Const.UNKNOWN_USER);
        }

        // Get the radius of the current discussion
        TextView radius = (TextView) v.findViewById(R.id.about_discussion_value_radius);
        radius.setText(Integer.toString(currentDiscussion.getRadius()) + " meters");

        // TODO - check why it displays wrong tags
        // Get the tags of the current discussion
        TextView tags = (TextView) v.findViewById(R.id.about_discussion_value_tags);
        ArrayList<String> tagsIDs = currentDiscussion.getTagIDsForDiscussion();
        String tagsListAsString = "";
        String currentTagName;
        for (String tagID : tagsIDs) {
            if (MainActivity.tagsIdToTag.containsKey(tagID)) {
                currentTagName = MainActivity.tagsIdToTag.get(tagID).getTagName();
                if (currentTagName != null) {
                    tagsListAsString += currentTagName + ", ";
                }
            }
        }

        if (tagsListAsString.equals("")) {
            tagsListAsString = getString(R.string.no_tags);
        }
        else {
            tagsListAsString = tagsListAsString.substring(0, tagsListAsString.length() - 2);
        }
        tags.setText(tagsListAsString);

        // Change the header
        getActivity().getActionBar().setTitle(currentDiscussion.getDiscussionName());

        // Change to the icon of the discussion
        // TODO
//        try {
//            getActivity().getActionBar().setIcon(currentDiscussion.getDiscussionLogo());
//        } catch (Exception e) {
//
//        }
    }
}