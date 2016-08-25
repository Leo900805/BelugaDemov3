package com.hosengamers.beluga.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.hosengamers.beluga.R;



public class BelugaService extends Service implements OnClickListener {
	
	private WindowManager windowManager;
	private ImageButton serviceAccBtn, b4;
	private FrameLayout fl, flBar, flLeft, flRight;
	WindowManager.LayoutParams params, flParams, flLeftParams, flRightParams;
	RelativeLayout serviceRelativeLayout;
	DisplayMetrics dm;
	int screenWidth;  
    int screenHeight;
    boolean flFlag = false, glFlag = false, flLeftFlag = false, flRightFlag = false;
    Bundle b;
    
	@SuppressLint("RtlHardcoded")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		
		
		dm = getResources().getDisplayMetrics();  
		screenWidth = dm.widthPixels;  
        screenHeight = dm.heightPixels;              	
  
        
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		 
		params= new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,				
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.CENTER | Gravity.LEFT;
		params.x = 0;
		params.y = 100;
		
		flParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);		
		flParams.gravity = Gravity.CENTER;
		
		flLeftParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		flLeftParams.alpha = 400;
		flLeftParams.x = 0;
		flLeftParams.y = 100;
		flLeftParams.gravity =  Gravity.CENTER |Gravity.LEFT;
		
		
		flRightParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		flRightParams.alpha = 400;
		flRightParams.x = 0;
		flRightParams.y = 100;
		flRightParams.gravity = Gravity.CENTER |Gravity.RIGHT;
		
		
		
		LayoutInflater inflater = LayoutInflater.from(this);
		fl = (FrameLayout)inflater.inflate(R.layout.service_layout, null);
		flLeft = (FrameLayout)inflater.inflate(R.layout.left_point_frame, null);
		flRight = (FrameLayout)inflater.inflate(R.layout.right_point_frame, null);
		fl.setOnClickListener(this);
		flLeft.setOnClickListener(this);
		flRight.setOnClickListener(this);
		
		flBar = (FrameLayout)inflater.inflate(R.layout.service_bar_layout, null);
		b4 = (ImageButton)flBar.findViewById(R.id.close_service_btn);
		b4.setOnClickListener(this);
		
		serviceAccBtn = (ImageButton)flBar.findViewById(R.id.service_account_btn);
		serviceAccBtn.setOnClickListener(this);
		
		flLeft.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
					flFlag = true;
					windowManager.addView(fl, params);
					flLeftFlag = false;
					windowManager.removeView(flLeft);
					
					break;	
				}
				return false;
			}
			
		});
		
		flRight.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
					flFlag = true;
					windowManager.addView(fl, params);
					flRightFlag = false;
					windowManager.removeView(flRight);
					break;	
				}
				return false;
			}
			
		});
		
		fl.setOnTouchListener(new OnTouchListener(){
			
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					initialX = params.x;
					initialY = params.y;
					initialTouchX = event.getRawX();
					initialTouchY = event.getRawY();
					//return true;
				case MotionEvent.ACTION_UP:
					
					float x2 = event.getRawX();
	                float y2 = event.getRawY();
					 if (params.x < (screenWidth / 2)) {
						 //go left
						 params.x = 0;
						 windowManager.updateViewLayout(fl, params);

	                  } else if(params.x > (screenWidth / 2)){
	                	  //go right
	                	  params.x = screenWidth;
	                	  windowManager.updateViewLayout(fl, params);

	                  }
					 double distance = Math.sqrt(Math.abs(initialTouchX-x2)*Math.abs(initialTouchX-x2)+Math.abs(initialTouchY-y2)*Math.abs(initialTouchY-y2));//两点之间的距离
					 // Math.sqrt(Math.abs(x1-x2)*Math.abs(x1-x2)+Math.abs(y1-y2)*Math.abs(y1-y2));//两点之间的距离
					 if (distance < 15) { // 距离较小，当作click事件来处理 
	                     //showToastDialog("点击了");
						 Log.i("service","clicked...");
	                    return false;
	                 } else {
	                	 Log.i("service","move...");
	                     //showToastDialog("滑动了");
	                	 if (params.x < (screenWidth / 2)) {
							 //go left
	                		 flLeftFlag = true;
	                		 flLeftParams.y = params.y;
	                		windowManager.addView(flLeft, flLeftParams);
	     					windowManager.removeView(fl);
	     					flFlag = false;
		                  } else if(params.x > (screenWidth / 2)){
		                	  //go right
		                	  flRightFlag = true;
		                	  flRightParams.y = params.y;
		                	  windowManager.addView(flRight, flRightParams);
		     				  windowManager.removeView(fl);
		     				  flFlag = false;

		                  }
	                    return true ;
	                }
					 
					//return false;
					
				case MotionEvent.ACTION_MOVE:
					params.x = initialX
							+ (int) (event.getRawX() - initialTouchX);
					params.y = initialY
							+ (int) (event.getRawY() - initialTouchY);
					windowManager.updateViewLayout(fl, params);
					//return true;
				}
				
				return false;
			}
			
		});
	
		//flFlag = true;
		//windowManager.addView(fl, params);
		flLeftFlag = true;
		windowManager.addView(flLeft, flLeftParams);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		b = (Bundle) intent.getExtras().get("data");
		return super.onStartCommand(intent, flags, startId);
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (flFlag == true){
			windowManager.removeView(fl);
		}
		if (glFlag == true){
			windowManager.removeView(flBar);
		}
		if (flLeftFlag == true){
			windowManager.removeView(flLeft);
		}
		if (flRightFlag == true){
			windowManager.removeView(flRight);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.i("service","clicked...");
		
		if(v.getId() == R.id.service_frame_layout){
			if (fl != null){
				windowManager.removeView(fl);
				flFlag = false; 
				windowManager.addView(flBar, flParams);
				glFlag = true;
			}
			
		}else if(v.getId() == R.id.close_service_btn){
			if (flBar != null){
				windowManager.removeView(flBar);
				glFlag = false;
				windowManager.addView(fl, params);
				flFlag = true;
			}
		}else if(v.getId() == R.id.service_account_btn){
			Log.i("service","click service_account_btn");
			windowManager.removeView(flBar);
			glFlag = false;
			windowManager.addView(fl, params);
			flFlag = true;

			Intent i = new Intent(this, com.hosengamers.beluga.service.BelugaServiceActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtras(b);
	        startActivity(i);

		}
			
	}

}
