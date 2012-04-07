/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class ScaledImage extends View {

    private Drawable logo;

    public ScaledImage(Context context) {
	    super(context);
    }

    public ScaledImage(Context context, AttributeSet attrs) {
	    super(context, attrs);
    }

    public ScaledImage(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
    }

    public void setLogo(int logoId) {
	    logo = getContext().getResources().getDrawable(logoId);
	    setBackgroundDrawable(logo);
    }

    public Drawable getLogo() {
    	return logo;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    int width = MeasureSpec.getSize(widthMeasureSpec);
	    int height = width * getLogo().getIntrinsicHeight() / getLogo().getIntrinsicWidth();
	    setMeasuredDimension(width, height);
    }
}
