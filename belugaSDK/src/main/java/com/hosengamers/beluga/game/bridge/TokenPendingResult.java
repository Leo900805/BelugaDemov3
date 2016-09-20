package com.hosengamers.beluga.game.bridge;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/**
 * Created by user on 2016/9/20.
 */
public class TokenPendingResult extends PendingResult<TokenResult>
{
    private CountDownLatch latch = new CountDownLatch(1);
    private TokenResult result;
    private ResultCallback<? super TokenResult> resultCallback;

    public TokenPendingResult()
    {
        this.result = new TokenResult();
    }

    public TokenResult await()
    {
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            setResult(null, null, null, 14);
        }

        return getResult();
    }

    public TokenResult await(long l, TimeUnit timeUnit)
    {
        try {
            if (!this.latch.await(l, timeUnit))
                setResult(null, null, null, 15);
        }
        catch (InterruptedException e) {
            setResult(null, null, null, 14);
        }
        return getResult();
    }

    public void cancel()
    {
        setResult(null, null, null, 16);
        this.latch.countDown();
    }

    @Deprecated
    void setToken(String accessToken, String idToken, String email, int resultCode) {
        setResult(accessToken, idToken, email, resultCode);
        this.latch.countDown();
        if (getCallback() != null)
            getCallback().onResult(getResult());
    }

    public boolean isCanceled()
    {
        return (getResult() != null) && (getResult().getStatus().isCanceled());
    }

    public void setResultCallback(ResultCallback<? super TokenResult> resultCallback)
    {
        setCallback(resultCallback);
    }

    public void setResultCallback(ResultCallback<? super TokenResult> resultCallback, long l, TimeUnit timeUnit)
    {
        try
        {
            if (!this.latch.await(l, timeUnit))
                setResult(null, null, null, 15);
        }
        catch (InterruptedException e) {
            setResult(null, null, null, 14);
        }

        resultCallback.onResult(getResult());
    }

    private synchronized void setCallback(ResultCallback<? super TokenResult> callback)
    {
        this.resultCallback = callback;
    }

    private synchronized ResultCallback<? super TokenResult> getCallback() {
        return this.resultCallback;
    }

    private synchronized void setResult(String accessToken, String idToken, String email, int resultCode)
    {
        String atok = (this.result != null) && (accessToken == null) ? this.result.getAccessToken() : accessToken;
        String itok = (this.result != null) && (idToken == null) ? this.result.getIdToken() : idToken;
        String em = (this.result != null) && (email == null) ? this.result.getEmail() : email;

        this.result = new TokenResult(atok, itok, em, resultCode);
    }

    private synchronized TokenResult getResult() {
        return this.result;
    }

    public void setStatus(int status)
    {
        this.result.setStatus(status);
        this.latch.countDown();
        if (getCallback() != null)
            getCallback().onResult(getResult());
    }

    public void setEmail(String email)
    {
        this.result.setEmail(email);
    }

    public void setAccessToken(String accessToken) {
        this.result.setAccessToken(accessToken);
    }

    public void setIdToken(String idToken) {
        this.result.setIdToken(idToken);
    }
}
