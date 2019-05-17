package com.example.haojia;
//籤筒搖晃監聽

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.haojia.ShakeListener.OnShakeListener;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

public class RandomActivity extends Activity {	
	//取得緯經度
	private LocationManager locationManager;
	private Location myLocation;
	private Criteria criteria;	
	
	//layout顯示
	TextView r_name; //顯示餐廳名
	TextView r_address; //顯示地址
	TextView r_distance; //顯示距離
	TextView random_info_text; //點即可進入詳細資訊
	
	//存放使用者座標位置
	String User_Latitude; //緯度
	String User_Longitude; //經度
	String Set_Distance; //設置範圍
	String R_no; //餐廳編號
	String R_name; //餐廳名
	String R_address; //餐廳地址
	double distance; //距離
	
	private Vibrator vibrator;
	//random_img(籤筒圖) random_head(使用前搖一搖) random_note(不喜歡嗎 在搖一次)
	private ImageView random_img, random_head, random_note;
	private ImageButton result_btn; //搖出後的空白圖 可點入詳細資訊
	private ShakeListener shakeListener;
	
	ShakeListener mShakeListener = null;	
	String Sql_string; //存放SQL指令變數
	
	//籤筒圖檔
	private	int ico[] = { R.drawable.random_img_left, R.drawable.random_img,R.drawable.random_img_right };
	private	int index = 0; 
	private int random;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//StrictMode嚴格模式，連線SQL必加，最新支援版本3.0
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
		.detectDiskReads()  
		.detectDiskWrites()  
		.detectNetwork()  
		.penaltyLog()  
		.build());  
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
		.detectLeakedSqlLiteObjects()   
		.penaltyLog()  
		.penaltyDeath()  
		.build()); 			
		
		// 取得座標位置程式碼
		// 取得LocationManager物件
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// 建立Criteria規則物件，並要求精準的定位功能
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		// 依據criteria的規則回傳最適合的定位名稱，
		// true代表只回傳目前可提供的定位名稱
		String provider = locationManager.getBestProvider(criteria, true);

		// 利用指定的定位名稱來取得自己最新位置
		Location myLocation = locationManager.getLastKnownLocation(provider);
		this.myLocation = myLocation;	
		
		//取得使用者座標 轉為String型態
		User_Latitude=String.valueOf(myLocation.getLatitude());
		User_Longitude=String.valueOf(myLocation.getLongitude());
		//手動/自動設定 距離範圍
	    Set_Distance="0.5";
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.random_activity);
		init();
		
		//元件連接 顯示搖出結果
		r_name = (TextView)this.findViewById(R.id.r_name);
		r_address = (TextView)this.findViewById(R.id.r_address);
		r_distance = (TextView)this.findViewById(R.id.r_distance);
		random_info_text = (TextView)this.findViewById(R.id.random_info_text);
	}
	
	public void init(){
		vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		shakeListener = new ShakeListener(this);
		shakeListener.setOnShake(onShake);

		random_head = (ImageView) this.findViewById(R.id.random_head); //標題
		random_img = (ImageView) this.findViewById(R.id.random_img); //籤筒圖
		random_note = (ImageView) this.findViewById(R.id.random_note); //在搖一次
		result_btn = (ImageButton) this.findViewById(R.id.result_btn); //顯示結果
	}

	private OnShakeListener onShake = new OnShakeListener() {
		@Override
		public void onShake() {
			//搖一搖後啟動
			//GONE 隱藏圖片
			random_head.setVisibility(View.GONE);
			result_btn.setVisibility(View.GONE);
			random_note.setVisibility(View.GONE);

			startVibrator();
			shakeListener.stop();

			handler.sendEmptyMessageDelayed(1, 200);
			handler.sendEmptyMessageDelayed(2, 2000);
			
			Sql_string="SELECT R_No,R_Name,R_Address,R_Latitude,R_Longitude," +
			"( 6371 * acos( cos( radians( "+User_Latitude+" ) ) * cos( radians( `R_Latitude` ) ) * cos( radians( `R_Longitude` ) - radians( "+User_Longitude+" ) ) + sin( radians( "+User_Latitude+" ) ) * sin(radians(`R_Latitude`)) ) ) AS `R_Distance` " +
			"FROM restaurant ";
			Sql_string += "HAVING `R_Distance` < "+ Set_Distance + " "; 
			Sql_string += "ORDER BY `R_Distance`";
				
			String result = DBconnection.executeQuery(Sql_string);		
			Log.e("log_tag","JSON"+result.toString());
			try{ 
				JSONArray jsonArray = new JSONArray(result);
				Random rd = new Random(); 
				random = rd.nextInt(jsonArray.length());
				JSONObject jsonData = jsonArray.getJSONObject(random);
				R_no = jsonData.getString("R_No");
				R_name = jsonData.getString("R_Name");
				R_address = jsonData.getString("R_Address");				
				distance = Distance(myLocation.getLatitude(),myLocation.getLongitude(),jsonData.getDouble("R_Latitude"),jsonData.getDouble("R_Longitude"));				
			}
			catch(JSONException e){
				Log.e("log_tag", "JSON轉換錯誤"+e.toString());
			}					
		}
	};
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			//隱藏 GONE  顯示 VISIBLE
			r_name.setVisibility(View.GONE);
			r_address.setVisibility(View.GONE);
			r_distance.setVisibility(View.GONE);
			random_info_text.setVisibility(View.GONE);

			if (msg.what == 1) {
				if (index < ico.length - 1) {
					index++;
				} else {
					index = 0;
				}
				random_img.setBackgroundResource(ico[index]);
				handler.sendEmptyMessageDelayed(1, 200);
			} 
			else {
				//random_img.setBackgroundResource(R.drawable.random_result);
				handler.removeMessages(1);
				shakeListener.start();
							
				result_btn.setVisibility(View.GONE);
				random_head.setVisibility(View.GONE);		
				random_head.setVisibility(View.VISIBLE);
				
				r_name.setVisibility(View.VISIBLE);
				r_address.setVisibility(View.VISIBLE);
				r_distance.setVisibility(View.VISIBLE);
				random_info_text.setVisibility(View.VISIBLE);
				
				//顯示搖一搖後的字串
				if(distance >= 1){ //大於等於1  不用轉換，小於轉
					r_name.setText(R_name);
					r_address.setText(R_address);
					r_distance.setText(Math.round((distance*10.0))/10.0+"km");
					random_info_text.setText("點擊即可進入詳細資訊");
					/*
					//字體大小
					r_name.setTextSize(19.0f);
					r_address.setTextSize(16.0f);
					r_distance.setTextSize(14.0f);
					*/
				}
	        	else if(distance < 1){
					r_name.setText(R_name);
					r_address.setText(R_address);
					r_distance.setText(Math.round(distance*1000)+"公尺");
					random_info_text.setText("點擊即可進入詳細資訊");
					/*
					//字體大小
					r_name.setTextSize(19.0f);
					r_address.setTextSize(16.0f);
					r_distance.setTextSize(14.0f);
	        	*/
	        	}

				result_btn.setVisibility(View.VISIBLE);
				random_head.setVisibility(View.VISIBLE);
				random_head.setBackgroundResource(R.drawable.random_note); 		
				result_btn.setOnClickListener(new ImageButton.OnClickListener()
				{
					public void onClick(View v)
						{
		        		Intent restaurant_info_Intent =new Intent();
		        		restaurant_info_Intent.setClass(RandomActivity.this,RestaurantInfo.class);
		        		//宣告要傳送的變數(並放入No) 
		        		Bundle r_no_bundle = new Bundle();
		        		r_no_bundle.putString("RNo",R_no); //No
		        		restaurant_info_Intent.putExtras(r_no_bundle);
		        		startActivity(restaurant_info_Intent);
						}						
				});
			}		
		}	
	};
	
	
	public void startVibrator() {
		vibrator.vibrate(new long[] { 500, 300, 500, 300 }, -1);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	//兩定位座標 距離公式
	public double Distance(double User_latitude,double User_longitude,double R_latitude ,double R_longitude) 
	{
		double User_lat	= ConvertDegreeToRadians(User_latitude);
		double R_lat = ConvertDegreeToRadians(R_latitude);
		double User_long = ConvertDegreeToRadians(User_longitude);
		double R_long = ConvertDegreeToRadians(R_longitude);
		
		double R = 6371; // Earth's radius (km)
		double distance = Math.acos(Math.sin(User_lat) *
		Math.sin(R_lat) + Math.cos(User_lat) *
		Math.cos(R_lat) *
		Math.cos(R_long - User_long)) * R;
		
		return distance; //傳回資料KM為單位
	}
	
	private double ConvertDegreeToRadians(double degrees){
		return (Math.PI/180)*degrees;
	}	
	
}
