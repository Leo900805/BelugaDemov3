package com.hosengamers.beluga.payment.mycard;

import java.security.MessageDigest;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings.Secure;

public class MyCardModel {
	protected static String AppId = "";
	protected static String ApiKey = "";
	protected static String SerialNumberURL = "http://api.belugame.com/MyCardPayment/InGamePurchase";
	protected static String SmallPaymentURL = "http://api.belugame.com/MyCardPayment/CPStart";
	
	private static final HashMap<String, String> urlMap; 
	static
	{
		urlMap = new HashMap<String, String>();
	}
	
	protected static String getTypeURL(String type) {
		if(urlMap.isEmpty()){
			urlMap.put("type_serial_number", SerialNumberURL);
	    	urlMap.put("type_small_payment", SmallPaymentURL);
		}
		return urlMap.get(type);
	}
	protected static Bundle getParams(Context context, String uid) {
		Bundle b = new Bundle();
		b.putString("appid", AppId);
		b.putString("devid", getDeviceId(context));
		b.putString("ts", getTimestamp());
		b.putString("sign", getSign(context, uid));
		b.putString("PackageID", getPackageName(context));
	
		return b;
	}
	
	private static String getDeviceId(Context context) {
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); 
	}
	
	private static String getTimestamp() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	private static String getSign(Context context, String userId) {
		return encryptMD5(ApiKey + AppId + userId + getDeviceId(context) + ApiKey);
	}
	private static String getPackageName(Context context) {
		
		return context.getApplicationContext().getPackageName();
	}
	
	private static String encryptMD5(String str) {  
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
}
