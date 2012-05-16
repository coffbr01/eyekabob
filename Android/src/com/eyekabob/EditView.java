package com.eyekabob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EditView extends EyekabobActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_view);
	}

	public void addArtistButtonHandler(View v) {
		startActivity(new Intent(this, AddBand.class));
	}
}
