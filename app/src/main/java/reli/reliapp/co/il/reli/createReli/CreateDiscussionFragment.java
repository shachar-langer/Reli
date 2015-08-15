package reli.reliapp.co.il.reli.createReli;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Date;

import bolts.Task;
import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.utils.Const;

public class CreateDiscussionFragment extends Fragment {

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_create_discussion, container, false);

        Button createDiscussionBtn = (Button) v.findViewById(R.id.discussion_btn_create);

        createDiscussionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText topicEditText = (EditText) getActivity().findViewById(R.id.discussion_edt_question);
                String topic = topicEditText.getText().toString();
                Toast.makeText(getActivity().getApplicationContext(), topic, Toast.LENGTH_SHORT).show();

                // Enter a new discussion entry in the Discussion Table
                Bundle extras = getActivity().getIntent().getExtras();
                double latitude = extras.getDouble(Const.LATITUDE);
                double altitude = extras.getDouble(Const.ALTITUDE);
                ParseGeoPoint location = new ParseGeoPoint(latitude, altitude);
                int radius = 27;
                Bitmap discussionLogo = null;
                Date creationDate = new Date();
                Date expirationDate = new Date();
                String ownerParseID = MainActivity.user.getParseID();
                final Discussion DiscussionEntry = new Discussion(topic, location, radius, discussionLogo,
                        creationDate, expirationDate, ownerParseID);
                DiscussionEntry.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Creating a new Table for the new Discussion
                        ParseObject discussionTable = ParseObject.create(DiscussionEntry.getParseID());
                        discussionTable.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                EditText topicEditText = (EditText) getActivity().findViewById(R.id.discussion_edt_question);
                                String topic = topicEditText.getText().toString();

                                Intent intent = new Intent(getActivity(), DiscussionActivity.class);
                                intent.putExtra(Const.BUDDY_NAME, topic);
                                intent.putExtra(Const.DISCUSSION_TABLE_NAME, DiscussionEntry.getParseID());
                                startActivity(intent);
                            }
                        });


                    }
                });




            }
        });

        return v;

    }

}
