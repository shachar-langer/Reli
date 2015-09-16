package il.co.reli.sidebar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import il.co.reli.R;

public class FaqFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_faq, container, false);


        // Load the names of the possible Fragments in the drawer
        ArrayList<FaqItem> mFaqItems = getFaqItems();
        FaqListAdapter adapter = new FaqListAdapter(getActivity().getApplicationContext(), mFaqItems);

        ListView mDrawerList = (ListView) v.findViewById(R.id.faqList);
        mDrawerList.setAdapter(adapter);

        return v;
    }

    /**
     * onClick handler
     */
    public void toggle_contents(View v, TextView txt_help_gest){

        if (txt_help_gest.isShown()) {
            slide_up(getActivity().getApplicationContext(), txt_help_gest);
            txt_help_gest.setVisibility(View.GONE);
        }
        else {
            txt_help_gest.setVisibility(View.VISIBLE);
            slide_down(getActivity().getApplicationContext(), txt_help_gest);
        }
    }


    public static void slide_down(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    private class FaqListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<FaqItem> faqItems;

        public FaqListAdapter(Context context, ArrayList<FaqItem> faqItems) {
            mContext = context;
            this.faqItems = faqItems;
        }

        @Override
        public int getCount() {
            return faqItems.size();
        }

        @Override
        public Object getItem(int position) {
            return faqItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.faq_item, null);
            }
            else {
                view = convertView;
            }

            final TextView subtitleView = (TextView) view.findViewById(R.id.faq_item_content);
            subtitleView.setText( faqItems.get(position).mSubtitle );
            subtitleView.setVisibility(View.GONE);

            TextView titleView = (TextView) view.findViewById(R.id.faq_item_title);
            titleView.setText( faqItems.get(position).mTitle );
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle_contents(v, subtitleView);
                }
            });

            ImageView iv = (ImageView) view.findViewById(R.id.faq_item_arrow);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle_contents(v, subtitleView);
                }
            });

            return view;
        }
    }

        /* ========================================================================== */

    /**
     * Populate the drawer with the different possible fragments
     */
    private ArrayList<FaqItem> getFaqItems() {
        String[] faqMenuTitles = getResources().getStringArray(R.array.faq_questions);
        String[] faqMenuSubtitles = getResources().getStringArray(R.array.faq_answers);
        ArrayList<FaqItem> faqDrawerItems = new ArrayList<FaqItem>();

        for (int i = 0; i < faqMenuTitles.length; i++) {
            faqDrawerItems.add(new FaqItem(faqMenuTitles[i], faqMenuSubtitles[i]));
        }

        return faqDrawerItems;
    }
}
