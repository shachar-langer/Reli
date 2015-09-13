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

public class MainAllRelisFragment extends BaseRelisFragment {

    private TextView tvNoUsers;

    /* ========================================================================== */

    public MainAllRelisFragment() {
        // Required empty public constructor
    }

    /* ========================================================================== */

    public void loadUserList() {

        ReliUser user = MainActivity.user;

//        final ProgressDialog dia = ProgressDialog.show(getActivity(), null, getString(R.string.alert_loading));
        tvNoUsers = (TextView) v.findViewById(R.id.no_relis);
//        tvNoUsers.setVisibility(View.GONE);

        ParseQuery<Discussion> discussionQuery = Discussion.getDiscussionQuery();
        discussionQuery.orderByDescending("createdAt");
        discussionQuery.whereWithinKilometers(Const.COL_DISCUSSION_LOCATION,
                user.getLocation(),
                (user.getNotificationsRadius() / Const.METERS_IN_KM))
                .findInBackground(new FindCallback<Discussion>() {

                    @Override
                    public void done(List<Discussion> li, ParseException e) {

                        if (li != null) {
                            if (li.size() == 0) {
                                displayNoRelisMessage();
                                chatsList = new ArrayList<>();
//                                dia.dismiss();
                                return;
                            }
                            else {
                                tvNoUsers.setVisibility(View.GONE);
                            }

                            chatsList = new ArrayList<>(li);
//                            ListView list = (ListView) v.findViewById(R.id.list_relis);
//                            list.setAdapter(new DiscussionAdapter());
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

                                    Intent intent = new Intent(ctx, DiscussionActivity.class);
                                    intent.putExtra(Const.DISCUSSION_TOPIC, chatsList.get(pos).getDiscussionName());
                                    intent.putExtra(Const.DISCUSSION_TABLE_NAME, chatsList.get(pos).getParseID());
                                    startActivity(intent);
                                }
                            });
                            discussionAdapter.notifyDataSetChanged();
                        } else {
//                            Utils.showDialog(ctx, getString(R.string.err_users) + " " + e.getMessage());
//                            e.printStackTrace();
                        }

//                        dia.dismiss();
                    }
                });
        allHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
//                if (HomeFragment.runningAll) {
//                    Toast.makeText(getActivity().getApplicationContext(), "In loadUserList ALL", Toast.LENGTH_SHORT).show();
//                    loadUserList();
//                }

                loadUserList();

            }
        }, 1000);
    }

    /* ========================================================================== */

    private void displayNoRelisMessage() {
        tvNoUsers.setText(R.string.no_relis_all);
        tvNoUsers.setVisibility(View.VISIBLE);
    }
}
