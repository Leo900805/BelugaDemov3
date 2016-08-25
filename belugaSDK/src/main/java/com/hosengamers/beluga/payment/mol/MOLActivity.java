package com.hosengamers.beluga.payment.mol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hosengamers.beluga.R;

import java.util.Iterator;

/**
 * Created by Leo on 2015/10/15.
 */
public class MOLActivity extends Activity {

    private WebView webView;
    private StringBuilder realURLBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_layout);
        Log.i("MOL", "version: "+MOLModel.version);
        Bundle b = getIntent().getExtras();
        MOLModel.AppId = b.getString("AppID");
        final ProgressDialog pg = ProgressDialog.show(this, null, "Loading");
        if(MOLModel.AppId  == ""){
            Log.e("MOL","ApiKey or AppId is null");
            Toast.makeText(this, "ApiKey or AppId is null", Toast.LENGTH_LONG).show();
        }
        if(MOLModel.AppId.compareTo("") == 0){
            Log.e("MOL", "ApiKey or AppId is empty");
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
        Toast.makeText(this, "Error MOL Payment", Toast.LENGTH_LONG).show();
        finish();
    }

    private String getRealURL() {
        Bundle b = getIntent().getExtras();
        String uid = b.getString("UserId");
        int time = 1;
        if (uid.isEmpty()) {
            errorFinish();
        } else {
            realURLBuilder.append(MOLModel.MOLPaymentURL);
            realURLBuilder.append("?");
            Log.i("realURLBuilder "+time+":", realURLBuilder.toString());
        }
        Iterator<String> it = b.keySet().iterator();

        while (it.hasNext()) {
            String bKey = it.next();
            realURLBuilder.append(bKey);
            realURLBuilder.append("=");
            realURLBuilder.append(b.getString(bKey));
            Log.i("realURLBuilder"+ time++ +":", realURLBuilder.toString());
            if (it.hasNext()) {
                realURLBuilder.append("&");
                Log.i("realURLBuilder"+ time++ +":", realURLBuilder.toString());
            }
        }
        Log.i("realURL", "Real URL: " + realURLBuilder.toString());

        return realURLBuilder.toString();
    }
}
