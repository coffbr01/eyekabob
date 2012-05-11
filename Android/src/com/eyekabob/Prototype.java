package com.eyekabob;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class Prototype extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prototype_image_fullscreen);
        ImageView iv = (ImageView)findViewById(R.id.prototypeImageView);
        int resourceId = getIntent().getExtras().getInt("resourceId");
        iv.setImageResource(resourceId);
	}

}
