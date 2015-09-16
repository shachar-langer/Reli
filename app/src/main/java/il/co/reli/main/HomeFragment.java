package il.co.reli.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.co.reli.custom.CustomActivity;
import il.co.reli.viewPageIndicator.CirclePageIndicator;

import il.co.reli.R;

public class HomeFragment extends Fragment {

//    private static final int MIDDLE_FRAGMENT = 1;
    private static final int MIDDLE_FRAGMENT = 0;

    private static MainAllRelisFragment allRelisFragment = null;
    private static MainMyRelisFragment myRelisFragment = null;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

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
    public void onPause()
    {
        super.onPause();
    }

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) v.findViewById(R.id.home_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Bind the title indicator to the adapter
        CirclePageIndicator titleIndicator = (CirclePageIndicator) v.findViewById(R.id.circles_title);
        titleIndicator.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                String title = "";
//                switch (position){
//                    case 0:
//                        title = getString(R.string.left_tab);
//                        break;
//                    case 1:
//                        title = getString(R.string.middle_tab);
//                        break;
//                    case 2:
//                        title = getString(R.string.right_tab);
//                        break;
//                }

                switch (position) {
                    case 0:
                        title = getString(R.string.middle_tab);
                        break;
                    case 1:
                        title = getString(R.string.right_tab);
                        break;
                }

                ((CustomActivity) getActivity()).getSupportActionBar().setTitle(title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setCurrentItem(MIDDLE_FRAGMENT);
        ((CustomActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.middle_tab));

        return v;
    }

    /* ========================================================================== */

    public static MainAllRelisFragment getAllRelisFragmentInstance() {
        if (allRelisFragment == null) {
            allRelisFragment = new MainAllRelisFragment();
//            allRelisFragment.loadUserList();
        }

        return allRelisFragment;
    }

    /* ========================================================================== */

    public static MainMyRelisFragment getMyRelisFragmentInstance() {
        if (myRelisFragment == null) {
            myRelisFragment = new MainMyRelisFragment();
//            myRelisFragment.loadUserList();
        }

        return myRelisFragment;
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
                    return getMyRelisFragmentInstance();
                case 1:
                    return getAllRelisFragmentInstance();
            }

//            switch (position) {
//                case 0:
//                    return new MainRelisAroundMeFragment();
//                case 1:
//                    return new MainMyRelisFragment();
//                case 2:
//                    return new MainAllRelisFragment();
//            }

            return null;
        }

        /* ========================================================================== */

        @Override
        public int getCount() {
            // In the Future - change to 3 (when adding the RelisAroundMe fragment)
            return 2;
        }
    }
}