package com.hosengamers.beluga.ads;

/**
 * Created by Leo on 2016/9/16.
 */
public abstract interface BannerAdListener
{
    public abstract void onAdLoaded();

    public abstract void onAdFailedToLoad(String paramString);

    public abstract void onAdOpened();

    public abstract void onAdClosed();

    public abstract void onAdLeftApplication();
}
