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

		//appLinkUrl ="https://fb.me/320004664998837";
		//previewImageUrl = "https://lh3.googleusercontent.com/8yh5cZfJpWbUADdb3zF_XF-zxVFhc7-w7nl1sMMZFdI3UgidVLsDZ7LXeChz5C_X8GQ=w300-rw";

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
						Log.i(TAG, "error");
						finish();
					}



					@Override
					public void onSuccess(Result result) {
						// TODO Auto-generated method stub
						Log.i(TAG, "result:"+result.toString());
						finish();
					}



					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						Log.i(TAG, "cancel");
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
