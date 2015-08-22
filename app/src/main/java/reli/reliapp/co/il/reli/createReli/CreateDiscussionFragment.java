package reli.reliapp.co.il.reli.createReli;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.utils.Const;

public class CreateDiscussionFragment extends Fragment {

    private View v;
    private SeekBar mSeekBar;
    private ReliUser currentUser;
    private NumberPicker npHours, npMinutes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_create_discussion, container, false);

        currentUser = MainActivity.user;
        mSeekBar  = (SeekBar) v.findViewById(R.id.create_discussion_seek_bar_radius);
        npHours   = (NumberPicker) v.findViewById(R.id.create_discussion_numberPicker_hours);
        npMinutes = (NumberPicker) v.findViewById(R.id.create_discussion_numberPicker_minutes);

        addSeekBarToScreen(v);
        setTimePickers();

        // Set the behavior "Let's Reli" button
        Button createDiscussionBtn = (Button) v.findViewById(R.id.discussion_btn_create);
        createDiscussionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Handle empty topic
                boolean isEmpty = checkAndHandleEmptyTitle();
                if (isEmpty) {
                    return;
                }

                // Enter a new discussion entry in the Discussion Table
                final Discussion DiscussionEntry = createDiscussionObject();
                DiscussionEntry.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
//                        // Creating a new Table for the new Discussion
//                        ParseObject discussionTable = ParseObject.create(DiscussionEntry.getParseID());
//                        discussionTable.saveEventually(new SaveCallback() {
//                            @Override
//                            public void done(ParseException e) {
//
//
//                            }
//                        });
                        // Adding the new discussion to the user discussions
                        String discussionsImIn = (String) MainActivity.user.get(Const.COL_NAME_DISCUSSIONS_IM_IN);
                        String currentDiscussion = DiscussionEntry.getParseID();
                        if (discussionsImIn.isEmpty()) {
                            MainActivity.user.put(Const.COL_NAME_DISCUSSIONS_IM_IN, currentDiscussion);
                        }
                        else {
                            MainActivity.user.put(Const.COL_NAME_DISCUSSIONS_IM_IN, discussionsImIn + "," + currentDiscussion);
                        }
                        MainActivity.discussionsImIn.add(currentDiscussion);

                        MainActivity.user.saveEventually();

                        // Opening the new discussion activity
                        EditText topicEditText = (EditText) getActivity().findViewById(R.id.discussion_edt_question);
                        String topic = topicEditText.getText().toString();

                        Intent intent = new Intent(getActivity(), DiscussionActivity.class);
                        intent.putExtra(Const.DISCUSSION_TOPIC, topic);
                        intent.putExtra(Const.DISCUSSION_TABLE_NAME, DiscussionEntry.getParseID());
                        startActivity(intent);
                    }
                });
            }
        });

        return v;

    }

    /* ========================================================================== */

    private Discussion createDiscussionObject() {
        // Get the current topic
        EditText topicEditText = (EditText) getActivity().findViewById(R.id.discussion_edt_question);
        String topic = topicEditText.getText().toString();

        // Get the current location
        Bundle extras = getActivity().getIntent().getExtras();
        double latitude = extras.getDouble(Const.LATITUDE);
        double altitude = extras.getDouble(Const.ALTITUDE);
        ParseGeoPoint location = new ParseGeoPoint(latitude, altitude);

        // Get the radius
        int radius = mSeekBar.getProgress();

        // Get the logo
        Bitmap discussionLogo = null;

        // Calculate the creation date
        Date creationDate = new Date();

        // Calculate the expiration date
        Calendar c = Calendar.getInstance();
        c.setTime(creationDate);
        c.add(Calendar.HOUR, npHours.getValue());
        c.add(Calendar.MINUTE, npMinutes.getValue());
        Date expirationDate = c.getTime();

        // Get the owner
        String ownerParseID = MainActivity.user.getParseID();

        // Create the discussion
        return new Discussion(topic, location, radius, discussionLogo, creationDate, expirationDate, ownerParseID);
    }

    /* ========================================================================== */

    private void addSeekBarToScreen(View v) {
        int progress;
        final View finalView = v;

        // Set the initial progress
        try {
            progress = currentUser.getRelisRadius();
        } catch (Exception e) {
            progress = Const.DEFAULT_RADIUS_FOR_RELIS;
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView mRadius = (TextView) finalView.findViewById(R.id.create_discussion_current_radius_value);
                mRadius.setText(String.valueOf(progress) + " meters");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mSeekBar.setProgress(progress);
    }

    /* ========================================================================== */

    private void setTimePickers() {
        int currentExpirationInMinutes = currentUser.getRelisExpirationInMinutes();
        int hours = currentExpirationInMinutes / Const.MINUTES_IN_HOUR;
        int minutes = currentExpirationInMinutes - hours * Const.MINUTES_IN_HOUR;

        // Set the hours picker
        npHours.setMinValue(Const.MINIMUM_TIME);
        npHours.setMaxValue(Const.MAX_HOURS);
        npHours.setValue(hours);

        // Set the minutes picker
        npMinutes.setMinValue(Const.MINIMUM_TIME);
        npMinutes.setMaxValue(Const.MAX_MINUTES);
        npMinutes.setValue(minutes);
    }

    /* ========================================================================== */

    private boolean checkAndHandleEmptyTitle() {
        EditText topicEditText = (EditText) getActivity().findViewById(R.id.discussion_edt_question);
        String topic = topicEditText.getText().toString();

        boolean isEmpty = false;
        if (topic.equals("")) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message)
                    .setPositiveButton(R.string.ok_button, null)
                    .create()
                    .show();
            isEmpty = true;
        }

        return isEmpty;
    }
}