package com.cellphones.mobilelunchmeet;


import android.content.Context;
import android.content.res.Configuration;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.view.MotionEvent;

public class SplashActivity extends Activity{
	private static final int splash_time = 5000;
	private boolean alive;
	private Activity this_reference;
	private View splashView;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		alive = true;
		this_reference = this;
		
		LayoutInflater inflater = getLayoutInflater();
	    splashView = inflater.inflate(R.layout.splash, null);
	    addContentView(splashView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
	    
		Thread SplashThread = new Thread() {  
			@Override
		    public void run() {
				try {
					int waited = 0;
		            while(alive && (waited < splash_time)) {
		            	sleep(100);
		                waited += 100;
		            }
		        } catch(InterruptedException e) {
		            // do nothing
		        } finally{
		            this_reference.finish();
		        }
			}
		 };
		SplashThread.start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        alive = false;
	    }
	    return true;
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig){
    	super.onConfigurationChanged(newConfig);
    }
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		((ViewGroup)splashView.getParent()).removeView(splashView);
	}
}
