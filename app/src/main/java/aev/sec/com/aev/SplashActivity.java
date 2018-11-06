package aev.sec.com.aev;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import aev.sec.com.aev.sharedPreference.SharedPreferenceUtility;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_TIME_OUT = 2;
    private SharedPreferenceUtility mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSharedPreferences=new SharedPreferenceUtility(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
     if(!mSharedPreferences.getUserEmail().isEmpty()) {
         startMainActivity();
     }else
     {
         startLoginActivity();
     }

            }
        },2000);
    }

    private void startLoginActivity() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

    private void startMainActivity() {
        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        finish();
    }
}
