package reli.reliapp.co.il.reli.main;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.utils.Const;

public abstract class BaseRelisFragment extends Fragment {

    protected ArrayList<Discussion> chatsList;
    protected View v;
    protected Context ctx;

    public BaseRelisFragment() {
        // Required empty public constructor
    }

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main_relis, container, false);
        ctx = getActivity().getApplicationContext();

        FloatingActionButton addDiscussionBtn = (FloatingActionButton) v.findViewById(R.id.add_discussion_btn_relis);
        addDiscussionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateReliActivity.class);

                Location location = ((MainActivity) getActivity()).getLocation();
                if (location != null) {
                    intent.putExtra(Const.LATITUDE, location.getLatitude());
                    intent.putExtra(Const.LONGTITUDE, location.getLongitude());
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

    protected abstract void loadUserList();

    /* ========================================================================== */

    /**
     * The Class UserAdapter is the adapter class for User ListView. This
     * adapter shows the user name and its only online status for each item.
     */
    protected class DiscussionAdapter extends BaseAdapter
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

            final int position = pos;

            ParseQuery<ParseObject> query = ParseQuery.getQuery(chatsList.get(pos).getParseID());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> li, ParseException e) {
                    if (e == null) {
                        ParseObject.fetchAllInBackground(li, new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> li, ParseException e) {
                                HashSet<String> messagesIDs = new HashSet<String>();

                                Calendar mostRecentMessageTime = null, currentMessageTime = Calendar.getInstance();
                                int counter = 0;
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
                                } catch (Exception ex) {

                                }
                            }
                        });
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
