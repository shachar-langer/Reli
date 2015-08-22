package reli.reliapp.co.il.reli.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import reli.reliapp.co.il.reli.createReli.DiscussionActivity;
import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.createReli.CreateReliActivity;
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.utils.Const;
import reli.reliapp.co.il.reli.utils.Utils;

public class MainMyRelisFragment extends Fragment {

    private ArrayList<Discussion> chatsList;
    private View v;
    private Context ctx;

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
                    Toast.makeText(getActivity(), "Can not find your location", Toast.LENGTH_SHORT).show();
                }
                else {
                    intent.putExtra(Const.LATITUDE, location.getLatitude());
                    intent.putExtra(Const.ALTITUDE, location.getAltitude());
                    startActivity(intent);
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

        String discussionsUserIsIn = (String) user.getString(Const.COL_NAME_DISCUSSIONS_IM_IN);

        // If there are no discussions I'm in, the frament should be empty
        // TODO - should not be compared to null
        if (discussionsUserIsIn == null) {
            user.put(Const.COL_NAME_DISCUSSIONS_IM_IN, "");
            user.saveEventually();
            dia.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), "None", Toast.LENGTH_SHORT).show();
            return;
        }

        if (discussionsUserIsIn.equals("")) {
            dia.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseQuery<Discussion> discussionQuery = Discussion.getDiscussionQuery();
        String[] listOfDiscussion = discussionsUserIsIn.split(",");
        ArrayList<String> discussionsArray = new ArrayList<String>(Arrays.asList(listOfDiscussion));
        discussionQuery.whereContainedIn("objectId", discussionsArray);

        for (String discussion : discussionsArray) {
            MainActivity.discussionsImIn.add(discussion);
        }


        // TODO - change 10 to the users radius choice
        discussionQuery.whereWithinKilometers(Const.COL_DISCUSSION_LOCATION, user.getLocation(), 1000000)
                .findInBackground(new FindCallback<Discussion>() {

                    @Override
                    public void done(List<Discussion> li, ParseException e) {
                        dia.dismiss();
                        if (li != null) {
                            if (li.size() == 0) {
                                Toast.makeText(ctx, R.string.msg_no_user_found, Toast.LENGTH_SHORT).show();
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
                                    intent.putExtra(Const.BUDDY_NAME, chatsList.get(pos).getDiscussionName());
                                    intent.putExtra(Const.DISCUSSION_TABLE_NAME, chatsList.get(pos).getParseID());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Utils.showDialog(ctx, getString(R.string.err_users) + " " + e.getMessage());
                            e.printStackTrace();
                        }
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

        @Override
        public View getView(int pos, View v, ViewGroup arg2)
        {
            if (v == null) {
                v = getActivity().getLayoutInflater().inflate(R.layout.discussion_item, null);
            }

            // TODO - change this. Bad practice.
            final View bla = v;

            ((TextView) v.findViewById(R.id.lbl1)).setText(chatsList.get(pos).getDiscussionName());

//            ParseQuery<ParseObject> query = ParseQuery.getQuery(chatsList.get(pos).getParseID());
//            query.countInBackground(new CountCallback() {
//                                        public void done(int count, ParseException e) {
//                                            if (e == null) {
//                                                ((TextView) bla.findViewById(R.id.lbl2)).setText(Integer.toString(count));
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

                        int counter = 0;
                        System.out.println(li.size());
                        for (ParseObject message : li) {
                            counter++;
                            messagesIDs.add((String) message.get("sender"));
                        }

                        ((TextView) bla.findViewById(R.id.lbl2)).setText(Integer.toString(counter));
                        ((TextView) bla.findViewById(R.id.lbl4)).setText(Integer.toString(messagesIDs.size()));
                    } else {
                        // TODO - something failed
                    }

                }
            });

            TextView userLabel = (TextView) v.findViewById(R.id.dummy);
            userLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow, 0);

            return v;
        }
    }
}
