package reli.reliapp.co.il.reli.sidebar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.List;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.ReliTag;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.utils.Const;

public class TagSelectionFragment extends Fragment {

    /* ========================================================================== */

    private ListView mListView;
    private TextView mRadius;
    private SeekBar mSeekBar;
    private ReliUser currentUser;

    private ArrayList<String> tagsNames = new ArrayList<String>();
    private ArrayList<ReliTag> tagsAsObjects;
    ArrayAdapter<String> mArrayAdapter;

    /* ========================================================================== */

    public TagSelectionFragment() {
        // Required empty public constructor
    }

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tag_selection, container, false);

        currentUser = MainActivity.user;
        mRadius = (TextView) v.findViewById(R.id.tag_selection_current_radius_value);
        mSeekBar = (SeekBar) v.findViewById(R.id.seek_bar_radius);

        addTagsToScreen(v);
        addSeekBarToScreen();
        addCheckBoxToScreen(v);

        enableTagSearch(v);

        return v;
    }

    /* ========================================================================== */

    @Override
    public void onDetach() {
        super.onDetach();

        boolean isChanged = saveNewRadius();
        isChanged |= saveNewTags();

        if (isChanged) {
            currentUser.saveEventually();
            Toast.makeText(getActivity().getApplicationContext(), R.string.new_settings_saved, Toast.LENGTH_SHORT).show();
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
            currentUser.saveEventually();
            isChanged = true;
        }

        Toast.makeText(getActivity().getApplicationContext(), "isChanged == " + isChanged + ", size = " + getCheckedTags().size(), Toast.LENGTH_SHORT).show();

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

    private void addTagsToScreen(View v) {
        tagsAsObjects = new ArrayList<ReliTag>(MainActivity.tagsIdToTag.values());
        for (ReliTag reliTag : tagsAsObjects) {
            tagsNames.add(reliTag.getTagName());
        }

        mListView = (ListView) v.findViewById(R.id.tags_list_view);
        mArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_multiple_choice, tagsNames);

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(mArrayAdapter);

        selectAlreadyChosenTags();
    }

    /* ========================================================================== */

    private void enableTagSearch(View v) {
        // Enabling Search Filter
        EditText inputSearch = (EditText) v.findViewById(R.id.tags_search);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                mArrayAdapter.getFilter().filter(cs);
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

    private void addCheckBoxToScreen(View v) {
        CheckBox cb = (CheckBox) v.findViewById(R.id.selectAllCheckBox);
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
        ArrayList<ReliTag> chosen = currentUser.getNotificationsTags();
        HashMap<String, ReliTag> bla = MainActivity.tagsIdToTag;

        for (int i = 0; i < mArrayAdapter.getCount(); i++) {
            if (chosen.contains(mArrayAdapter.getItem(i))) {
                mListView.setItemChecked(i, true);
            }
        }
    }
}