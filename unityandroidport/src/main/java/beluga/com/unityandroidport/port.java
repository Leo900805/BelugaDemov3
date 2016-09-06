package beluga.com.unityandroidport;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hosengamers.beluga.belugakeys.Keys;
import com.hosengamers.beluga.loginpage.AuthClientActivity;
import com.hosengamers.beluga.payment.iab.InAppBillingActivity;
import com.hosengamers.beluga.payment.mol.MOLActivity;
import com.hosengamers.beluga.payment.mycard.MyCardActivity;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import org.json.JSONObject;

/**
 * Created by user on 2016/8/23.
 */
public class port extends UnityPlayerActivity
{

    private static String unityGameObjName;
    private static String unityMethod;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("Main Demo", "onActivityResult start...");
        Log.i("Main Demo", "requestCode:" + requestCode);
        Log.i("Main Demo", "resultCode:-1");
        if ((requestCode == 100) && (resultCode == -1))
        {
            try
            {
                Bundle bundle = data.getExtras();
                String type = bundle.getString("type");

                Log.i("Main Demo", "type is " + type);
                if (type.equals("LOGIN")) {
                    Log.i("Main Demo", "Is " + type + "do if condition...");
                    String jsonData = bundle.getString(Keys.JsonData.toString());

                    String sendUnityStr = jsonData + "\n";
                    Log.i("Main Demo", "sendUnityStr value:" + sendUnityStr );
                    UnityPlayer.UnitySendMessage(unityGameObjName, unityMethod, sendUnityStr);
                }
                else if (type.equals("PAYMENT")) {

                    JSONObject statusJson = new JSONObject();

                    Log.i("Main Demo", "Is " + type + "do else if condition...");

                    statusJson.put("response", bundle.getInt("response"));
                    Log.i("statusJson", "response"+bundle.getInt("response"));
                    statusJson.put("status", bundle.getString("status"));
                    Log.i("statusJson", bundle.getString("status"));
                    JSONObject orderJson = new JSONObject( bundle.getString("order") );
                    Log.i("orderJson", orderJson.toString());
                    statusJson.put("order", orderJson);
                    Log.i("statusJson", statusJson.toString() );
                    String sendUnityStr = statusJson.toString();
                    Log.i("sent to unity payment", unityGameObjName + "," + unityMethod + "," + sendUnityStr);
                    UnityPlayer.UnitySendMessage(unityGameObjName, unityMethod, sendUnityStr);
                }else if(type.equals("SHARE")){
                    String value = bundle.getString(Keys.JsonData.toString());
                    UnityPlayer.UnitySendMessage(unityGameObjName, unityMethod, value);
                }else if(type.equals("GAME_INVITE")){
                    String value = bundle.getString(Keys.JsonData.toString());
                    UnityPlayer.UnitySendMessage(unityGameObjName, unityMethod, value);
                }else if (type.equals("APP_INVITE")){
                    String value = bundle.getString(Keys.JsonData.toString());
                    UnityPlayer.UnitySendMessage(unityGameObjName, unityMethod, value);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        Log.i("Main Demo", "onActivityResult end...");
    }

    public static void StartAuthClient(String UnityGameObj, String UnityMethod, String appid, String apikey, byte[] gameLogo, String packageID, boolean inMaintain, String dialogTitle, String dialogMessage)
    {
        Log.e("UnityActivity", "StartAuthClient...");

        unityGameObjName = UnityGameObj;
        unityMethod = UnityMethod;

        Intent intent = new Intent(UnityPlayer.currentActivity, AuthClientActivity.class);
        intent.putExtra(Keys.AppID.toString(), appid);
        intent.putExtra(Keys.ApiKey.toString(), apikey);
        intent.putExtra(Keys.PackageID.toString(), packageID);
        intent.putExtra(Keys.GameLogoForByteArray.toString(), gameLogo);
        intent.putExtra(Keys.ActiveMaintainDialog.toString(), inMaintain);
        intent.putExtra(Keys.DialogMessage.toString(), dialogMessage);
        intent.putExtra(Keys.DialogTitle.toString(), dialogTitle);
        Log.i("pID", packageID);
        UnityPlayer.currentActivity.startActivityForResult(intent, 100);
    }

    public static void startGooglePaymentButtonPress(String UnityGameObj, String UnityMethod, String SKU_GAS, String base64, String userId,
                                              String serverId, String role, String orderId){
        //String SKU_GAS = "beluga.gold";
        //String base64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn2J6q0hd9FhArBYBcKSJabarKunSudfg/LUAwstUY/6UN581eoXEKBo7U2Kd2IA1GaAAXS3vAx4Nv9DAJrurBNof6JpCaEKjhzHLI8TWRqXh77K9dwM8mNMBnN83pP05pRLOMUz33Q/gd1wpQgFzumjl2ai/wAaIqb2YLCvOCUKPIBz5F4RedIySdMfSvIVsDt1FrIOxmPgyL7PFfU42nJMGle7o01hB+vvcMoOaOJu6Kmjkgbru6X6TRWXFfVXY/27iTbCmF1ASsS6btJgQAZr49Km23lZUlV4T+Po9CFfy04PS+uBXJvleUJPuKQe4GMLtcEfUkhQDZpllUvEI7wIDAQAB";
        //String UserId = "1030176"; //user id
        Log.i("Unity Activity", "IAB Start...");

        unityGameObjName = UnityGameObj;
        unityMethod = UnityMethod;
        Intent i = new Intent(UnityPlayer.currentActivity, com.hosengamers.beluga.payment.iab.InAppBillingActivity.class);
        Bundle b = new Bundle();
        b.putString(InAppBillingActivity.base64EncodedPublicKey, base64);
        b.putString(InAppBillingActivity.ItemID, SKU_GAS);
        b.putString(InAppBillingActivity.User_ID, userId);


        b.putString(InAppBillingActivity.Server, serverId);
        b.putString(InAppBillingActivity.Role, role);
        b.putString(InAppBillingActivity.Order, orderId);
        i.putExtras(b);
        UnityPlayer.currentActivity.startActivityForResult(i, InAppBillingActivity.GBilling_REQUEST);
        Log.i("Unity Activity", "IAB end...");
    }

    public static void startMOLPaymentButtonPress(String UnityGameObj, String UnityMethod, String user_id, String game_id, String app_id,
                                           String PackageID, String server_id, String role) {
        //String user_id = "1000005";
        //String game_id = "04101786";
        //String app_id = "herinv";
        //String PackageID = this.getClass().getPackage().getName();
        //String server_id = "999";
        //String role = "leo";
        unityGameObjName = UnityGameObj;
        unityMethod = UnityMethod;
        Intent i = new Intent(UnityPlayer.currentActivity, MOLActivity.class);
        Bundle b = new Bundle();
        b.putString("UserId",user_id);
        b.putString("gamerID", game_id);
        b.putString("AppID", app_id);
        b.putString("PackageID", PackageID);
        b.putString("ServerID", server_id);
        b.putString("RoleName", role);
        i.putExtras(b);
        UnityPlayer.currentActivity.startActivity(i);
    }

    public static void startMyCardSmallPaymentButtonPress(String UnityGameObj, String UnityMethod, String apikey,String appid, String uid,
                                                   String server_id, String role, String itemId, String orderId) {

        unityGameObjName = UnityGameObj;
        unityMethod = UnityMethod;
        Intent i = new Intent(UnityPlayer.currentActivity, MyCardActivity.class);
        Bundle b = new Bundle();
        //String serviceType = MyCardActivity.TYPE_SMALL_PAYMENT;
        //String apikey = "412c1bd510967dce3b050842a35fae18";
        //String appid = "kilmasa";
        //String uid = "1040714";
        b.putString("type",  MyCardActivity.TYPE_SMALL_PAYMENT);
        b.putString(Keys.ApiKey.toString(), apikey);
        b.putString(Keys.AppID.toString(), appid);
        b.putString("uid", uid);
        b.putString("tserver", server_id);
        b.putString("trol", role);
        b.putString("titem", itemId);
        b.putString("order", orderId);
        i.putExtras(b);
        UnityPlayer.currentActivity.startActivity(i);
    }

    public static void startMyCardSerialNumberButtonPress(String UnityGameObj, String UnityMethod, String apikey,String appid, String uid,
                                                   String server_id, String role, String itemId, String orderId) {

        unityGameObjName = UnityGameObj;
        unityMethod = UnityMethod;
        Intent i = new Intent(UnityPlayer.currentActivity, MyCardActivity.class);
        Bundle b = new Bundle();
        b.putString("type", MyCardActivity.TYPE_SERIAL_NUMBER);
        b.putString(Keys.ApiKey.toString(), "4fbc7b551f8ab63d4dadd8694ff261bf");
        b.putString(Keys.AppID.toString(), "fanadv3");
        b.putString("uid","1000448");

        b.putString("tserver", server_id);
        b.putString("trol", role);
        b.putString("titem", itemId);
        b.putString("order", orderId);
        i.putExtras(b);
        UnityPlayer.currentActivity.startActivity(i);
    }

    public static void callFacebookInvite(String UnityGameObj, String UnityMethod, String appUrl, String preImageUrl  ){
        Log.i("main act", "invite start...");

        unityGameObjName = UnityGameObj;
        unityMethod = UnityMethod;
        String appLinkUrl =appUrl;
        String previewImageUrl = preImageUrl;
        Intent i = new Intent(UnityPlayer.currentActivity, com.hosengamers.beluga.invite.FacebookFriendsInviteActivity.class);
        i.putExtra(Keys.AppLinkUrl.toString(), appLinkUrl);
        i.putExtra(Keys.AppLinkPreviewImageUrl.toString(), previewImageUrl);
        UnityPlayer.currentActivity.startActivityForResult(i, 100);
    }

    public static void callFacebookGameInvite(String UnityGameObj, String UnityMethod, String title, String description ){
        Log.i("main act", "game invite start...");

        unityGameObjName = UnityGameObj;
        unityMethod = UnityMethod;
        String shareContentTitle = title;
        String shareContentDescription = description;
        Intent intent = new Intent(UnityPlayer.currentActivity, com.hosengamers.beluga.invite.FacebookGameInviteActivity.class);
        intent.putExtra(Keys.ShareContentDescription.toString(), shareContentDescription);
        intent.putExtra(Keys.ShareContentTitle.toString(), shareContentTitle);
        UnityPlayer.currentActivity.startActivityForResult(intent, 100);
    }

    public static void callFacebookShare(String UnityGameObj, String UnityMethod, String title, String description, String contentUrl, String imageUrl){
        Log.i("main act", "invite start...");

        unityGameObjName = UnityGameObj;
        unityMethod = UnityMethod;
        String shareContentTitle = title;
        String shareContentDescription = description;
        String shareContentUrl = contentUrl;
        String shareImageUrl = imageUrl;
        Intent intent = new Intent(UnityPlayer.currentActivity, com.hosengamers.beluga.share.FacebookShare.class);
        intent.putExtra(Keys.ShareContentUrl.toString(), shareContentUrl);
        intent.putExtra(Keys.ShareImageUrl.toString(), shareImageUrl);
        intent.putExtra(Keys.ShareContentDescription.toString(), shareContentDescription);
        intent.putExtra(Keys.ShareContentTitle.toString(), shareContentTitle);
        UnityPlayer.currentActivity.startActivityForResult(intent, 100);

    }
}