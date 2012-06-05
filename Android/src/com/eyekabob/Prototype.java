/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

// This class just displays a mockup image
public class Prototype extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prototype_image_fullscreen);
        ImageView iv = (ImageView)findViewById(R.id.prototypeImageView);
        int resourceId = getIntent().getExtras().getInt("resourceId");
        iv.setImageResource(resourceId);
    }

}
