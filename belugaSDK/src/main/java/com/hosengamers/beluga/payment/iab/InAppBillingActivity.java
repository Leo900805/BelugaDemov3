package com.hosengamers.beluga.payment.iab;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.hosengamers.beluga.payment.iab.util.IabHelper;
import com.hosengamers.beluga.payment.iab.util.IabResult;
import com.hosengamers.beluga.payment.iab.util.Inventory;
import com.hosengamers.beluga.payment.iab.util.Purchase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class InAppBillingActivity extends Activity{
	
	private String base64 = "";
	private String mTradeid;
	private String mReceipt;
	private String mOrder;
	private String mOrdersign;
	private IabHelper mHelper;
	private String mItemId;
	private String mUserId;
	private String TAG = "Beluga IAB";
	private boolean afterPurchase = false;
	private ProgressDialog dialog;
	private int RC_REQUEST = 10001;
	public static int GBilling_REQUEST = 100;
	public static String base64EncodedPublicKey = "base64EncodedPublicKey";
	public static String User_ID = "u";

	public static String ItemID = "i";
	public static String message = "message";
	public static String code = "code";
	public static String Tradeid = "Tradeid";
	public static String Order = "o";
	public static String Server = "s";
	public static String Role = "r";
	public static String ApiUrl = "api_url";
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG,"GBilling 20160126");
		
		
		SetUi();
		
		String base64 = getIntent().getExtras().getString(base64EncodedPublicKey);
		String User_Id =getIntent().getExtras().getString(User_ID);
		String Item_ID =getIntent().getExtras().getString(ItemID);
		String Order_str = getIntent().getExtras().getString(Order);
		String Server_str = getIntent().getExtras().getString(Server);
		String Role_str = getIntent().getExtras().getString(Role);

		if(base64 == null || User_Id == null || Item_ID == null){
			sendOnTradeGoogleFinished(-10,"Insert value wrong");
			return;
		}
		if(base64.compareTo("") == 0 || User_Id.compareTo("") == 0 || Item_ID.compareTo("") == 0){
			sendOnTradeGoogleFinished(-10,"Insert value wrong");
			return;
		}
		
		//Log.i(TAG,"base64 = "+base64);
		//Log.i(TAG,"User_Id = "+User_Id);
		//Log.i(TAG,"Item_ID = "+Item_ID);
		
		
		BuyItem(base64, User_Id, Item_ID,Role_str,Server_str,Order_str);
		ShowProgressbar();
	}
	
	
	private void ShowProgressbar(){
		try{
			//Log.i("NicePlayGB", "now ProgressDialog.show()");
			dialog = ProgressDialog.show(InAppBillingActivity.this, "Please wait...", Tools.getStringByName(this, "payingprogressmsg"));
			//Log.i("NicePlayGB", "ProgressDialog.show() end");
		}catch(Exception e){
			//Log.e("NicePlayGB","Can't show progress bar \n"+ dialog);
			//Log.e("NicePlayGB","Can't show progress bar \n"+e.toString());
		}
	}
	private void BuyItem(String base64EncodedPublicKey,String UserId,String ItemID,String Roleid,String Serverid,String Orderid) {
		Log.i(TAG, "BuyItem start");
		mItemId = ItemID;
		mUserId = UserId;
		base64 = base64EncodedPublicKey;
		afterPurchase = false;
		if(!CheckAllItemsAvailable())
			return;
        
        Bundle b = new Bundle();
        b.putString("u", mUserId);
        b.putString("i", mItemId);
        if(Roleid != null){
        	b.putString(Role, Roleid);
        }
        if(Serverid != null){
        	b.putString(Server, Serverid);
        }
        if(Orderid != null){
        	b.putString(Order, Orderid);
        }
        //Log.i(TAG,"bundle "+b.toString());
        //Log.i(TAG, "call tradeInfoCreateInNP()");
        tradeInfoCreateInNP(b);
        
        //Log.i(TAG, "BuyItem end");
    }
		
	private void SetUi(){
		RelativeLayout  layout_base = new RelativeLayout(this);	
		RelativeLayout.LayoutParams param_base= new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		layout_base.setPadding(0, 0, 0, 0);
		layout_base.setBackgroundColor(Color.TRANSPARENT);
		layout_base.setLayoutParams(param_base);
		
		setContentView(layout_base);
		
	}
	private void tradeInfoCreateInNP(final Bundle b) {
		Log.i(TAG, "tradeInfoCreateInNP start");
		new AsyncTask<Void, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Void... params) {
				//Log.i("JSONObject doInBackground:", "call ServerUtilities.getTradeInfoWithGet()" );
				JSONObject jObj = ServerUtilities.getTradeInfoWithGet(InAppBillingActivity.this,b, ServerUtilities.TradeInfoUrl);
				//Log.i("JSONObject doInBackground:", "ServerUtilities.TradeInfoUrl value:"+ ServerUtilities.TradeInfoUrl );
				//Log.i("JSONObject doInBackground:", "doInBackground jObj value:"+ jObj);
				return jObj;
			}

			@Override
			protected void onPostExecute(JSONObject jObj) {
				super.onPostExecute(jObj);
				//Log.i("onPostExecute:", "onPostExecute process");
				if(jObj == null){
					sendOnTradeGoogleFinished(-2,"create trade id from server failed");
					return;
				}
				
				try {
					//Log.d(TAG,"flag line 152");
					mTradeid = jObj.getString("tradeid");
					
					int code = jObj.getInt("code");
					String message = jObj.getString("message");
					Log.d(TAG,"tradeInfoCreateInNP code = "+code+" ; MESSAGE = "+ message + " ; tradeid is "+mTradeid );
					if(code != 1){
						//Log.d(TAG,"flag line 159");
						sendOnTradeGoogleFinished(-2,"create trade id from server failed : "+message);
					}else{
						Log.d(TAG,"flag line :"+Thread.currentThread().getStackTrace()[2].getLineNumber());
						PayAppBill();
					}
					//Log.d(TAG,"flag line 165");
					Log.d(TAG, "msg1:"+jObj.toString());
				} catch (JSONException e) {
					//Log.d(TAG,"flag line 168");
					e.printStackTrace();
					sendOnTradeGoogleFinished(-2,"create trade id from server failed"+e.toString());
				}
				
			}

		}.execute(null, null, null);
		Log.i(TAG, "tradeInfoCreateInNP end");
	}
	private void PayAppBill(){
		Log.d(TAG, "Creating IAB helper.");
		if(!CheckAllItemsAvailable()){
			return;
		}
        mHelper = new IabHelper(InAppBillingActivity.this, base64);//init
        mHelper.enableDebugLogging(true);
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                	//Log
                    //complain("Problem setting up in-app billing: " + result);
                	Log.e(TAG,"Problem setting up in-app billing: " + result);
                	sendOnTradeGoogleFinished(-1, "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;
                
                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
                
            }
        });
	}
	
	private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
            	Log.e(TAG,"helper is null");
            	sendOnTradeGoogleFinished(-1, "helper is null");
            	return;
            }

            // Is it a failure?
            if (result.isFailure()) {
            	//Log
                //complain("Failed to query inventory: " + result);
            	Log.e(TAG,"Failed to query inventory: " + result);
            	sendOnTradeGoogleFinished(-1, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");
            
            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
//            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
//            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
//            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
//
//            // Do we have the infinite gas plan?
//            Purchase infiniteGasPurchase = inventory.getPurchase(SKU_INFINITE_GAS);
//            mSubscribedToInfiniteGas = (infiniteGasPurchase != null && verifyDeveloperPayload(infiniteGasPurchase));
//            Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE") + " infinite gas subscription.");
//           // if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Log.d(TAG, "Purchase item "+mItemId);
            List<String> Purchaselist = inventory.getAllOwnedSkus();
            Log.d(TAG, "show all item ："+Purchaselist);
            Purchase gasPurchase = inventory.getPurchase(mItemId);
            Log.d(TAG, "gasPurchase ："+gasPurchase);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                Log.d(TAG, "We have this item. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(mItemId), mConsumeFinishedListener);
                return;
            }else{
            	Log.d(TAG, "gasPurchase == NULL or verifyDeveloperPayload(gasPurchase) is false");
            	if(!afterPurchase){
            		if(dialog != null){
            			Log.d(TAG, "dialog.dismiss");
            			dialog.dismiss();
            		}
            		String payload = "";
            		Log.d(TAG, "line 300 launchPurchaseFlow()....");
                    mHelper.launchPurchaseFlow(InAppBillingActivity.this, mItemId, RC_REQUEST, mPurchaseFinishedListener, payload);
                
            	}
            }
            
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
	private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
            	Log.e(TAG,"helper is null");
            	sendOnTradeGoogleFinished(-1, "helper is null");
            	return;
            }

            if (result.isFailure()) {

            	Log.e(TAG,"Error purchasing: " + result);
				sendOnTradeGoogleFinished(result, purchase);
            	sendOnTradeGoogleFinished(-1, "Error purchasing: " + result);
                //complain("Error purchasing: " + result);
                //setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
            	Log.e(TAG,"Error purchasing. Authenticity verification failed.");
            	sendOnTradeGoogleFinished(-1, "Error purchasing. Authenticity verification failed.");
                //complain("Error purchasing. Authenticity verification failed.");
                //setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");
            Log.d(TAG, purchase.getSku()+".equals "+mItemId+":"+ purchase.getSku().equals(mItemId));
            if (purchase.getSku().equals(mItemId)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
            afterPurchase = true;
            
            
            //bug
            //Log.d(TAG, "Line 331 exe verifyReceipt()...");
            //verifyReceipt(purchase);
            Log.d(TAG, "Line 386 exe sendOnTradeGoogleFinished()...");
            sendOnTradeGoogleFinished(result, purchase);
            
            
        }
    };
	private static boolean verifyDeveloperPayload(Purchase p) {
		Log.d("Beluga IAB", "Line 405 verifyDeveloperPayload start...");
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */
        Log.d("Beluga IAB", "Line 430 verifyDeveloperPayload end return true...");
        return true;
    }
	private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null){
            	Log.e(TAG,"if we were disposed of in the meantime, quit.");
            	sendOnTradeGoogleFinished(-1, "if we were disposed of in the meantime, quit.");
            	return;
            }
           
            if (result.isSuccess()) {
                Log.d(TAG, "Consumption successful. Provisioning.");
                if(!afterPurchase){
                	if(dialog != null)	dialog.dismiss();
                	String payload = "";
                	Log.d(TAG, "Line 449 launchPurchaseFlow()...");
                    mHelper.launchPurchaseFlow(InAppBillingActivity.this, mItemId, RC_REQUEST, mPurchaseFinishedListener, payload);
                }else{
                	Log.d(TAG, "exe verifyReceipt()...");
                	verifyReceipt(purchase);
                	//here add notice
                }
            }
            else {
            	Log.e(TAG,"Error while consuming: " + result);
            	sendOnTradeGoogleFinished(-1, "Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };
    
    private void verifyReceipt(Purchase purchase) {
    	Bundle b = new Bundle();
    	mReceipt ="GPA.1369-6784-3344-09799";
    	//purchase.getOrderId();
    	Log.i("IAB", "mReceipt:"+mReceipt);
    	mOrder = purchase.getOriginalJson();
    	Log.i("IAB", "mOrder:"+mOrder);
    	mOrdersign = purchase.getSignature();
    	Log.i("IAB", "mOrdersign:"+mOrdersign);
    	if(mTradeid == null){
    		Log.e(TAG,"Trade id is null");
    		sendOnTradeGoogleFinished(-3, "Trade id is null");
    		return;
    	}
    	//Log.d(TAG,"Trade id is "+mTradeid);
        b.putString("tradeid", mTradeid);
        b.putString("receipt", mReceipt);
        b.putString("order", mOrder);
        b.putString("ordersign", mOrdersign);
        //bug
        verifyReceiptToServer(b);
    }
	private void verifyReceiptToServer(final Bundle b) {
		Log.i(TAG, "verifyReceiptToServer start...");
		new AsyncTask<Void, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Void... params) {
				Log.i(TAG, "ServerUtilities.getTradeInfoWithGet exe...");
				JSONObject jObj = ServerUtilities.getTradeInfoWithGet(InAppBillingActivity.this,b, ServerUtilities.VerifyReceiptUrl);
				Log.i(TAG, "return jObj is "+ jObj);
				return jObj;
			}

			@Override
			protected void onPostExecute(JSONObject jObj) {
				// TODO Auto-generated method stub
				super.onPostExecute(jObj);
				
				if (jObj != null) {
					Log.d(TAG, "msg1:"+jObj.toString());
					try {
						int code = jObj.getInt("code");
						String message = jObj.getString("message");
						if (code != 1) {
							Log.d(TAG, "VerifyReceiptError: " + message);
							sendOnTradeGoogleFinished(-3, "VerifyReceiptError: " + message);
						}
						else {
							Log.d(TAG, "VerifyReceipt success");
							sendOnTradeGoogleFinished(1, "ok");
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else {
					Log.d(TAG, "there is no network connection");
					sendOnTradeGoogleFinished(-3,"there is no network connection");
				}
			}

		}.execute(null, null, null);

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
        	Log.d(TAG, "In if condition");
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
        
        
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        Log.d(TAG, "Destroying helper.");
        if(dialog!=null){
        	dialog.dismiss();
        	dialog = null;
        }
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
        
        
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	
	private void sendOnTradeGoogleFinished(int code , String message){
		Intent intent = getIntent();	
		Bundle b = new Bundle();
		Log.i(TAG,"seted code and message " + code +"  "+ message);
		b.putString("type", "PAYMENT");
		b.putInt("code", code);
		b.putString("message", message);
		if(code == 1){
			//b.putString(Tradeid, mTradeid);
			b.putString("tradeid", mTradeid);
			//b.putString("receipt", mReceipt);
			//b.putString("order", mOrder);
			//b.putString("ordersign", mOrdersign);
		}
		intent.putExtras(b);
		setResult(Activity.RESULT_OK, intent); 
		mItemId = null;
		mUserId = null;
		this.finish();
	}
	
	private void sendOnTradeGoogleFinished(IabResult result ,Purchase purchase){

		Log.i(TAG,"result is:" +result.getResponse()+ ","+result.getMessage());
		int response_code = result.getResponse();
		Intent intent = getIntent();
		Bundle b = new Bundle();
		//Log.i(TAG,"seted code and message " + code +"  "+ message);
		b.putString("type", "PAYMENT");
		if(result.getResponse() == 0){
			Log.i(TAG,"in if response"+result.getResponse());
			b.putInt("response", response_code);
			b.putString("status", result.getMessage());
			b.putString("order", purchase.getOriginalJson());
			b.putString("sign", purchase.getSignature());
			intent.putExtras(b);
			setResult(Activity.RESULT_OK, intent);
			this.finish();
		}else {
			Log.i(TAG,"in else response"+result.getResponse());
			b.putInt("response", response_code);
			b.putString("status", result.getMessage());
			b.putString("order", null);
			b.putString("sign", null);
			intent.putExtras(b);
			setResult(Activity.RESULT_OK, intent);
			this.finish();
		}

	}
	
	private boolean CheckAllItemsAvailable(){
		if( mItemId != null && mUserId != null && base64 != null){	
			return true;
		}else{
			sendOnTradeGoogleFinished(-10, "Insert value wrong");
			return false;
		}
	}
	

}
