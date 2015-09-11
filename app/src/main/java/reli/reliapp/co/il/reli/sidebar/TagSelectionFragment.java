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

import java.util.ArrayList;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.dataStructures.ReliTag;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.utils.Const;
import reli.reliapp.co.il.reli.utils.Utils;

public class TagSelectionFragment extends Fragment {

    /* ========================================================================== */

    private ListView mListView;
    private TextView mRadius;
    private SeekBar mSeekBar;
    private ReliUser currentUser;

    private ArrayList<String> tagsNames = new ArrayList<>();
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
    public void onPause() {
        super.onPause();

        boolean isChanged = saveNewRadius();
        isChanged |= saveNewTags();

        if (isChanged) {
            currentUser.saveEventually();
            MainActivity.installation.saveInBackground();
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
        ArrayList<String> wantedTagsIDs = getCheckedTagsIDs();

        // Save the new notifications
        if (!currentUser.getNotificationsTagsIDs().equals(wantedTagsIDs)) {
            updateNotifications(wantedTagsIDs); // Must be before setNotificationsTagsIDs
            currentUser.setNotificationsTagsIDs(wantedTagsIDs);
            isChanged = true;
        }

        return isChanged;
    }

    /* ========================================================================== */

    private void updateNotifications(ArrayList<String> shouldBeAdded) {
        ArrayList<String> allItems = currentUser.getNotificationsTagsIDs();
        ArrayList<String> shouldBeRemoved = getUncheckedTagsIDs(allItems, shouldBeAdded);

        // Add the tags that the user wants to follow from the list
        for (String currentShouldBeAdded : shouldBeAdded) {
            MainActivity.installation.put(currentShouldBeAdded, true);
        }

        // Remove the tags that the user doesn't want to follow from the list
        for (String currentShouldBeAdded : shouldBeRemoved) {
            MainActivity.installation.put(currentShouldBeAdded, false);
//            MainActivity.installation.remove(currentShouldBeAdded);
        }
    }

    /* ========================================================================== */

    private ArrayList<String> getCheckedTagsIDs() {
        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<>();

        int position;
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                //                selectedItems.add(mArrayAdapter.getItem(position));
                selectedItems.add(tagsAsObjects.get(position).getTagParseID());

            }
        }

        return selectedItems;
    }

    /* ========================================================================== */

    private ArrayList<String> getUncheckedTagsIDs(ArrayList<String> allItems, ArrayList<String> selectedItems) {
        ArrayList<String> unselectedItems = new ArrayList<>();

        for (String currentTagID : allItems) {
            if (!selectedItems.contains(currentTagID)) {
                unselectedItems.add(currentTagID);
            }
        }

        return unselectedItems;
    }

    /* ========================================================================== */

    private void addTagsToScreen(View v) {
        tagsAsObjects = new ArrayList<>(MainActivity.tagsIdToTag.values());
        for (ReliTag reliTag : tagsAsObjects) {
            tagsNames.add(reliTag.getTagName());
        }

        mListView = (ListView) v.findViewById(R.id.tags_list_view);
        mArrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_multiple_choice, tagsNames);

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(mArrayAdapter);

        selectAlreadyChosenTags();
    }

    /* ========================================================================== */

    private void enableTagSearch(View v) {
        // Enabling Search Filter
        EditText inputSearch = (EditText) v.findViewById(R.id.tags_search);
        Utils.setHideKeyboardCallback(getActivity(), inputSearch);
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
            progress = currentUser.getNotificationsRadius();
        } catch (Exception e) {
            progress = Const.DEFAULT_RADIUS_FOR_NOTIFICATIONS;
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = (Math.round(progress / Const.STEP_SIZE)) * Const.STEP_SIZE;
                mSeekBar.setProgress(progress);
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
        ArrayList<String> chosenIDs = currentUser.getNotificationsTagsIDs();
        ArrayList<String> chosenTagNames = new ArrayList<>();

        String currentID;
        for (int i = 0; i < chosenIDs.size(); i++) {
            currentID = chosenIDs.get(i);
            chosenTagNames.add(MainActivity.tagsIdToTag.get(currentID).getTagName());
        }

        String currentTagName;
        for (int i = 0; i < mArrayAdapter.getCount(); i++) {
            currentTagName = mArrayAdapter.getItem(i);
            if (chosenTagNames.contains(currentTagName)) {
                mListView.setItemChecked(i, true);
            }
        }
    }
}