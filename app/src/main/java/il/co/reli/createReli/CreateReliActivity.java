package il.co.reli.createReli;

import android.os.Bundle;

import il.co.reli.R;
import il.co.reli.custom.CustomActivity;

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