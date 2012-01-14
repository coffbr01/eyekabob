package com.eyekabob;

import android.os.Bundle;

import com.phonegap.DroidGap;

public class Eyekabob extends DroidGap {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUrl("file:///android_asset/www/index.html");
    }
}
