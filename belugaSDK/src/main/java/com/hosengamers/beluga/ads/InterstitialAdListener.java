package com.hosengamers.beluga.ads;

/**
 * Created by user on 2016/9/19.
 */
public interface InterstitialAdListener {
    public abstract void onAdLoaded();

    public abstract void onAdFailedToLoad(String paramString);

    public abstract void onAdOpened();

    public abstract void onAdClosed();

    public abstract void onAdLeftApplication();
}
