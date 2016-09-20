package com.hosengamers.beluga.game.bridge;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
/**
 * Created by user on 2016/9/20.
 */
public class TokenResult
        implements Result
{
    private Status status;
    private String accessToken;
    private String idToken;
    private String email;

    public TokenResult()
    {
    }

    TokenResult(String accessToken, String idToken, String email, int resultCode)
    {
        this.status = new Status(resultCode);
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.email = email;
    }

    public Status getStatus()
    {
        return this.status;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getIdToken() {
        return this.idToken;
    }

    public String getEmail() {
        return this.email;
    }

    public void setStatus(int status) {
        this.status = new Status(status);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
