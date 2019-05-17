package com.example.haojia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearMap extends Activity implements LocationListener{
	//宣告 google map 物件
	GoogleMap gmap;
	Marker marker;
	float zoom;
    private LocationManager locMgr;
	String bestProv;
	
	//存放使用者座標位置
	String User_Latitude; //使用者緯度
	String User_Longitude; //使用者經度
	String Set_Distance; //設置範圍 目前做死的
	
	//SQL查詢
	String Sql_string; //SQL指令
	
	//存放google map標記資料
	String Map_name[] = new String[]{};
	String Map_address[] = new String[]{};
	double Map_lat[] = new double[]{}; 
	double Map_lng[] = new double[]{};
	
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
		setContentView(R.layout.near_map);

		//取得 google map 元件     
		gmap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		gmap.getUiSettings().setCompassEnabled(true);
		gmap.getUiSettings().setZoomControlsEnabled(true);

        locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        Criteria criteria = new Criteria();
        bestProv = locMgr.getBestProvider(criteria, true); 
	}

	@Override
	public void onLocationChanged(Location myLocation) {
		// TODO Auto-generated method stub
		
		addviewmarkers();
		/*
		LatLng location = new LatLng(25.0172264,121.506378);
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(location)
		.zoom(8)
		.bearing(0)
		.tilt(0)
		.build();
		gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		*/
		
		// 取得地圖座標值:緯度,經度   
		User_Latitude=String.valueOf(myLocation.getLatitude());
		User_Longitude=String.valueOf(myLocation.getLongitude());
        
		LatLng Point = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());  
      	zoom=17; //設定放大倍率1(地球)-21(街景)
		gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(Point, zoom));	
		gmap.setMyLocationEnabled(true); // 顯示定位圖示
        //Toast.makeText(NearMap.this,"緯度:"+myLocation.getLongitude()+"\n"+"經度:"+myLocation.getLongitude(), Toast.LENGTH_LONG).show();
		gmap.clear();
        final LatLng User_Location = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
		Marker Location_Marker = gmap.addMarker(new MarkerOptions().position(User_Location).title("目前您的所在位置").snippet(""));
		gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(User_Location, 16));
		addMarkersToMap();		
	}
	private  void addMarkersToMap(){
		Set_Distance="0.5";
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
				gmap.addMarker(new MarkerOptions()
				.position(new LatLng(jsonData.getDouble("R_Latitude"), jsonData.getDouble("R_Longitude")))
		        .title(jsonData.getString("R_Name"))
		        .snippet(jsonData.getString("R_Address"))
		        .visible(true)
		        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
		        .alpha(0.7f));
			}
		}
		catch(JSONException e)
		{	        
			Log.e("log_tag","JSON轉換錯誤"+e.toString());
		}
	    //end addMarkersToMap
	}
	

	protected void onResume(){
		super.onResume();
		// 如果GPS或網路定位開啟，更新位置
		if(locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)||locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			locMgr.requestLocationUpdates(bestProv, 10000, 10, this);
			gmap.clear();
		}
		else{
			Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();			
		}		
	}
	
	protected void onPause() {	
		super.onPause();
		locMgr.removeUpdates(this);
	}	
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Criteria criteria = new Criteria();
		bestProv = locMgr.getBestProvider(criteria, true);		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item){		
    	switch (item.getItemId()){
	    	case R.id.menu_normal:
	    		gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // 一般地圖
	    		break;
	    	case R.id.menu_hybrid:
	    		gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // 混合地圖
	    	    break;
	    	case R.id.menu_satellite:
	    		gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); // 衛星地圖
	    	    break;
	    	case R.id.menu_terrain:
	    		gmap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);   // 地形圖 
	    	    break;
	    	case R.id.menu_traffic:
	    		gmap.setTrafficEnabled(true);
	    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }	
	public void addviewmarkers(){
		LatLng view = new LatLng(23.7052369,120.7213469);
		gmap.setMyLocationEnabled(true);
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(view)
		.zoom(8)
		.build();
		
		CameraUpdate cameraUpdate = CameraUpdateFactory
				.newCameraPosition(cameraPosition);
		gmap.animateCamera(cameraUpdate);
	}
}
