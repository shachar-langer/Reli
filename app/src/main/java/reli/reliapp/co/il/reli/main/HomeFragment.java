package reli.reliapp.co.il.reli.main;

import java.util.Locale;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.viewpagerindicator.CirclePageIndicator;

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
        getActivity().getActionBar().setTitle(getString(R.string.middle_tab));

        //Bind the title indicator to the adapter
        CirclePageIndicator titleIndicator = (CirclePageIndicator) v.findViewById(R.id.circles_title);
        titleIndicator.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                String title = "";
                switch (position){
                    case 0:
                        title = getString(R.string.left_tab);
                        break;
                    case 1:
                        title = getString(R.string.middle_tab);
                        break;
                    case 2:
                        title = getString(R.string.right_tab);
                        break;
                }

                getActivity().getActionBar().setTitle(title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

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
    }
}