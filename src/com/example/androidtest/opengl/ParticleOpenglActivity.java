package com.example.androidtest.opengl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class ParticleOpenglActivity extends Activity {
	GLSurfaceView gView;
	private ParticleRenderer particleRenderer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initBitmap.init(this.getResources());
		gView = new GLSurfaceView(this);
		particleRenderer = new ParticleRenderer();
		gView.setRenderer(particleRenderer);
		setContentView(gView);
	}
}
