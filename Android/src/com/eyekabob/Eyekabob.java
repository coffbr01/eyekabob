package com.eyekabob;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Eyekabob extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void findMusicHandler(View v) {
        Toast.makeText(v.getContext(), "WOOT", 3000).show();
    }
}
