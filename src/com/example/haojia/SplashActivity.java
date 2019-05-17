package com.example.haojia;
//進入"好食在"起始畫面
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class SplashActivity extends Activity{
	private final int SPLASH_DISPLAY_LENGHT = 2000;//延遲2秒鐘
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash); 
		
		new Handler().postDelayed(new Runnable() {
			public void run() {  
				Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);  
				SplashActivity.this.startActivity(mainIntent);  
				SplashActivity.this.finish();  
			} 
		}, SPLASH_DISPLAY_LENGHT);  
	}
}
