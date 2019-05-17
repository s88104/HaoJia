package com.example.haojia;
//個人最愛收藏清單，存於手機端
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class FavoriteActivity extends Activity{
	//取得緯經度
	private LocationManager locationManager;
	private Location myLocation;
	private Criteria criteria;
	
	String Sql_string; //存放SQL指令變數
	ArrayList<HashMap<String, Object>> data;
	String sql_rno;
	
	//將搜尋到的資料以listview呈現
	ListView favorite_listview;
	double distance; //與目前所在距離	
	String RNo; //bundle變數
	
	//存放使用者座標位置
	String User_Latitude; //緯度
	String User_Longitude; //經度	

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
		setContentView(R.layout.favorite_activity);
		
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
		favorite_listview = (ListView)findViewById(R.id.favorite_listview);
		
		getPrefNO(); //抓取NO副程式
		//將經緯度轉字串 SQL用
	    User_Latitude=String.valueOf(myLocation.getLatitude());
		User_Longitude=String.valueOf(myLocation.getLongitude());
		
		//ListView配適器 
	    data = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String,Object>> ListData = getListData();
	    SimpleAdapter adapter = new SimpleAdapter(this, ListData, R.layout.listview_item, 
	    		new String[]{"name", "address","distance","no"}, 
	    		new int[]{R.id.r_name, R.id.r_address, R.id.r_distance,R.id.r_no});
	    favorite_listview.setAdapter(adapter);
	    
	    //favorite_listview Item Click監聽
	    favorite_listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//抓取click item編號中的 no欄位值
				RNo = (String) data.get(position).get("no");
				
				//傳送字串至RestaurantInfo.java
        		Intent restaurant_info_Intent =new Intent();
        		restaurant_info_Intent.setClass(FavoriteActivity.this,RestaurantInfo.class);
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
		//整理字串，若sql_rno = 1,2,3, 則將最後一個逗號刪除，變1,2,3// 
		int rno_count = sql_rno.length();
		StringBuffer no = new StringBuffer(sql_rno);
		no.delete(rno_count - 1, rno_count);
		//Toast.makeText(FavoriteActivity.this, sql_rno+"\n"+no,Toast.LENGTH_LONG).show();
		
		Sql_string="SELECT R_No,R_Name,R_Address,R_Latitude,R_Longitude," +
		"( 6371 * acos( cos( radians( "+User_Latitude+" ) ) * cos( radians( `R_Latitude` ) ) * cos( radians( `R_Longitude` ) - radians( "+User_Longitude+" ) ) + sin( radians( "+User_Latitude+" ) ) * sin(radians(`R_Latitude`)) ) ) AS `R_Distance` " +
		"FROM restaurant ";
		Sql_string += "WHERE `R_No` IN("+ no +") "; 
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
	//抓取NO副程式
	public void getPrefNO(){
		SharedPreferences settings = getSharedPreferences( "favorite" , MODE_PRIVATE);
        Map<String,?> prefsMap = settings.getAll();
        sql_rno =""; //清空值
        for(Map.Entry<String,?> entry : prefsMap.entrySet())
        {
        	sql_rno += entry.getKey()+","; //將編號抓出，於後端加上逗號，例:1,2,3,//
            //Toast.makeText(FavoriteActivity.this,"資料為"+ entry.getKey(),Toast.LENGTH_SHORT).show();
        }
	}
}
