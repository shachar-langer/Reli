package reli.reliapp.co.il.reli.main;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.Discussion;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutDiscussionFragment extends Fragment {


    public AboutDiscussionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_discussion, container, false);

        // TODO - get the current discussion
        Discussion currentDiscussion = new Discussion();

        TextView creationDate = (TextView) v.findViewById(R.id.about_discussion_value_creation);
        creationDate.setText(currentDiscussion.getCreationDate().toString());

        TextView expirationDate = (TextView) v.findViewById(R.id.about_discussion_value_expiration);
        expirationDate.setText(currentDiscussion.getExpirationDate().toString());

        TextView owner = (TextView) v.findViewById(R.id.about_discussion_value_owner);
        // TODO - change to a query that returns the owner according to its parseID
        expirationDate.setText(currentDiscussion.getOwnerParseID());

        TextView radius = (TextView) v.findViewById(R.id.about_discussion_value_radius);
        radius.setText(currentDiscussion.getRadius());

        // TODO
//        TextView tags = (TextView) v.findViewById(R.id.about_discussion_value_tags);
//        tags.setText(currentDiscussion.tag);

        // Change the header
        getActivity().getActionBar().setTitle(currentDiscussion.getDiscussionName());

        // Change to the icon of the discussion
//        try {
//            getActivity().getActionBar().setIcon(currentDiscussion.getDiscussionLogo());
//        } catch (Exception e) {
//
//        }


        return v;
    }


}
