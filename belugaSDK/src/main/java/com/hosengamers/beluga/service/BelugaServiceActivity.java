package com.hosengamers.beluga.service;

import com.hosengamers.beluga.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class BelugaServiceActivity extends Activity {
	
	private TextView idContent, userIdContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_account_layout);
		
		Bundle b = getIntent().getExtras(); 
		idContent = (TextView)findViewById(R.id.id_content_textView);
		userIdContent = (TextView)findViewById(R.id.user_id_content_textView);
		
		Log.i("ServiceAccActivity", "B :"+b.getString("uid"));
		idContent.setText(b.getString("uid"));
		userIdContent.setText(b.getString("userid"));
	}

}
