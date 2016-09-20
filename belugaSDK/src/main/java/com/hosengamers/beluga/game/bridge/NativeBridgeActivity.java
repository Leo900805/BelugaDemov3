package com.hosengamers.beluga.game.bridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
/**
 * Created by user on 2016/9/20.
 */
public final class NativeBridgeActivity extends Activity
{
    private static final String BRIDGED_INTENT = "BRIDGED_INTENT";
    private static final int GPG_RESPONSE_CODE = 4673607;
    private static final int BG_COLOR = 1090519039;
    private static final String TAG = "NativeBridgeActivity";
    private boolean pendingResult;

    private native void forwardActivityResult(int paramInt1, int paramInt2, Intent paramIntent);

    public void onCreate(Bundle savedInstanceState)
    {
        View v = new View(this);
        v.setBackgroundColor(1090519039);
        setContentView(v);
        super.onCreate(savedInstanceState);
    }

    protected void onStart()
    {
        Intent bridgedIntent = (Intent)getIntent().getParcelableExtra("BRIDGED_INTENT");
        if (bridgedIntent != null) {
            startActivityForResult(bridgedIntent, 4673607);
        }
        super.onStart();
    }

    public void startActivityForResult(Intent intent, int requestCode)
    {
        this.pendingResult = (requestCode == 4673607);
        if (this.pendingResult)
            Log.d("NativeBridgeActivity", "starting GPG activity: " + intent);
        else {
            Log.i("NativeBridgeActivity", "starting non-GPG activity: " + requestCode + " " + intent);
        }
        super.startActivityForResult(intent, requestCode);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 4673607) {
            Log.d("NativeBridgeActivity", "Forwarding activity result to native SDK.");
            forwardActivityResult(requestCode, resultCode, data);

            this.pendingResult = false;
        } else {
            Log.d("NativeBridgeActivity", "onActivityResult for unknown request code: " + requestCode + " calling finish()");
        }

        finish();

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void launchBridgeIntent(Activity parentActivity, Intent intent)
    {
        Log.d("NativeBridgeActivity", "Launching bridge activity: parent:" + parentActivity + " intent " + intent);
        Intent bridgeIntent = new Intent(parentActivity, NativeBridgeActivity.class);
        bridgeIntent.putExtra("BRIDGED_INTENT", intent);
        parentActivity.startActivity(bridgeIntent);
    }

    protected void onDestroy()
    {
        if (this.pendingResult) {
            Log.w("NativeBridgeActivity", "onDestroy called with pendingResult == true.  forwarding canceled result");
            forwardActivityResult(4673607, 0, null);
            this.pendingResult = false;
        }

        super.onDestroy();
    }

    static
    {
        System.loadLibrary("gpg");
    }
}
