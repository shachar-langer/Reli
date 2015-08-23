package reli.reliapp.co.il.reli.createReli;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.dataStructures.ReliTag;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.utils.Const;

public class CreateDiscussionFragment extends Fragment {

    private View v;
    private SeekBar mSeekBar;
    private ReliUser currentUser;
    private NumberPicker npHours, npMinutes;
    private LinearLayout changeTag;
    private ArrayList<String> tagsNames = new ArrayList<String>();
    private ArrayList<ReliTag> tagsAsObjects;

    CharSequence[] items;
    boolean[] checkedItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_create_discussion, container, false);

        currentUser = MainActivity.user;
        mSeekBar  = (SeekBar) v.findViewById(R.id.create_discussion_seek_bar_radius);
        npHours   = (NumberPicker) v.findViewById(R.id.create_discussion_numberPicker_hours);
        npMinutes = (NumberPicker) v.findViewById(R.id.create_discussion_numberPicker_minutes);
        changeTag = (LinearLayout) v.findViewById(R.id.create_discussion_change_tag);

        addSeekBarToScreen(v);
        setTimePickers();
        setChangeTagsBehavior();

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
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
            isEmpty = true;
        }

        return isEmpty;
    }

    /* ========================================================================== */

    private void setChangeTagsBehavior() {

        addTagsToScreen();
        changeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO - i was here
                String names[] ={"A","B","C","D","A","B","C","D","A","B","C","D","A","B","C","D","A","B","C","D"};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getLayoutInflater(new Bundle());
                View convertView = (View) inflater.inflate(R.layout.custom, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle("List");
                ListView lv = (ListView) convertView.findViewById(R.id.listView1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,names);
                lv.setAdapter(adapter);
                alertDialog.show();





/*

                final ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.create_discussion_pick_tags);

                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected


//                builder.setMultiChoiceItems(R.array.toppings, null,
                builder.setMultiChoiceItems(items,
                        checkedItems,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which,
                                                        boolean isChecked) {
                                        if (isChecked) {
                                            // If the user checked the item, add it to the selected items
                                            mSelectedItems.add(which);
                                        } else if (mSelectedItems.contains(which)) {
                                            // Else, if the item is already in the array, remove it
                                            mSelectedItems.remove(Integer.valueOf(which));
                                        }
                                    }
                                });

                // Set the action buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the dialog
                                // TODO
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }
                        });

                builder.create().show();
                */
            }
        });

    }

    private void addTagsToScreen() {

//        ArrayList<ReliTag> reliTags = new ArrayList<ReliTag>(MainActivity.tagsIdToTag.values());
//        ArrayList<String> shouldBeChecked = currentUser.getNotificationsTagsIDs();
//
//        items = new String[reliTags.size() * 2];
//        checkedItems = new boolean[reliTags.size() * 2];
//
//        for (int i = 0; i < reliTags.size(); i=i+2) {
//            ReliTag currentReliTag = reliTags.get(i);
//            items[i] = currentReliTag.getTagName();
//            items[i+1] = currentReliTag.getTagName();
//            checkedItems[i] = shouldBeChecked.contains(currentReliTag.getTagParseID());
//            checkedItems[i+1] = shouldBeChecked.contains(currentReliTag.getTagParseID());
//        }


        ArrayList<ReliTag> reliTags = new ArrayList<ReliTag>(MainActivity.tagsIdToTag.values());
        ArrayList<String> shouldBeChecked = currentUser.getNotificationsTagsIDs();

        items = new String[reliTags.size()];
        checkedItems = new boolean[reliTags.size()];

        for (int i = 0; i < reliTags.size(); i++) {
            ReliTag currentReliTag = reliTags.get(i);
            items[i] = currentReliTag.getTagName();
            checkedItems[i] = shouldBeChecked.contains(currentReliTag.getTagParseID());
        }
    }
}