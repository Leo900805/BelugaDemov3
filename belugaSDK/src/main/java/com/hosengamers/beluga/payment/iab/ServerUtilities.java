package com.hosengamers.beluga.payment.iab;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;


public final class ServerUtilities {
	private static final int MAX_ATTEMPTS = 2;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
 
    protected static String TradeInfoUrl = "http://api.belugame.com/IAP/GoogleIAPCreate";
    protected static String VerifyReceiptUrl = "http://api.belugame.com/IAP/Receipt";
	
    protected static JSONObject getTradeInfoWithGet(final Context context, Bundle b, final String url) {
    	Log.i("ServerUtilities", "getTradeInfoWithGet start");
    	
    	Log.i("ServerUtilities", "call generateHttpGetURL()");
    	String httpGetURL = generateHttpGetURL(context, b, url);
    	Log.i("URL", httpGetURL);
    	
    	long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
    	Log.i("ServerUtilities", "backoff value:" + backoff);
    	
    	for (int i = 0; i <= MAX_ATTEMPTS; i++) {
    		try {
    			
    			JSONObject JSONObj; //= url.equals(VerifyReceiptUrl) ? httpPostStart(url, b) : httpGetStart(httpGetURL);
    			if( url.equals(VerifyReceiptUrl) ){
    				Log.i("ServerUtilities", "url.equals(VerifyReceiptUrl) is:"+url.equals(VerifyReceiptUrl) );
    				JSONObj = httpPostStart(url, b);
    				Log.i("ServerUtilities", "JSONObj return value is:"+ JSONObj);
    			}else{
    				Log.i("ServerUtilities", "url.equals(VerifyReceiptUrl) is:"+url.equals(VerifyReceiptUrl) );
    				JSONObj = httpGetStart(httpGetURL);
    				Log.i("ServerUtilities", "JSONObj return value is:"+ JSONObj);
    			}
    				
    			Log.i("getTradeInfoWithGet", "getTradeInfoWithGet JSONObj value:" + JSONObj);
    			return JSONObj;
    		} catch (IOException e) {
    			if (i == MAX_ATTEMPTS) {
    				break;
    			}
    			try {
    				Thread.sleep(backoff);
    			}
    			catch (InterruptedException te) {
    				Thread.currentThread().interrupt();
    				return null;
    			}
    			backoff *= 2;
    		} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    	Log.i("ServerUtilities", "getTradeInfoWithGet end");
    	return null;
    }
   
    private static JSONObject httpGetStart(String endpoint) throws IOException, JSONException {      	
    	HttpClient httpClient = new DefaultHttpClient();
    	HttpGet get = new HttpGet(endpoint);
    	HttpResponse response = null;
		try {
			response = httpClient.execute(get);
			Log.i("ServerUtilities", "response value:"+ response);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("ServerUtilities", "catch 1:"+ e);
			throw e;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("ServerUtilities", "catch 2:"+ e);
			throw e;
		}
    	HttpEntity httpEntity = response.getEntity();
    	String result = EntityUtils.toString(httpEntity);
    	try {
    		Log.i("httpGetStart", "httpGetStart result:"+ result);
        	JSONObject jObj = new JSONObject(result);
        	Log.i("httpGetStart", "httpGetStart jObj value:"+ jObj);
			return jObj;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("httpGetStart", "httpGetStart catch:"+ e);
			throw e;
		}
    }
    
    private static JSONObject httpPostStart(String endpoint, Bundle b) {
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost(endpoint);
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	Iterator<String> it = b.keySet().iterator();
    	
    	while (it.hasNext()) {
    		String key = it.next();
        	params.add(new BasicNameValuePair(key, b.getString(key)));
    	}
    	
    	Log.d("Post Body", params.toString());
    	
    	UrlEncodedFormEntity ent = null;
		try {
			ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	post.setEntity(ent);
    	HttpResponse response = null;
    	
		try {
			response = client.execute(post);
			Log.d("response now", response.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	HttpEntity resEntity = response.getEntity();
    	String result = null;
    	
		try {
			result = EntityUtils.toString(resEntity);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	try {
			return new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    private static String generateHttpGetURL(Context context, Bundle b, String url) {
    	Log.i("ServerUtilities", "generateHttpGetURL start");
    	processBundle(context, b, url.equals(VerifyReceiptUrl));
    	StringBuilder sb = new StringBuilder(url + "?");
    	Iterator<String> it = b.keySet().iterator();
    	
    	while (it.hasNext()) {
    		String key = it.next();
    		sb.append(key + "=" + b.getString(key));
    		
    		if (it.hasNext()) {
    			sb.append("&");
    		}
    	}
    	Log.i("ServerUtilities", "generateHttpGetURL end");
    	return sb.toString();
    }
    
    
    private static String MD5(String str)  
    {  
        MessageDigest md5 = null;  
        try {  
            md5 = MessageDigest.getInstance("MD5");  
        }
        catch(Exception e) {  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = str.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
          
        for(int i = 0; i < charArray.length; i++) {  
            byteArray[i] = (byte)charArray[i];  
        }  
        byte[] md5Bytes = md5.digest(byteArray);    
        StringBuffer hexValue = new StringBuffer();  
       
        for( int i = 0; i < md5Bytes.length; i++) {  
            int val = ((int)md5Bytes[i])&0xff;  
            
            if(val < 16) {  
                hexValue.append("0");  
            }  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    } 
    
    private static String getTimestamp() {
    	return String.valueOf(System.currentTimeMillis());
    }
    
    private static void processBundle(Context context, Bundle b, boolean isVerifyReceipt) {
    	if (isVerifyReceipt) {
    		Log.i("processBundle", "isVerifyReceipt is:"+ isVerifyReceipt);
    		String timestamp = getTimestamp();
    		Log.i("processBundle", "timestamp value is:"+ timestamp);
    		b.putString("ts", timestamp);
    		b.putString("sign", MD5(b.getString("tradeid") + timestamp + "buy"));
    	}
    	else {
    		Log.i("processBundle", "isVerifyReceipt is:"+ isVerifyReceipt);
    		String timestamp = getTimestamp();
    		Log.i("processBundle", "timestamp value is:"+ timestamp);
    		String packageName = context.getPackageName();
    		Log.i("processBundle", "packageName value is:"+ packageName);
        	b.putString("p", packageName);
        	b.putString("ts", timestamp);
        	b.putString("sign", MD5(packageName + b.getString("u") + b.getString("i") + timestamp + "buy"));
    	}
    }
    
    
}
