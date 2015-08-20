package reli.reliapp.co.il.reli.tags;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.custom.CustomActivity;
import reli.reliapp.co.il.reli.dataStructures.ReliTag;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.utils.Const;

public class TagSelectionActivity extends CustomActivity {

    /* ========================================================================== */

    private ListView mListView;
    private TextView mRadius;
    private SeekBar mSeekBar;
    private ReliUser currentUser;

    private ArrayList<String> tagsNames = new ArrayList<String>();
    private ArrayList<ReliTag> tagsAsObjects;
    ArrayAdapter<String> mArrayAdapter;

    /* ========================================================================== */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_selection);

        currentUser = MainActivity.user;
        mRadius = (TextView) findViewById(R.id.tag_selection_current_radius_value);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar_radius);

        // TODO - remove
        if (currentUser == null) {
            Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_SHORT).show();
            return;
        }

        addTagsToScreen();
        addSeekBarToScreen();
        addCheckBoxToScreen();

        enableTagSearch();
    }

    /* ========================================================================== */

//    public void onClick(View v) {
//        ArrayList<ReliTag> selectedItems = getCheckedTags();
//
//        String[] outputStrArr = new String[selectedItems.size()];
//        for (int i = 0; i < selectedItems.size(); i++) {
//            outputStrArr[i] = selectedItems.get(i).getTagName();
//        }
//
//        Toast.makeText(getApplicationContext(), Arrays.toString(outputStrArr), Toast.LENGTH_SHORT).show();
//    }
//
//    /* ========================================================================== */
//
//    private class MyArrayAdapter extends ArrayAdapter<String>{
//
//        private HashMap<Integer, Boolean> myChecked = new HashMap<Integer, Boolean>();
//
//        /* ========================================================================== */
//
//        public MyArrayAdapter(Context context, int resource,
//                              int textViewResourceId, List<String> objects) {
//            super(context, resource, textViewResourceId, objects);
//
//            for (int i = 0; i < objects.size(); i++) {
//                myChecked.put(i, false);
//            }
//        }
//
//        /* ========================================================================== */
//
//        public void toggleChecked(int position){
//            myChecked.put(position, !myChecked.get(position));
//            notifyDataSetChanged();
//        }
//
//        /* ========================================================================== */
//
//        public List<String> getCheckedItems(){
//            List<String> checkedItems = new ArrayList<String>();
//
//            for(int i = 0; i < myChecked.size(); i++){
//                if (myChecked.get(i)) {
//                    checkedItems.add(tagsNames.get(i));
//                }
//            }
//
//            return checkedItems;
//        }
//
//        /* ========================================================================== */
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View row = convertView;
//
//            if(row == null){
//                LayoutInflater inflater=getLayoutInflater();
//                row = inflater.inflate(R.layout.tag_item, parent, false);
//            }
//
//            CheckedTextView checkedTextView = (CheckedTextView)row.findViewById(R.id.tag_name);
//            checkedTextView.setText(tagsNames.get(position));
//
//            Boolean checked = myChecked.get(position);
//            if (checked != null) {
//                checkedTextView.setChecked(checked);
//            }
//
//            return row;
//        }
//    }

    /* ========================================================================== */

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        boolean isChanged = saveNewRadius();
        isChanged |= saveNewTags();

        if (isChanged) {
            currentUser.saveEventually();
            Toast.makeText(getApplicationContext(), R.string.new_settings_saved, Toast.LENGTH_SHORT).show();
        }
    }

    /* ========================================================================== */

    private boolean saveNewRadius() {
        boolean isChanged = false;
        int wantedRadius = mSeekBar.getProgress();

        if (currentUser.getNotificationsRadius() != wantedRadius) {
            currentUser.setNotificationsRadius(wantedRadius);
            isChanged = true;
        }

        return isChanged;
    }

    /* ========================================================================== */

    private boolean saveNewTags() {
        boolean isChanged = false;
        ArrayList<ReliTag> wantedTags = getCheckedTags();

        // Save the new notifications
        if (!currentUser.getNotificationsTags().equals(wantedTags)) {
            currentUser.setNotificationsTags(wantedTags);
            isChanged = true;
        }

        return isChanged;
    }

    /* ========================================================================== */

    private ArrayList<ReliTag> getCheckedTags() {
        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        ArrayList<ReliTag> selectedItems = new ArrayList<ReliTag>();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i)) {
                //                selectedItems.add(mArrayAdapter.getItem(position));
                selectedItems.add(tagsAsObjects.get(position));
            }
        }

        return selectedItems;
    }

    /* ========================================================================== */

    private void addTagsToScreen() {
        ParseQuery<ReliTag> query = ParseQuery.getQuery("ReliTag");
        query.findInBackground(new FindCallback<ReliTag>() {
            @Override
            public void done(List<ReliTag> reliTags, ParseException e) {
                tagsAsObjects = new ArrayList<ReliTag>(reliTags);
                for (ReliTag reliTag : reliTags) {
                    tagsNames.add(reliTag.getTagName());
                }

                mListView = (ListView)findViewById(R.id.tags_list_view);

//                mArrayAdapter = new MyArrayAdapter(getApplicationContext(), R.layout.tag_item, R.id.tag_name, tagsNames);
//                mListView.setAdapter(mArrayAdapter);
//                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        mArrayAdapter.toggleChecked(position);
//                    }
//                });

                mArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_multiple_choice, tagsNames);

//                mArrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.tag_item, R.id.tag_name, tagsNames);

                mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                mListView.setAdapter(mArrayAdapter);

                selectAlreadyChosenTags();
            }
        });

    }

    /* ========================================================================== */

    private void enableTagSearch() {
        // Enabling Search Filter
        EditText inputSearch = (EditText) findViewById(R.id.tags_search);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                TagSelectionActivity.this.mArrayAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    /* ========================================================================== */

    private void addSeekBarToScreen() {
        int progress;

        // Set the initial progress
        try {
            // TODO - check why in the first time it doesn't show the correct values
            progress = currentUser.getNotificationsRadius();
        } catch (Exception e) {
            progress = Const.DEFAULT_RADIUS_FOR_NOTIFICATIONS;
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRadius.setText(String.valueOf(progress) + " meters");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mSeekBar.setProgress(progress);
    }

    /* ========================================================================== */

    private void addCheckBoxToScreen() {
        CheckBox cb = (CheckBox) findViewById(R.id.selectAllCheckBox);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v.findViewById(R.id.selectAllCheckBox)).isChecked()) {
                    selectAll();
                } else {
                    deselectAll();
                }
            }
        });
    }

    /* ========================================================================== */

    private void selectAll() {
        for (int i = 0; i < mArrayAdapter.getCount(); i++) {
            mListView.setItemChecked(i, true);
        }
    }

    /* ========================================================================== */

    private void deselectAll() {
        for (int i = 0; i < mArrayAdapter.getCount(); i++) {
            mListView.setItemChecked(i, false);
        }
    }

    /* ========================================================================== */

    private void selectAlreadyChosenTags() {
        for (int i = 0; i < mArrayAdapter.getCount(); i++) {
            if (currentUser.getNotificationsTags().contains(mArrayAdapter.getItem(i))) {
                mListView.setItemChecked(i, true);
            }
        }
    }
}







//        getResult = (Button)findViewById(R.id.testbutton);
//        getResult.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                String result = "";
//                List<String> resultList = mArrayAdapter.getCheckedItems();
//
//                for(int i = 0; i < resultList.size(); i++){
//                    result += String.valueOf(resultList.get(i)) + "\n";
//                }
//
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//            }});

// http://theopentutorials.com/post/uncategorized/android-multiple-selection-listview/