package il.co.reli.createReli;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import il.co.reli.R;
import il.co.reli.dataStructures.Discussion;
import il.co.reli.dataStructures.ReliTag;
import il.co.reli.dataStructures.ReliUser;
import il.co.reli.main.MainActivity;
import il.co.reli.notifications.ReliNotifications;
import il.co.reli.utils.Const;
import il.co.reli.utils.Utils;

public class CreateDiscussionFragment extends Fragment {

    private SeekBar mSeekBar;
    private ReliUser currentUser;
    private NumberPicker npHours, npMinutes;
    private LinearLayout changeTag;
    private EditText topicEditText;

    private ArrayList<ReliTag> allTags = new ArrayList<>(MainActivity.tagsIdToTag.values());
    private ArrayList<String> tagIDsForDiscussion = new ArrayList<>();
    private CharSequence[] tagNamesForDialog;
    private boolean[] checkedTagNamesForDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_discussion, container, false);

        currentUser   = MainActivity.user;

        mSeekBar      = (SeekBar) v.findViewById(R.id.create_discussion_seek_bar_radius);
        npHours       = (NumberPicker) v.findViewById(R.id.create_discussion_numberPicker_hours);
        npMinutes     = (NumberPicker) v.findViewById(R.id.create_discussion_numberPicker_minutes);
        changeTag     = (LinearLayout) v.findViewById(R.id.create_discussion_change_tag);
        topicEditText = (EditText) v.findViewById(R.id.discussion_edt_question);

        Utils.setHideKeyboardCallback(getActivity(), topicEditText);

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
                final Discussion discussionEntry = createDiscussionObject();
                discussionEntry.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Adding the new discussion to the user discussions
                        MainActivity.updateDiscussionsImIn(discussionEntry.getParseID());

                        // Opening the new discussion activity
                        String topic = topicEditText.getText().toString();

                        // Send notifications - Cancelled
                        // handleNotifications(discussionEntry);

                        // Go to the DiscussionActivity itself
                        Intent intent = new Intent(getActivity(), DiscussionActivity.class);
                        intent.putExtra(Const.DISCUSSION_TOPIC, topic);
                        intent.putExtra(Const.DISCUSSION_TABLE_NAME, discussionEntry.getParseID());
                        startActivity(intent);

                        getActivity().finish();
                    }
                });
            }
        });

        return v;

    }

    /* ========================================================================== */

    private Discussion createDiscussionObject() {
        // Get the current topic
        String topic = topicEditText.getText().toString();

        // Get the current location
        Bundle extras = getActivity().getIntent().getExtras();
        double latitude = extras.getDouble(Const.LATITUDE);
        double altitude = extras.getDouble(Const.LONGTITUDE);
        ParseGeoPoint location = new ParseGeoPoint(latitude, altitude);

        // Get the radius
        int radius = mSeekBar.getProgress();

        // Get the logo - Future Version
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
        return new Discussion(topic, location, radius, discussionLogo, creationDate, expirationDate, ownerParseID, tagIDsForDiscussion);
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
                progress = ((int)Math.round(progress / Const.STEP_SIZE)) * Const.STEP_SIZE;
                mSeekBar.setProgress(progress);
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
        String topic = topicEditText.getText().toString();

        boolean isEmpty = false;
        if (topic.equals("")) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog_topic_title)
                    .setMessage(R.string.dialog_topic_message)
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
            isEmpty = true;
        }

        return isEmpty;
    }

    /* ========================================================================== */

    private void setChangeTagsBehavior() {

        // Add tags
        addTagsToScreen();

        // Define the Dialog
        changeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Where we track the index of the selected items
                final ArrayList mSelectedItemsIndices = new ArrayList();
                final boolean[] originalCheckedTagNames = checkedTagNamesForDialog.clone();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.create_discussion_pick_tags);
                builder.setMultiChoiceItems(
                        tagNamesForDialog,                   // Tags
                        checkedTagNamesForDialog,            // items to be selected by default
                        new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int index, boolean isChecked) {
                                        if (isChecked) {
                                            mSelectedItemsIndices.add(index);
                                        } else if (mSelectedItemsIndices.contains(index)) {
                                            mSelectedItemsIndices.remove(Integer.valueOf(index));
                                        }
                                    }
                                });

                // Set the action buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the dialog
                                for (int i = 0; i < mSelectedItemsIndices.size(); i++) {
                                    if (!tagIDsForDiscussion.contains(allTags.get(i).getTagParseID())) {
                                        tagIDsForDiscussion.add(allTags.get(i).getTagParseID());
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                checkedTagNamesForDialog = originalCheckedTagNames;
                            }
                        });

                builder.create().show();

            }
        });

    }

    /* ========================================================================== */

    private void addTagsToScreen() {
        ArrayList<String> idsShouldBeChecked = currentUser.getNotificationsTagsIDs();

        int numOfTags = allTags.size();

        tagNamesForDialog = new CharSequence[numOfTags];
        checkedTagNamesForDialog = new boolean[numOfTags];

        for (int i = 0; i < numOfTags; i++) {
            ReliTag currentReliTag = allTags.get(i);
            String currentReliTagID = currentReliTag.getTagParseID();

            // Set containers that should be displayed in the dialog
            tagNamesForDialog[i] = currentReliTag.getTagName();
            checkedTagNamesForDialog[i] = idsShouldBeChecked.contains(currentReliTagID);

            // Set the container that is sent to the constructor of the new Discussion object
            if (idsShouldBeChecked.contains(currentReliTagID)) {
                tagIDsForDiscussion.add(currentReliTagID);
            }
        }
    }

    /* ========================================================================== */

    private void handleNotifications(Discussion discussionObject) {
        // Make sure that we have instance of the Discussion object
        if (discussionObject == null) {
            return;
        }

        /**************
         * A new discussion was added -
         * we want to send notification according to location and tags
         * ***********/

        // Get queries of devices that should get the notification
        ArrayList<ParseQuery<ParseInstallation>> queries = new ArrayList<>();
        queries.add(ReliNotifications.getQueryAccordingToLocation(discussionObject));
        queries.add(ReliNotifications.getQueryAccordingToTags(discussionObject));

        // Get the list of devices that should be excluded
//        ParseQuery<ParseInstallation> excludedQuery = ReliNotifications.getExcludedUsers(discussionObject);

        // Combine the queries
//        ParseQuery<ParseInstallation> pushQuery = ReliNotifications.combineQueries(queries, excludedQuery);

        // Send push notification
//        ReliNotifications.sendNotifications(pushQuery, MainActivity.user.getFullName() + " " + getString(R.string.notification_new_discussion));
    }
}
