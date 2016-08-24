package com.hosengamers.chee.invite;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.GameRequestDialog.Result;
import com.hosengamers.chee.belugakeys.Keys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class FacebookGameInviteActivity extends Activity{
	
	GameRequestDialog requestDialog;
	CallbackManager callbackManager;
	JSONObject json_obj;
	ArrayList<String> listID = new ArrayList<String>();
	String id, after_page = "", next = null;
	boolean hasNextPage = true;
	GraphRequest nextRequest;
	Bundle param = new Bundle();
	GraphRequest.Callback graphCallback;
	String shareContentTitle;
	String shareContentDescription;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 FacebookSdk.sdkInitialize(this.getApplicationContext());
		 
		 Intent intent = getIntent();  
		 shareContentTitle = intent.getStringExtra(Keys.ShareContentTitle.toString());
		 shareContentDescription = intent.getStringExtra(Keys.ShareContentDescription.toString());
			
		 callbackManager = CallbackManager.Factory.create();
		 requestDialog = new GameRequestDialog(this);
		 requestDialog.registerCallback(callbackManager,
				 new FacebookCallback<Result>() {

					@Override
					public void onSuccess(Result result) {
						// TODO Auto-generated method stub
						Log.i("success",result.getRequestId());
						if(nextRequest != null){
							 param.putString("pretty", "0");
							 param.putString("limit", "49");
							 param.putString("after", after_page);
							 param.putString("fields", "id");
							 Runnable runnable = new Runnable() {
						            @Override
						            public void run() {
						        		//send first request, the rest should be called by the callback
						       		 new GraphRequest(AccessToken.getCurrentAccessToken(), 
						       		         "/me/invitable_friends",param, HttpMethod.GET, graphCallback).executeAndWait();
						            }
						         };
						         new Thread(runnable).start();
						}else{
							finish();
						}
			            
						
					}

					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						finish();
					}

					@Override
					public void onError(FacebookException error) {
						// TODO Auto-generated method stub
						
					}
			 
		 });
		 
		 
		 param.putString("pretty", "0");
		 param.putString("limit", "49");
		 param.putString("after", after_page);
		 param.putString("fields", "id");
		 //setup a general callback for each graph request sent, this callback will launch the next request if exists.
		 graphCallback = new GraphRequest.Callback(){
		     @Override
		     public void onCompleted(GraphResponse response) {                       
		         try {
		        	
							JSONArray rawName = response.getJSONObject().getJSONArray("data");
							for (int i = 0; i < rawName.length(); i++) {
								json_obj = rawName.getJSONObject(i);
	       					    Log.d("Type", json_obj.toString());
	       					    listID.add(json_obj.getString("id"));
							}
							
							
							
							//get next batch of results of exists
				             nextRequest = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT); 
				             if(nextRequest != null){
				            	 JSONObject obj = response.getJSONObject().getJSONObject("paging");
								 JSONObject obj_cursors = obj.optJSONObject("cursors");
								 after_page = obj_cursors.getString("after");
								 Log.i("json after", after_page);
				             }
						
							GameRequestContent content = new GameRequestContent.Builder()
									.setTitle(shareContentTitle)
	   							    .setMessage(shareContentDescription)
	   							    .setTo(concatArrayListID(listID))
	   							    .build();
	   							  requestDialog.show(content);
				
		         } catch (JSONException e) {
		             e.printStackTrace();
		         }
		     }
		 };
		 if(GameRequestDialog.canShow()){
			 Runnable runnable = new Runnable() {
		            @Override
		            public void run() {
		        		//send first request, the rest should be called by the callback
		            	Log.i("access", ""+AccessToken.getCurrentAccessToken());
		       		 new GraphRequest(AccessToken.getCurrentAccessToken(), 
		       		         "/me/invitable_friends",param, HttpMethod.GET, graphCallback).executeAndWait();
		            }
		         };
		         new Thread(runnable).start();
		 }
	}
	
	private String concatArrayListID(ArrayList<String> listID){
		String to = "";
		for (Iterator<String> ID = listID.iterator(); ID.hasNext();) {
			to = to + ID.next();
			to = to.concat(",");
		}
		Log.i("concatArrayListID", to);
		listID.clear();
		Log.i("concatArrayListID", "listID.isEmpty(): "+listID.isEmpty());
		return to; 
	}

}
