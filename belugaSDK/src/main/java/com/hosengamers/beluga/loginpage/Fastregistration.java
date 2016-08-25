package com.hosengamers.beluga.loginpage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
public class Fastregistration extends Activity implements OnClickListener{

	final static int DELAY_TIME = 650; 
	private static final float CONSTANT_INCHES = 7;
	
    private EditText inputaccount,inputpassword;
    private AuthHttpClient authhttpclient;
    private ImageButton qsReturnBtn, qsComfirmBtn;
    private CheckBox checkBox;
    private Animation selectedMoveLeft, selectedMoveRight,scaleHide;
    private RelativeLayout qSignUpRelativeLayout;
    private String uid;

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
        	this.setContentView(R.layout.quick_sign_up_page_large_size);
        }else{
        	this.setContentView(R.layout.quick_sign_up_page_v2);
        }

        
        selectedMoveLeft = AnimationUtils.loadAnimation(this, R.anim.anim_selected_move_left_effect);
        selectedMoveRight= AnimationUtils.loadAnimation(this, R.anim.anim_selected_move_right_effect);
        this.scaleHide = AnimationUtils.loadAnimation(this, R.anim.anim_scale_hide);
        
        this.qSignUpRelativeLayout = (RelativeLayout)findViewById(R.id.quick_sign_up_bg_side);
        qSignUpRelativeLayout.setBackgroundResource(GameBackground.GAME_BACKGROUND);
        
        this.qsComfirmBtn = (ImageButton)this.findViewById(R.id.qscomfirmbtn);
        this.qsComfirmBtn.setOnClickListener(this);
        this.qsReturnBtn = (ImageButton)this.findViewById(R.id.qsreturnbtn);
        this.qsReturnBtn.setOnClickListener(this);
        
        inputpassword = (EditText)this.findViewById(R.id.qspwdeditText);
        inputaccount = (EditText)this.findViewById(R.id.qsacceditText);
        this.checkBox = (CheckBox)this.findViewById(R.id.qscheckBox);
        String source= this.getString(R.string.I_Agree_and_Read_Type)+ "<u><font color='red'>"+
                this.getString(R.string.Membership_Policy_Type)+ "</font></u>";
        checkBox.setText(Html.fromHtml(source));
        checkBox.setOnClickListener(this);
        
        
        CreateHttpClient();
        authhttpclient.Auth_QuickAccount();
    }

    private void CreateHttpClient(){

      
        authhttpclient = new AuthHttpClient(this);
      
        authhttpclient.AuthEventListener(new OnAuthEventListener() {
            public void onProcessDoneEvent(int Code, String Message) {
                String CodeStr = UsedString.getFastRegistrationGenerateString(getApplicationContext(), Code);
                if (CodeStr.compareTo("") == 0 && Code != 1) {
                    //Looper.prepare();
                    Toast.makeText(Fastregistration.this, Message, Toast.LENGTH_SHORT).show();
                    //Looper.loop();
                }else {
                    Toast.makeText(Fastregistration.this, CodeStr, Toast.LENGTH_LONG).show();
                }
            }

			@Override
			public void onProcessDoneEvent(Bundle bundle) {
				// TODO Auto-generated method stub
				
					String jsonData = bundle.getString(Keys.JsonData.toString());
					JSONObject jObj;
					try {
						jObj = new JSONObject( jsonData );
						
						Log.i("Fast regis page", "Fast regis page got json:"+jObj.toString());
						
						int Code = jObj.getInt("code");
						uid = jObj.getString("uid");
						String Account = jObj.getString("userid");
						String Pwd = jObj.getString("pwd");
						String Message = jObj.getString("message");
						String CodeStr = UsedString.getFastRegistrationGenerateString(getApplicationContext(), Code);
		                if (CodeStr.compareTo("") == 0 && Code != 1) {
		                    //Looper.prepare();
		                    Toast.makeText(Fastregistration.this, Message, Toast.LENGTH_SHORT).show();
		                    //Looper.loop();
		                } else if (Code == 1) {
		                    Log.i("FastResg","CrateHttpClient got Account value is:"+Account);
		                    inputaccount.setText(Account);
		                    Log.i("FastResg", "CrateHttpClient got password value is:" + Pwd);
		                    inputpassword.setText(Pwd);
		                } else {
		                    Toast.makeText(Fastregistration.this, CodeStr, Toast.LENGTH_LONG).show();
		                }
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			}
        });
    }




    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.qscheckBox) {
            if (v.getClass() == CheckBox.class) {
                ((CheckBox) v).setChecked(true);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.belugame.com/clause.asp"));
            startActivity(intent);

        } else if (i == R.id.qscomfirmbtn) {
        	//Log.i("fast regis", "comfirm clicked...");
        	
            InformationProcess.saveUserUid(uid, Fastregistration.this);
            
            qsComfirmBtn.startAnimation(selectedMoveLeft);
        	qsReturnBtn.startAnimation(scaleHide);
        	
        	final Handler handler = new Handler();
       	 	handler.postDelayed(new Runnable() {
       	     @Override
       	     public void run() {
       	         // Do something after 700ms
       	    	//unlock can't edit and click
       	    	SetFinish(inputaccount.getText().toString(), inputpassword.getText().toString());
       	     }
       	 	}, DELAY_TIME);
        	
        } else if (i == R.id.qsreturnbtn) {
        	
        	Log.i("fastReg return", "finish() start");
            
            this.qsReturnBtn.startAnimation(selectedMoveRight);
        	this.qsComfirmBtn.startAnimation(scaleHide);
        	
        	final Handler handler = new Handler();
       	 	handler.postDelayed(new Runnable() {
       	     @Override
       	     public void run() {
       	         // Do something after 700ms
       	    	//unlock can't edit and click
       	    	finish();
       	     }
       	 	}, DELAY_TIME);
            
            Log.i("fastReg return", "finish() end");

        } 
    }

    private void SetFinish(String thisuserid,String thisuid)
    {
    	Log.i("fastReg set finish", "start...");
        Intent resultdata = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("ResultType",1);
        Log.i("fastReg set finish", "userid："+ thisuserid);
        Log.i("fastReg set finish", "userpwd："+ thisuid);
        bundle.putString("userid", thisuserid);
        bundle.putString("userpwd", thisuid);
        resultdata.putExtras(bundle);

        if (getParent() == null) {
            setResult(Activity.RESULT_OK, resultdata);
            Log.d("tag", "F resultdata: "+resultdata);
        } else {
            getParent().setResult(Activity.RESULT_OK, resultdata);
        }
        finish();
        Log.i("fastReg sset finish", "end...");
    }

}
