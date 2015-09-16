package il.co.reli.main;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;
import il.co.reli.R;
import il.co.reli.createReli.DiscussionActivity;
import il.co.reli.dataStructures.Discussion;
import il.co.reli.dataStructures.ReliUser;
import il.co.reli.utils.Const;


public class MainMyRelisFragment extends BaseRelisFragment {

    private TextView tvNoUsers;

    /* ========================================================================== */

    public MainMyRelisFragment() {
        // Required empty public constructor
    }

    /* ========================================================================== */

    public void loadUserList() {

        final ReliUser user = MainActivity.user;

        ArrayList<String> discussionsUserIsIn = user.getDiscussionImIn();
        tvNoUsers = (TextView) v.findViewById(R.id.no_relis);

        // If there are no discussions I'm in, the fragment should be empty
        if (discussionsUserIsIn == null) {
            user.initDiscussionImIn();
            user.saveEventually();
            chatsList = new ArrayList<>();
            displayNoRelisMessage();
            return;
        }

        if (discussionsUserIsIn.size() == 0) {
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
                                chatsList = new ArrayList<>();
                                return;
                            } else {
                                tvNoUsers.setVisibility(View.GONE);
                            }

                            chatsList = new ArrayList<>(li);
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
                        }
                    }
                });
    }

    /* ========================================================================== */

    private void displayNoRelisMessage() {
        tvNoUsers.setText(R.string.no_relis_my);
        tvNoUsers.setVisibility(View.VISIBLE);
    }
}