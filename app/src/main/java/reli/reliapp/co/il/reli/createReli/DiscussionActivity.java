package reli.reliapp.co.il.reli.createReli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.custom.CustomActivity;
import reli.reliapp.co.il.reli.dataStructures.Message;
import reli.reliapp.co.il.reli.dataStructures.MessageStatus;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.main.AboutDiscussionFragment;
import reli.reliapp.co.il.reli.main.HomeFragment;
import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.utils.Const;

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
		getActionBar().setTitle(discussionTopic);

		messagesList = new ArrayList<Message>();

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
		}
	}

	/* ========================================================================== */

	private void insertDiscussionToMYRelis() {
		String currentDiscussion = discussionTableName;
        MainActivity.user.addDiscussionImIn(currentDiscussion);
		MainActivity.user.saveEventually();
	}

    /* ========================================================================== */

	private void sendMessage()
	{

		if (!MainActivity.discussionsImIn.contains(discussionTableName)) {
			MainActivity.discussionsImIn.add(discussionTableName);
			insertDiscussionToMYRelis();
		}

        EditText messageTxt = (EditText) findViewById(R.id.txt);
		if (messageTxt.length() == 0) {
            return;
        }

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(messageTxt.getWindowToken(), 0);

		ReliUser user = MainActivity.user;

		String s = messageTxt.getText().toString();
		final Message message = new Message(s, new Date(), MainActivity.user.getParseID(), user.getFullName());
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

						// TODO - change? It means the message was always in status sent and not failed (which me sense,
						// TODO - because If we get here, it was sent.
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

			lbl = (TextView) v.findViewById(R.id.lbl2);
			lbl.setText(c.getMessageContent());

			lbl = (TextView) v.findViewById(R.id.lbl3);
            lbl.setText(c.isSentByUser() ? c.getStatus().statusDescription() : "");

			return v;
		}

	}

    /* ========================================================================== */

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
        switch (item.getItemId()) {
            case R.id.settings_follow:
                Toast.makeText(getApplicationContext(), R.string.message_follow, Toast.LENGTH_SHORT).show();
                // TODO - make sure with Shachar that it's ok
                insertDiscussionToMYRelis();
                return true;

            case R.id.settings_unfollow:
                Toast.makeText(getApplicationContext(), R.string.message_unfollow, Toast.LENGTH_SHORT).show();
                // TODO
                return true;

            case R.id.settings_reli_info:
                Bundle bundle = getIntent().getExtras();
                Fragment f = new AboutDiscussionFragment();
                f.setArguments(bundle);

                // TODO - change DiscussionActivity to use fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.linear_layout_activity_discussion, f)
                        .addToBackStack(null)
                        .commit();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
	}

    /* ========================================================================== */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_discussion, menu);

        return true;
    }
}