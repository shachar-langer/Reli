package reli.reliapp.co.il.reli.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.createReli.DiscussionActivity;
import reli.reliapp.co.il.reli.createReli.CreateReliActivity;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.utils.Const;
import reli.reliapp.co.il.reli.utils.Utils;

public class MainAllRelisFragment extends Fragment {

    private ArrayList<ParseUser> chatsList;
    private View v;
    private Context ctx;

    /* ========================================================================== */

    public MainAllRelisFragment() {
        // Required empty public constructor
    }

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_main_all_relis, container, false);

        ctx = getActivity().getApplicationContext();
        Button addDiscussionBtn = (Button) v.findViewById(R.id.add_discussion_btn_all_relis);
        addDiscussionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, "You clicked me!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), CreateReliActivity.class);

                Location location = ((MainActivity) getActivity()).getLocation();
                if (location == null) {
                    Toast.makeText(getActivity(), "Can not find your location", Toast.LENGTH_SHORT).show();
                }
                intent.putExtra(Const.LATITUDE, location.getLatitude());
                intent.putExtra(Const.ALTITUDE, location.getAltitude());
                startActivity(intent);
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

    private void loadUserList()
    {

        ReliUser user = ReliUser.getCurrentReliUser();

        final ProgressDialog dia = ProgressDialog.show(getActivity(), null, getString(R.string.alert_loading));

        ParseUser.getQuery().whereNotEqualTo("username", user.getUsername())
                .findInBackground(new FindCallback<ParseUser>() {

                    @Override
                    public void done(List<ParseUser> li, ParseException e) {
                        dia.dismiss();
                        if (li != null) {
                            if (li.size() == 0) {
                                Toast.makeText(ctx, R.string.msg_no_user_found, Toast.LENGTH_SHORT).show();
                            }

                            chatsList = new ArrayList<ParseUser>(li);
                            ListView list = (ListView) v.findViewById(R.id.list_all_relis);
                            list.setAdapter(new UserAdapter());
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                                    Intent intent = new Intent(ctx, DiscussionActivity.class);
                                    intent.putExtra(Const.BUDDY_NAME, chatsList.get(pos).getUsername());
                                    startActivity(intent);
                                }
                            });
                        }
                        else {
                            Utils.showDialog(ctx, getString(R.string.err_users) + " " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }

    /* ========================================================================== */

    /**
     * The Class UserAdapter is the adapter class for User ListView. This
     * adapter shows the user name and its only online status for each item.
     */
    private class UserAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return chatsList.size();
        }

        /* ========================================================================== */

        @Override
        public ParseUser getItem(int arg0)
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

            TextView userLabel = (TextView) v.findViewById(R.id.dummy);
            userLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow, 0);

            return v;
        }
    }
}
