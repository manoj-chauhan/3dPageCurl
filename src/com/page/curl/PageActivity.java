package com.page.curl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

public class PageActivity extends Activity {

	
	private GLSurfaceView glSurface;
	private PageRenderer renderer;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		glSurface = new GLSurfaceView(this);
		renderer = new PageRenderer(this);
		glSurface.setRenderer(renderer);
		setContentView(glSurface);
	}
	
	float x1=0;
	float pos =0;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			x1=event.getX();
			pos = renderer.page.curlCirclePosition;
			break;
		case MotionEvent.ACTION_MOVE:
			if((event.getX()-x1)>0)
			renderer.page.curlCirclePosition+=(4.0f/(float)renderer.page.GRID);
			else
				renderer.page.curlCirclePosition-=(4.0f/(float)renderer.page.GRID);
			x1=event.getX();
			break;
		case MotionEvent.ACTION_UP:
			
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		glSurface.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		glSurface.onPause();
	}
	
}