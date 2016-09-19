package com.hosengamers.beluga.ads;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
/**
 * Created by Leo on 2016/9/16.
 */
public class Banner
{
    private static final int POSITION_TOP = 0;
    private static final int POSITION_BOTTOM = 1;
    private static final int POSITION_TOP_LEFT = 2;
    private static final int POSITION_TOP_RIGHT = 3;
    private static final int POSITION_BOTTOM_LEFT = 4;
    private static final int POSITION_BOTTOM_RIGHT = 5;
    private AdView adView;
    private Activity activity;
    private BannerAdListener listener;

    public Banner(Activity activity, BannerAdListener listener)
    {
        this.activity = activity;
        this.listener = listener;
    }

    public void create(final String publisherId, final AdSize adSize, final int positionCode)
    {
        this.activity.runOnUiThread(new Runnable()
        {
            public void run() {
                Banner.this.adView = new AdView(Banner.this.activity);

                Banner.this.adView.setBackgroundColor(0);
                Banner.this.adView.setAdUnitId(publisherId);
                Banner.this.adView.setAdSize(adSize);
                Banner.this.adView.setAdListener(new AdListener()
                {
                    public void onAdLoaded() {
                        if (Banner.this.listener != null)
                            Banner.this.listener.onAdLoaded();
                    }

                    public void onAdFailedToLoad(int errorCode)
                    {
                        if (Banner.this.listener != null)
                            Banner.this.listener.onAdFailedToLoad(PluginUtils.getErrorReason(errorCode));
                    }

                    public void onAdOpened()
                    {
                        if (Banner.this.listener != null)
                            Banner.this.listener.onAdOpened();
                    }

                    public void onAdClosed()
                    {
                        if (Banner.this.listener != null)
                            Banner.this.listener.onAdClosed();
                    }

                    public void onAdLeftApplication()
                    {
                        if (Banner.this.listener != null)
                            Banner.this.listener.onAdLeftApplication();
                    }
                });
                FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(-2, -2);

                switch (positionCode) {
                    case 0:
                        adParams.gravity = 49;
                        break;
                    case 1:
                        adParams.gravity = 81;
                        break;
                    case 2:
                        adParams.gravity = 51;
                        break;
                    case 3:
                        adParams.gravity = 53;
                        break;
                    case 4:
                        adParams.gravity = 83;
                        break;
                    case 5:
                        adParams.gravity = 85;
                }

                Banner.this.activity.addContentView(Banner.this.adView, adParams);
            }
        });
    }

    public void setAdListener(BannerAdListener listener) {
        this.listener = listener;
    }

    public void loadAd(final AdRequest request)
    {
        this.activity.runOnUiThread(new Runnable()
        {
            public void run() {
                Log.d("AdsUnity", "Calling loadAd() on Android");
                Banner.this.adView.loadAd(request);
            }
        });
    }

    public void show()
    {
        this.activity.runOnUiThread(new Runnable()
        {
            public void run() {
                Log.d("AdsUnity", "Calling show() on Android");
                Banner.this.adView.setVisibility(View.VISIBLE);
                Banner.this.adView.resume();
            }
        });
    }

    public void hide()
    {
        this.activity.runOnUiThread(new Runnable()
        {
            public void run() {
                Log.d("AdsUnity", "Calling hide() on Android");
                Banner.this.adView.setVisibility(View.INVISIBLE);
                Banner.this.adView.pause();
            }
        });
    }

    public void destroy()
    {
        this.activity.runOnUiThread(new Runnable()
        {
            public void run() {
                Log.d("AdsUnity", "Calling destroy() on Android");
                Banner.this.adView.destroy();
                ViewParent parentView = Banner.this.adView.getParent();
                if ((parentView != null) && ((parentView instanceof ViewGroup)))
                    ((ViewGroup)parentView).removeView(Banner.this.adView);
            }
        });
    }
}
