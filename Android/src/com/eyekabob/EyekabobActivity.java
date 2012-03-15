package com.eyekabob;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

public class EyekabobActivity extends Activity {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
