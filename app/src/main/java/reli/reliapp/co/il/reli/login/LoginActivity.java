package reli.reliapp.co.il.reli.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Profile;

import android.support.v4.app.FragmentActivity;

import reli.reliapp.co.il.reli.main.MainActivity;
import reli.reliapp.co.il.reli.R;


public class LoginActivity extends FragmentActivity {

    /* ========================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Fragment
        if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }

        // TODO -  delete
        Button b = (Button) findViewById(R.id.test_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Profile.getCurrentProfile() == null) {
                    Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), Profile.getCurrentProfile().getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button b2 = (Button) findViewById(R.id.continue_button);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /* ========================================================================== */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /* ========================================================================== */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}