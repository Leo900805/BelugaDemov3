package beluga.com.belugademov3;

import android.app.Activity;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hosengamers.beluga.invite.FacebookFriendsInviteActivity;
import com.hosengamers.beluga.loginpage.AuthClientActivity;
import com.hosengamers.beluga.payment.iab.InAppBillingActivity;
import com.hosengamers.beluga.payment.mol.MOLActivity;
import com.hosengamers.beluga.payment.mycard.MyCardActivity;
import com.hosengamers.beluga.belugakeys.Keys;
import com.hosengamers.beluga.share.FacebookShare;;


@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

    String appid = "MEMBER";
    String apikey = "a0c5560931b60786a9190a29c03a38bc";
    Intent serviceIntent;
    public final static int REQUEST_CODE = -1010101; /*(see edit II)*/
    Bundle b;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Main Demo", "onActivityResult start...");
        Log.i("Main Demo", "requestCode:" + requestCode);
        Log.i("Main Demo", "resultCode:" + Activity.RESULT_OK);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            try {
                TextView v = (TextView) this.findViewById(R.id.tv1);
                Bundle bundle = data.getExtras();
                String type = bundle.getString("type");
                Log.i("Main Demo", "type is " + type);
                if (type.equals("LOGIN")) {
                    Log.i("Main Demo", "Is " + type + "do if condition...");

                    String jsonData = bundle.getString(Keys.JsonData.toString());
                    v.setText("Login info :\nLogin Json Data:" + jsonData);

                	/*
                                         * 测试强验证：
                                         * 将 String 转换成Json
                                         * 在Json中取得token，送去需求参数appid，apikey，Token，MD5
                                         *
                                         * MD5 加密三次 MD5(MD5(MD5(appid + apikey + token)))
                                         */
                    JSONObject jObj = new JSONObject(jsonData);
                    String token = jObj.getString("token");
                    final List<NameValuePair> params = new ArrayList<NameValuePair>();
                    //MD5 加密三次
                    String sign = MD5(MD5(MD5(appid + apikey + token)));
                    params.add(new BasicNameValuePair("appid", appid));
                    params.add(new BasicNameValuePair("apikey", apikey));
                    params.add(new BasicNameValuePair("Token", token));
                    params.add(new BasicNameValuePair("MD5", sign));

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                            makePostRequest(params);
                        }
                    };
                    new Thread(runnable).start();

                } else if (type.equals("PAYMENT")) {
                    Log.i("Main Demo", "Is " + type + "do else if condition...");

                    String order = bundle.getString("order");
                    String sign = bundle.getString("sign");
                    v.setText("json order: \n" + order + "\n" +
                            "sign:\n" + sign + "\n");

                }



            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        Log.i("Main Demo", "onActivityResult end...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            Log.d("KeyHash:", "start...");
            PackageInfo info = getPackageManager().getPackageInfo(
                    "beluga.com.belugademov3", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
            Log.d("KeyHash:", "end...");
        } catch (NameNotFoundException e) {
            Log.d("KeyHash e1:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash e2:", e.toString());
        }

        Button btnMorph1 = (Button) this.findViewById(R.id.b1);
        btnMorph1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartAuthClient();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void StartAuthClient() {

        String appid = "MEMBER";
        String apikey = "a0c5560931b60786a9190a29c03a38bc";
        String packageID = this.getClass().getPackage().getName();
        Intent intent;
        boolean inMaintain = false;
        String dialogTitle = "Warnings";// if inMaintain is false setDialog title null
        String dialogMessage = "server in maintain...";// if inMaintain is false setDialog message null

        intent = new Intent(this, AuthClientActivity.class);
        intent.putExtra(Keys.AppID.toString(), appid);
        intent.putExtra(Keys.ApiKey.toString(), apikey);
        intent.putExtra(Keys.PackageID.toString(), packageID);
        intent.putExtra(Keys.GameLogo.toString(), R.drawable.bg_pic);
        intent.putExtra(Keys.GameBackground.toString(), R.drawable.bg_pic);
        intent.putExtra(Keys.ActiveMaintainDialog.toString(), inMaintain);
        intent.putExtra(Keys.DialogMessage.toString(), dialogMessage);
        intent.putExtra(Keys.DialogTitle.toString(), dialogTitle);
        Log.i("pID", packageID);
        startActivityForResult(intent, 100);
    }

    public void startGooglePaymentButtonPress(View v) {
        String SKU_GAS = "gold";
        String base64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn2J6q0hd9FhArBYBcKSJabarKunSudfg/LUAwstUY/6UN581eoXEKBo7U2Kd2IA1GaAAXS3vAx4Nv9DAJrurBNof6JpCaEKjhzHLI8TWRqXh77K9dwM8mNMBnN83pP05pRLOMUz33Q/gd1wpQgFzumjl2ai/wAaIqb2YLCvOCUKPIBz5F4RedIySdMfSvIVsDt1FrIOxmPgyL7PFfU42nJMGle7o01hB+vvcMoOaOJu6Kmjkgbru6X6TRWXFfVXY/27iTbCmF1ASsS6btJgQAZr49Km23lZUlV4T+Po9CFfy04PS+uBXJvleUJPuKQe4GMLtcEfUkhQDZpllUvEI7wIDAQAB";
        String UserId = "1030176"; //user id

        Intent i = new Intent(MainActivity.this, InAppBillingActivity.class);
        Bundle b = new Bundle();
        b.putString(InAppBillingActivity.base64EncodedPublicKey, base64);
        b.putString(InAppBillingActivity.ItemID, SKU_GAS);
        b.putString(InAppBillingActivity.User_ID, UserId);


        b.putString(InAppBillingActivity.Server, "TestServer1");
        b.putString(InAppBillingActivity.Role, "leo");
        b.putString(InAppBillingActivity.Order, "TestBelugaItem");
        i.putExtras(b);
        startActivityForResult(i, InAppBillingActivity.GBilling_REQUEST);
    }


    public void startMOLPaymentButtonPress(View v) {
        String user_id = "1000005";
        String game_id = "04101786";
        String app_id = "herinv";
        String PackageID = this.getClass().getPackage().getName();
        String server_id = "999";
        String role = "leo";
        Intent i = new Intent(this, MOLActivity.class);
        Bundle b = new Bundle();
        b.putString("UserId", user_id);
        b.putString("gamerID", game_id);
        b.putString("AppID", app_id);
        b.putString("PackageID", PackageID);
        b.putString("ServerID", server_id);
        b.putString("RoleName", role);
        i.putExtras(b);
        startActivity(i);
    }


    public void startMyCardSmallPaymentButtonPress(View v) {
        Intent i = new Intent(this, MyCardActivity.class);
        Bundle b = new Bundle();

        String serviceType = MyCardActivity.TYPE_SMALL_PAYMENT;
        String apikey = "412c1bd510967dce3b050842a35fae18";
        String appid = "kilmasa";
        String uid = "1040714";


        b.putString("type", serviceType);
        b.putString(Keys.ApiKey.toString(), apikey);
        b.putString(Keys.AppID.toString(), appid);
        b.putString("uid", uid);


        b.putString("tserver", "TestServer1");
        b.putString("trol", "test");
        b.putString("titem", "TestItem1");


        b.putString("order", "order12345");
        i.putExtras(b);
        startActivity(i);
    }

    public void startMyCardSerialNumberButtonPress(View v) {
        Intent i = new Intent(this, MyCardActivity.class);
        Bundle b = new Bundle();

        b.putString("type", MyCardActivity.TYPE_SERIAL_NUMBER);
        b.putString(Keys.ApiKey.toString(), "4fbc7b551f8ab63d4dadd8694ff261bf");
        b.putString(Keys.AppID.toString(), "fanadv3");
        b.putString("uid", "1000448");


        b.putString("tserver", "TestServer1");
        b.putString("trol", "test");
        b.putString("titem", "TestItem1");

        b.putString("order", "order12345");
        i.putExtras(b);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://beluga.com.belugademov3/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        /*
                if(serviceIntent != null){
			stopService(serviceIntent);
		}
		*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    public void stopService(View v) {
        if (serviceIntent != null) {
            stopService(serviceIntent);
        }
    }

    public void callFacebookInvite(View v) {
        Log.i("main act", "invite start...");

        String appLinkUrl = "https://fb.me/320004664998837";
        String previewImageUrl = "https://lh3.googleusercontent.com/8yh5cZfJpWbUADdb3zF_XF-zxVFhc7-w7nl1sMMZFdI3UgidVLsDZ7LXeChz5C_X8GQ=w300-rw";
        Intent i = new Intent(this, FacebookFriendsInviteActivity.class);
        i.putExtra(Keys.AppLinkUrl.toString(), appLinkUrl);
        i.putExtra(Keys.AppLinkPreviewImageUrl.toString(), previewImageUrl);
        startActivity(i);

			/*
			String shareContentTitle = "testing";
			String shareContentDescription = "I am teing sorry!";
			Intent intent = new Intent(this, com.hosengamers.chee.invite.FacebookGameInviteActivity.class);
			intent.putExtra(Keys.ShareContentDescription.toString(), shareContentDescription);
			intent.putExtra(Keys.ShareContentTitle.toString(), shareContentTitle);
			startActivity(intent);
			*/

    }

    public void callFacebookShare(View v) {
        Log.i("main act", "invite start...");

        String shareContentTitle = "hello";
        String shareContentDescription = "hello";
        String shareContentUrl = "https://play.google.com/store/apps/details?id=com.HOSON.chess";
        String shareImageUrl = "https://lh3.googleusercontent.com/8yh5cZfJpWbUADdb3zF_XF-zxVFhc7-w7nl1sMMZFdI3UgidVLsDZ7LXeChz5C_X8GQ=w300-rw";
        Intent intent = new Intent(this, FacebookShare.class);
        intent.putExtra(Keys.ShareContentUrl.toString(), shareContentUrl);
        intent.putExtra(Keys.ShareImageUrl.toString(), shareImageUrl);
        intent.putExtra(Keys.ShareContentDescription.toString(), shareContentDescription);
        intent.putExtra(Keys.ShareContentTitle.toString(), shareContentTitle);
        startActivity(intent);

    }


    private void makePostRequest(List<NameValuePair> params) {


        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost("http://games.belugame.com/api/Auth/?");


        //Encoding POST data
        try {
            Log.i("test", "line 266...");
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            // log exception
            Log.i("test", "line 270...");
            e.printStackTrace();
        }

        //making POST request.
        try {
            Log.i("test", "line 276...");

            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
            Log.d("Http Post Response:", response.toString());
            Log.i("post status>>>", "httpResponse.getStatusLine().getStatusCode():" + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {

                String strResult = EntityUtils.toString(response.getEntity());
                Log.i("post Result>>>", "strResult:" + strResult);
            }
        } catch (ClientProtocolException e) {
            Log.i("test", "line 281...");
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("test", "line 285...");
            // Log exception
            e.printStackTrace();
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


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://beluga.com.belugademov3/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }
}
