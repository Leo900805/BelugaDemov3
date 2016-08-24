package com.hosengamers.chee.share;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.Sharer.Result;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.hosengamers.chee.belugakeys.Keys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class FacebookShare extends FragmentActivity {
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        
        
		
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        /*
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {



			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(FacebookException error) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Result result) {
				// TODO Auto-generated method stub
				
			} 
		});
		*/
        Intent intent = getIntent();  
		String shareContentTitle = intent.getStringExtra(Keys.ShareContentTitle.toString());
		String shareContentDescription = intent.getStringExtra(Keys.ShareContentDescription.toString());
		String shareContentUrl = intent.getStringExtra(Keys.ShareContentUrl.toString());
		Log.i("FBShare", "shareContentTitle:"+shareContentUrl);
		String shareImageUrl = intent.getStringExtra(Keys.ShareImageUrl.toString());
        
        if (ShareDialog.canShow(ShareLinkContent.class)) {
        	
	        ShareLinkContent linkContent = new ShareLinkContent.Builder()
	                .setContentTitle(shareContentTitle)
	                .setContentDescription(shareContentDescription)
	                .setContentUrl(Uri.parse(shareContentUrl))
	                .setImageUrl(Uri.parse(shareImageUrl))
	                .build();

	        shareDialog.show(linkContent);
	    }
        finish();
    }
    
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    
    
}


