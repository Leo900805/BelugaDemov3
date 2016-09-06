package com.hosengamers.beluga.invite;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.AppInviteDialog.Result;
import com.hosengamers.beluga.belugakeys.Keys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class FacebookFriendsInviteActivity extends Activity {
	final String TAG = "FacebookFriendsInviteActivity";
	CallbackManager sCallbackManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(this.getApplicationContext());
		Intent intent = getIntent();  
		String appLinkUrl = intent.getStringExtra(Keys.AppLinkUrl.toString());
		String previewImageUrl = intent.getStringExtra(Keys.AppLinkPreviewImageUrl.toString());;



		    if (AppInviteDialog.canShow()) {
		        AppInviteContent content = new AppInviteContent.Builder()
		                    .setApplinkUrl(appLinkUrl)
		                    .setPreviewImageUrl(previewImageUrl)
		                    .build();
		        
		        
		        AppInviteDialog appInviteDialog = new AppInviteDialog(this);
		        sCallbackManager = CallbackManager.Factory.create();
		        appInviteDialog.registerCallback(sCallbackManager, new FacebookCallback<AppInviteDialog.Result>()
		        {
		           

					@Override
					public void onError(FacebookException error) {
						// TODO Auto-generated method stub
						JSONObject json  = new JSONObject();
						Intent resultdata = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("type", "GAME_INVITE");
						try {
							json.put("status", "error");
							json.put("code", "-1");
							bundle.putString(Keys.JsonData.toString(), json.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						resultdata.putExtras(bundle);
						setResult(Activity.RESULT_OK, resultdata); //回傳RESULT_OK
						finish();
					}



					@Override
					public void onSuccess(Result result) {
						// TODO Auto-generated method stub
						JSONObject json  = new JSONObject();
						Intent resultdata = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("type", "GAME_INVITE");
						try {
							json.put("status", "success");
							json.put("code", "1");
							bundle.putString(Keys.JsonData.toString(), json.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						resultdata.putExtras(bundle);
						setResult(Activity.RESULT_OK, resultdata); //回傳RESULT_OK
						finish();
					}



					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						JSONObject json  = new JSONObject();
						Intent resultdata = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("type", "GAME_INVITE");
						try {
							json.put("status", "cancel");
							json.put("code", "0");
							bundle.putString(Keys.JsonData.toString(), json.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						resultdata.putExtras(bundle);
						setResult(Activity.RESULT_OK, resultdata); //回傳RESULT_OK
						finish();
					}
		        });

		        //AppInviteDialog.show(this, content);
		        appInviteDialog.show(content);
		        Log.i("fb invite", "appInviteDialog.getRequestCode():"+appInviteDialog.getRequestCode());
		        //finish();
		    }
		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		 sCallbackManager.onActivityResult(requestCode, resultCode,data);
	}
	
	
}
