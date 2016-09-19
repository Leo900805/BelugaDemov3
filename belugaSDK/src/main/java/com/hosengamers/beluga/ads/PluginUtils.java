package com.hosengamers.beluga.ads;

import android.util.Log;

/**
 * Created by Leo on 2016/9/16.
 */
public class PluginUtils {
    public static final String LOGTAG = "AdsUnity";

    public static String getErrorReason(int errorCode)
    {
        switch (errorCode) {
            case 0:
                return "Internal error";
            case 1:
                return "Invalid request";
            case 2:
                return "Network Error";
            case 3:
                return "No fill";
        }
        Log.w("AdsUnity", String.format("Unexpected error code: %s", new Object[] { Integer.valueOf(errorCode) }));
        return "";
    }
}
