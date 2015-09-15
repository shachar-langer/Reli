//package reli.reliapp.co.il.reli.main;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.location.Location;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.parse.FindCallback;
//import com.parse.ParseException;
//
//import java.util.List;
//
//import reli.co.co.il.reli.R;
//import reli.co.co.il.reli.dataStructures.Discussion;
//import reli.co.co.il.reli.dataStructures.ReliUser;
//import reli.co.co.il.reli.utils.Const;
//
//public class MainRelisAroundMeFragment extends Fragment {
//
//    /* ========================================================================== */
//
//    private static final int INNER_RADIUS = 50;
//    private static final int MIDDLE_RADIUS = 100;
//    private static final int OUTER_RADIUS = 150;
//
//    private View v;
//
//    /* ========================================================================== */
//
//    public MainRelisAroundMeFragment() {
//        // Required empty public constructor
//    }
//
//    /* ========================================================================== */
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    /* ========================================================================== */
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        v = inflater.inflate(R.layout.fragment_main_relis_around_me, container, false);
//
//        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.layout_around);
//        SimpleDrawingView sdv = new SimpleDrawingView(getActivity());
//
//        relativeLayout.addView(sdv);
//        updateNumberOfDiscussionsInRadius(R.id.textInnerCircle, INNER_RADIUS);
//        updateNumberOfDiscussionsInRadius(R.id.textMiddleCircle, MIDDLE_RADIUS);
//        updateNumberOfDiscussionsInRadius(R.id.textOuterCircle, OUTER_RADIUS);
//
//        return v;
//    }
//
//    /* ========================================================================== */
//
//    public class SimpleDrawingView extends View {
//        // setup initial color
//        private final int paintColor = Color.BLACK;
//        // defines paint and canvas
//        private Paint drawPaint;
//
//        private final int innerCircle_X = 550;
//        private final int innerCircle_Y = 650;
//        private final int innerCircle_Radius = 200;
//
//        private final int middleCircle_X = 550;
//        private final int middleCircle_Y = 650;
//        private final int middleCircle_Radius = 350;
//
//        private final int outerCircle_X = 550;
//        private final int outerCircle_Y = 650;
//        private final int outerCircle_Radius = 500;
//
//        /* ========================================================================== */
//
//        public SimpleDrawingView(Context context) {
//            super(context);
//            setFocusable(true);
//            setFocusableInTouchMode(true);
//            setupPaint();
//        }
//
//        /* ========================================================================== */
//
//        // Setup paint with color and stroke styles
//        private void setupPaint() {
//            drawPaint = new Paint();
//            drawPaint.setColor(paintColor);
//            drawPaint.setAntiAlias(true);
//            drawPaint.setStrokeWidth(5);
//            drawPaint.setStyle(Paint.Style.STROKE);
//            drawPaint.setStrokeJoin(Paint.Join.ROUND);
//            drawPaint.setStrokeCap(Paint.Cap.ROUND);
//        }
//
//        /* ========================================================================== */
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            canvas.drawCircle(outerCircle_X, outerCircle_Y, outerCircle_Radius, drawPaint);
//            canvas.drawCircle(middleCircle_X, middleCircle_Y, middleCircle_Radius, drawPaint);
//            canvas.drawCircle(innerCircle_X, innerCircle_Y, innerCircle_Radius, drawPaint);
//
//            ((RelativeLayout) v.findViewById(R.id.layout_around)).setOnTouchListener(new OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        float xPressed = event.getX(0);
//                        float yPressed = event.getY(0);
//
//                        boolean clickedOnACircle = false;
//                        int clickedRadius = 0;
//
//                        Intent intent = new Intent(getActivity(), MainRelisAroundMeActivity.class);
//
//                        // Click in the inner circle
//                        if ((xPressed > (innerCircle_X - innerCircle_Radius)) &&
//                                (xPressed < (innerCircle_X + innerCircle_Radius)) &&
//                                (yPressed > (innerCircle_Y - innerCircle_Radius)) &&
//                                (yPressed < (innerCircle_Y + innerCircle_Radius))) {
//
//                            clickedOnACircle = true;
//                            clickedRadius = INNER_RADIUS;
//                        }
//
//                        // Click in the middle circle
//                        if ((xPressed > (middleCircle_X - middleCircle_Radius)) &&
//                                (xPressed < (middleCircle_X + middleCircle_Radius)) &&
//                                (yPressed > (middleCircle_Y - middleCircle_Radius)) &&
//                                (yPressed < (middleCircle_Y + middleCircle_Radius))) {
//
//                            clickedOnACircle = true;
//                            clickedRadius = MIDDLE_RADIUS;
//                        }
//
//                        // Click in the outer circle
//                        if ((xPressed > (outerCircle_X - outerCircle_Radius)) &&
//                                (xPressed < (outerCircle_X + outerCircle_Radius)) &&
//                                (yPressed > (outerCircle_Y - outerCircle_Radius)) &&
//                                (yPressed < (outerCircle_Y + outerCircle_Radius))) {
//
//                            clickedOnACircle = true;
//                            clickedRadius = OUTER_RADIUS;
//                        }
//
//                        // Check if there was a click
//                        if (clickedOnACircle) {
//                            Location location = ((MainActivity) getActivity()).getLocation();
//                            if (location == null) {
//                                Toast.makeText(getActivity(), "Can not find your location", Toast.LENGTH_SHORT).show();
//                            } else {
//                                intent.putExtra(Const.LATITUDE, location.getLatitude());
//                                intent.putExtra(Const.LONGTITUDE, location.getLongitude());
//                                intent.putExtra(Const.RADIUS, clickedRadius);
//
////                                relativeLayout.removeAllViewsInLayout();
////                                FragmentManager fragmentManager = getFragmentManager();
////                                fragmentManager.beginTransaction()
////                                        .replace(R.id.blabla, new FaqFragment(), "AROUND")
////                                        .addToBackStack("AROUND")
////                                        .commit();
//
////                                startActivity(intent);
////                                getActivity().finish();
//                                return true;
//                            }
//                        }
//                    }
//
//                    return false;
//                }
//            });
//        }
//    }
//
//    /* ========================================================================== */
//
//    private void updateNumberOfDiscussionsInRadius(final int textId, int radius) {
//        final ProgressDialog dia = ProgressDialog.show(getActivity(), null, getString(R.string.alert_loading));
//
//        final View finalView = v;
//
//        ReliUser currentUser = MainActivity.user;
//        Discussion.getDiscussionQuery().whereWithinKilometers(Const.COL_DISCUSSION_LOCATION, currentUser.getLocation(), radius * 100)
//                .findInBackground(new FindCallback<Discussion>() {
//                    @Override
//                    public void done(List<Discussion> li, ParseException e) {
//                        // if (e != null) {
//                        ((TextView) finalView.findViewById(textId)).setText(Integer.toString(li.size()));
//                        dia.dismiss();
//                        //}
//                    }
//                });
//    }
//}