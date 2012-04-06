package com.eyekabob;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

public class EyekabobActivity extends Activity {
	private Dialog alertDialog;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // TODO: use dialogfragment to show dialog
    protected void createDialog(int message) {
	    Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(message);
	    builder.setCancelable(false);
	    alertDialog = builder.create();
	    alertDialog.setOwnerActivity(this);
    }

    protected void showDialog() {
    	if (!alertDialog.isShowing()) {
    		alertDialog.show();
    	}
    }

    protected void dismissDialog() {
    	if (alertDialog.isShowing()) {
    		alertDialog.dismiss();
    	}
    }
}
