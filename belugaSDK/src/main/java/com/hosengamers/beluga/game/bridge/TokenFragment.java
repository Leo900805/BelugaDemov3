package com.hosengamers.beluga.game.bridge;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/9/20.
 */
@TargetApi(11)
public class TokenFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = "TokenFragment";
    private static final String FRAGMENT_TAG = "gpg.TokenSupport";
    private static final int RC_SIGN_IN = 9001;
    private static final int REQUEST_ACCT_PERM = 10;
    private static final int OK_KEY = 43947;
    private GoogleApiClient mGoogleApiClient;
    private static final List<TokenRequest> pendingTokenRequests = new ArrayList();
    private boolean mShouldResolve = false;
    private boolean mIsResolving = false;

    private boolean mPendingPermissionRequest = false;
    private int mPermissionResult = -2147483648;

    public static PendingResult fetchToken(Activity parentActivity, String rationale, boolean fetchEmail, boolean fetchAccessToken, boolean fetchIdToken, String scope)
    {
        TokenRequest request = new TokenRequest(fetchEmail, fetchAccessToken, fetchIdToken, scope);
        request.setRationale(rationale);
        synchronized (pendingTokenRequests) {
            pendingTokenRequests.add(request);
        }

        TokenFragment fragment = (TokenFragment)parentActivity
                .getFragmentManager().findFragmentByTag("gpg.TokenSupport");

        if (fragment == null) {
            try {
                Log.d("TokenFragment", "Creating fragment");
                fragment = new TokenFragment();
                FragmentTransaction trans = parentActivity.getFragmentManager().beginTransaction();
                trans.add(fragment, "gpg.TokenSupport");
                trans.commit();
            } catch (Throwable th) {
                Log.e("TokenFragment", "Cannot launch token fragment:" + th.getMessage(), th);
                request.setResult(13);
                synchronized (pendingTokenRequests) {
                    pendingTokenRequests.remove(request);
                }
            }
        } else {
            Log.d("TokenFragment", "Fragment exists.. calling processRequests");
            fragment.processRequests(0);
        }

        return request.getPendingResponse();
    }

    private void processRequests(int errorCode)
    {
        TokenRequest request = null;

        if ((this.mGoogleApiClient == null) || (!this.mGoogleApiClient.isConnected())) {
            Log.d("TokenFragment", "mGoogleApiClient not created yet...");
            if ((this.mGoogleApiClient != null) && (!this.mGoogleApiClient.isConnecting())) {
                this.mGoogleApiClient.connect();
            }
            return;
        }

        if (!pendingTokenRequests.isEmpty()) {
            if (!permissionResolved()) {
                return;
            }
            if (this.mPermissionResult == -1) {
                errorCode = 3001;
            }
        }

        Log.d("TokenFragment", "Pending map in processRequests is " + pendingTokenRequests.size());
        while (!pendingTokenRequests.isEmpty())
        {
            if (!this.mGoogleApiClient.isConnected()) {
                if (this.mGoogleApiClient.isConnecting()) {
                    Log.w("TokenFragment", "Still connecting.... hold on...");
                } else {
                    Log.w("TokenFragment", "Google API Client not connected! calling connect");
                    this.mGoogleApiClient.connect();
                }
                return;
            }
            try
            {
                synchronized (pendingTokenRequests) {
                    if (!pendingTokenRequests.isEmpty()) {
                        request = (TokenRequest)pendingTokenRequests.remove(0);
                    }
                }
                if (request != null)
                {
                    if (errorCode == 0)
                        doGetToken(request);
                    else
                        request.setResult(errorCode);
                }
            } catch (Throwable th) {
                if (request != null) {
                    Log.e("TokenFragment", "Cannot process request", th);
                    request.setResult(13);
                }
            }
        }
        Log.d("TokenFragment", "Done with processRequests!");
    }

    @RequiresPermission("android.permission.GET_ACCOUNTS")
    @TargetApi(23)
    private boolean permissionResolved()
    {
        int rc = ActivityCompat.checkSelfPermission(getActivity(), "android.permission.GET_ACCOUNTS");
        if (rc == 0) {
            this.mPermissionResult = rc; } else {
            if ((!this.mPendingPermissionRequest) && (this.mPermissionResult == -2147483648)) {
                Log.d("TokenFragment", "GET_ACCOUNTS not granted, requesting.");
                this.mPendingPermissionRequest = true;

                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), "android.permission.GET_ACCOUNTS")) &&
                        (getActivity().getCurrentFocus() != null)) {
                    String rationale = ((TokenRequest)pendingTokenRequests.get(0)).getRationale();
                    if ((rationale == null) || (rationale.isEmpty())) {
                        rationale = "This application requires your email address or identity token";
                    }

                    Log.i("TokenFragment", "Displaying permission rationale to provide additional context.");
                    Snackbar.make(getActivity().getCurrentFocus(), rationale, -2)
                            .setAction("OK", new View.OnClickListener()
                            {
                                public void onClick(View view)
                                {
                                    ActivityCompat.requestPermissions(TokenFragment.this.getActivity(), new String[] { "android.permission.GET_ACCOUNTS" }, 10);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(
                            getActivity(), new String[] { "android.permission.GET_ACCOUNTS" }, 10);
                }

                return false;
            }

            this.mPermissionResult = -1;
            Log.i("TokenFragment", "Request is denied, permission for GET_ACCOUNTS is not granted: (" + this.mPermissionResult + ")");
        }

        return true;
    }

    private void doGetToken(final TokenRequest tokenRequest)
    {
        final Activity theActivity = getActivity();
        final GoogleApiClient googleApiClient = this.mGoogleApiClient;

        Log.d("TokenFragment", "Calling doGetToken for " + Plus.PeopleApi
                .getCurrentPerson(googleApiClient)
                .getDisplayName() + "e: " + tokenRequest.doEmail +
                " a:" + tokenRequest.doAccessToken + " i:" + tokenRequest.doIdToken);

        AsyncTask t = new AsyncTask()
        {
            protected TokenFragment.TokenRequest doInBackground(Object[] params)
            {
                String email = null;

                int statusCode = 0;

                if ((tokenRequest.doEmail) || (tokenRequest.doIdToken) || (tokenRequest.doAccessToken)) {
                    Log.d("TokenFragment", "Calling getAccountName");
                    try
                    {
                        email = Plus.AccountApi.getAccountName(googleApiClient);
                        tokenRequest.setEmail(email);
                    } catch (Throwable th) {
                        Log.e("TokenFragment", "Exception getting email: " + th.getMessage(), th);
                        statusCode = 8;
                        email = null;
                    }
                }

                if ((tokenRequest.doAccessToken) && (email != null))
                {
                    String accessScope = "oauth2:https://www.googleapis.com/auth/plus.me";
                    try {
                        Log.d("TokenFragment", "getting accessToken for " + email);
                        String accessToken = GoogleAuthUtil.getToken(theActivity, email, accessScope);
                        tokenRequest.setAccessToken(accessToken);
                    } catch (Throwable th) {
                        Log.e("TokenFragment", "Exception getting access token", th);
                        statusCode = 8;
                    }

                }

                if ((tokenRequest.doIdToken) && (email != null))
                {
                    if ((tokenRequest.getScope() != null) &&
                            (!tokenRequest
                                    .getScope().isEmpty())) {
                        try {
                            Log.d("TokenFragment", "Getting ID token.  Scope = " + tokenRequest
                                    .getScope() + " email: " + email);
                            String idToken = GoogleAuthUtil.getToken(theActivity, email, tokenRequest
                                    .getScope());
                            tokenRequest.setIdToken(idToken);
                        } catch (Throwable th) {
                            Log.e("TokenFragment", "Exception getting access token", th);
                            statusCode = 8;
                        }
                    } else {
                        Log.w("TokenFragment", "Skipping ID token: scope is empty");
                        statusCode = 10;
                    }
                }

                Log.d("TokenFragment", "Done with tokenRequest status: " + statusCode);
                tokenRequest.setResult(statusCode);

                return tokenRequest;
            }

            protected void onCancelled()
            {
                super.onCancelled();
                tokenRequest.cancel();
            }

            protected void onPostExecute(TokenFragment.TokenRequest tokenPendingResult)
            {
                Log.d("TokenFragment", "onPostExecute for the token fetch");
                super.onPostExecute(tokenPendingResult);
            }
        };
        t.execute(new Object[0]);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("TokenFragment", "onActivityResult: " + requestCode + ": " + resultCode);
        if (requestCode == 9001)
        {
            if (resultCode != -1) {
                this.mShouldResolve = false;
            }

            this.mIsResolving = false;
            this.mGoogleApiClient.connect();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onStart()
    {
        Log.d("TokenFragment", "onStart");
        this.mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        this.mShouldResolve = true;
        this.mGoogleApiClient.connect();
        this.mPermissionResult = -2147483648;

        super.onStart();
    }

    public void onResume()
    {
        Log.d("TokenFragment", "onResume called");
        processRequests(0);

        super.onResume();
    }

    public void onPause()
    {
        Log.d("TokenFragment", "onPause called");

        this.mGoogleApiClient.disconnect();
        super.onPause();
    }

    public void onConnected(Bundle bundle)
    {
        Log.d("TokenFragment", "onConnected:" + bundle);
        this.mShouldResolve = false;
        this.mPermissionResult = -2147483648;
        processRequests(0);
    }

    public void onConnectionSuspended(int i)
    {
        Log.d("TokenFragment", "Connection suspended");
    }

    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.d("TokenFragment", "onConnectionFailed:" + connectionResult);
        this.mPermissionResult = -2147483648;

        if ((!this.mIsResolving) && (this.mShouldResolve)) {
            if (connectionResult.hasResolution()) {
                try {
                    this.mIsResolving = true;
                    connectionResult.startResolutionForResult(getActivity(), 9001);
                } catch (IntentSender.SendIntentException e) {
                    Log.e("TokenFragment", "Could not resolve ConnectionResult.", e);
                    this.mIsResolving = false;
                    this.mGoogleApiClient.connect();
                }
            }
            else
            {
                showErrorDialog(connectionResult);
                this.mIsResolving = true;
            }
        }
        else processRequests(connectionResult.getErrorCode());
    }

    public void onStop()
    {
        if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient.disconnect();
        }
        this.mPermissionResult = -2147483648;
        super.onStop();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        Log.d("TokenFragment", "onRequestPermissionsResult: " + requestCode + "grants: " + grantResults.length);
        if (requestCode == 10) {
            this.mPendingPermissionRequest = false;
            if ((permissions.length == 1) && (permissions[0].equals("android.permission.GET_ACCOUNTS"))) {
                this.mPermissionResult = grantResults[0];
            }
            if (this.mPermissionResult == 0)
            {
                processRequests(0);
            } else {
                Log.w("TokenFragment", "Request for GET_ACCOUNTS was denied");
                processRequests(3001);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showErrorDialog(ConnectionResult connectionResult)
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (resultCode != 0)
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, 9001, new DialogInterface.OnCancelListener()
                {
                    public void onCancel(DialogInterface dialog)
                    {
                        TokenFragment.this.mShouldResolve = false;
                        TokenFragment.this.processRequests(resultCode);
                    }
                }).show();
            } else {
                Log.w("TokenFragment", "Google Play Services Error:" + connectionResult);
                String errorString = apiAvailability.getErrorString(resultCode);
                Toast.makeText(getActivity(), errorString, Toast.LENGTH_LONG).show();

                this.mShouldResolve = false;
                processRequests(resultCode); }
    }
    private static class TokenRequest { private TokenPendingResult pendingResponse;
        private boolean doEmail;
        private boolean doAccessToken;
        private boolean doIdToken;
        private String scope;
        private String rationale;

        public TokenRequest(boolean fetchEmail, boolean fetchAccessToken, boolean fetchIdToken, String scope) { this.pendingResponse = new TokenPendingResult();
            this.doEmail = fetchEmail;
            this.doAccessToken = fetchAccessToken;
            this.doIdToken = fetchIdToken;
            this.scope = scope; }

        public PendingResult getPendingResponse()
        {
            return this.pendingResponse;
        }

        public void setResult(int code) {
            this.pendingResponse.setStatus(code);
        }

        public void setEmail(String email) {
            this.pendingResponse.setEmail(email);
        }

        public void cancel() {
            this.pendingResponse.cancel();
        }

        public String getScope() {
            return this.scope;
        }

        public void setAccessToken(String accessToken) {
            this.pendingResponse.setAccessToken(accessToken);
        }

        public void setIdToken(String idToken) {
            this.pendingResponse.setIdToken(idToken);
        }

        public String getRationale() {
            return this.rationale;
        }

        public void setRationale(String rationale) {
            this.rationale = rationale;
        }
    }
}
