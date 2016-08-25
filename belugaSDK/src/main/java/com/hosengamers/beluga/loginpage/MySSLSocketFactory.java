package com.hosengamers.beluga.loginpage;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.os.Build;
import android.util.Log;

public class MySSLSocketFactory extends SSLSocketFactory {
    //SSLContext sslContext = SSLContext.getInstance("TLS");
	SSLContext sslContext;
    
    

    public MySSLSocketFactory(KeyStore truststore)
                    throws NoSuchAlgorithmException, KeyManagementException,
                    KeyStoreException, UnrecoverableKeyException {
            super(truststore);
            Log.i("MySSLSocketFactory", "KeyStore:"+truststore);
            TrustManager tm = new X509TrustManager() {

					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
						// TODO Auto-generated method stub
						
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						// TODO Auto-generated method stub
						return null;
					}
            };
            
            if(Build.VERSION.SDK_INT<16){
            	Log.i("SSLSocketFactory", "Build.VERSION in if"+ Build.VERSION.SDK_INT);
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[] { tm }, null);
            }
            else{
            	Log.i("SSLSocketFactory", "Build.VERSION in else"+ Build.VERSION.SDK_INT);
            	sslContext = SSLContext.getInstance("TLSv1.2");
            	sslContext.init(null, new TrustManager[] { tm }, null);
            }
                

            
    }


    @Override
    public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
    }

	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
