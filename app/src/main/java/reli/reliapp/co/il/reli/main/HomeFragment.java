package reli.reliapp.co.il.reli.main;

import java.util.Locale;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import reli.reliapp.co.il.reli.R;

public class HomeFragment extends Fragment {

    public static final int MIDDLE_FRAGMENT = 1;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    /* ========================================================================== */

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    /* ========================================================================== */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) v.findViewById(R.id.home_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(MIDDLE_FRAGMENT);

        // TODO - do we want it?
        // TODO - do we want http://viewpagerindicator.com?
//        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radiogroup);
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position){
//                    case 0:
//                        radioGroup.check(R.id.radioButton);
//                        break;
//                    case 1:
//                        radioGroup.check(R.id.radioButton2);
//                        break;
//                    case 2:
//                        radioGroup.check(R.id.radioButton3);
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        return v;
    }

    /* ========================================================================== */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /* ========================================================================== */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MainRelisAroundMeFragment();
                case 1:
                    return new MainMyRelisFragment();
                case 2:
                    return new MainAllRelisFragment();
            }

            return null;
        }

        /* ========================================================================== */

        @Override
        public int getCount() {
            return 3;
        }

        /* ========================================================================== */

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.left_tab).toUpperCase(l);
                case 1:
                    return getString(R.string.middle_tab).toUpperCase(l);
                case 2:
                    return getString(R.string.right_tab).toUpperCase(l);
            }

            return null;
        }
    }
}