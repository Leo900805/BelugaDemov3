package com.hosengamers.beluga.loginpage;

import org.json.JSONObject;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hosengamers.beluga.loginpage.datacontrol.InformationProcess;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class FacebookInfoManager {
	
	private CallbackManager callbackManager;
	private AccessToken accessToken;
	private AuthHttpClient authhttpclient;
	private Activity act;
	private String FaceboookId;
	
	protected FacebookInfoManager(Activity act, AuthHttpClient authhttpclient){
		this.act = act;
		this.authhttpclient = authhttpclient;
	}
	
	protected void loginWithFacebook(LoginButton loginButton){
		
		//宣告callback Manager
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //登入成功
            @Override
            public void onSuccess(LoginResult loginResult) {
                //accessToken之後或許還會用到 先存起來
                accessToken = loginResult.getAccessToken();
                Log.d("FB","access token got.");
                
                //send request and call graph api
                GraphRequest request = GraphRequest.newMeRequest(accessToken, 
                        new GraphRequest.GraphJSONObjectCallback() {
                           //當RESPONSE回來的時候
 							@Override
 							public void onCompleted(JSONObject object, GraphResponse response) {
 								// TODO Auto-generated method stub
 								//讀出姓名 ID FB個人頁面連結
 	                            Log.d("FB","complete");
 	                            //fbId = object.optString("id");
 	                            setFacebokId( object.optString("id") );
 	                            
 	                            Log.d("FB", getFacebokId() );
 	                            InformationProcess.saveThirdPartyInfo( getFacebokId() , act);
 	                            authhttpclient.Auth_FacebookLoignRegister( getFacebokId() );        
 							}
                   });
                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id");
                request.setParameters(parameters);
                request.executeAsync();
            }

            //登入取消
            @Override
            public void onCancel() {
                // App code
                Log.d("FB","CANCEL");
            }

          //登入失敗
	   		@Override
	   		public void onError(FacebookException error) {
	   			// TODO Auto-generated method stub
	   			Log.d("FB",error.toString());
	   		}
 		
        });
	}

	public CallbackManager getCallbackManager() {
		return callbackManager;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}
	
	public String getFacebokId() {
		return this.FaceboookId;
	}
	private void setFacebokId(String fbID) {
		this.FaceboookId = fbID;
	}
	
	
}
