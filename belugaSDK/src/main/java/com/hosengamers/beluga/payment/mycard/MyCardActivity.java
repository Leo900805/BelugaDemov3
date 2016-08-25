package com.hosengamers.beluga.payment.mycard;

import java.util.Iterator;

import com.hosengamers.beluga.R;
import com.hosengamers.beluga.belugakeys.Keys;

import android.app.Activity;
import android.app.ProgressDialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MyCardActivity extends Activity{
	public static final String TYPE_SERIAL_NUMBER = "type_serial_number";
	public static final String TYPE_SMALL_PAYMENT = "type_small_payment";
	private WebView webView;
    private StringBuilder realURLBuilder = new StringBuilder();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_layout);

		final ProgressDialog pg = ProgressDialog.show(this, null, "Loading");
        Bundle b = getIntent().getExtras();
        MyCardModel.ApiKey = b.getString(Keys.ApiKey.toString());
        MyCardModel.AppId = b.getString(Keys.AppID.toString());
        if(MyCardModel.ApiKey == "" || MyCardModel.AppId  == ""){
        	Log.e("MyCard","ApiKey or AppId is null");
        	Toast.makeText(this, "ApiKey or AppId is null", Toast.LENGTH_LONG).show();
        }
        if(MyCardModel.ApiKey.compareTo("") == 0 || MyCardModel.AppId.compareTo("") == 0){
        	Log.e("MyCard","ApiKey or AppId is empty");
        	Toast.makeText(this, "ApiKey or AppId is empty", Toast.LENGTH_LONG).show();
        }

        this.webView = (WebView)this.findViewById(R.id.webView);
        this.webView.loadUrl(getRealURL());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        //webView.setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pg.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pg.dismiss();
            }
        });
		
	}
	
	private void errorFinish() {
		Toast.makeText(this, "Error MyCard Payment Type", Toast.LENGTH_LONG).show();
		finish();
	}
	private String getRealURL() {
		Bundle b = getIntent().getExtras();
		String paymentType = b.getString("type");
		String uid = b.getString("uid");
		
		if (uid.isEmpty() ||
			paymentType.isEmpty() || 
			!paymentType.equals(TYPE_SERIAL_NUMBER) && 
			!paymentType.equals(TYPE_SMALL_PAYMENT)) {
			errorFinish();
		}
		else {
			realURLBuilder.append(MyCardModel.getTypeURL(paymentType));
			realURLBuilder.append("?");
			b.putAll(MyCardModel.getParams(this, uid));
			b.remove("type");
		}
		Iterator<String> it = b.keySet().iterator();
		
		while (it.hasNext()) {
			String bKey = it.next();
			realURLBuilder.append(bKey);
			realURLBuilder.append("=");
			realURLBuilder.append(b.getString(bKey));
			
			if (it.hasNext()) {
				realURLBuilder.append("&");
			}
		}
		Log.i("realURL", "Real URL: " + realURLBuilder.toString());
		
		return realURLBuilder.toString();
	}

}
