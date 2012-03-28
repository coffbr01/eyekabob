package com.eyekabob;

import android.content.Context;
import android.util.AttributeSet;

public class ListFadeImage extends ScaledImage {

    public ListFadeImage(Context context) {
	    super(context);
	    setLogo(R.drawable.bottom_fade);
    }

    public ListFadeImage(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    setLogo(R.drawable.bottom_fade);
    }

    public ListFadeImage(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    setLogo(R.drawable.bottom_fade);
    }
}
