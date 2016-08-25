package com.hosengamers.beluga.loginpage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.hosengamers.beluga.R;
import com.hosengamers.beluga.belugakeys.Keys;
import com.hosengamers.beluga.loginpage.AuthHttpClient.OnAuthEventListener;
import com.hosengamers.beluga.loginpage.datacontrol.GameBackground;
import com.hosengamers.beluga.loginpage.datacontrol.InformationProcess;
import com.hosengamers.beluga.loginpage.datacontrol.UsedString;
/**
 * Created by Leo on 2015/10/5.
 */
public class Changepassword extends Activity implements OnClickListener{
	
	final static int DELAY_TIME = 650; 
	private static final float CONSTANT_INCHES = 7;
	
    private EditText  inputpassword ,inputnewpassword,inputdeterminepassword;
    private TextView inputaccount;
    private AuthHttpClient authhttpclient;
    private ImageButton modReturnBtn, modComirmBtn;
    private Animation selectedMoveLeft, selectedMoveRight,scaleHide;
    private RelativeLayout modifyPwdRelativeLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        Point point = new Point();  
        getWindowManager().getDefaultDisplay().getRealSize(point);  
        DisplayMetrics dm = getResources().getDisplayMetrics();  
        double x = Math.pow(point.x/ dm.xdpi, 2);  
        double y = Math.pow(point.y / dm.ydpi, 2);  
        double screenInches = Math.sqrt(x + y);  
        Log.d("sign up OnCreate", "Screen inches : " + screenInches); 
        

        if(screenInches > CONSTANT_INCHES){
        	this.setContentView(R.layout.modify_pwd_page_large_size);
        }else{
        	this.setContentView(R.layout.modify_pwd_page_v2);
        }
        
        this.modifyPwdRelativeLayout = (RelativeLayout)findViewById(R.id.modify_bg_side);
        this.modifyPwdRelativeLayout.setBackgroundResource(GameBackground.GAME_BACKGROUND);
        selectedMoveLeft = AnimationUtils.loadAnimation(this, R.anim.anim_selected_move_left_effect);
        selectedMoveRight= AnimationUtils.loadAnimation(this, R.anim.anim_selected_move_right_effect);
        this.scaleHide = AnimationUtils.loadAnimation(this, R.anim.anim_scale_hide);
        
        this.modComirmBtn = (ImageButton)this.findViewById(R.id.modcomfirmbtn);
        this.modComirmBtn.setOnClickListener(this);
        this.modReturnBtn = (ImageButton)this.findViewById(R.id.modreturnbtn);
        this.modReturnBtn.setOnClickListener(this);
        inputpassword = (EditText)this.findViewById(R.id.modpwdeditText);
        inputaccount =  (TextView)this.findViewById(R.id.modacctextView);
        inputdeterminepassword = (EditText)this.findViewById(R.id.modretypepwdeditText);
        inputnewpassword = (EditText)this.findViewById(R.id.modnewpwdeditText);

        inputaccount.setText(InformationProcess.getAccountString(this));
        inputpassword.setText(InformationProcess.getPasswordString(this));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.modreturnbtn) {
        	this.modReturnBtn.startAnimation(selectedMoveRight);
        	this.modComirmBtn.startAnimation(scaleHide);
        	
        	final Handler handler = new Handler();
       	 	handler.postDelayed(new Runnable() {
       	     @Override
       	     public void run() {
       	         // Do something after 700ms
       	    	//unlock can't edit and click
       	    	finish();
       	     }
       	 	}, DELAY_TIME);

        } else if (i == R.id.modcomfirmbtn) {
            ChangeAccountPassword();
        }
    }


    private void ChangeAccountPassword()
    {
       
        authhttpclient = new AuthHttpClient(this);
       
        authhttpclient.AuthEventListener(new OnAuthEventListener(){
            public void onProcessDoneEvent(int Code,String Message)
            {
                String CodeStr = UsedString.getChangePasswordString(getApplicationContext(), Code);
                if(CodeStr.compareTo("")==0){
                    Toast.makeText(Changepassword.this, Message, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(Changepassword.this, CodeStr, Toast.LENGTH_LONG).show();
                }

            }

			

			@Override
			public void onProcessDoneEvent(Bundle bundle) {
				// TODO Auto-generated method stub
				
				String jsonData = bundle.getString(Keys.JsonData.toString());
				JSONObject jObj;
				try {
					jObj = new JSONObject( jsonData );
					
					Log.i("Change pwd page", "Change pwd page got json:"+jObj.toString());
					
					int Code = jObj.getInt("code");
					String Message = jObj.getString("message");
					String CodeStr = UsedString.getChangePasswordString(getApplicationContext(), Code);
	                if(CodeStr.compareTo("")==0){
	                    Toast.makeText(Changepassword.this, Message, Toast.LENGTH_LONG).show();
	                }else if(Code == 1){
	                    Toast.makeText(Changepassword.this, CodeStr, Toast.LENGTH_LONG).show();
	                    modComirmBtn.startAnimation(selectedMoveLeft);
	                    modReturnBtn.startAnimation(scaleHide);
	                	
	                	final Handler handler = new Handler();
	               	 	handler.postDelayed(new Runnable() {
	               	     @Override
	               	     public void run() {
	               	         // Do something after 700ms
	               	    	//unlock can't edit and click
	               	    	SetFinish(inputaccount.getText().toString(),inputnewpassword.getText().toString());
	               	     }
	               	 	}, DELAY_TIME);
	                    
	                }else{
	                    Toast.makeText(Changepassword.this, CodeStr, Toast.LENGTH_LONG).show();
	                }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
        });
        
        authhttpclient.Auth_ChangePassword(this.inputaccount.getText().toString(),this.inputpassword.getText().toString(),this.inputdeterminepassword.getText().toString());
    }

    private void SetFinish(String thisuserid,String thisuid)
    {
        Intent resultdata = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("ResultType",2);
        bundle.putString("userid", thisuserid);
        bundle.putString("userpwd", thisuid);
        resultdata.putExtras(bundle);
        this.setResult(Activity.RESULT_OK, resultdata);
        finish();
    }
}
