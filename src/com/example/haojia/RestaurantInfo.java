package com.example.haojia;

//餐廳詳細資訊
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RestaurantInfo extends Activity{
	//取得緯經度
	private LocationManager locationManager;
	private Location myLocation;
	private Criteria criteria;
	
	//bundle宣告 存放上一頁傳來變數
	String RNo;
	
	//版面顯示宣告
	//ImageView宣告
	ImageView r_photo;
	//顯示TextView宣告
	TextView r_name_textview; //名稱
	TextView r_score_textview; //分數
	TextView r_type_textview; //類別
	TextView r_distance_textview; //距離
	TextView r_address_textview; //地址
	TextView r_telephone_textview; //電話 
	TextView r_openclose_hours_textview; //營業區間 
	TextView r_note_textview; //備註
	TextView r_suggest_textview;
	//img btn宣告
	ImageButton favorite_btn;
	ImageButton r_address_btn; //位置導航btn
	ImageButton r_telephone_btn; //撥打電話btn
	
	String Sql_string;//存放SQL指令變數
	//存放SQL傳回資料變數
	String r_photo_url; //圖片網址
	String r_name; //名稱
	String r_score; //分數
	String r_type_group; //類別-群組
	String r_type_class; //類別-種類
	String r_address; //地址
	String r_telephone; //電話 
	double r_latitude; //緯度
	double r_longitude; //經度
	double distance; //距離(自行於APP端計算)
	String r_openinghours; //開店時間
	String r_closinghours; //打烊時間
	String r_note; //備註
	String r_suggest; //建議菜色
	
	//使用者座標 
	double User_Latitude; //緯度
	double User_Longitude; //經度
	
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
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restaurant_info);
		
		//本機測試時 需註解
		//取得座標位置程式碼
		//取得LocationManager物件
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		//建立Criteria規則物件，並要求精準的定位功能
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		//依據criteria的規則回傳最適合的定位名稱，
		//true代表只回傳目前可提供的定位名稱
		String provider = locationManager.getBestProvider(criteria, true);

		//利用指定的定位名稱來取得自己最新位置
		Location myLocation = locationManager.getLastKnownLocation(provider);
		this.myLocation = myLocation;		
		
		//元件連接
		r_photo = (ImageView) findViewById(R.id.r_photo); //照片
		r_name_textview = (TextView) findViewById(R.id.r_name); //餐廳名稱
		r_score_textview = (TextView) findViewById(R.id.r_score); //分數
		r_type_textview = (TextView) findViewById(R.id.r_type); //類別
		r_distance_textview = (TextView) findViewById(R.id.r_distance); //距離
		r_address_textview = (TextView) findViewById(R.id.r_address); //地址
		r_telephone_textview = (TextView) findViewById(R.id.r_telephone); //電話
		r_openclose_hours_textview = (TextView) findViewById(R.id.r_openclose_hours); //營業區間
		r_note_textview = (TextView) findViewById(R.id.r_note); //備註
		r_suggest_textview = (TextView) findViewById(R.id.r_suggest); //建議菜色
		favorite_btn =  (ImageButton) findViewById(R.id.favoriteI_btn); //最愛btn
		r_address_btn = (ImageButton) findViewById(R.id.r_address_btn); //地址btn
		r_telephone_btn = (ImageButton) findViewById(R.id.r_telephone_btn); //電話btn
		
		User_Latitude = myLocation.getLatitude();
		User_Longitude = myLocation.getLongitude();
		
		//接收上一Activity傳來的字串，並放進RNo變數裡
		Bundle r_no_bundle = this.getIntent().getExtras();
	    RNo = r_no_bundle.getString("RNo"); //RNo
	    
	    Sql_string = "SELECT restaurant.*,restaurant_type.R_TypeGroup,restaurant_type.R_TypeClass " +
	    "FROM `restaurant` " +
	    "LEFT JOIN `restaurant_type` ON restaurant.R_Type = restaurant_type.R_TypeID " +
		"WHERE `R_No` = " + RNo; 
		String result = DBconnection.executeQuery(Sql_string);
		Log.e("log_tag","JSON格式錯誤"+result.toString());
		try 
		{
			JSONArray jsonArray = new JSONArray(result);
	        JSONObject jsonData = jsonArray.getJSONObject(0);
	        //獲取JSON值 進變數
	        r_name = jsonData.getString("R_Name");
	        r_score = jsonData.getString("R_Score");
	        r_address = jsonData.getString("R_Address");
	        r_telephone = jsonData.getString("R_Telephone");
	        r_latitude = jsonData.getDouble("R_Latitude");
	        r_longitude = jsonData.getDouble("R_Longitude");
	        r_openinghours = jsonData.getString("R_OpeningHours");
	    	r_closinghours = jsonData.getString("R_ClosingHours");
	    	r_note = jsonData.getString("R_Note");
	    	r_suggest = jsonData.getString("R_Suggest");
	    	r_photo_url = jsonData.getString("R_Photo");
	    	r_type_group = jsonData.getString("R_TypeGroup"); 
	    	r_type_class = jsonData.getString("R_TypeClass");
		}
	    catch(JSONException e)
	    {
	        Log.e("log_tag","JSON轉換錯誤"+e.toString());
	    }
	    	
	    //結果顯示
		r_name_textview.setText(r_name); //餐廳名稱
	    r_score_textview.setText("點擊數："+r_score); //分數
	    
	    //類別
	    if(r_type_group.equals(r_type_class) == true){
	    	r_type_textview.setText(r_type_group);
	    }
	    else{
	    	r_type_textview.setText(r_type_group+" - "+r_type_class);	    	
	    }
	    
	    //距離
	    distance=Distance(User_Latitude,User_Longitude,r_latitude,r_longitude);
	    if(distance >= 1){ //大於等於1  不用轉換，小於轉
	    	r_distance_textview.setText(Math.round((distance*10.0))/10.0+"公里"); //
	    }
        else if(distance < 1){
        	r_distance_textview.setText(Math.round(distance*1000)+"公尺"); //轉公尺取整數
        }	        
	    
	    //地址
	    r_address_textview.setText(r_address); 
	        
	    //電話
	    if(r_telephone.equals("null")==true){
	       	r_telephone_textview.setText("無");
	    }
	    else{
	        r_telephone_textview.setText(r_telephone); 
	    }
	    
	    //營業時間    
	    if(r_openinghours.equals("null")==true && r_closinghours.equals("null")==true){
	    	r_openclose_hours_textview.setText("無");
	    }
	    else if(r_openinghours.equals("null")==false && r_closinghours.equals("null")==true){
	    	StringBuffer op = new StringBuffer(r_openinghours);
	    	op.delete(5, 8); //刪除3個字元  ex. 10:00:00 > 10:00
	    	
	    	r_openclose_hours_textview.setText(op + " - " +"");
	    }
	    else if(r_openinghours.equals("null")==true && r_closinghours.equals("null")==false){
	    	StringBuffer cl = new StringBuffer(r_closinghours);
	    	cl.delete(5, 8);
	    	
	    	r_openclose_hours_textview.setText(" - " +cl);
	    }
	    else{
	    	StringBuffer op = new StringBuffer(r_openinghours);
	    	op.delete(5, 8);
	    	StringBuffer cl = new StringBuffer(r_closinghours);
	    	cl.delete(5, 8);
	    	r_openclose_hours_textview.setText(op+" - "+cl);
	    }

	    //備註
	    if(r_note.equals("null")==true){
	    	r_note_textview.setText("無"); 
	    }
	    else{
	    	r_note_textview.setText(r_note); 
	    }
	    
	    //建議菜色
	    if(r_suggest.equals("null")==true){
	    	r_suggest_textview.setText("無"); 
	    }
	    else{
	    	r_suggest_textview.setText(r_suggest); 
	    }
	    
		//照片 
	    //透過JSON傳回網址 獲取網路圖片
	    new AsyncTask<String, Void, Bitmap>(){
			@Override
			protected Bitmap doInBackground(String... params) {
				// TODO Auto-generated method stub
	            String url = params[0];
	            return getBitmapFromURL(url);				
			}
			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				//判斷傳回結果 
				if (result == null) //結果為空表沒抓到URL顯示內建圖檔 
				{
					r_photo.setBackgroundResource(R.drawable.nophoto);
				}
				else{
					r_photo.setImageBitmap (result);
					super.onPostExecute(result);
				}
			} 	
	    }.execute(r_photo_url); //放入圖片網址

	    //地址導航btn監聽 
		r_address_btn.setOnClickListener(new OnClickListener(){ 
			@Override
			public void onClick(View v) {
				// 取得自己位置與使用者輸入位置的緯經度
				double fromLat = User_Latitude;
				double fromLng = User_Longitude;
				double toLat = r_latitude;
				double toLng = r_longitude;
				direct(fromLat, fromLng, toLat, toLng);			
			}
		});
		
		//電話撥打btn監聽
		r_telephone_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri telephone_uri = Uri.parse("tel:"+r_telephone);
		        Intent intent = new Intent(Intent.ACTION_DIAL, telephone_uri);
		        startActivity(intent);				
			}
		});
		
		//加入我的最愛btn監聽
		favorite_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int RNoint = Integer.parseInt(RNo);
				Toast.makeText(RestaurantInfo.this, "成功加入我的最愛", Toast.LENGTH_LONG).show();
							
				SharedPreferences settings = getSharedPreferences("favorite", Context.MODE_PRIVATE); 
				settings.edit()
				.putInt(RNo, RNoint)
				.commit();
			}
		});
		
	}
	
	//照片r_photo副程式
	private static Bitmap getBitmapFromURL(String imageUrl){
		try {
			URL url = new URL(imageUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap bitmap = BitmapFactory.decodeStream(input);
	        return bitmap;
		}
	    catch (IOException e){
	        e.printStackTrace();
	        return null;
	    } 
	}
	
	//兩定位座標距離 副程式
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
	
	//地址導航btn副程式
	private void direct(double fromLat, double fromLng, double toLat, double toLng) {
		// 設定欲前往的Uri，fromLat.fromLng出發地緯經度，toLat.toLng目的地緯經度
		String uriStr = String.format(
				"http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f"
				, fromLat, fromLng, toLat, toLng);

		Intent intentre = new Intent();
		// 指定交由Google地圖應用程式接手
		intentre.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
		intentre.setAction(android.content.Intent.ACTION_VIEW); //ACTION_VIEW呈現資料給使用者觀看
		intentre.setData(Uri.parse(uriStr)); //將Uri資訊附加到Intent物件上
		startActivity(intentre);
	}
	
}
