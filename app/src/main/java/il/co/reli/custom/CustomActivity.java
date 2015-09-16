package il.co.reli.custom;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import il.co.reli.R;
import il.co.reli.utils.TouchEffect;

/**
 * This is a common activity that all other activities of the app can extend to
 * inherit the common behaviors like implementing a common interface that can be
 * used in all child activities.
 */
public class CustomActivity extends AppCompatActivity implements OnClickListener
{
	/**
	 * Apply this Constant as touch listener for views to provide alpha touch
	 * effect. The view must have a Non-Transparent background.
	 */
	public static final TouchEffect TOUCH = new TouchEffect();

    /* ========================================================================== */

    @Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);
		setupActionBar();
	}

    /* ========================================================================== */

    /**
	 * This method will setup the top title bar (Action bar) content and display
	 * values. It will also setup the custom background theme for ActionBar. You
	 * can override this method to change the behavior of ActionBar for
	 * particular Activity
	 */
	protected void setupActionBar()
	{
		final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		if (actionBar == null) {
            return;
        }

		actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);

        try {
            actionBar.setIcon(getResources().getColor(R.color.transparent));
        } catch (Exception e) {

        }
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
	}


    /* ========================================================================== */

    /**
	 * Sets the touch and click listener for a view with given id.
	 */

	public View setTouchNClick(int id)
	{
		View v = setClick(id);
		if (v != null) {
            v.setOnTouchListener(TOUCH);
        }

		return v;
	}

    /* ========================================================================== */

    /**
	 * Sets the click listener for a view with given id.
	 */
	public View setClick(int id)
	{
		View v = findViewById(id);
		if (v != null) {
            v.setOnClickListener(this);
        }

		return v;
	}

    /* ========================================================================== */

	@Override
	public void onClick(View v)
	{
	}
}