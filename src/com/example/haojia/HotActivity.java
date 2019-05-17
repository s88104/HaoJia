package com.example.haojia;
//熱門推薦
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class HotActivity extends Activity{
	//取得緯經度
	private LocationManager locationManager;
	private Location myLocation;
	private Criteria criteria;
	
	//存放使用者座標位置
	String User_Latitude; //使用者緯度
	String User_Longitude; //使用者經度

	String Sql_string;//存放SQL指令變數
	ArrayList<HashMap<String, Object>> data;
	
	//將搜尋到的資料以listview呈現
	ListView hot_listview;
	double distance; //與目前所在距離
	
	//存放分數進行判斷
	int score;
	
	//Bundle變數宣告 傳送RestaurantInfo
	String RNo;
	
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
		setContentView(R.layout.hot_activity);
		
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
		hot_listview = (ListView)findViewById(R.id.hot_listview);
		
		//ListView配適器 
	    data = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String,Object>> ListData = getListData();
	    SimpleAdapter adapter = new SimpleAdapter(this, ListData, R.layout.hot_listview_item, 
	    		new String[]{"name", "address","distance","no","score","photo"}, 
	    		new int[]{R.id.r_name, R.id.r_address, R.id.r_distance,R.id.r_no,R.id.r_score,R.id.r_score_photo});
	    
	    hot_listview.setAdapter(adapter);
	    //near_listview Item Click監聽
	    hot_listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//抓取click item編號中的 no欄位值
				RNo = (String) data.get(position).get("no");
					
				//傳送字串至RestaurantInfo.java
        		Intent restaurant_info_Intent =new Intent();
        		restaurant_info_Intent.setClass(HotActivity.this,RestaurantInfo.class);
        		//宣告要傳送的變數(並放入No) 
        		Bundle r_no_bundle = new Bundle();
        		r_no_bundle.putString("RNo",RNo); //No
        		restaurant_info_Intent.putExtras(r_no_bundle);
        		startActivity(restaurant_info_Intent);
				//Toast.makeText(SearchList.this,"點選了"+position+",NO."+RNo,Toast.LENGTH_SHORT).show();				
			}
	    });
	}
	private List<HashMap<String,Object>> getListData() 
	{		
		Sql_string = "SELECT R_No,R_Name,R_Address,R_Latitude,R_Longitude,R_Score ";
		Sql_string += "FROM restaurant ";
		Sql_string += "WHERE `R_Score` > 19 ";
		Sql_string += "ORDER BY `R_Score` DESC ";	
		//Toast.makeText(HotActivity.this,"R_Score" ,Toast.LENGTH_SHORT).show();
		
		String result = DBconnection.executeQuery(Sql_string);
		//String result = DBconnection.executeQuery_Near(User_Latitude,User_Longitude,Set_Distance);
		Log.e("log_tag","JSON格式錯誤"+result.toString());
		try 
		{
			JSONArray jsonArray = new JSONArray(result);
	        for(int i=0; i<jsonArray.length(); i++)
	        {	
	        	JSONObject jsonData = jsonArray.getJSONObject(i);
	        	HashMap<String, Object> hashmap = new HashMap<String, Object>();
	        	hashmap.put("no", jsonData.getString("R_No")); //NO
	        	hashmap.put("name", jsonData.getString("R_Name")); //名稱
	        	hashmap.put("address", jsonData.getString("R_Address")); //地址
	        	
	        	/*
	        	score = jsonData.getInt("R_Score"); //將分數抓出做判斷
	        	hashmap.put("score", score); //分數顯示
	        	if(score > 59 && score < 75 ){ //銅
	        		hashmap.put("photo",R.drawable.copper);
	        	}
	        	
	        	if(score > 74 && score < 90){
	        		hashmap.put("photo",R.drawable.silver);
	        	}
	        	if(score > 89){
	        		hashmap.put("photo",R.drawable.gold);
	        	}
	        	*/
	        	score = jsonData.getInt("R_Score"); //將分數抓出做判斷
	        	hashmap.put("score", score); //分數顯示
	        	if(score > 19 && score <30 ){ //銅
	        		hashmap.put("photo",R.drawable.copper);
	        	}
	        	if(score > 29 && score < 40){
	        		hashmap.put("photo",R.drawable.silver);
	        	}
	        	if(score > 39){
	        		hashmap.put("photo",R.drawable.gold);
	        	}
	        	
	        	distance=Distance(myLocation.getLatitude(),myLocation.getLongitude(),jsonData.getDouble("R_Latitude"),jsonData.getDouble("R_Longitude"));
	        	if(distance >= 1){ //大於等於1  不用轉換，小於轉
	        		hashmap.put("distance",Math.round((distance*10.0))/10.0+"公里"); //
	        	}
	        	else if(distance < 1){
	        		hashmap.put("distance",Math.round(distance*1000)+"公尺"); //轉公尺取整數
	        	}
	        	data.add(hashmap);
	        }
	    }
	    catch(JSONException e)
	    {
	        Log.e("log_tag","JSON轉換錯誤"+e.toString());
	    }
		return data;
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

