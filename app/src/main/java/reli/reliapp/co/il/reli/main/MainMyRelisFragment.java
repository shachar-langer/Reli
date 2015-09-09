package reli.reliapp.co.il.reli.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.createReli.CreateReliActivity;
import reli.reliapp.co.il.reli.createReli.DiscussionActivity;
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.utils.Const;
import reli.reliapp.co.il.reli.utils.Utils;

//import android.app.Fragment;

public class MainMyRelisFragment extends Fragment {

    private ArrayList<Discussion> chatsList;
    private View v;
    private Context ctx;

    public static int MILLIS_PER_DAY = 86400000;

    /* ========================================================================== */

    public MainMyRelisFragment() {
        // Required empty public constructor
    }

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_main_my_relis, container, false);

        ctx = getActivity().getApplicationContext();
        Button addDiscussionBtn = (Button) v.findViewById(R.id.add_discussion_btn_my_relis);
        addDiscussionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, "You clicked me!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), CreateReliActivity.class);

                Location location = ((MainActivity) getActivity()).getLocation();
                if (location == null) {
                    Toast.makeText(getActivity().getApplicationContext(), "No location found. Can not open a new Reli", Toast.LENGTH_SHORT).show();
                }
                else {
                    intent.putExtra(Const.LATITUDE, location.getLatitude());
                    intent.putExtra(Const.LONGTITUDE, location.getLongitude());
                    startActivity(intent);
//                    getActivity().finish();
                }
            }
        });

        return v;
    }

    /* ========================================================================== */

    @Override
    public void onResume()
    {
        super.onResume();
        loadUserList();
    }

    /* ========================================================================== */

    private void loadUserList() {

        final ReliUser user = MainActivity.user;
        final ProgressDialog dia = ProgressDialog.show(getActivity(), null, getString(R.string.alert_loading));

        ArrayList<String> discussionsUserIsIn = user.getDiscussionImIn();
        final TextView tvNoUsers = (TextView) v.findViewById(R.id.no_my_relis);
        tvNoUsers.setVisibility(View.GONE);

        // If there are no discussions I'm in, the fragment should be empty
        if (discussionsUserIsIn == null) {
            user.initDiscussionImIn();
            user.saveEventually();
            dia.dismiss();
//            Toast.makeText(getActivity().getApplicationContext(), "None", Toast.LENGTH_SHORT).show();
            tvNoUsers.setVisibility(View.VISIBLE);
            return;
        }

        if (discussionsUserIsIn.size() == 0) {
            dia.dismiss();
//            Toast.makeText(getActivity().getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
            tvNoUsers.setVisibility(View.VISIBLE);
            return;
        }

        ParseQuery<Discussion> discussionQuery = Discussion.getDiscussionQuery();
        discussionQuery.whereContainedIn("objectId", discussionsUserIsIn);

        // Initialize the static variable
        for (String discussion : discussionsUserIsIn) {
            MainActivity.discussionsImIn.add(discussion);
        }

        // TODO (Shachar) - change 10 to the users radius choice
        discussionQuery.whereWithinKilometers(Const.COL_DISCUSSION_LOCATION, user.getLocation(), 1000000)
                .findInBackground(new FindCallback<Discussion>() {

                    @Override
                    public void done(List<Discussion> li, ParseException e) {

                        if (li != null) {
                            if (li.size() == 0) {
                                tvNoUsers.setVisibility(View.VISIBLE);
//                                Toast.makeText(ctx, R.string.msg_no_relis_found, Toast.LENGTH_SHORT).show();
                            }

                            chatsList = new ArrayList<Discussion>(li);
                            ListView list = (ListView) v.findViewById(R.id.list_my_relis);
                            list.setAdapter(new DiscussionAdapter());
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

//                                    // Adding the new discussion to the user discussions if needed
//                                    String discussionsImIn = (String) MainActivity.user.get(Const.COL_NAME_DISCUSSIONS_IM_IN);
//                                    String[] listOfDiscussion = discussionsImIn.split(",");
//                                    String newDiscussion = chatsList.get(pos).getParseID();
//                                    boolean isNewDiscussion = true;
//                                    for (int i = 0; i < listOfDiscussion.length; i++) {
//                                        if (listOfDiscussion[i].equals(newDiscussion)) {
//                                            Toast.makeText(getActivity().getApplicationContext(), "I'm toasted", Toast.LENGTH_SHORT).show();
//                                            isNewDiscussion = false;
//                                        }
//                                    }
//                                    if (isNewDiscussion) {
//                                        Toast.makeText(getActivity().getApplicationContext(), "I'm not toasted", Toast.LENGTH_SHORT).show();
//                                        MainActivity.user.put(Const.COL_NAME_DISCUSSIONS_IM_IN, discussionsImIn + "," + newDiscussion);
//                                        MainActivity.user.saveEventually();
//                                    }

                                    // Switching to the user activity
                                    Intent intent = new Intent(ctx, DiscussionActivity.class);
                                    intent.putExtra(Const.DISCUSSION_TOPIC, chatsList.get(pos).getDiscussionName());
                                    intent.putExtra(Const.DISCUSSION_TABLE_NAME, chatsList.get(pos).getParseID());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Utils.showDialog(ctx, getString(R.string.err_users) + " " + e.getMessage());
                            e.printStackTrace();
                        }

                        dia.dismiss();
                    }
                });

//        user.fetchInBackground(new GetCallback<Object>() {
//            @Override
//            public void done(Object object, ParseException e) {
//
//                String discussionsUserIsIn = (String) object;
//
//
//            }
//        });
        

    }

    /* ========================================================================== */

    /**
     * The Class UserAdapter is the adapter class for User ListView. This
     * adapter shows the user name and its only online status for each item.
     */
    private class DiscussionAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return chatsList.size();
        }

        /* ========================================================================== */

        @Override
        public Discussion getItem(int arg0)
        {
            return chatsList.get(arg0);
        }

        /* ========================================================================== */

        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        /* ========================================================================== */

        public boolean isSameDay(Calendar date1, Calendar date2) {
            // If they now are equal then it is the same day.
            return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                    date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR);
        }

        /* ========================================================================== */

        @Override
        public View getView(int pos, View v, ViewGroup arg2)
        {
            if (v == null) {
                v = getActivity().getLayoutInflater().inflate(R.layout.discussion_item, null);
            }

            final View finalView = v;

            ((TextView) v.findViewById(R.id.lbl1)).setText(chatsList.get(pos).getDiscussionName());

//            ParseQuery<ParseObject> query = ParseQuery.getQuery(chatsList.get(pos).getParseID());
//            query.countInBackground(new CountCallback() {
//                                        public void done(int count, ParseException e) {
//                                            if (e == null) {
//                                                ((TextView) finalView.findViewById(R.id.lbl2)).setText(Integer.toString(count));
//                                            } else {
//                                                Toast.makeText(getActivity().getApplicationContext(), "Failed to retrieve the number of messages at a discussion", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });

            final ParseObject po = new ParseObject(chatsList.get(pos).getParseID());

            final int position = pos;

            ParseQuery<ParseObject> query = ParseQuery.getQuery(chatsList.get(pos).getParseID());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> li, ParseException e) {
                    if (e == null) {
                        HashSet<String> messagesIDs = new HashSet<String>();

                        Calendar mostRecentMessageTime = null, currentMessageTime = Calendar.getInstance();
                        int counter = 0;
                        System.out.println(li.size());
                        for (ParseObject message : li) {
                            Date updateTime = message.getUpdatedAt();

                            currentMessageTime.setTime(updateTime);

                            if ((mostRecentMessageTime == null) ||
                                    (currentMessageTime.after(mostRecentMessageTime))) {
                                mostRecentMessageTime = currentMessageTime;
                            }
                            counter++;
                            messagesIDs.add((String) message.get(Const.COL_MESSAGE_SENDER_ID));
                        }

                        // TODO - Shachar - it happened that mostRecentMessageTime == null (despite the for loop above). We should handle it.
                        try {
                            Calendar currentDate = Calendar.getInstance();
                            String lastModifiedHour = "";
                            if (!isSameDay(currentDate, mostRecentMessageTime)) {
                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                lastModifiedHour = dateFormat.format(
                                        mostRecentMessageTime.getTime());
                            } else {
                                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                                lastModifiedHour = dateFormat.format(
                                        mostRecentMessageTime.getTime());
                            }

                            ((TextView) finalView.findViewById(R.id.lbl2)).setText(Integer.toString(counter));
                            ((TextView) finalView.findViewById(R.id.lbl3)).setText(lastModifiedHour);
                            ((TextView) finalView.findViewById(R.id.lbl4)).setText(Integer.toString(messagesIDs.size()));
                        }
                        catch (Exception ex) {

                        }
                    } else {
                        // TODO - something failed
                    }
                }
            });

            TextView userLabel = (TextView) v.findViewById(R.id.dummy);
            userLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow, 0);

            // Change the logo of the discussion
            ImageView iv = (ImageView) v.findViewById(R.id.imgLogo);
            if ((position % 2) == 0) {
                iv.setImageResource(R.drawable.discussion_logo_0);
            } else {
                iv.setImageResource(R.drawable.discussion_logo_1);
            }

            return v;
        }
    }
}
