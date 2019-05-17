package com.example.haojia;
//主畫面


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends Activity {
	//在按一次退出應用程式
	private long exitTime = 0; 
	
	//主頁面按鍵宣告
	ImageButton near_btn;
	ImageButton random_btn; 
	ImageButton search_btn;
	ImageButton hot_btn;
	ImageButton favorite_btn;
	ImageButton insert_btn;
	ImageButton email_btn;
	ImageButton info_btn;
	
	private ViewFlow viewFlow;
	//private static final int[] ids = { R.drawable.test1, R.drawable.test2,
		//	R.drawable.test3,};

	private String[] urls = {
			"https://686b7c72e12dd7eb61467c66e4f4a08c50413c8e.googledrive.com/host/0B6Q-ykLT4CtpaXJBdDFVUDdYa0U/viewfolw_index.png",
			"https://686b7c72e12dd7eb61467c66e4f4a08c50413c8e.googledrive.com/host/0B6Q-ykLT4CtpaXJBdDFVUDdYa0U/viewfolw_note.png",
			"https://686b7c72e12dd7eb61467c66e4f4a08c50413c8e.googledrive.com/host/0B6Q-ykLT4CtpaXJBdDFVUDdYa0U/viewfolw_photo1.png",
			"https://686b7c72e12dd7eb61467c66e4f4a08c50413c8e.googledrive.com/host/0B6Q-ykLT4CtpaXJBdDFVUDdYa0U/viewfolw_photo2.png" };

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        near_btn = (ImageButton) findViewById(R.id.near_btn);
        random_btn = (ImageButton) findViewById(R.id.random_btn);
        search_btn = (ImageButton) findViewById(R.id.search_btn);
        hot_btn = (ImageButton) findViewById(R.id.hot_btn);
        favorite_btn = (ImageButton) findViewById(R.id.favorite_btn);
        insert_btn = (ImageButton) findViewById(R.id.insert_btn);
        email_btn = (ImageButton) findViewById(R.id.email_btn);
        info_btn = (ImageButton) findViewById(R.id.info_btn);
        
        viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(new ImageAdapter(this, urls));
		viewFlow.setmSideBuffer(urls.length); // 实际图片张数， 我的ImageAdapter实际图片张数为3

		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);
		viewFlow.setTimeSpan(2000);
		viewFlow.setSelection(3 * 1000); // 设置初始位置
		viewFlow.startAutoFlowTimer(); // 启动自动播放
        
        //附近ImageButton監聽
        near_btn.setOnClickListener(new ImageButton.OnClickListener()
        {	
        	public void onClick(View v){
        		Intent nearIntent =new Intent();
        		nearIntent.setClass(MainActivity.this,NearActivity.class);
        		startActivity(nearIntent);
        	}
        });
        
        //籤筒ImageButton監聽
        random_btn.setOnClickListener(new ImageButton.OnClickListener()
        {	
        	public void onClick(View v){
        		Intent nearIntent =new Intent();
        		nearIntent.setClass(MainActivity.this,RandomActivity.class);
        		startActivity(nearIntent);
        	}
        });
        
        //搜尋ImageButton監聽
        search_btn.setOnClickListener(new ImageButton.OnClickListener()
        {	
        	public void onClick(View v){
        		Intent searchIntent =new Intent();
        		searchIntent.setClass(MainActivity.this,SearchActivity.class);
        		startActivity(searchIntent);
        	}
        });
        
        //熱門ImageButton監聽
        hot_btn.setOnClickListener(new ImageButton.OnClickListener()
        {	
        	public void onClick(View v){
        		Intent hotIntent =new Intent();
        		hotIntent.setClass(MainActivity.this,HotActivity.class);
        		startActivity(hotIntent);
        	}
        });
        
        //最愛ImageButton監聽
        favorite_btn.setOnClickListener(new ImageButton.OnClickListener()
        {	
        	public void onClick(View v){
        		Intent favoriteIntent =new Intent();
        		favoriteIntent.setClass(MainActivity.this,FavoriteActivity.class);
        		startActivity(favoriteIntent);
        	}
        });
        
        //上傳ImageButton監聽
        insert_btn.setOnClickListener(new ImageButton.OnClickListener()
        {	
        	public void onClick(View v){
        		Intent insertIntent =new Intent();
        		insertIntent.setClass(MainActivity.this,InsertActivity.class);
        		startActivity(insertIntent);
        	}
        });
        
        //建議ImageButton監聽
        email_btn.setOnClickListener(new ImageButton.OnClickListener()
        {	
        	public void onClick(View v){
        		//Intent emailIntent =new Intent();
        		//emailIntent.setClass(MainActivity.this,EmailActivity.class);
        		//startActivity(emailIntent);
        		
        		//啟動信件功能
        		Intent it=new Intent(Intent.ACTION_SEND);  
        		String[] tos={"HaoJia@gmail.com"};  //收信者
        		String[] ccs={""};  //副本
        		it.putExtra(Intent.EXTRA_EMAIL, tos);  
        		it.putExtra(Intent.EXTRA_CC, ccs);  
        		it.putExtra(Intent.EXTRA_SUBJECT, "好食在  美食APP使用者建議"); //主旨 
        		it.putExtra(Intent.EXTRA_TEXT, "");   //信件內容
        		it.setType("message/rfc822");  
        		startActivity(Intent.createChooser(it, "請選擇Gmail")); //選擇信件標題
        	}
        });
        
        //關於ImageButton監聽
        info_btn.setOnClickListener(new ImageButton.OnClickListener()
        {	
        	public void onClick(View v){
        		Intent infoIntent =new Intent();
        		infoIntent.setClass(MainActivity.this,InfoActivity.class);
        		startActivity(infoIntent);
        	}
        });   
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //在按一次退出應用程式 程式片段
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
    	if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){ 
    		if((System.currentTimeMillis()-exitTime) > 2000){ 
    			Toast.makeText(getApplicationContext(), "在按一次退出「好食在」", Toast.LENGTH_SHORT).show(); 
    			exitTime = System.currentTimeMillis(); 
    		}
    		else { 
    			finish(); 
    			System.exit(0); 
    		} 
    		return true; 
    	}
    	return super.onKeyDown(keyCode, event); 
    }
}
