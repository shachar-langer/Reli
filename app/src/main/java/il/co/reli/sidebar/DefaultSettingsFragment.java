package il.co.reli.sidebar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import il.co.reli.il.reli.R;
import il.co.reli.dataStructures.ReliUser;
import il.co.reli.main.MainActivity;
import il.co.reli.utils.Const;

public class DefaultSettingsFragment extends Fragment {

    /* ========================================================================== */

    private ReliUser currentUser;
    private NumberPicker npHours, npMinutes;
    private SeekBar mSeekBar;

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_default_settings, container, false);

        currentUser = MainActivity.user;

        npHours   = (NumberPicker) v.findViewById(R.id.numberPicker_hours);
        npMinutes = (NumberPicker) v.findViewById(R.id.numberPicker_minutes);
        mSeekBar  = (SeekBar) v.findViewById(R.id.default_settings_seek_bar_radius);

        addSeekBarToScreen(v);
        setTimePickers();

        return v;
    }

    /* ========================================================================== */

    private void addSeekBarToScreen(View v) {
        int progress;
        final View finalView = v;

        // Set the initial progress
        try {
            progress = currentUser.getRelisRadius();
        } catch (Exception e) {
            progress = Const.DEFAULT_RADIUS_FOR_RELIS;
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int)Math.round(progress / Const.STEP_SIZE)) * Const.STEP_SIZE;
                mSeekBar.setProgress(progress);
                TextView mRadius = (TextView) finalView.findViewById(R.id.default_settings_current_radius_value);
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

    private void setTimePickers() {
        int currentExpirationInMinutes = currentUser.getRelisExpirationInMinutes();
        int hours = currentExpirationInMinutes / Const.MINUTES_IN_HOUR;
        int minutes = currentExpirationInMinutes - hours * Const.MINUTES_IN_HOUR;

        // Set the hours picker
        npHours.setMinValue(Const.MINIMUM_TIME);
        npHours.setMaxValue(Const.MAX_HOURS);
        npHours.setValue(hours);

        // Set the minutes picker
        npMinutes.setMinValue(Const.MINIMUM_TIME);
        npMinutes.setMaxValue(Const.MAX_MINUTES);
        npMinutes.setValue(minutes);
    }

    /* ========================================================================== */

    @Override
    public void onPause() {
        super.onPause();

        boolean isChanged = saveNewRadius();
        isChanged |= saveNewExpiration();

        if (isChanged) {
            currentUser.saveEventually();
            Toast.makeText(getActivity().getApplicationContext(), R.string.new_settings_saved, Toast.LENGTH_SHORT).show();
        }

    }

    /* ========================================================================== */

    private boolean saveNewRadius() {
        boolean isChanged = false;
        int wantedRadius = mSeekBar.getProgress();

        if (currentUser.getRelisRadius() != wantedRadius) {
            currentUser.setRelisRadius(wantedRadius);
            isChanged = true;
        }

        return isChanged;
    }

    /* ========================================================================== */

    private boolean saveNewExpiration() {
        boolean isChanged = false;
        int wantedExpiration = npHours.getValue() * Const.MINUTES_IN_HOUR + npMinutes.getValue();

        if (currentUser.getRelisExpirationInMinutes() != wantedExpiration) {
            currentUser.setRelisExpirationInMinutes(wantedExpiration);
            isChanged = true;
        }

        return isChanged;
    }
}
