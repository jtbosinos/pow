package pow.unionbankph.com.pow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class Splash extends Activity {

    private static final long SPLASH_DISPLAY_LENGTH = 3000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);

        ImageView splashImage = (ImageView) findViewById(R.id.splashView);

        int orient = getResources().getConfiguration().orientation;
        Log.d("orient",String.valueOf(orient));
        if (orient == 1) {
            splashImage.setBackgroundResource(R.drawable.splash_screen);
        } else {
            //splashImage.setBackgroundResource(R.drawable.splash_screen_landscape);
        }

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(Splash.this,LoginActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}