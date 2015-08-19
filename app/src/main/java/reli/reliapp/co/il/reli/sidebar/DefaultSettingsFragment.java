package reli.reliapp.co.il.reli.sidebar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import reli.reliapp.co.il.reli.R;

public class DefaultSettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_default_settings, container, false);

        NumberPicker np = (NumberPicker) v.findViewById(R.id.numberPicker1);
        np.setMinValue(0);
        np.setMaxValue(24);
        np.setWrapSelectorWheel(true);


        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Toast.makeText(getActivity().getApplicationContext(), "Old: " + String.valueOf(oldVal) + ", new: " + String.valueOf(newVal), Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}
