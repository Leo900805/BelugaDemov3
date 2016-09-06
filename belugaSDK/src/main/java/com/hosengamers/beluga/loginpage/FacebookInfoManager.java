package com.hosengamers.beluga.loginpage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.GameRequestDialog;
import com.hosengamers.beluga.loginpage.datacontrol.InformationProcess;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class FacebookInfoManager {

	final String LIMIT = "49";
	final String FIELDS_PARAMS = "id,name";
	final String PRETTY = "0";
	String  after_page = "";

	Bundle param = new Bundle();
	GraphRequest.Callback graphCallback;
	private CallbackManager callbackManager;
	private AccessToken accessToken;
	private AuthHttpClient authhttpclient;
	private Activity act;
	private String FaceboookId;
	JSONArray jsonArrayFriendsList;
	JSONObject json_obj, jsonObjectDataList;
	GraphRequest nextRequest;
	Boolean hasNextPage = true;
	
	protected FacebookInfoManager(Activity act, AuthHttpClient authhttpclient){
		this.act = act;
		this.authhttpclient = authhttpclient;
	}

	public FacebookInfoManager(){
		super();
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
	public String getFriendsList(){
		Log.i("getFriendsList","start...");
		synchronized (FacebookInfoManager.class){
			try {
				Log.i("return","wait start...");
				FacebookInfoManager.class.wait();
				Log.i("return","wait end...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return jsonObjectDataList.toString();
	}
	public String getFacebokId() {
		return this.FaceboookId;
	}
	private void setFacebokId(String fbID) {
		this.FaceboookId = fbID;
	}

	public void loadFriendsList(){

		//setup a general callback for each graph request sent, this callback will launch the next request if exists.
		graphCallback = new GraphRequest.Callback(){
			@Override
			public void onCompleted(GraphResponse response) {
				synchronized (FacebookInfoManager.class) {
					try {
						jsonArrayFriendsList = new JSONArray();
						jsonObjectDataList = new JSONObject();
						JSONArray rawName = response.getJSONObject().getJSONArray("data");
						for (int i = 0; i < rawName.length(); i++) {
							json_obj = rawName.getJSONObject(i);
							Log.d("Type", json_obj.toString());
							jsonArrayFriendsList.put(json_obj);
						}
						jsonObjectDataList.put("list", jsonArrayFriendsList);
						Log.d("Data List", jsonObjectDataList.toString());

						nextRequest = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
						if (nextRequest != null) {
							JSONObject obj = response.getJSONObject().getJSONObject("paging");
							JSONObject obj_cursors = obj.optJSONObject("cursors");
							after_page = obj_cursors.getString("after");
							Log.i("json after", after_page);
							hasNextPage = true;
							Log.i("json after", "hasNextPage is :" + hasNextPage);
						} else {
							hasNextPage = false;
							Log.i("json after", "hasNextPage is :" + hasNextPage);
							Log.i("json after", "notify start...");
							FacebookInfoManager.class.notify();
							Log.i("json after", "notify end	...");
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		};


		param.putString("pretty", PRETTY);
		param.putString("limit", LIMIT);
		param.putString("after", after_page);
		param.putString("fields", FIELDS_PARAMS);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				//send first request, the rest should be called by the callback
				while (hasNextPage){
					Log.i("access", ""+AccessToken.getCurrentAccessToken());
					new GraphRequest(AccessToken.getCurrentAccessToken(),
							"/me/friends",param, HttpMethod.GET, graphCallback).executeAndWait();
				}
			}
		};
		new Thread(runnable).start();

		/*
		synchronized (FacebookInfoManager.class){
			try {
				Log.i("return","wait start...");
				FacebookInfoManager.class.wait();
				Log.i("return","wait end...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return jsonObjectDataList.toString();
		*/
	}
}
