package reli.reliapp.co.il.reli.createReli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.custom.CustomActivity;
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.dataStructures.Message;
import reli.reliapp.co.il.reli.dataStructures.MessageStatus;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.dataStructures.ReliUserType;
import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.notifications.ReliNotifications;
import reli.reliapp.co.il.reli.utils.Const;
import reli.reliapp.co.il.reli.utils.Utils;

/**
 * The Class Chat is the Activity class that holds main activity_discussion screen. It shows
 * all the conversation messages between two users and also allows the user to
 * send and receive messages.
 */
public class DiscussionActivity extends CustomActivity
{
	private ArrayList<Message> messagesList;
	private ChatAdapter chatAdapter;
	private String discussionTopic;
	private String discussionTableName;
	private Date lastMsgDate;
    private Menu menu;
    private Discussion discussionObject;

    /** Flag to hold if the activity is running or not. */
	private boolean isRunning;

	/** The handler. */
	private static Handler handler;

    /* ========================================================================== */

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discussion);

		discussionTopic = getIntent().getStringExtra(Const.DISCUSSION_TOPIC);
		discussionTableName = getIntent().getStringExtra(Const.DISCUSSION_TABLE_NAME);
        getSupportActionBar().setTitle(discussionTopic);

        saveDiscussionObject();

        messagesList = new ArrayList<>();

		ListView list = (ListView) findViewById(R.id.list);
		chatAdapter = new ChatAdapter();
		list.setAdapter(chatAdapter);
		list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		list.setStackFromBottom(true);

        // Custom new message area
        EditText messageTxt = (EditText) findViewById(R.id.txt);
		messageTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        // Custom send button
		setTouchNClick(R.id.btnSend);

		handler = new Handler();
	}

    /* ========================================================================== */

	@Override
	protected void onResume()
	{
		super.onResume();
		isRunning = true;
		loadConversationList();
	}

    /* ========================================================================== */

	@Override
	protected void onPause()
	{
		super.onPause();
		isRunning = false;
	}

    /* ========================================================================== */

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		if (v.getId() == R.id.btnSend)
		{
			sendMessage();
            // Update discussions I'm in
            MainActivity.updateDiscussionsImIn(discussionTableName);
            // Send notifications
            handleNotifications();
		}
	}

    /* ========================================================================== */

	private void sendMessage()
	{
        EditText messageTxt = (EditText) findViewById(R.id.txt);
		if (messageTxt.length() == 0) {
            return;
        }

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(messageTxt.getWindowToken(), 0);

		ReliUser user = MainActivity.user;

		String s = messageTxt.getText().toString();
		final Message message = new Message(s, new Date(), user.getParseID(), user.getFullName());
		messagesList.add(message);
		chatAdapter.notifyDataSetChanged();
		messageTxt.setText(null);

		ParseObject po = new ParseObject(discussionTableName);
		po.put(Const.COL_MESSAGE_SENDER_ID, user.getParseID());
		po.put(Const.COL_MESSAGE_SENDER_NAME, user.getFullName());
		po.put(Const.COL_MESSAGE_CONTENT, s);
		po.saveEventually(new SaveCallback() {
			@Override
			public void done(ParseException e)
			{
				message.setStatus((e == null) ? MessageStatus.STATUS_SENT : MessageStatus.STATUS_FAILED);

				chatAdapter.notifyDataSetChanged();
			}
		});
	}

	/* ========================================================================== */

    private void handleNotifications() {
        // Make sure that we have instance of the Discussion object
        if (discussionObject == null) {
            return;
        }

        /**************
         * A new message was added to an existing discussion -
         * we want to send notification according to discussionsImIn
         * ***********/

        // Get queries of devices that should get the notification
        ParseQuery<ParseInstallation> includedQuery = ReliNotifications.getQueryAccordingToDiscussion(discussionObject);

        // Get the list of devices that should be excluded
        ParseQuery<ParseInstallation> excludedQuery = ReliNotifications.getExcludedUsers(discussionObject);

        // Combine the queries
        ParseQuery<ParseInstallation> pushQuery = ReliNotifications.combineQueries(includedQuery, excludedQuery);

        // Send push notification
        ReliNotifications.sendNotifications(pushQuery, MainActivity.user.getFullName() + getString(R.string.notification_new_message_part_1) + discussionObject.getDiscussionName() + getString(R.string.notification_new_message_part_2));
    }

    /* ========================================================================== */

    /**
	 * Load the conversation list from Parse server and save the date of last
	 * message that will be used to load only recent new messages
	 */
	private void loadConversationList()
	{
		ParseQuery<ParseObject> q = ParseQuery.getQuery(discussionTableName);
		if (messagesList.size() != 0)
		{
//			// load all messages...
//			ArrayList<String> al = new ArrayList<String>();
//			al.add(discussionTopic);
//			al.add(MainActivity.user.getParseID());
//			q.whereContainedIn("sender", al);
//		}
//		else {
			// load only newly received message..
			if (lastMsgDate != null)
				// Load only new messages, that weren't send by me
				q.whereGreaterThan(Const.COL_MESSAGE_CREATED_AT, lastMsgDate);
				q.whereNotEqualTo(Const.COL_MESSAGE_SENDER_ID, MainActivity.user.getParseID());
		}
		q.orderByDescending(Const.COL_MESSAGE_CREATED_AT);
		q.setLimit(100);
		q.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> li, ParseException e) {
				if (li != null && li.size() > 0) {
					for (int i = li.size() - 1; i >= 0; i--) {
						ParseObject po = li.get(i);

						Message message = new Message(po.getString(
								Const.COL_MESSAGE_CONTENT),
								po.getCreatedAt(),
								po.getString(Const.COL_MESSAGE_SENDER_ID),
								po.getString(Const.COL_MESSAGE_SENDER_NAME));

						message.setStatus(MessageStatus.STATUS_SENT);
						messagesList.add(message);

						if (lastMsgDate == null || lastMsgDate.before(message.getDate())) {
							lastMsgDate = message.getDate();
						}
						chatAdapter.notifyDataSetChanged();
					}
				}
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (isRunning)
							loadConversationList();
					}
				}, 1000);
			}
		});

	}

    /* ========================================================================== */

	/**
	 * The Class ChatAdapter is the adapter class for Chat ListView. This
	 * adapter shows the Sent or Receieved Chat message in each list item.
	 */
	private class ChatAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return messagesList.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Message getItem(int arg0)
		{
			return messagesList.get(arg0);
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int arg0)
		{
			return arg0;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int pos, View v, ViewGroup arg2)
		{
			Message c = getItem(pos);
			
			if (c.isSentByUser()) {
				v = getLayoutInflater().inflate(R.layout.message_item_sent, null);
			}
			else {
				v = getLayoutInflater().inflate(R.layout.message_item_rcv, null);
				((TextView)v.findViewById(R.id.senderName)).setText(c.getSenderName());
			}

			TextView lbl = (TextView) v.findViewById(R.id.lbl1);
			lbl.setText(DateUtils.getRelativeDateTimeString(DiscussionActivity.this, c
					.getDate().getTime(), DateUtils.SECOND_IN_MILLIS,
					DateUtils.DAY_IN_MILLIS, 0));

            // Fill in the message
			lbl = (TextView) v.findViewById(R.id.lbl2);
			lbl.setText(c.getMessageContent());

            // Fill in the status of the message
			lbl = (TextView) v.findViewById(R.id.lbl3);
            lbl.setText(c.isSentByUser() ? c.getStatus().statusDescription() : "");

            // Fill in the image
            ImageView iv = (ImageView) v.findViewById(R.id.senderAvatar);
            Utils.setAvatar(iv, c.getSenderID());

			return v;
		}

	}

    /* ========================================================================== */

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
        switch (item.getItemId()) {
            case R.id.settings_follow:
                return followReli();

            case R.id.settings_unfollow:
                return unfollowReli();

            case R.id.settings_reli_info:
                return displayDiscussionInfo();

            default:
                return super.onOptionsItemSelected(item);
        }
	}

    /* ========================================================================== */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_discussion, menu);
        this.menu = menu;
        updateMenuTitles();

        return true;
    }

    /* ========================================================================== */

    /**
     * Make sure that either "follow" or "unfollow" will be displayed in the menu
     */
    private void updateMenuTitles() {
        boolean isFollow = MainActivity.discussionsImIn.contains(discussionTableName);
        menu.findItem(R.id.settings_follow).setVisible(!isFollow);
        menu.findItem(R.id.settings_unfollow).setVisible(isFollow);
    }

    /* ========================================================================== */

    private void saveDiscussionObject() {
        if (discussionTableName != null) {
            ParseQuery<Discussion> discussionQuery = Discussion.getDiscussionQuery();
            discussionQuery.getInBackground(discussionTableName, new GetCallback<Discussion>() {
                public void done(Discussion returnedDiscussion, ParseException e) {
                    if (e == null) {
                        discussionObject = returnedDiscussion;
                    }
                }
            });
        }
    }

    /* ========================================================================== */

    @Override
    public void onBackPressed() {
        finish();
    }

    /* ========================================================================== */

    private boolean displayDiscussionInfo() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(DiscussionActivity.this);
        LayoutInflater inflater = DiscussionActivity.this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.about_discussion, null);

        final ProgressDialog dia = ProgressDialog.show(DiscussionActivity.this, null, getString(R.string.alert_loading));
        dia.setCanceledOnTouchOutside(false);
        ParseQuery<Discussion> discussionQuery = Discussion.getDiscussionQuery();
        discussionQuery.getInBackground(discussionTableName, new GetCallback<Discussion>() {
            public void done(Discussion currentDiscussion, ParseException e) {
                if ((e == null) && (currentDiscussion != null)) {
                    fetchDiscussionInformation(v, currentDiscussion);
                }

                dia.dismiss();
            }
        });

        builder.setView(v)
                .setTitle(discussionTopic + " (" + messagesList.size() + " messages)")
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();

        return true;
    }

    /* ========================================================================== */

    private void fetchDiscussionInformation(View v, Discussion currentDiscussion) {
        // Get the creation date of the current discussion
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());

        // Get the expiration and creation date of the current discussion
        TextView creationDate   = (TextView) v.findViewById(R.id.about_discussion_value_creation);
        TextView expirationDate = (TextView) v.findViewById(R.id.about_discussion_value_expiration);
        TextView lastUpdateDate = (TextView) v.findViewById(R.id.about_discussion_value_update);
        creationDate.setText(sdf.format(currentDiscussion.getCreationDate()));
        expirationDate.setText(sdf.format(currentDiscussion.getExpirationDate()));
		if (lastMsgDate != null) {
			lastUpdateDate.setText(sdf.format(lastMsgDate));
		}

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

        // Write the tags on screen
        if (tagsListAsString.equals("")) {
            tagsListAsString = getString(R.string.no_tags);
        }
        else {
            tagsListAsString = tagsListAsString.substring(0, tagsListAsString.length() - 2);
        }
        tags.setText(tagsListAsString);
    }

    /* ========================================================================== */

    private boolean followReli() {
        Toast.makeText(getApplicationContext(), R.string.message_follow, Toast.LENGTH_SHORT).show();
        MainActivity.updateDiscussionsImIn(discussionTableName);
        updateMenuTitles();

        return true;
    }

    /* ========================================================================== */

    private boolean unfollowReli() {
        Toast.makeText(getApplicationContext(), R.string.message_unfollow, Toast.LENGTH_SHORT).show();
        MainActivity.removeDiscussionFromMyRelis(discussionTableName);
        updateMenuTitles();

        return true;
    }

    /* ========================================================================== */


}