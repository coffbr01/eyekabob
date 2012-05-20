/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import com.eyekabob.util.EyekabobHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.View;

public class EyekabobActivity extends Activity {
	private Dialog alertDialog;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	Intent signInIntent = new Intent(this, SignInView.class);
    	startActivity(signInIntent);
    	return true;
    }

    // TODO: use dialogfragment to show dialog
    protected void createDialog(int message) {
    	if (alertDialog != null) {
    		alertDialog.setTitle(message);
    		return;
    	}

	    Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(message);
	    alertDialog = builder.create();
	    alertDialog.setOwnerActivity(this);
    }

    protected void showDialog() {
		alertDialog.show();
    }

    protected void dismissDialog() {
		alertDialog.dismiss();
    }

    public void adHandler(View v) {
        EyekabobHelper.launchEmail(this);
    }
}
