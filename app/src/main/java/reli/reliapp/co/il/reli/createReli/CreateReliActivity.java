package reli.reliapp.co.il.reli.createReli;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.custom.CustomActivity;

public class CreateReliActivity extends CustomActivity {

    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reli);

        // Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_create_reli, new CreateDiscussionFragment())
                    .commit();
        }
    }

    /* ========================================================================== */

    @Override
    public void onBackPressed() {
        finish();
    }
}