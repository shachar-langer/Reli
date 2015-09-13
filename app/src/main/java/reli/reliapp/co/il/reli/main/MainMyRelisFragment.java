package reli.reliapp.co.il.reli.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;
import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.createReli.DiscussionActivity;
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.utils.Const;
import reli.reliapp.co.il.reli.utils.Utils;


public class MainMyRelisFragment extends BaseRelisFragment {

    private TextView tvNoUsers;

    /* ========================================================================== */

    public MainMyRelisFragment() {
        // Required empty public constructor
    }

    /* ========================================================================== */

    public void loadUserList() {

        final ReliUser user = MainActivity.user;
//        final ProgressDialog dia = ProgressDialog.show(getActivity(), null, getString(R.string.alert_loading));

        ArrayList<String> discussionsUserIsIn = user.getDiscussionImIn();
        tvNoUsers = (TextView) v.findViewById(R.id.no_relis);

        // If there are no discussions I'm in, the fragment should be empty
        if (discussionsUserIsIn == null) {
            user.initDiscussionImIn();
            user.saveEventually();
//            dia.dismiss();
            chatsList = new ArrayList<>();
            displayNoRelisMessage();
            return;
        }

        if (discussionsUserIsIn.size() == 0) {
//            dia.dismiss();
            chatsList = new ArrayList<>();
            displayNoRelisMessage();
            return;
        }

        ParseQuery<Discussion> discussionQuery = Discussion.getDiscussionQuery();
        discussionQuery.whereContainedIn("objectId", discussionsUserIsIn);

        // Initialize the static variable
        for (String discussion : discussionsUserIsIn) {
            MainActivity.discussionsImIn.add(discussion);
        }
        discussionQuery.orderByDescending("createdAt");
        discussionQuery.findInBackground(new FindCallback<Discussion>() {

                    @Override
                    public void done(List<Discussion> li, ParseException e) {

                        if (li != null) {
                            if (li.size() == 0) {
                                displayNoRelisMessage();
//                                dia.dismiss();
                                chatsList = new ArrayList<>();
                                return;
                            }
                            else {
                                tvNoUsers.setVisibility(View.GONE);
                            }

                            chatsList = new ArrayList<>(li);
//                            ListView list = (ListView) v.findViewById(R.id.list_relis);
//                            list.setAdapter(new DiscussionAdapter());
                            discussionAdapter.notifyDataSetChanged();
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

                                    // Switching to the user activity
                                    Intent intent = new Intent(ctx, DiscussionActivity.class);
                                    intent.putExtra(Const.DISCUSSION_TOPIC, chatsList.get(pos).getDiscussionName());
                                    intent.putExtra(Const.DISCUSSION_TABLE_NAME, chatsList.get(pos).getParseID());
                                    startActivity(intent);
                                }
                            });
                        } else {
//                            Utils.showDialog(ctx, getString(R.string.err_users) + " " + e.getMessage());
//                            e.printStackTrace();
                        }

//                        dia.dismiss();
                    }
                });
        myHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
//                if (HomeFragment.runningMy) {
//                    Toast.makeText(getActivity().getApplicationContext(), "In loadUserList MY", Toast.LENGTH_SHORT).show();
//                    loadUserList();
//                }
                loadUserList();

            }
        }, 1000);
    }

    /* ========================================================================== */

    private void displayNoRelisMessage() {
        tvNoUsers.setText(R.string.no_relis_my);
        tvNoUsers.setVisibility(View.VISIBLE);
    }
}