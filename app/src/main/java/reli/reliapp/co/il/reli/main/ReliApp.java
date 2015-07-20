package reli.reliapp.co.il.reli.main;

import android.app.Application;
import android.widget.Toast;

import com.parse.Parse;

public class ReliApp extends Application {

    private final String PARSE_APP_ID = "jmxBnVR9fWE39FZxG5tCuVCVwpMp7j62DjdjTFj2";
    private final String PARSE_CLIENT_KEY = "LILbHV2CYBZp10mqzXUKr1lwGv2Oguo82dvp5c2P";

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getApplicationContext(), "Hello from ReliApp", Toast.LENGTH_SHORT).show();
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
    }
}
