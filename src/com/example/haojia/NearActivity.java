package com.example.haojia;
//附近美食主頁 以清單呈現


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.R.integer;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class NearActivity extends Activity{
	//取得緯經度
	private LocationManager locationManager;
	private Location myLocation;
	private Criteria criteria;
	
	//存放使用者座標位置
	String User_Latitude; //使用者緯度
	String User_Longitude; //使用者經度
	TextView User_Latitude_Longitude; //顯示使用者經緯度
	String Set_Distance; //設置範圍 目前做死的
	TextView range_text; //顯示方圓?KM範圍內
	ImageButton map_btn; //ImageButton點擊進入地圖瀏覽
	
	String Sql_string; //存放SQL指令變數
	String Click_Sql_string; //點擊率+1指令
	ArrayList<HashMap<String, Object>> data;
	
	//將搜尋到的資料以listview呈現
	ListView near_listview;
	double distance; //與目前所在距離
	
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
		setContentView(R.layout.near_activity);
		
		//取得座標位置程式碼
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
			
		
		//元件連接
		range_text = (TextView)findViewById(R.id.range_text); //顯示範圍
		User_Latitude_Longitude = (TextView)findViewById(R.id.User_Latitude_Longitude); //顯示經緯度
		near_listview = (ListView)findViewById(R.id.near_listview);
		map_btn = (ImageButton) findViewById(R.id.map_btn);
		
		//取得使用者座標 轉為String型態
		User_Latitude=String.valueOf(myLocation.getLatitude());
		User_Longitude=String.valueOf(myLocation.getLongitude());
		//本機測試用
		//User_Latitude="24.995194";
	    //User_Longitude="121.453119";
		
		//手動/自動設定 距離範圍
	    Set_Distance="0.5";
	    //Set_Distance="1";
	    
		//設定title版面顯示
	    User_Latitude_Longitude.setText(User_Latitude+","+User_Longitude);
	    if(Double.parseDouble(Set_Distance)<1){
	    	range_text.setText("距離"+Double.parseDouble(Set_Distance)*1000+"公尺以內的餐廳");
	    }
	    else{
	    	range_text.setText("距離"+Set_Distance+"公里以內的餐廳");
	    }
	    
		//ListView配適器 
	    data = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String,Object>> ListData = getListData();
	    SimpleAdapter adapter = new SimpleAdapter(this, ListData, R.layout.listview_item, 
	    		new String[]{"name", "address","distance","no"}, 
	    		new int[]{R.id.r_name, R.id.r_address, R.id.r_distance,R.id.r_no});
	    near_listview.setAdapter(adapter);
	    
	    //near_listview Item Click監聽
	    near_listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//抓取click item編號中的 no欄位值
				RNo = (String) data.get(position).get("no");
				
				//執行點擊次數+1				
				Click_Sql_string = "UPDATE restaurant SET R_Score = R_Score +1 WHERE R_No="+RNo ;
				String result = DBconnection.executeQuery(Click_Sql_string);
				Log.e("log_tag","點擊率SQL"+result.toString());				
				
				//傳送字串至RestaurantInfo.java
        		Intent restaurant_info_Intent =new Intent();
        		restaurant_info_Intent.setClass(NearActivity.this,RestaurantInfo.class);
        		//宣告要傳送的變數(並放入No) 
        		Bundle r_no_bundle = new Bundle();
        		r_no_bundle.putString("RNo",RNo); //No
        		restaurant_info_Intent.putExtras(r_no_bundle);
        		startActivity(restaurant_info_Intent);
				//Toast.makeText(SearchList.this,"點選了"+position+",NO."+RNo,Toast.LENGTH_SHORT).show();				
			}
	    });
	    
	    //map ImageButton監聽
        map_btn.setOnClickListener(new ImageButton.OnClickListener()
        {	
        	public void onClick(View v){
        		Intent nearIntent =new Intent();
        		nearIntent.setClass(NearActivity.this,NearMap.class);
        		startActivity(nearIntent);
        	}
        });
	    //Toast顯示 座標範圍距離 
	    //Toast.makeText(NearActivity.this,User_Latitude+","+User_Longitude+","+Set_Distance+"KM內",Toast.LENGTH_SHORT).show();

	}
	private List<HashMap<String,Object>> getListData() 
	{
		Sql_string="SELECT R_No,R_Name,R_Address,R_Latitude,R_Longitude," +
		"( 6371 * acos( cos( radians( "+User_Latitude+" ) ) * cos( radians( `R_Latitude` ) ) * cos( radians( `R_Longitude` ) - radians( "+User_Longitude+" ) ) + sin( radians( "+User_Latitude+" ) ) * sin(radians(`R_Latitude`)) ) ) AS `R_Distance` " +
		"FROM restaurant ";
		Sql_string += "HAVING `R_Distance` < "+ Set_Distance + " "; 
		Sql_string += "ORDER BY `R_Distance`";
		
		String result = DBconnection.executeQuery(Sql_string);
		Log.e("log_tag","JSON格式錯誤"+result.toString());
		try 
		{
			JSONArray jsonArray = new JSONArray(result);
	        for(int i=0; i<jsonArray.length(); i++)
	        {	
	        	JSONObject jsonData = jsonArray.getJSONObject(i);
	        	HashMap<String, Object> hashmap = new HashMap<String, Object>();
	        	hashmap.put("no", jsonData.getString("R_No"));
	        	hashmap.put("name", jsonData.getString("R_Name"));
	        	hashmap.put("address", jsonData.getString("R_Address"));
	        	
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
