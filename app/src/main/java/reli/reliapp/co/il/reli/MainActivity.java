package reli.reliapp.co.il.reli;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import reli.reliapp.co.il.reli.custom.CustomActivity;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.utils.Const;
import reli.reliapp.co.il.reli.utils.Utils;

/**
 * The Class UserList is the Activity class. It shows a list of all users of
 * this app. It also shows the Offline/Online status of users.
 */
public class MainActivity extends CustomActivity
{
    private ArrayList<ParseUser> chatsList;
    public static ReliUser user;

    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayHomeAsUpEnabled(false);

        user = ReliUser.getCurrentReliUser();
        updateUserStatus(true);
    }

    /* ========================================================================== */

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        updateUserStatus(false);
    }

    /* ========================================================================== */

    @Override
    protected void onResume()
    {
        super.onResume();
        loadUserList();
    }

    /* ========================================================================== */

    // TODO - move to ReliUser
    private void updateUserStatus(boolean online)
    {
        user.put("online", online);
        user.saveEventually();
    }

    /* ========================================================================== */

    private void loadUserList()
    {
        final ProgressDialog dia = ProgressDialog.show(this, null, getString(R.string.alert_loading));
        ParseUser.getQuery().whereNotEqualTo("username", user.getUsername())
                .findInBackground(new FindCallback<ParseUser>() {

                    @Override
                    public void done(List<ParseUser> li, ParseException e) {
                        dia.dismiss();
                        if (li != null) {
                            if (li.size() == 0) {
                                Toast.makeText(MainActivity.this, R.string.msg_no_user_found, Toast.LENGTH_SHORT).show();
                            }

                            chatsList = new ArrayList<ParseUser>(li);
                            ListView list = (ListView) findViewById(R.id.list);
                            list.setAdapter(new UserAdapter());
                            list.setOnItemClickListener(new OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                                    Intent intent = new Intent(MainActivity.this, Chat.class);
                                    intent.putExtra(Const.BUDDY_NAME, chatsList.get(pos).getUsername());
                                    startActivity(intent);
                                }
                            });
                        }
                        else {
                            Utils.showDialog (MainActivity.this, getString(R.string.err_users) + " " + e.getMessage());
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
                v = getLayoutInflater().inflate(R.layout.chat_item, null);
            }

            TextView userLabel = (TextView) v;
            userLabel.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.arrow,
                    0);

            return v;
        }
    }
}