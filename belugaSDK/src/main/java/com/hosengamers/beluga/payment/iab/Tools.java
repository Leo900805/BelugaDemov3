package com.hosengamers.beluga.payment.iab;

import android.app.Activity;
import android.util.Log;

public class Tools {
	protected static String getStringByName(Activity act ,String name){
		Log.i("class Tools name:", ""+ name);
		Log.i("class Tools name:", ""+ act.getPackageName());
		//Log.i("class Tools name:", ""+ act.getApplicationContext().getPackageName());
		int resourceId = act.getResources().getIdentifier(name, "String", act.getPackageName());
		Log.i("class Tools resourceId:", ""+resourceId);
		String rStr = act.getResources().getString(resourceId);
		Log.i("class Tools rStr:", rStr);
		return rStr;
	}
}
