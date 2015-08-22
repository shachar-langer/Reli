package reli.reliapp.co.il.reli.createReli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.custom.CustomActivity;
import reli.reliapp.co.il.reli.dataStructures.Message;
import reli.reliapp.co.il.reli.dataStructures.MessageStatus;
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

		discussionTopic = getIntent().getStringExtra(Const.DISCUSSION_TOPIC);
		discussionTableName = getIntent().getStringExtra(Const.DISCUSSION_TABLE_NAME);
		getActionBar().setTitle(discussionTopic);

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
		// Adding the new discussion to the user discussions if needed
		String discussionsImIn = (String) MainActivity.user.get(Const.COL_NAME_DISCUSSIONS_IM_IN);
		String currentDiscussion = discussionTableName;

		if (discussionsImIn.equals("")) {
			MainActivity.user.put(Const.COL_NAME_DISCUSSIONS_IM_IN, currentDiscussion);
		}
		else {
			MainActivity.user.put(Const.COL_NAME_DISCUSSIONS_IM_IN, discussionsImIn + "," + currentDiscussion);
		}

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

		String s = messageTxt.getText().toString();
		final Message message = new Message(s, new Date(), MainActivity.user.getParseID());
		messagesList.add(message);
		chatAdapter.notifyDataSetChanged();
		messageTxt.setText(null);

		ParseObject po = new ParseObject(discussionTableName);
		po.put("sender", MainActivity.user.getParseID());
		po.put("message", s);
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
		if (messagesList.size() == 0)
		{
			// load all messages...
			ArrayList<String> al = new ArrayList<String>();
			al.add(discussionTopic);
			al.add(MainActivity.user.getParseID());
			q.whereContainedIn("sender", al);
		}
		else {
			// load only newly received message..
			if (lastMsgDate != null)
				q.whereGreaterThan("createdAt", lastMsgDate);
			q.whereEqualTo("sender", discussionTopic);
		}
		q.orderByDescending("createdAt");
		q.setLimit(30);
		q.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> li, ParseException e)
			{
				if (li != null && li.size() > 0)
				{
					for (int i = li.size() - 1; i >= 0; i--)
					{
						ParseObject po = li.get(i);
						Message message = new Message(po.getString("message"), po.getCreatedAt(), po.getString("senderID"));
						messagesList.add(message);

						if (lastMsgDate == null || lastMsgDate.before(message.getDate())) {
                            lastMsgDate = message.getDate();
                        }
						chatAdapter.notifyDataSetChanged();
					}
				}
				handler.postDelayed(new Runnable() {

					@Override
					public void run()
					{
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
			if (c.isSentByUser())
				v = getLayoutInflater().inflate(R.layout.message_item_sent, null);
			else
				v = getLayoutInflater().inflate(R.layout.message_item_rcv, null);

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

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}