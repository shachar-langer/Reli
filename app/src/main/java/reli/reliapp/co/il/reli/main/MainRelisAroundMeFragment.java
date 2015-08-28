package reli.reliapp.co.il.reli.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.createReli.DiscussionActivity;
import reli.reliapp.co.il.reli.createReli.CreateReliActivity;
import reli.reliapp.co.il.reli.dataStructures.Discussion;
import reli.reliapp.co.il.reli.dataStructures.ReliUser;
import reli.reliapp.co.il.reli.utils.Const;
import reli.reliapp.co.il.reli.utils.Utils;

public class MainRelisAroundMeFragment extends Fragment {

    private static final int INNER_RADIUS = 50;
    private static final int MIDDLE_RADIUS = 100;
    private static final int OUTER_RADIUS = 150;

    View v;

    /* ========================================================================== */

    public MainRelisAroundMeFragment() {
        // Required empty public constructor
    }

    /* ========================================================================== */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* ========================================================================== */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_main_relis_around_me, container, false);
        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.blabla);
        relativeLayout.addView(new SimpleDrawingView(getActivity()));
        updateNumberOfDiscussionsInRadius(R.id.textInnerCircle, INNER_RADIUS);
        updateNumberOfDiscussionsInRadius(R.id.textMiddleCircle, MIDDLE_RADIUS);
        updateNumberOfDiscussionsInRadius(R.id.textOuterCircle, OUTER_RADIUS);
        return v;
    }

    /* ========================================================================== */

    public class SimpleDrawingView extends View {
        // setup initial color
        private final int paintColor = Color.BLACK;
        // defines paint and canvas
        private Paint drawPaint;

        private Context ctx;

        private int innerCircle_X = 550;
        private int innerCircle_Y = 650;
        private int innerCircle_Radius = 200;

        private int middleCircle_X = 550;
        private int middleCircle_Y = 650;
        private int middleCircle_Radius = 350;

        private int outerCircle_X = 550;
        private int outerCircle_Y = 650;
        private int outerCircle_Radius = 500;

        public SimpleDrawingView(Context context) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);
            setupPaint();
            ctx = context;
        }

        // Setup paint with color and stroke styles
        private void setupPaint() {
            drawPaint = new Paint();
            drawPaint.setColor(paintColor);
            drawPaint.setAntiAlias(true);
            drawPaint.setStrokeWidth(5);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(outerCircle_X, outerCircle_Y, outerCircle_Radius, drawPaint);
            canvas.drawCircle(middleCircle_X, middleCircle_Y, middleCircle_Radius, drawPaint);
            canvas.drawCircle(innerCircle_X, innerCircle_Y, innerCircle_Radius, drawPaint);

            ((RelativeLayout) v.findViewById(R.id.blabla)).setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        float xPressed = event.getX(0);
                        float yPressed = event.getY(0);

                        boolean clickedOnACircle = false;
                        int clickedRadius = 0;

                        Intent intent = new Intent(getActivity(), MainRelisAroundMeActivity.class);

                        if ((xPressed > (innerCircle_X - innerCircle_Radius)) &&
                                (xPressed < (innerCircle_X + innerCircle_Radius)) &&
                                (yPressed > (innerCircle_Y - innerCircle_Radius)) &&
                                (yPressed < (innerCircle_Y + innerCircle_Radius))) {
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!! inner");

                            clickedOnACircle = true;
                            clickedRadius = INNER_RADIUS;

//                            intent.putExtra(Const.RADIUS, innerRadius);
//                            startActivity(intent);
//                            return true;
                        }

                        if ((xPressed > (middleCircle_X - middleCircle_Radius)) &&
                                (xPressed < (middleCircle_X + middleCircle_Radius)) &&
                                (yPressed > (middleCircle_Y - middleCircle_Radius)) &&
                                (yPressed < (middleCircle_Y + middleCircle_Radius))) {
                            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ middle");

                            clickedOnACircle = true;
                            clickedRadius = MIDDLE_RADIUS;

//                            intent.putExtra(Const.RADIUS, middleRadius);
//                            startActivity(intent);
//                            return true;
                        }

                        if ((xPressed > (outerCircle_X - outerCircle_Radius)) &&
                                (xPressed < (outerCircle_X + outerCircle_Radius)) &&
                                (yPressed > (outerCircle_Y - outerCircle_Radius)) &&
                                (yPressed < (outerCircle_Y + outerCircle_Radius))) {
                            System.out.println("############################ outer");

                            clickedOnACircle = true;
                            clickedRadius = OUTER_RADIUS;

//                            intent.putExtra(Const.RADIUS, outerRadius);
//                            startActivity(intent);
//                            return true;
                        }

                        if (clickedOnACircle) {

                            Location location = ((MainActivity) getActivity()).getLocation();
                            if (location == null) {
                                Toast.makeText(getActivity(), "Can not find your location", Toast.LENGTH_SHORT).show();
                            } else {
                                intent.putExtra(Const.LATITUDE, location.getLatitude());
                                intent.putExtra(Const.ALTITUDE, location.getAltitude());
                                intent.putExtra(Const.RADIUS, clickedRadius);
                                startActivity(intent);
                                return true;
                            }
                        } else {
                            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ None");
                        }

                    }

                    return false;
                }
            });

        }

    }
        private void updateNumberOfDiscussionsInRadius(final int textId, int radius) {
            final ProgressDialog dia = ProgressDialog.show(getActivity(), null, getString(R.string.alert_loading));

            // TODO - change the name of the variable. Need to change in other fragments as well
            final View bla = v;

            // TODO - change the last argument we gave to whereWithinKilometers
            ReliUser currentUser = MainActivity.user;
            Discussion.getDiscussionQuery().whereWithinKilometers(Const.COL_DISCUSSION_LOCATION, currentUser.getLocation(), radius * 100)
                    .findInBackground(new FindCallback<Discussion>() {

                        @Override
                        public void done(List<Discussion> li, ParseException e) {
                            ((TextView) bla.findViewById(textId)).setText(Integer.toString(li.size()));
                            dia.dismiss();
                        }
                    });
        }
    }
