package com.hosengamers.beluga.loginpage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

import com.hosengamers.beluga.R;
import com.hosengamers.beluga.belugakeys.Keys;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.security.KeyStoreException;


import org.apache.http.conn.ClientConnectionManager;
//import org.apache.http.conn.ssl.SSLSocketFactory;
//import javax.net.ssl.SSLSocketFactory;

import java.io.*;

/**
 * Created by Leo on 2015/10/6.
 */
@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class AuthHttpClient {
	//channel
	//public final static int LOW_AUTH = 1;
	//public final static int STRONG_AUTH = 2;
	
	//Global variable
    private Activity MainActivity;
    protected OnAuthEventListener AuthEventListener;
    protected static int AuthChannel;
    protected static String AppID = "";
    protected static String ApiKey = "";
    protected static String ApiUrl = "";
    protected static String PackageID = "";
    protected static String version="10405121054";
    public static InputStream caInput;
	

    private ProgressDialog loadingProgress;
    //private httpPostAsyncTask postAsyncTask;
    
    //AuthHttpClient Constructor
    protected AuthHttpClient(Activity act) {
        MainActivity = act;
       
    }
    
    //Check API whether exists 
    private boolean isApiInfoExists() {
        if (AppID.length() == 0) {
            return false;
        }
        if (ApiKey.length() == 0) {
            return false;
        }
        if (ApiKey.length() != 32) {
            return false;
        }
        return true;
    }

    //Create OnAuthEventListener interface 
    protected interface OnAuthEventListener {
    	public void onProcessDoneEvent(Bundle bundle);
        public void onProcessDoneEvent(int Code, String Message);
    }

    //For general Auth Event
    private void OnAuthEvent(int Code, String Message) {
        if (AuthEventListener != null) {
            AuthEventListener.onProcessDoneEvent(Code, Message);
        }
    }
    //For the Strong Auth Event
    
    private void OnAuthEvent(Bundle bundle) {
        if (AuthEventListener != null) {
            AuthEventListener.onProcessDoneEvent(bundle);
        }
    }
    
    //Method AuthEventListener(); 
    protected void AuthEventListener(OnAuthEventListener onAuthEventListener) {

        AuthEventListener = onAuthEventListener;
    }

    //Check Internet whether available
    private boolean isInternetAvailable() {
        ConnectivityManager manager = (ConnectivityManager) MainActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if(manager == null)	return false;

        NetworkInfo Wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo Mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // For WiFi Check
        if(Wifi != null){
            if (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting()) {
                return true;
            }
        }
        // For Data network check
        if(Mobile != null){
            if (manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }
    
    public static HttpClient getHttpsClient(HttpClient client) {
        try{
   		   X509TrustManager x509TrustManager = new X509TrustManager() { 	           
   				@Override
   				public void checkClientTrusted(X509Certificate[] chain,
   						String authType) throws CertificateException {
   				}

   				@Override
   				public void checkServerTrusted(X509Certificate[] chain,
   						String authType) throws CertificateException {
   				}

   				@Override
   				public X509Certificate[] getAcceptedIssuers() {
   					return null;
   				}
   	        };
   	        
   	        SSLContext sslContext = SSLContext.getInstance("TLS");
   	        sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
   	        SSLSocketFactory sslSocketFactory = new ExSSLSocketFactory(sslContext);
   	        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
   	        ClientConnectionManager clientConnectionManager = client.getConnectionManager();
   	        SchemeRegistry schemeRegistry = clientConnectionManager.getSchemeRegistry();
   	        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
   	        return new DefaultHttpClient(clientConnectionManager, client.getParams());
   	    } catch (Exception ex) {
   	        return null;
   	    }
   	}
    
 private void httpPOST(AuthCommandType t, String url, List<NameValuePair> list) {
    	
    	//postAsyncTask = new httpPostAsyncTask(t,list);
    	//postAsyncTask.execute(url);
    	
        MainActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (loadingProgress == null) {
                    loadingProgress = new ProgressDialog(MainActivity);
                    loadingProgress.setMessage("Loading");
                }
                loadingProgress.show();
            }
        });
        
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv =
                    HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("www.belugame.com", session);
            }
        };
        
        X509TrustManager x509TrustManager = new X509TrustManager() { 	           
			@Override
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
        };
     
        
      
        HttpPost post = new HttpPost(url);
       
        Log.i("httpClient", "url:" + url);
        try {
        	
            DefaultHttpClient httpClient = new DefaultHttpClient();
        	//DefaultHttpClient httpClient = null;
        	//HttpClient client = null;
        	Log.i("httpClient", "in if condition:" + url.indexOf("https"));
        	
            if (url.indexOf("https") == 0) {
            	Log.i("httpClient", "in if condition:" + url.indexOf("https"));
            	
            	SSLContext sslContext = SSLContext.getInstance("TLSv1");
            	if(Build.VERSION.SDK_INT<23){
                	Log.i("SSLSocketFactory", "Build.VERSION in if"+ Build.VERSION.SDK_INT);
                    sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
                }
                else{
                	Log.i("SSLSocketFactory", "Build.VERSION in else"+ Build.VERSION.SDK_INT);
                	sslContext = SSLContext.getInstance("TLSv1.2");
                	sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
                }
            	 //sslcontext.init(null,null,null);
            	 //SSLSocketFactory sslSocketFactory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
            	 //SSLContext sslContext = SSLContext.getInstance("TLS");
     	         //sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
     	        //SSLSocketFactory sslSocketFactory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
     	        SSLSocketFactory sslSocketFactory = new ExSSLSocketFactory(sslContext);
       	        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
       	        ClientConnectionManager clientConnectionManager = httpClient.getConnectionManager();
    	        SchemeRegistry schemeRegistry = clientConnectionManager.getSchemeRegistry();
    	        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
    	        httpClient=new DefaultHttpClient(clientConnectionManager, httpClient.getParams());
    	        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);    	        
    	        /*
                SchemeRegistry registry = new SchemeRegistry();
                // SSL All Allow.
                //HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                
                DefaultHttpClient client = new DefaultHttpClient();
                SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
                //socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                //socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                registry.register(new Scheme("https", socketFactory, 443));
                SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
                httpClient = new DefaultHttpClient(mgr, client.getParams());
                // Set verifierHttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            	*/
            } else {
            	Log.i("httpClient", "in else condition:" + url.indexOf("https"));
                httpClient = new DefaultHttpClient();
            }
            
          
            for(int i=0;i<list.size();i++){
                Log.d("list:", list.get(i).toString());
            }
            Log.i("httpClient", "post entity...");
            post.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
            //post.setEntity(new UrlEncodedFormEntity(list));

            Log.i("httpClient", "httpResponse= httpClient.execute(post)");
            HttpResponse httpResponse = httpClient.execute(post);
            //HttpResponse httpResponse = client.execute(post);
            
            
            Log.i("htthttpClient", "pResponse.getStatusLine().getStatusCode() is " +httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
            	
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("AuthValue", strResult);
                data.putInt("AuthType", t.getIntValue());
                
                msg.setData(data);
                
                HttpPostHandler.sendMessage(msg);
                
            } else {
            	
                OnAuthEvent(-101, "ServerHttpStatusError"
                        + httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OnAuthEvent(-100, "HttpPostError");
        }
        
       
    }
    
   
    
    Handler HttpPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (loadingProgress != null) {
                loadingProgress.dismiss();
            }

            String cmdstr = msg.getData().getString("AuthValue");
            int cmdtyp = msg.getData().getInt("AuthType");
            System.out.println("HttpPostHandler get data----- " + cmdstr);
            try {
                AuthBackDataProcess(AuthCommandType.values()[cmdtyp], cmdstr);
                System.out.println("getDtaFromJSON--------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private enum AuthCommandType {
        Login(0), QuickAccount(1), RegisterAccount(2), ChangePassword(3),
        FacebookLoginRegister(4),GoogleLoginRegister(5);

        private final int AuthTypevalue;

        private AuthCommandType(int value) {
            this.AuthTypevalue = value;
        }

        public int getIntValue() {
            return AuthTypevalue;
        }
    }

    //MD5 encrypt
    private static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    
    //Get Device Name
    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    
    //Let string to upper case string
    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    
    //Get package name
    private String getPackgetName() {
        return MainActivity.getApplicationContext().getPackageName();
    }

    public static boolean isAccountRuleString(String target) {

        boolean result = false;

        result = target.matches("^[A-Za-z0-9]+$");

        return result;
    }
    
    //Check Account Length
    protected static boolean isAccountRuleLength(String target) {

        if (target.length() < 6 || target.length() > 32) {
            return false;
        }
        return true;
    }
    
    protected String getIP(){
    	String ip = null;
    	try {
     	   for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
     	              NetworkInterface intf = (NetworkInterface) en.nextElement();
     	              
     	              for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
     	            	  
     	                  InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
     	                  if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip = inetAddress.getHostAddress())) {
     	                	  Log.i("ip", "ip is :"+ ip );
     	                	  return ip;
     	                  }
     	              }
     	          }
     	  } catch (Exception e) {
     	   Log.e("------------", e.toString());
     	  }
		return ip;
    }
    
    
  
    protected void Auth_UserLogin(String UserID, String UserPassword) {
        
        UserID = UserID.trim();
        
        UserPassword = UserPassword.trim();
        if (!isAccountRuleLength(UserID)) {
            OnAuthEvent(-2, MainActivity.getString(R.string.Ac_Length_Err_Type));
            return;
        }
        if (!isAccountRuleLength(UserPassword)) {
            OnAuthEvent(-2, MainActivity.getString(R.string.Pwd_Length_Err_Type));
            return;
        }
        if (!isAccountRuleString(UserID)) {
            
            OnAuthEvent(-2, MainActivity.getString(R.string.Ac_Only_Char_And_Num_Type));
            return;
        }
        if (!isAccountRuleString(UserPassword)) {
           
            OnAuthEvent(-2, MainActivity.getString(R.string.Pwd_Only_Char_And_Num_Type));
            return;
        }
        if (!isApiInfoExists()) {
            
            OnAuthEvent(-2, MainActivity.getString(R.string.Appid_Or_Apikey_Err_Type));
            return;
        }
        if (!isInternetAvailable()) {
            
            OnAuthEvent(-2, MainActivity.getString(R.string.Network_Connection_Failure_Type));
            return;
        }
        
        String url = null;
        url = "MemberLogin/?";
        
        
        final String UrlAction = url;
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        
        	/*
			 * appid userid pwd devid sign
			 */
	        String devid = Secure.getString(MainActivity.getContentResolver(), Secure.ANDROID_ID);
	        String devtype = getDeviceName();
	        String ip = getIP();
	           
	        // apikey+appid+userid+pwd+apikey
	        String sign = MD5(ApiKey + AppID + UserID + UserPassword + ApiKey );    
	        params.add(new BasicNameValuePair("appid", AppID));
	        params.add(new BasicNameValuePair("userid", UserID));
	        params.add(new BasicNameValuePair("pwd", UserPassword));
	        params.add(new BasicNameValuePair("devid", devid));
	        params.add(new BasicNameValuePair("sign", sign));
	        params.add(new BasicNameValuePair("clientip", ip));
	        params.add(new BasicNameValuePair("devtype", devtype));
	        params.add(new BasicNameValuePair("ostype", "1"));
        
	        Runnable runnable = new Runnable() {
	            @Override
	            public void run() {
	            	
	                httpPOST(AuthCommandType.Login, ApiUrl + UrlAction, params);
	            }
	         };
	         new Thread(runnable).start();
    }

    //Facebook login and Register
    protected void Auth_FacebookLoignRegister(String fbID){
        
    	Long tsLong = System.currentTimeMillis()/1000;
    	String ts = tsLong.toString();
        Log.i("fb timestamp", "ts " + ts);
        String sign = MD5(AppID + fbID + ApiKey + ts);
        final String UrlAction = "http://belugame.com/api/facebook/?";
        
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("appid", AppID));
        params.add(new BasicNameValuePair("fbid", fbID));
        params.add(new BasicNameValuePair("apikey", ApiKey));
        params.add(new BasicNameValuePair("ts", ts));
        params.add(new BasicNameValuePair("sign", sign));
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                httpPOST(AuthCommandType.FacebookLoginRegister, UrlAction,params);
            }
        };
        new Thread(runnable).start();
    }
	 protected void Auth_GoogleLoignRegister(String googleID, String gmail, String gname, String photoUrl){
	        
	    	Long tsLong = System.currentTimeMillis()/1000;
	    	String ts = tsLong.toString();
	        Log.i("gg timestamp", "ts " + ts);
	        String sign = MD5(AppID +  googleID + ApiKey + ts);
	        final String UrlAction = "http://belugame.com/api/google/?";
	
	        final List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("appid", AppID));
	        params.add(new BasicNameValuePair("googleid", googleID));
	        params.add(new BasicNameValuePair("gmail", gmail));
	        params.add(new BasicNameValuePair("gname", gname));
	        params.add(new BasicNameValuePair("photourl", photoUrl));
	        params.add(new BasicNameValuePair("apikey", ApiKey));
	        params.add(new BasicNameValuePair("ts", ts));
	        params.add(new BasicNameValuePair("sign", sign));

	        Runnable runnable = new Runnable() {
	            @Override
	            public void run() {

	                httpPOST(AuthCommandType.GoogleLoginRegister, UrlAction,
	                        params);
	            }
	        };
	        new Thread(runnable).start();
	    }

    protected void Auth_QuickAccount() {

        if (!isApiInfoExists()) {
            
            OnAuthEvent(-501, MainActivity.getString(R.string.Appid_Or_Apikey_Err_Type));
            return;
        }
        if (!isInternetAvailable()) {
            
            OnAuthEvent(-500, MainActivity.getString(R.string.Network_Connection_Failure_Type));
            return;
        }
        
        final String UrlAction = "MemberMake/?";

        String devid = Secure.getString(MainActivity.getContentResolver(),
                Secure.ANDROID_ID);
        System.out.println("devid ----" + devid);
        String devtype = getDeviceName();
        System.out.println("devtype ----" + devtype);
        String packid = getPackgetName();
        System.out.println("packid ----" + packid);
        String sign = MD5(ApiKey + AppID + devid + ApiKey);
        System.out.println("sign ----" + sign);
        String ip = getIP();
        
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("appid", AppID));
        params.add(new BasicNameValuePair("devid", devid));
        params.add(new BasicNameValuePair("devtype", devtype));
        params.add(new BasicNameValuePair("packid", packid));
        params.add(new BasicNameValuePair("packageid", PackageID));
        params.add(new BasicNameValuePair("ostype", "1"));
        params.add(new BasicNameValuePair("clientip", ip));
        params.add(new BasicNameValuePair("sign", sign));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                httpPOST(AuthCommandType.QuickAccount, ApiUrl + UrlAction,
                        params);
            }
        };
        new Thread(runnable).start();
    }


    protected void Auth_RegisterAccount(String UserID, String UserPassword) {
        UserID = UserID.trim();
        UserPassword = UserPassword.trim();

        if (!isAccountRuleLength(UserID)) {

            OnAuthEvent(-2, MainActivity.getString(R.string.Ac_Length_Err_Type));
            return;
        }
        if (!isAccountRuleLength(UserPassword)) {

            OnAuthEvent(-2, MainActivity.getString(R.string.Pwd_Length_Err_Type));
            return;
        }
        if (!isAccountRuleString(UserID)) {

            OnAuthEvent(-2, MainActivity.getString(R.string.Ac_Only_Char_And_Num_Type));
            return;
        }
        if (!isAccountRuleString(UserPassword)) {

            OnAuthEvent(-2, MainActivity.getString(R.string.Pwd_Only_Char_And_Num_Type));
            return;
        }
        if (!isApiInfoExists()) {

            OnAuthEvent(-501, MainActivity.getString(R.string.Appid_Or_Apikey_Err_Type));
            return;
        }
        if (!isInternetAvailable()) {

            OnAuthEvent(-500, MainActivity.getString(R.string.Network_Connection_Failure_Type));
            return;
        }

        
        
        String url = null;
        //if(AuthChannel == AuthHttpClient.LOW_AUTH){
        	//url = "MemberCreate";
        //}else if (AuthChannel == AuthHttpClient.STRONG_AUTH){
        	url = "MemberCreate/?";
        //}
        
        final String UrlAction = url;
        String devid = Secure.getString(MainActivity.getContentResolver(),
                Secure.ANDROID_ID);
        String devtype = "";
        String packid = "";
        try {
            devtype = TextUtils.htmlEncode(getDeviceName());
            packid = TextUtils.htmlEncode(getPackgetName());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        String ip = getIP();
        
        String sign = MD5(ApiKey + AppID + UserID + devid + ApiKey);
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("appid", AppID));
        params.add(new BasicNameValuePair("userid", UserID));
        params.add(new BasicNameValuePair("pwd", UserPassword));
        params.add(new BasicNameValuePair("devid", devid));
        params.add(new BasicNameValuePair("devtype", devtype));
        params.add(new BasicNameValuePair("ostype", "1"));
        params.add(new BasicNameValuePair("packid", packid));
        params.add(new BasicNameValuePair("clientip", ip));
        params.add(new BasicNameValuePair("sign", sign));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                httpPOST(AuthCommandType.RegisterAccount, ApiUrl + UrlAction, params);
            }
        };
        new Thread(runnable).start();

    }

    protected void Auth_ChangePassword(String UserID, String OldPassword,
                                    String NewPassword) {
        UserID = UserID.trim();
        OldPassword = OldPassword.trim();
        NewPassword = NewPassword.trim();
        Log.i("Auth_ChangePassword","UserID "+UserID + " OldPassword "+OldPassword +" NewPassword "+NewPassword);
        if (!isAccountRuleLength(UserID)) {
            OnAuthEvent(-2, MainActivity.getString(R.string.Ac_Length_Err_Type));
            return;
        }
        if (!isAccountRuleLength(OldPassword)) {
            OnAuthEvent(-2, MainActivity.getString(R.string.Old_Password_Length_Err_Type));
            return;
        }
        if (!isAccountRuleLength(NewPassword)) {
            OnAuthEvent(-2, MainActivity.getString(R.string.New_Pwd_Length_Err_Type));
            return;
        }
        if (!isAccountRuleString(UserID)) {
            OnAuthEvent(-2, MainActivity.getString(R.string.Ac_Only_Char_And_Num_Type));
            return;
        }
        if (!isAccountRuleString(OldPassword)) {
            OnAuthEvent(-2, MainActivity.getString(R.string.Old_Password_Only_Char_And_Num_Type));
            return;
        }
        if (!isAccountRuleString(NewPassword)) {
            OnAuthEvent(-2, MainActivity.getString(R.string.New_Password_Only_Char_And_Num_Type));
            return;
        }
        if (!isApiInfoExists()) {
            OnAuthEvent(-501, MainActivity.getString(R.string.Appid_Or_Apikey_Err_Type));
            return;
        }
        if (!isInternetAvailable()) {
            OnAuthEvent(-500, MainActivity.getString(R.string.Network_Connection_Failure_Type));
            return;
        }
        if (!isApiInfoExists()) {
            OnAuthEvent(-501, MainActivity.getString(R.string.Appid_Or_Apikey_Err_Type));
            return;
        }
        
        final String UrlAction = "MemberModPwd/?";

        String devid = Secure.getString(MainActivity.getContentResolver(),
                Secure.ANDROID_ID);
        String sign = MD5(ApiKey + AppID + UserID + OldPassword + ApiKey);
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("appid", AppID));
        params.add(new BasicNameValuePair("userid", UserID));
        params.add(new BasicNameValuePair("pwd", OldPassword));
        params.add(new BasicNameValuePair("newpwd", NewPassword));
        params.add(new BasicNameValuePair("devid", devid));
        params.add(new BasicNameValuePair("sign", sign));
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                httpPOST(AuthCommandType.ChangePassword, ApiUrl + UrlAction,
                        params);
            }
        };
        new Thread(runnable).start();
    }

    private void AuthBackDataProcess(AuthCommandType t, String Data) {
        switch (t) {
            case Login:
                AuthBackDataProc_Login(Data);
                break;
            case QuickAccount:
                AuthBackDataProc_QuickAccount(Data);
                break;
            case RegisterAccount:
                AuthBackDataProc_RegisterAccount(Data);
                break;
            case ChangePassword:
                AuthBackDataProc_ChangePassword(Data);
                break;
            case FacebookLoginRegister:
                AuthBackDataProc_FacebookLoginRegister(Data);
                break;
            case GoogleLoginRegister:
                AuthBackDataProc_GoogleLoginRegister(Data);
                break;
            default:
                AuthBackDataProc_UnknowType();
                break;
        }
    }

    private void AuthBackDataProc_Login(String Data) {
        try {
              
            Bundle bundle = new Bundle();
            bundle.putString(Keys.JsonData.toString(), Data);
            OnAuthEvent(bundle);
            
        } catch (Exception e) {
        	Log.i(" AuthBackDataProc_Login", "Data_Parse_Error_Type :"+MainActivity.getString(R.string.Data_Parse_Error_Type));
            OnAuthEvent(-102,  MainActivity.getString(R.string.Data_Parse_Error_Type));
        }

    }

    private void AuthBackDataProc_QuickAccount(String Data) {
        try {
        	
        	Bundle bundle = new Bundle();
            bundle.putString(Keys.JsonData.toString(), Data);
            OnAuthEvent(bundle);
        } catch (Exception e) {
        	Log.i(" AuthBackDataProc_QuickAccount", "Data_Parse_Error_Type :"+MainActivity.getString(R.string.Data_Parse_Error_Type));
            OnAuthEvent(-102,  MainActivity.getString(R.string.Data_Parse_Error_Type));
        }
    }

    private void AuthBackDataProc_RegisterAccount(String Data) {
        try {
        	Bundle bundle = new Bundle();
            bundle.putString(Keys.JsonData.toString(), Data);
            OnAuthEvent(bundle);
            
        } catch (Exception e) {
        	Log.i(" AuthBackDataProc_RegisterAccount", "Data_Parse_Error_Type :"+MainActivity.getString(R.string.Data_Parse_Error_Type));
            OnAuthEvent(-102,  MainActivity.getString(R.string.Data_Parse_Error_Type));
        }
    }

    private void AuthBackDataProc_ChangePassword(String Data) {
        try {
        	Bundle bundle = new Bundle();
            bundle.putString(Keys.JsonData.toString(), Data);
            OnAuthEvent(bundle);
        } catch (Exception e) {
        	
            OnAuthEvent(-102,  MainActivity.getString(R.string.Data_Parse_Error_Type));
        }
    }

    private void AuthBackDataProc_FacebookLoginRegister(String Data) {
    	Log.i("AuthBackDataProc_FacebookLoginRegister", "Start...");
        try {
        	Bundle bundle = new Bundle();
            bundle.putString(Keys.JsonData.toString(), Data);
            OnAuthEvent(bundle);
        } catch (Exception e) {
        	Log.i("AuthBackDataProc_FacebookLoginRegister", "Data_Parse_Error_Type");
            OnAuthEvent(-102,  MainActivity.getString(R.string.Data_Parse_Error_Type));
        }
        Log.i("AuthBackDataProc_FacebookLoginRegister", "end...");
    }
    
    private void AuthBackDataProc_GoogleLoginRegister(String Data) {
    	Log.i("AuthBackDataProc_GoogleLoginRegister", "Start...");
        try {
        	Bundle bundle = new Bundle();
            bundle.putString(Keys.JsonData.toString(), Data);
            OnAuthEvent(bundle);
        } catch (Exception e) {
        	Log.i("AuthBackDataProc_GoogleLoginRegister", "Data_Parse_Error_Type");
            OnAuthEvent(-102,  MainActivity.getString(R.string.Data_Parse_Error_Type));
        }
        Log.i("AuthBackDataProc_GoogleLoginRegister", "end...");
    }

    private void AuthBackDataProc_UnknowType() {
    	Log.i("AuthBackDataProc_UnknowType", "Unknow");
        OnAuthEvent(-102,  MainActivity.getString(R.string.Data_Parse_Error_Type));
    }
    
    public class EasyX509TrustManager implements X509TrustManager {

    	private X509TrustManager standardTrustManager = null;

    	/**
    	 * Constructor for EasyX509TrustManager.
    	 */
    	public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
    	    super();
    	    TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    	    factory.init(keystore);
    	    TrustManager[] trustmanagers = factory.getTrustManagers();
    	    if (trustmanagers.length == 0) {
    	        throw new NoSuchAlgorithmException("no trust manager found");
    	    }
    	    this.standardTrustManager = (X509TrustManager) trustmanagers[0];
    	}

    	/**
    	 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],String authType)
    	 */
    	public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
    	    standardTrustManager.checkClientTrusted(certificates, authType);
    	}

    	/**
    	 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],String authType)
    	 */
    	public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
    	    if ((certificates != null) && (certificates.length == 1)) {
    	        certificates[0].checkValidity();
    	    } else {
    	        standardTrustManager.checkServerTrusted(certificates, authType);
    	    }
    	}

    	/**
    	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
    	 */
    	public X509Certificate[] getAcceptedIssuers() {
    	    return this.standardTrustManager.getAcceptedIssuers();
    	}
    	}
    
    
    
    
   
    
    
    
    
    
}

