package com.example.haojia;
//餐廳搜尋結果，以ListView呈現，點擊後進到restaurant info
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchList extends Activity{
	//取得緯經度
	private LocationManager locationManager;
	private Location myLocation;
	private Criteria criteria;
	
	//bundle 存放上一頁傳來的字串變數
	String search_keyword; // 關鍵字
	String location_county; //縣市
	String location_area; //鄉鎮市區
	String type_group; //種類群組
	String type_class; //群組類別
	
	//最上方顯示 搜尋條件 變數
	TextView search_keyword_text; //關鍵字
	TextView type_text; //類別
	TextView location_text; //地點

	String Sql_string; //存放SQL指令變數
	String Click_Sql_string; //點擊率+1指令
	String Click_Sql_String; //點擊率+1指令
	ArrayList<HashMap<String, Object>> data;
	
	//以ListView 顯示查詢結果
	ListView search_listview; 
	double distance; //距離
	
	//存放使用者座標位置
	String User_Latitude; //緯度
	String User_Longitude; //經度
	
	//Bundle傳送變數宣告
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
		setContentView(R.layout.search_list);
	
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
		search_listview = (ListView) findViewById(R.id.search_listView);
		search_keyword_text = (TextView) findViewById(R.id.search_keyword_text);
		type_text = (TextView) findViewById(R.id.type_text);
		location_text = (TextView) findViewById(R.id.location_text);
		
		//接收上一Activity傳來的字串，並放進search_keyword變數裡
		Bundle search_bundle = this.getIntent().getExtras();
	    search_keyword=search_bundle.getString("search_keyword"); //關鍵字
	    type_group=search_bundle.getString("type_group"); //群組
	    type_class=search_bundle.getString("type_class"); //種類
	    location_county=search_bundle.getString("location_county"); //縣市
	    location_area=search_bundle.getString("location_area"); //鄉鎮市區
	    
	    //顯示 設計於頁面上方 使用者所設置搜尋條件
	    search_keyword_text.setText("您搜尋了："+search_keyword);
	    type_text.setText("類別："+type_group+" - "+type_class);
	    location_text.setText("地點："+location_county+" - "+location_area);
	   
	    /*//Toast顯示
	    Toast.makeText(SearchList.this,"您搜尋了:"+search_keyword+
	    		",類別:"+type_group+"-"+type_class+
	    		",地點:"+location_county+"-"+location_area,Toast.LENGTH_SHORT).show();
	    */
	    
	    //將經緯度轉字串 SQL用
	    User_Latitude=String.valueOf(myLocation.getLatitude());
		User_Longitude=String.valueOf(myLocation.getLongitude());
	    
	    //ListView配適器 
	    data = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String,Object>> ListData = getListData();
	    SimpleAdapter adapter = new SimpleAdapter(this, ListData, R.layout.listview_item, 
	    		new String[]{"name", "address","distance","no"}, 
	    		new int[]{R.id.r_name, R.id.r_address,R.id.r_distance,R.id.r_no});
	    search_listview.setAdapter(adapter);
	    
	    //search_listview Item Click監聽
	    search_listview.setOnItemClickListener(new OnItemClickListener(){
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
        		restaurant_info_Intent.setClass(SearchList.this,RestaurantInfo.class);
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
		//傳回結果為空,記得要顯示 沒有查到資料
		
		//只抓R_No,R_Name,R_Address,R_Latitude,R_Longitude
		Sql_string="SELECT R_No,R_Name,R_Address,R_Latitude,R_Longitude," +
		"( 6371 * acos( cos( radians( "+User_Latitude+" ) ) * cos( radians( `R_Latitude` ) ) * cos( radians( `R_Longitude` ) - radians( "+User_Longitude+" ) ) + sin( radians( "+User_Latitude+" ) ) * sin(radians(`R_Latitude`)) ) ) AS `R_Distance` " +
		"FROM restaurant " +
		"WHERE R_Name LIKE '%"+search_keyword+"%' ";
		//類別判斷
		if( type_group.equals("全部") == false && type_class.equals("全部") == true){ //表只設定group，class預設"全部"
			Sql_string += "AND `R_Type` IN(SELECT R_TypeID FROM `restaurant_type` WHERE `R_TypeGroup` = '"+ type_group +"') "; 
		}
		else if(type_group.equals("全部") == false && type_class.equals("全部") == false){ //表group跟class都有做設定
			Sql_string += "AND `R_Type` IN(SELECT R_TypeID FROM `restaurant_type` WHERE `R_TypeGroup` = '"+ type_group +"' AND `R_TypeClass` = '"+ type_class +"') ";
		}
		else if(type_group.equals("全部") == true && type_class.equals("全部") == true){//表類別 無條件
			Sql_string +=""; //無條件為空
		} 
		
		//地點判斷
		if(location_county.equals("全區") == false && location_area.equals("全區") == true){ //表只設定了county，area預設"全區"
			Sql_string += "AND `R_Address` LIKE '%"+ location_county +"%' ";
		}
		else if(location_county.equals("全區") == false && location_area.equals("全區") == false){
			Sql_string += "AND `R_Address` LIKE '%"+ location_county+location_area +"%' ";
		}
		else if(location_county.equals("全區") == true && location_area.equals("全區") == true){
			Sql_string += ""; //無條件為空
		}
	
		//依距離近至遠排序
		Sql_string+="ORDER BY `R_Distance` LIMIT 0 , 200"; 
		
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
