package com.eyekabob;

import android.content.Context;
import android.util.AttributeSet;

public class AdBanner extends ScaledImage {

    public AdBanner(Context context) {
	    super(context);
	    setLogo(R.drawable.advertisement);
    }

    public AdBanner(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    setLogo(R.drawable.advertisement);
    }

    public AdBanner(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    setLogo(R.drawable.advertisement);
    }
}
