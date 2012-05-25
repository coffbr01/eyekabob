/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.facebook.DialogError;
import com.eyekabob.util.facebook.Facebook;
import com.eyekabob.util.facebook.Facebook.DialogListener;
import com.eyekabob.util.facebook.FacebookError;

public class SignInView extends EyekabobActivity {
    private static final String ACCESS_TOKEN_KEY = "fb_access_token";
    private static final String EXPIRES_TOKEN_KEY = "fb_access_expires";
    private static SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        if (isSessionValid()) {
            startActivity(new Intent(this, EditView.class));
            this.finish();
            return;
        }

        signIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EyekabobHelper.Facebook.getInstance().authorizeCallback(requestCode, resultCode, data);
    }

    protected void signIn() {
        final Facebook fb = EyekabobHelper.Facebook.getInstance();
        fb.authorize(this, new String[]{}, new DialogListener() {
            public void onComplete(Bundle values) {
                SharedPreferences.Editor prefs = getPrefs().edit();
                prefs.putString(ACCESS_TOKEN_KEY, fb.getAccessToken());
                prefs.putLong(EXPIRES_TOKEN_KEY, fb.getAccessExpires());
                prefs.commit();
                SignInView.this.startActivity(new Intent(SignInView.this, EditView.class));
                SignInView.this.finish();
                // TODO: there is a bug here. if a fb user changes his/her password or
                // disallows this app, this will fail.
            }

            public void onFacebookError(FacebookError e) {
                Toast.makeText(SignInView.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            public void onError(DialogError e) {
                Toast.makeText(SignInView.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            public void onCancel() {
            }
        });
    }

    protected boolean isSessionValid() {
        SharedPreferences prefs = getPrefs();

        String access_token = prefs.getString(ACCESS_TOKEN_KEY, null);
        long expires = prefs.getLong(EXPIRES_TOKEN_KEY, 0);
        Facebook fb = EyekabobHelper.Facebook.getInstance();

        if (access_token != null) {
            fb.setAccessToken(access_token);
        }
        if (expires != 0) {
            fb.setAccessExpires(expires);
        }

        return fb.isSessionValid();
    }

    protected SharedPreferences getPrefs() {
        if (preferences == null) {
            preferences = getPreferences(Context.MODE_PRIVATE);
        }
        return preferences;
    }
}
